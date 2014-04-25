package visitor;

import syntaxtree.*;
import symboltree.*;
import assembler.*;
import error.*;

import static error.ErrorObject.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class BytecodeEmitterVisitor implements Visitor {
    private int stackSize = 0;
    private int maxStackSize = 0;
    public LinkedList<Table> stack = new LinkedList<Table>();
    private ArrayList<CompilerError> errors  = new ArrayList<CompilerError>();
    public HashMap<Object, Table> scopeLookupTable;
    public TypeDepthFirstVisitor typeChecker;

    public BytecodeEmitterVisitor(){
    }

    public void error(final CompilerError err) {
        errors.add(err);
    }

    public boolean hasErrors(){
        return !errors.isEmpty();
    }

    public ArrayList<CompilerError> getErrors(){
        return errors;
    }


    public Table getCurrentScope(){
        return stack.peekFirst();
    }

    public Table getPreviousScope(){
        return stack.get(1);
    }

    public int clearStack(){
        /* Increment stack size tracker by one */
        stackSize = maxStackSize = 0;
        return Bytecode.stackDepth();
    }

    private void storeStack(int p) {
        Bytecode.newline();
        Bytecode.writeind(p, ".limit stack " + maxStackSize);

        if (stackSize != 0){
            error(DEBUG_NEGATIVE_STACK_SIZE.at(0,0));
        }
    }

    public void incrementStack(){
        /* Increment stack size tracker by one */
        incrementStack(1);
    }

    public void incrementStack(int s){
        /* Increment stack size tracker */
        stackSize += s;
        maxStackSize = Math.max(maxStackSize, stackSize);
    }

    public void decrementStack(){
        /* Decrement stack size tracker by one */
        decrementStack(1);
    }

    public void decrementStack(int s){
        /* Decrement stack size tracker */
        stackSize -= s;

        if(stackSize < 0){
            error(DEBUG_NEGATIVE_STACK_SIZE.at(0,0));
        }

    }

    public Table startScope(Object n){

        Table parentScope = stack.peekFirst();

        Table currentScope = scopeLookupTable.get(n);
        if(currentScope == null){
            System.out.println("Null scope");
        }

        /* Push current scope on stack */
        stack.push(currentScope);

        return parentScope;
    }

    public Table endScope(){
        /* Pop first on stack */
        stack.pop();

        /* Return parent scope if needed */
        return stack.peekFirst();
    }   

// MainClass m;
// ClassDeclList cl;
    public void visit(Program n) {
        startScope(n);

        /* Setup main class */
        Bytecode.init();
        n.m.accept(this);
        Bytecode.save();
        for ( int i = 0; i < n.cl.size(); i++ ) {
            Bytecode.init();
            n.cl.elementAt(i).accept(this);
            Bytecode.save();
        }

        endScope();
    }

// Identifier i1,i2;
// Statement s;
    public void visit(MainClass n) {
        startScope(n);

        Bytecode.setClassName(n.i1.s);
        Bytecode.directive(".class public " + n.i1.s);
        Bytecode.directive(".super java/lang/Object");
        Bytecode.newline();
        Bytecode.comment("standard initializer (calls java.lang.Object's initializer)");
        Bytecode.standardConstructor("java/lang/Object");
        Bytecode.newline();
        Bytecode.comment("main() - main method follows");
        Bytecode.directive(".method public static main([Ljava/lang/String;)V");

        /* Variable declaraton plus paramaters (1) */
        Bytecode.writeind(".limit locals " + (n.vdl.size() + 1));

        n.i1.accept(this);
        n.i2.accept(this);

        int offset = 0;
        VariableBinding v;
        for (int i = 0; i < n.vdl.size() ;++i){
            n.vdl.elementAt(i).accept(this);
            v = (VariableBinding) getCurrentScope().find(Symbol.symbol(n.vdl.elementAt(i).i.s), "variable");
            v.setStackOffset(offset++);
        }


        int stackDepth = clearStack();

        for(int i = 0; i < n.sl.size(); ++i){
            n.sl.elementAt(i).accept(this);
        }

        storeStack(stackDepth);

        Bytecode.writeind("return");
        Bytecode.directive(".end method");

        endScope();
    }

// Identifier i;
// VarDeclList vl;
// MethodDeclList ml;
    public void visit(ClassDeclSimple n) {
        startScope(n);

        Bytecode.setClassName(n.i.s);
        Bytecode.directive(".class public " + n.i.s);
        Bytecode.directive(".super java/lang/Object");
       
        n.i.accept(this);
        int offset = 0;
        VariableBinding v;
        for (int i = 0; i < n.vl.size() ;++i){
            n.vl.elementAt(i).accept(this);
            v = (VariableBinding) getCurrentScope().find(Symbol.symbol(n.vl.elementAt(i).i.s), "variable");
            v.setStackOffset(offset++);
        }


        Bytecode.newline();
        Bytecode.standardConstructor(n.i.s);

        for ( int i = 0; i < n.ml.size(); i++ ) {
            Bytecode.newline();
            n.ml.elementAt(i).accept(this);
        }
    
        endScope();
    }

// Identifier i;
// Identifier j;
// VarDeclList vl;
// MethodDeclList ml;
    public void visit(ClassDeclExtends n) {
        startScope(n);

        n.i.accept(this);
        n.j.accept(this);
        for ( int i = 0; i < n.vl.size(); i++ ) {
            n.vl.elementAt(i).accept(this);
        }
        for ( int i = 0; i < n.ml.size(); i++ ) {
            n.ml.elementAt(i).accept(this);
        }

        endScope();
    }

// Type t;
// Identifier i;
    public void visit(VarDecl n) {
        n.t.accept(this);
        n.i.accept(this);
        Bytecode.writeind(".field protected " + n.i.s + " " + Bytecode.descriptor(n.t));
    }

// Type t;
// Identifier i;
// FormalList fl;
// VarDeclList vl;
// StatementList sl;
// Exp e;
    public void visit(MethodDecl n) {
        startScope(n);

        n.t.accept(this);
        n.i.accept(this);
        MethodBinding m = (MethodBinding) getCurrentScope().find(Symbol.symbol(n.i.s), "method");
        Bytecode.directive(".method public " + n.i.s + Bytecode.getMethodParams(m));
        Bytecode.writeind(".limit locals " + (n.fl.size() + n.vl.size()) );

        int stackDepth = clearStack();


        int offset = 0;
        VariableBinding v;
        for ( int i = 0; i < n.fl.size(); i++ ) {
            n.fl.elementAt(i).accept(this);		
            v = (VariableBinding) getCurrentScope().find(Symbol.symbol(n.fl.elementAt(i).i.s), "variable");
            v.setStackOffset(offset++);
        }

        n.i.accept(this);

        for (int i = 0; i < n.vl.size() ;++i){
            n.vl.elementAt(i).accept(this);
            v = (VariableBinding) getCurrentScope().find(Symbol.symbol(n.vl.elementAt(i).i.s), "variable");
            v.setStackOffset(offset++);
        }

        for ( int i = 0; i < n.sl.size(); i++ ) {
            n.sl.elementAt(i).accept(this);		
        }
        n.e.accept(this);

        if (isReferenceType(m.getType())){
            Bytecode.writeind("areturn");
        } else {
            Bytecode.writeind("ireturn");
        }

        decrementStack();
        storeStack(stackDepth);
        Bytecode.directive(".end method");

        endScope();
    }

// Type t;
// Identifier i;
    public void visit(Formal n) {
        n.t.accept(this);
        n.i.accept(this);
    }

    public void visit(IntArrayType n) {
    }

    public void visit(BooleanType n) {
    }

    public void visit(IntegerType n) {
    }

// String s;
    public void visit(IdentifierType n) {
    }

// StatementList sl;
    public void visit(Block n) {
        startScope(n);

        for ( int i = 0; i < n.vl.size(); i++ ) {
            n.vl.elementAt(i).accept(this);
        }
        for ( int i = 0; i < n.sl.size(); i++ ) {
            n.sl.elementAt(i).accept(this);
        }

        endScope();
    }

// Exp e;
// Statement s1,s2;
    public void visit(If n) {
        n.e.accept(this);
        String trueLabel = Bytecode.label("IF_TRUE");
        String falseLabel = Bytecode.label("IF_FALSE");
        
        Bytecode.writeind("ifeq " + falseLabel);
        Bytecode.write(trueLabel + ":");
        n.s.accept(this);

        Bytecode.write(falseLabel + ":");

    }

// Exp e;
// Statement s1,s2;
    public void visit(IfElse n) {
        n.e.accept(this);
        String trueLabel = Bytecode.label("IF_TRUE");
        String falseLabel = Bytecode.label("IF_FALSE");
        String nextLabel = Bytecode.label("IF_NEXT");
        
        Bytecode.writeind("ifeq " + falseLabel);

        Bytecode.write(trueLabel + ":");
        n.s1.accept(this);
        Bytecode.writeind("goto " + nextLabel);

        Bytecode.write(falseLabel + ":");
        n.s2.accept(this);
        Bytecode.write(nextLabel + ":");

    }

// Exp e;
// Statement s;
    public void visit(While n) {
        n.e.accept(this);
        n.s.accept(this);
    }

// Exp e;
    public void visit(Print n) {
        typeChecker.enterScope(getCurrentScope());
        Type t = n.e.accept(typeChecker);
        Bytecode.writeind("getstatic java/lang/System/out Ljava/io/PrintStream;");
        incrementStack();
        n.e.accept(this);
        Bytecode.writeind("invokevirtual java/io/PrintStream/println(" + Bytecode.descriptor(t) + ")V");
        decrementStack();
    }

// Identifier i;
// Exp e;
    public void visit(Assign n) {
        n.i.accept(this);
        VariableBinding v = (VariableBinding) getCurrentScope().find(Symbol.symbol(n.i.s), "variable");
        if (v.getVariableType() == VariableBinding.VariableType.LOCAL) {
            n.e.accept(this);
            Bytecode.writeind(Bytecode.store(v.getType(), v.getStackOffset()));
            decrementStack();
         }else if(v.getVariableType() == VariableBinding.VariableType.FIELD) {
            Bytecode.writeind(Bytecode.getConstant("aload", 0));
            incrementStack();
            n.e.accept(this);
            Bytecode.writeind("putfield " + Bytecode.getClassName() + "/" + n.i.s + " " + Bytecode.descriptor(v.getType()));
            decrementStack(2);
        }

    }

// Identifier i;
// Exp e1,e2;
    public void visit(ArrayAssign n) {
        n.i.accept(this);
        n.e1.accept(this);	
        n.e2.accept(this);
        Bytecode.writeind("iastore");
        decrementStack(3);
    }

// Exp e1,e2;
    public void visit(ArrayLookup n) {
        n.e1.accept(this);  
        n.e2.accept(this);
        Bytecode.writeind("arraylength");
        decrementStack();
    }

// Exp e;
    public void visit(ArrayLength n) {
        n.e.accept(this);
        Bytecode.writeind("arraylength");
    }

// Exp e1,e2;
    public void visit(And n) {
        n.e1.accept(this);
        n.e2.accept(this);
    }

// Exp e1,e2;
    public void visit(LessThan n) {
        char t = accept(n.e1, n.e2);
        eval(t, "lt");
    }

// Exp e1,e2;
    public void visit(LessThanOrEqual n) {
        char t = accept(n.e1, n.e2);
        eval(t, "le");
    }

    public void visit(LongLiteral n) {
    }

    public void visit(LongType n) {
    }

    public void visit(LongArrayType n) {
    }

    public void eval(char t, String op){
        String trueLabel = Bytecode.label("TRUE_LABEL");
        String endLabel = Bytecode.label("NEXT_LABEL");
        String instr = "";

        if(t == 'l'){
            Bytecode.writeind("lcmp");
            decrementStack(2);
            instr = "if"+op;
        }else {
            instr = "if_" + t + "cmp" + op;
        }

        Bytecode.writeind(instr + " " + trueLabel);
        Bytecode.writeind("iconst_0");
        Bytecode.writeind("goto " + endLabel);
        Bytecode.write(trueLabel + ":");
        Bytecode.writeind("iconst_1");
        Bytecode.write(endLabel + ":");

        incrementStack();
    }

    public char accept(Exp e1, Exp e2){
        typeChecker.enterScope(getCurrentScope());
        Type t1 = e1.accept(typeChecker);
        Type t2 = e2.accept(typeChecker);

        if( !(t1 instanceof LongType) && t2 instanceof LongType){
            e1.accept(this);
            Bytecode.writeind("i2l");  //One was not long so increment stack by one
            incrementStack();
            e2.accept(this);
            return 'l';
        }else if( t1 instanceof LongType && !(t2 instanceof LongType) ) {
            e1.accept(this);
            e2.accept(this);
            Bytecode.writeind("i2l");  //One was not long so increment stack by one
            incrementStack();
            return 'l';
        } else if( t1 instanceof LongType && t2 instanceof LongType ) {
            e1.accept(this);
            e2.accept(this);
            return 'l';
        } else {
            return 'i';
        }

    }

// Exp e1,e2;
    public void visit(GreaterThan n) {
        char t = accept(n.e1, n.e2);
        eval(t, "gt");
    }

// Exp e1,e2;
    public void visit(GreaterThanOrEqual n) {
        char t = accept(n.e1, n.e2);
        eval(t, "ge");
    }

// Exp e1,e2;
    public void visit(Equal n) {
        char t = accept(n.e1, n.e2);
        eval(t, "eq");
    }

// Exp e1,e2;
    public void visit(NotEqual n) {
        char t = accept(n.e1, n.e2);
        eval(t, "ne");
    }

// Exp e1,e2;
    public void visit(Or n) {
        char t = accept(n.e1, n.e2);
        eval(t, "le");
    }

// Exp e1,e2;
    public void visit(Plus n) {
        n.e1.accept(this);
        n.e2.accept(this);
        Bytecode.writeind("iadd");
        decrementStack();
    }

// Exp e1,e2;
    public void visit(Minus n) {
        n.e1.accept(this);
        n.e2.accept(this);
        Bytecode.writeind("isub");
        decrementStack();
    }

// Exp e1,e2;
    public void visit(Times n) {
        n.e1.accept(this);
        n.e2.accept(this);
        Bytecode.writeind("imul");
        decrementStack();
    }

// Exp e;
// Identifier i;
// ExpList el;
    public void visit(Call n) {
        ClassBinding c = (ClassBinding) getCurrentScope().find(Symbol.symbol(n.c), "class");
        MethodBinding m = (MethodBinding) c.getScope().find(Symbol.symbol(n.i.s), "method");

        n.e.accept(this);
        for ( int i = 0; i < n.el.size(); i++ ) {
            n.el.elementAt(i).accept(this);
        }
        
        Bytecode.writeind("invokevirtual " + n.i.s + "/" + m.getIdName() + Bytecode.getMethodParams(m));   
    }

// int i;
    public void visit(IntegerLiteral n) {
    }

    public void visit(True n) {
        Bytecode.writeind("iconst_1");
        incrementStack();
    }

    public void visit(False n) {
        Bytecode.writeind("iconst_0");
        incrementStack();
    }

// String s;
    public void visit(IdentifierExp n) {
        VariableBinding v = (VariableBinding) getCurrentScope().find(Symbol.symbol(n.s), "variable");
        if (v.getVariableType() == VariableBinding.VariableType.LOCAL) {
            Bytecode.writeind(Bytecode.load(v.getType(), v.getStackOffset()));
         }else if(v.getVariableType() == VariableBinding.VariableType.FIELD || v.getVariableType() == VariableBinding.VariableType.PARAM) {
            Bytecode.writeind(Bytecode.getConstant("aload", 0));
            Bytecode.writeind("getfield " + Bytecode.getClassName() + "/" + n.s + " " + Bytecode.descriptor(v.getType()));
        }
        incrementStack();
    }

    public void visit(This n) {
        Bytecode.writeind("aload_0");
        incrementStack();
    }

// Exp e;
    public void visit(NewIntArray n) {	
        n.e.accept(this);
        Bytecode.writeind("newarray int");
    }

// Exp e;
    public void visit(NewLongArray n) {  
        n.e.accept(this);
        Bytecode.writeind("newarray long");
    }


// Identifier i;
    public void visit(NewObject n) {
        Bytecode.writeind("new '" + n.i.s + "'");
        incrementStack();
        Bytecode.writeind("dup");
        incrementStack();
        Bytecode.writeind("invokespecial '" + n.i.s + "/<init>()V'");
        decrementStack();
    }

// Exp e;
    public void visit(Not n) {
        Bytecode.writeind("iconst_1");
        incrementStack();
        n.e.accept(this);
        Bytecode.writeind("ixor");
        decrementStack();

    }

// String s;
    public void visit(Identifier n) {
    }

    public void visit(VoidType n) {
    }


  private boolean isReferenceType(Type t) {
    return !(t instanceof IntegerType || t instanceof BooleanType);
  }

}