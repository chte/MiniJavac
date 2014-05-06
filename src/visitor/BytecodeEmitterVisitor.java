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
    private int stackSize = 1;
    private int maxStackSize = 0;
    public LinkedList<Table> stack = new LinkedList<Table>();
    private ArrayList<CompilerError> errors  = new ArrayList<CompilerError>();
    public HashMap<Object, Table> scopeLookupTable;
    public TypeDepthFirstVisitor typeChecker;
    public int long_locals = 0;

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
        // Bytecode.writeind(".limit locals " + 100);

        n.i1.accept(this);
        n.i2.accept(this);

        int offset = 0;
        long_locals = 0;
        VariableBinding v;
        for (int i = 0; i < n.vdl.size() ;++i){
            n.vdl.elementAt(i).accept(this);
            v = (VariableBinding) getCurrentScope().find(Symbol.symbol(n.vdl.elementAt(i).i.s), "variable");
            v.setStackOffset(++offset);
            if(v.getType() instanceof LongType){
                offset++;
                long_locals++;
            }
        }


        int stackDepth = clearStack();

        for(int i = 0; i < n.sl.size(); ++i){
            n.sl.elementAt(i).accept(this);
        }

       Bytecode.writeind(".limit locals " + (offset + 1));
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
        Bytecode.directive(".class public '" + n.i.s + "'");
        Bytecode.directive(".super java/lang/Object");
       
        n.i.accept(this);
        int offset = 1;

        VariableBinding v;
        for (int i = 0; i < n.vl.size() ;++i){
            n.vl.elementAt(i).accept(this);
            v = (VariableBinding) getCurrentScope().find(Symbol.symbol(n.vl.elementAt(i).i.s), "variable");
            v.setStackOffset(offset++);
            offset += v.getType() instanceof LongType ? 1 : 0;

            if(v.getVariableType() == VariableBinding.VariableType.FIELD){
                Bytecode.writeind(".field public '" + n.vl.elementAt(i).i.s + "' " + Bytecode.descriptor(n.vl.elementAt(i).t));
            }

        }


        Bytecode.newline();
        Bytecode.standardConstructor("java/lang/Object");

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

        Bytecode.setClassName(n.i.s);
        Bytecode.directive(".class public '" + n.i.s + "'");
        Bytecode.directive(".super '" + n.j.s + "'");
        n.i.accept(this);
        int offset = 0;

        VariableBinding v;
        for (int i = 0; i < n.vl.size() ;++i){
            n.vl.elementAt(i).accept(this);
            v = (VariableBinding) getCurrentScope().find(Symbol.symbol(n.vl.elementAt(i).i.s), "variable");
            v.setStackOffset(++offset);
            offset += v.getType() instanceof LongType ? 1 : 0;

            if(v.getVariableType() == VariableBinding.VariableType.FIELD){
                Bytecode.writeind(".field public '" + n.vl.elementAt(i).i.s + "' " + Bytecode.descriptor(n.vl.elementAt(i).t));
            }

        }


        Bytecode.newline();
        Bytecode.standardConstructor(n.j.s);

        for ( int i = 0; i < n.ml.size(); i++ ) {
            Bytecode.newline();
            n.ml.elementAt(i).accept(this);
        }
    
        endScope();
    }

// Type t;
// Identifier i;
    public void visit(VarDecl n) {
        n.t.accept(this);
        n.i.accept(this);
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

        // Bytecode.writeind(".limit locals " + 100 );
        int stackDepth = clearStack();


        int offset = 0;
        long_locals = 0;
        VariableBinding v;
        boolean prev_long = false;
        for ( int i = 0; i < n.fl.size(); i++ ) {
            n.fl.elementAt(i).accept(this);		
            v = (VariableBinding) getCurrentScope().find(Symbol.symbol(n.fl.elementAt(i).i.s), "variable");
            if(prev_long){
                offset++;
            }

            if(v.getType() instanceof LongType){
                long_locals++;
                prev_long = true;
            }else{
                prev_long = false;
            }

            v.setStackOffset(++offset);

        }

        n.i.accept(this);
        for (int i = 0; i < n.vl.size() ;++i){
            n.vl.elementAt(i).accept(this);
            v = (VariableBinding) getCurrentScope().find(Symbol.symbol(n.vl.elementAt(i).i.s), "variable");
            if(prev_long){
                offset++;
            }

            if(v.getType() instanceof LongType){
                long_locals++;
                prev_long = true;
            }else{
                prev_long = false;
            }


            v.setStackOffset(++offset);

        }

        for ( int i = 0; i < n.sl.size(); i++ ) {
            n.sl.elementAt(i).accept(this);		
        }
        n.e.accept(this);

        if (isReferenceType(m.getType())){
            Bytecode.writeind("areturn");
        } else if (isLongType(m.getType())){
            Bytecode.writeind("lreturn");
        } else {
            Bytecode.writeind("ireturn");
        }

        storeStack(stackDepth);
        Bytecode.writeind(".limit locals " + (offset + long_locals + n.vl.size() + 1) );
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
        String startLabel = Bytecode.label("WHILE");
        String endLabel = Bytecode.label("END_WHILE");

        Bytecode.write(startLabel + ":");
        n.e.accept(this);
        Bytecode.writeind("ifeq " + endLabel);

        n.s.accept(this);
        Bytecode.writeind("goto " + startLabel);

        Bytecode.write(endLabel + ":");
    }

// Exp e;
    public void visit(Print n) {
        typeChecker.enterScope(getCurrentScope());
        Type t = n.e.accept(typeChecker);
        Bytecode.writeind("getstatic java/lang/System/out Ljava/io/PrintStream;");
        incrementStack();
        n.e.accept(this);
        if(Bytecode.descriptor(t).equals("Z")){
            Bytecode.writeind("invokestatic java/lang/Boolean/toString(" +  Bytecode.descriptor(t) + ")Ljava/lang/String;");
        }else if(Bytecode.descriptor(t).equals("J")){
            Bytecode.writeind("invokestatic java/lang/Long/toString(" +  Bytecode.descriptor(t) + ")Ljava/lang/String;");
        }else{
            Bytecode.writeind("invokestatic java/lang/Integer/toString(" +  Bytecode.descriptor(t) + ")Ljava/lang/String;");
        }
        Bytecode.writeind("invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V");
        decrementStack();
    }

// Identifier i;
// Exp e;
    public void visit(Assign n) {
        /* TODO: fix so integer can be assigned to long */

        n.i.accept(this);
        VariableBinding v = (VariableBinding) getCurrentScope().find(Symbol.symbol(n.i.s), "variable");
        if (v.getVariableType() == VariableBinding.VariableType.LOCAL || v.getVariableType() == VariableBinding.VariableType.PARAM) {
            n.e.accept(this);
            typeChecker.enterScope(getCurrentScope());
            Type t = n.e.accept(typeChecker);
            if(v.getType() instanceof LongType && t instanceof IntegerType){
                Bytecode.writeind("i2l");
                incrementStack();
            }
            if(v.getType() instanceof LongType) {
                long_locals++;
            }    

            Bytecode.writeind(Bytecode.store(v.getType(), v.getStackOffset()));
            decrementStack();

         }else if(v.getVariableType() == VariableBinding.VariableType.FIELD) {
            Bytecode.writeind("aload 0");
            incrementStack();
            n.e.accept(this);
            typeChecker.enterScope(getCurrentScope());
            Type t = n.e.accept(typeChecker);
            if(v.getType() instanceof LongType && t instanceof IntegerType){
                Bytecode.writeind("i2l");
                incrementStack();
            }
            if(v.getType() instanceof LongType) {
                long_locals++;
            }  
    
            Bytecode.writeind("putfield '" + Bytecode.getClassName() + "/" + n.i.s + "' " + Bytecode.descriptor(v.getType()));
            decrementStack(2);
        }

    }

// Identifier i;
// Exp e1,e2;
    public void visit(ArrayAssign n) {
        /* TODO: fix aload offset for long */
        n.i.accept(this);
        VariableBinding v = (VariableBinding) getCurrentScope().find(Symbol.symbol(n.i.s), "variable");

        if(v.getVariableType() == VariableBinding.VariableType.FIELD) {
            Bytecode.writeind("aload 0");
            incrementStack();
            Bytecode.writeind("getfield '" + Bytecode.getClassName() + "/" + n.i.s + "' " + Bytecode.descriptor(v.getType()));
        } else{
            Bytecode.writeind(Bytecode.load(v.getType(), v.getStackOffset()));
            incrementStack();
        }

        n.e1.accept(this);  
        n.e2.accept(this);

        typeChecker.enterScope(getCurrentScope());
        Type t2 = n.e2.accept(typeChecker);

        if(v.getType() instanceof LongArrayType && t2 instanceof LongType){
            Bytecode.writeind("lastore");
            decrementStack(4);
        }else if(v.getType() instanceof LongArrayType && t2 instanceof IntegerType){
            Bytecode.writeind("i2l"); 
            incrementStack();
            Bytecode.writeind("lastore");
            decrementStack(4);
        }
        else{
            Bytecode.writeind("iastore");
            decrementStack(3);
        }

    }

// Exp e1,e2;
    public void visit(ArrayLookup n) {

        n.e1.accept(this);  
        n.e2.accept(this);

        typeChecker.enterScope(getCurrentScope());
        Type t1 = n.e1.accept(typeChecker);

        if(t1 instanceof LongArrayType){
            Bytecode.writeind("laload");

        }else{
            Bytecode.writeind("iaload");
        }
        // decrementStack(2);

    }

// Exp e;
    public void visit(ArrayLength n) {
        n.e.accept(this);
        Bytecode.writeind("arraylength");
        decrementStack();
    }

// Exp e1,e2;
    public void visit(And n) {
        typeChecker.enterScope(getCurrentScope());
        Type bool = n.e1.accept(typeChecker);
        if(bool instanceof BooleanType){
            n.e1.accept(this);
        }

        // char t = accept(n.e1, n.e2);
        //     if(t == 'l'){
        //         Bytecode.writeind("lor");
        //         decrementStack(4);
        //     }else if( t == 'i' ){
        //         Bytecode.writeind("ior");
        //         decrementStack(2);
        //     }


        String trueLabel = Bytecode.label("IF_TRUE");
        String falseLabel = Bytecode.label("IF_FALSE");
        String nextLabel = Bytecode.label("IF_NEXT");
        
        Bytecode.writeind("ifeq " + falseLabel);

        Bytecode.write(trueLabel + ":");
            /* If true */
            bool = n.e2.accept(typeChecker);
            if(bool instanceof BooleanType){
                n.e2.accept(this);
            }

        Bytecode.writeind("goto " + nextLabel);

        Bytecode.write(falseLabel + ":");
            /* If false */
            Bytecode.writeind("iconst_0");

        Bytecode.write(nextLabel + ":");
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

        if (n.i >= 0 && n.i <= 1) {
            Bytecode.writeind(Bytecode.getConstant("lconst", (int) n.i));
        } else {
            Bytecode.writeind("ldc2_w " + n.i);
        }
        incrementStack(2);

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
        } else if( t1 instanceof IdentifierType && t2 instanceof IdentifierType ) {
            e1.accept(this);
            e2.accept(this);
            return 'a';
        } else {
            e1.accept(this);
            e2.accept(this);
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
        typeChecker.enterScope(getCurrentScope());
        Type bool = n.e1.accept(typeChecker);
        if(bool instanceof BooleanType){
            n.e1.accept(this);
        }

        // char t = accept(n.e1, n.e2);
        //     if(t == 'l'){
        //         Bytecode.writeind("lor");
        //         decrementStack(4);
        //     }else if( t == 'i' ){
        //         Bytecode.writeind("ior");
        //         decrementStack(2);
        //     }


        String trueLabel = Bytecode.label("IF_TRUE");
        String falseLabel = Bytecode.label("IF_FALSE");
        String nextLabel = Bytecode.label("IF_NEXT");
        
        Bytecode.writeind("ifeq " + falseLabel);

        Bytecode.write(trueLabel + ":");

        Bytecode.writeind("iconst_1");

        Bytecode.writeind("goto " + nextLabel);

        Bytecode.write(falseLabel + ":");

            bool = n.e2.accept(typeChecker);
            if(bool instanceof BooleanType){
                n.e2.accept(this);
            }

        Bytecode.write(nextLabel + ":");

    }

// Exp e1,e2;
    public void visit(Plus n) {
        char t = accept(n.e1, n.e2);
        if(t == 'l'){
            Bytecode.writeind("ladd");
            decrementStack(2);
        }else if( t == 'i' ){
            Bytecode.writeind("iadd");
            decrementStack();
        }
    }

// Exp e1,e2;
    public void visit(Minus n) {
        char t = accept(n.e1, n.e2);
        if(t == 'l'){
            Bytecode.writeind("lsub");
            decrementStack(2);
        }else if( t == 'i' ){
            Bytecode.writeind("isub");
            decrementStack();
        }
    }

// Exp e1,e2;
    public void visit(Times n) {
        char t = accept(n.e1, n.e2);
        if(t == 'l'){
            Bytecode.writeind("lmul");
            decrementStack(2);
        }else if( t == 'i' ){
            Bytecode.writeind("imul");
            decrementStack();
        }
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
        
        Bytecode.writeind("invokevirtual " + c.getIdName() + "/" + m.getIdName() + Bytecode.getMethodParams(m));   
    }

// int i;
    public void visit(IntegerLiteral n) {
        if (n.i >= -1 && n.i <= 5) {
            Bytecode.writeind(Bytecode.getConstant("iconst", n.i));
        } else {
            Bytecode.writeind("ldc " + n.i);
        }

        incrementStack();
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
        if (v.getVariableType() == VariableBinding.VariableType.LOCAL || v.getVariableType() == VariableBinding.VariableType.PARAM) {
            Bytecode.writeind(Bytecode.load(v.getType(), v.getStackOffset()));
         }else if(v.getVariableType() == VariableBinding.VariableType.FIELD ) {
            Bytecode.writeind("aload 0");
            Bytecode.writeind("getfield '" + Bytecode.getClassName() + "/" + n.s + "' " + Bytecode.descriptor(v.getType()));
        }
        incrementStack();
    }

    public void visit(This n) {
        Bytecode.writeind(Bytecode.getConstant("aload", 0));
        incrementStack(2);
    }

// Exp e;
    public void visit(NewIntArray n) {	
        n.e.accept(this);
        Bytecode.writeind("newarray int");
        incrementStack(1);
    }

// Exp e;
    public void visit(NewLongArray n) {  
        n.e.accept(this);
        Bytecode.writeind("newarray long");
        incrementStack(2);
    }


// Identifier i;
    public void visit(NewObject n) {
        Bytecode.writeind("new '" + n.i.s + "'");
        Bytecode.writeind("dup");
        incrementStack(2);
        Bytecode.writeind("invokespecial " + n.i.s + "/<init>()V");
        decrementStack();
    }

// Exp e;
    public void visit(Not n) {
        n.e.accept(this);
        Bytecode.writeind("iconst_1");
        incrementStack();
        Bytecode.writeind("ixor");
        decrementStack();

    }

// String s;
    public void visit(Identifier n) {

    }

    public void visit(VoidType n) {
    }


  private boolean isReferenceType(Type t) {
    return !(t instanceof IntegerType || t instanceof LongType || t instanceof BooleanType);
  }

  private boolean isLongType(Type t) {
    return (t instanceof LongType);
  }


}