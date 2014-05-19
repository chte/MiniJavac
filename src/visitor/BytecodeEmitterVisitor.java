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

/**
 * Bytecode emitter visitor, that visit each of the Abstract Syntax tree
 * and generate the belonging jasmin machine code for the JVM.
 *
 * When done the code is written down to file on disk.
 *
 * @see Bytecode.java for more information about the counters for the jvm
 */
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

    /**
     * Appends an error to an error buffer 
     *
     * @param   err    error message wrapped in a CompilerError object 
     *
     */
    public void error(final CompilerError err) {
        errors.add(err);
    }

    /**
     * Checks for any buffered errors. 
     *
     * @return   returns true if errors exist, false otherwise.
     *
     */
    public boolean hasErrors(){
        return !errors.isEmpty();
    }
    /**
     * Returns the buffered errors as a list.
     *
     * @return   returns an ArrayList of CompilerError objects
     *
     */
    public ArrayList<CompilerError> getErrors(){
        return errors;
    }

    /**
     * Returns current scope which the visitor is in,
     * by peeking in the table stack structure.
     *
     * @return   returns a symbol table of current scope.
     *
     */
    public Table getCurrentScope(){
        return stack.peekFirst();
    }

    /**
     * Returns the parent scope of the current scope
     * which the visitor is in. 
     *
     * @return   returns current scope's parent scope 
     *           symbol table.
     *
     */
    public Table getPreviousScope(){
        return stack.get(1);
    }

    /**
     * Clears the stack depth. Both stackSize and maxStackSize
     * will be set to 0.
     *
     * @return returns the new stack depth.
     *
     */
    public int clearStack(){
        /* Increment stack size tracker by one */
        stackSize = maxStackSize = 0;
        return Bytecode.stackDepth();
    }

    /**
     * Set's the stack limit. The limit is the current value
     * from the variable maxStacksize
     *
     * @param   p   index location where to write this in code
     *
     */
    private void storeStack(int p) {
        Bytecode.newline();
        Bytecode.writeind(p, ".limit stack " + maxStackSize);

        if (stackSize != 0){
            error(DEBUG_NEGATIVE_STACK_SIZE.at(0,0));
        }
    }

    /**
     * Increments the stack with 1. This method calls
     * incrementsStack(int s) with 1 as in parameter
     *
     */
    public void incrementStack(){
        /* Increment stack size tracker by one */
        incrementStack(1);
    }

    /**
     * Increases the stack counter with the given input.
     * If the new stack counter gets higher than the old
     * max stack counter the maxStackSize will be updated.
     *
     * @param s value of how much to increase the stack
     *
     */
    public void incrementStack(int s){
        /* Increment stack size tracker */
        stackSize += s;
        maxStackSize = Math.max(maxStackSize, stackSize);
    }

    /**
     * Decrements the stack with 1. This method calls
     * decrementStack(int s) with 1 as in parameter
     *
     */
    public void decrementStack(){
        /* Decrement stack size tracker by one */
        decrementStack(1);
    }

    /**
     * Decreases the stack counter with the given input.
     *
     * @param s value of how much to decrease the stack
     *
     */
    public void decrementStack(int s){
        /* Decrement stack size tracker */
        stackSize -= s;

        if(stackSize < 0){
            error(DEBUG_NEGATIVE_STACK_SIZE.at(0,0));
        }

    }

    /**
     * Begins a new scope, should be used when visiting a,
     * main class, class, method or block.
     * A reference to this table is made by mapping visiting
     * object to the table.
     *
     * @param   n           current object that is being visited
     *
     * @return              returns the newly created Table.
     *
     */
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

    /**
     * Ends the current scope, should be used when leaving a
     * class, method or block. A special case is (Program).
     * This pops the current symbol table from the symbol
     * table stack and returns the next symbol table on stack.
     *
     * @return    returns next symbol table on stack.
     *
     */
    public Table endScope(){
        /* Pop first on stack */
        stack.pop();

        /* Return parent scope if needed */
        return stack.peekFirst();
    }   

    /**
     * This is where the visiting begins. Creates a
     * new scope and setup the needed init code for jasmin.
     *
     * @param   a Program object which should be top level 
     *          of the Abstract Syntax Tree
     */
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

    /**
     * Visitation of main class. This is a special case in which
     * also handles the main method of the program. This is a quick
     * and dirty solution which only works in the restricted MiniJava
     * grammar version. 
     *
     * By visiting this node, the visitor creates a new scope and
     * generate the necessary jasmin code.
     *
     * In case of extending this compiler the main method should be 
     * regarded as a method.
     *
     * @param   a MainClass object 
     */
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

        /* Variable declaration plus parameters (1) */
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

    /**
     * Visitation of a class that does not extend another class. 
     *
     * By visiting this node, the visitor has reached a new scope and
     * the necessary jasmin code for a normal class declaration is added
     * to the Bytecode object
     *
     * @param   n   a ClassDeclSimple object 
     */
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

    /**
     * Visitation of a class that does extend another class. 
     *
     * By visiting this node, the visitor has reached a new scope and
     * generates the necessary jasmin code.
     *
     * Main difference from the ClassDeclSimple is the super class
     * is set to another class instead of the java base object. 
     *  
     * @param   n   a ClassDeclExtends object 
     */
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

    /**
     * Visitation of a method declaration.
     * It will only continue the recursion.
     *
     * @param   n   a VarDecl object 
     */
    public void visit(VarDecl n) {
        n.t.accept(this);
        n.i.accept(this);
    }

    /**
     * Visitation of a method declaration.
     *
     * By visiting this node, the visitor has reached a new scope and
     * the code for creating a new method will be added.
     * 
     * @param   n   a MethodDecl object 
     */
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

    /**
     * Visitation of a formal parameter in a method declaration.
     *
     * This will continue the recursion.
     * 
     * @param   n   a Formal object 
     */
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

    public void visit(IdentifierType n) {
    }

    /**
     * Visitation of a block. 
     *
     * By visiting this node, the visitor has reached a new scope and
     * a all the variable and statements will be accepted. 
     *  
     * @param   n   a Block object 
     */
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

    /**
     * This will generate the needed jasmine code for
     * simple if statement
     *
     * @param n     a If object 
     *
     */
    public void visit(If n) {
        n.e.accept(this);
        String trueLabel = Bytecode.label("IF_TRUE");
        String falseLabel = Bytecode.label("IF_FALSE");
        
        Bytecode.writeind("ifeq " + falseLabel);
        Bytecode.write(trueLabel + ":");
        n.s.accept(this);

        Bytecode.write(falseLabel + ":");
    }

    /**
     * This will generate the needed jasmine code for
     * simple if else statement
     *
     * @param n     a IfElse object 
     *
     */
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

    /**
     * This will generate the needed jasmine code for
     * a while loop
     *
     * @param n     a While object 
     *
     */
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

    /**
     * This will generate the needed jasmine code to
     * print the variable to the console.
     *
     * Depending on the type, different jasmin code
     * is generated.
     *
     * @param n     a Print object 
     *
     */
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

    /**
     * This will generate the needed jasmine code for
     * assignment a value to variable. 
     *
     * Note: special handling for the long type since 
     *       it has other properties than an normal int.
     *
     * @param n     a Assign object 
     *
     */
    public void visit(Assign n) {
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

    /**
     * This will generate the needed jasmine code for
     * assignment a a array either the type int or long
     *
     * Note: special handling for the long type since 
     *       it has other properties than an normal int.
     *
     * @param n    a ArrayAssign object 
     *
     */
    public void visit(ArrayAssign n) {
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

    /**
     * This will generate the needed jasmine code for
     * looking up either a long array or and int array
     *
     * @param n     a ArrayLookup object 
     *
     */
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
    }

    /**
     * This will generate the needed jasmine code for
     * get the length for an array.
     *
     * @param n     a ArrayLength object 
     *
     */
    public void visit(ArrayLength n) {
        n.e.accept(this);
        Bytecode.writeind("arraylength");
        decrementStack();
    }

    /**
     * This will generate the needed jasmine code for
     * and operation. Keep in mind that it
     * uses lazy evaluation.
     *
     * @param n     a And object 
     *
     */
    public void visit(And n) {
        typeChecker.enterScope(getCurrentScope());
        Type bool = n.e1.accept(typeChecker);
        if(bool instanceof BooleanType){
            n.e1.accept(this);
        }

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

    /**
     * This will generate the needed jasmine code for
     * an less than expression
     *
     * @param n     a LessThan object 
     *
     */
    public void visit(LessThan n) {
        char t = accept(n.e1, n.e2);
        eval(t, "lt");
    }

    /**
     * This will generate the needed jasmine code for
     * an less than or equal expression
     *
     * @param n     a LessThanEqual object 
     *
     */
    public void visit(LessThanOrEqual n) {
        char t = accept(n.e1, n.e2);
        eval(t, "le");
    }

    /**
     * This will generate the needed jasmine code for
     * long literal.
     *
     * Note: The stack get increased by 2 because of the long size
     *
     * @param n     a LongLiteral object 
     *
     */
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

    /**
     * This will generate the needed jasmine code for
     * long literal.
     *
     * Note: The stack get increased by 2 because of the long size
     *
     * @param t     char object, 'l' for long
     * @param op    String operator, how to do the comparison
     *
     */
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

    /**
     * This will generate the needed jasmine code for
     * the result of the two expressions. 
     *
     * Note: The stack get increased by 2 because of the long size
     *
     * @param e1    a Exp object
     * @param e2    a Exp object
     *
     * @return      return what the two expression becomes
     *
     */
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

    /**
     * This will generate the needed jasmine code for
     * a greater than expression, this will call 
     * eval function.
     *
     * @param n     a GreaterThan object 
     *
     */
    public void visit(GreaterThan n) {
        char t = accept(n.e1, n.e2);
        eval(t, "gt");
    }

    /**
     * This will generate the needed jasmine code for
     * a greater than or equal expression, this will call 
     * eval function.
     *
     * @param n     a GreaterThanOrEqual object 
     *
     */
    public void visit(GreaterThanOrEqual n) {
        char t = accept(n.e1, n.e2);
        eval(t, "ge");
    }

    /**
     * This will generate the needed jasmine code for
     * a equal expression, this will call 
     * eval function.
     *
     * @param n     a Equal object 
     *
     */
    public void visit(Equal n) {
        char t = accept(n.e1, n.e2);
        eval(t, "eq");
    }

    /**
     * This will generate the needed jasmine code for
     * not equal expression, this will call 
     * eval function.
     *
     * @param n     a NotEqual object 
     *
     */
    public void visit(NotEqual n) {
        char t = accept(n.e1, n.e2);
        eval(t, "ne");
    }

    /**
     * This will generate the needed jasmine code for
     * or expression.
     *
     * Note: This will do lazy evaluation
     *
     * @param n     a Or object 
     *
     */
    public void visit(Or n) {
        typeChecker.enterScope(getCurrentScope());
        Type bool = n.e1.accept(typeChecker);
        if(bool instanceof BooleanType){
            n.e1.accept(this);
        }

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

    /**
     * This will generate the needed jasmine code for
     * plus expression.
     *
     * @param n     a Plus object 
     *
     */
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

    /**
     * This will generate the needed jasmine code for
     * minus expression.
     *
     * @param n     a Minus object 
     *
     */
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

    /**
     * This will generate the needed jasmine code for
     * times expression, this will call 
     * eval function.
     *
     * @param n     a Times object 
     *
     */
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

    /**
     * This will generate the needed jasmine code for
     * calling a specific method in a class.
     *
     * @param n     a Call object 
     *
     */
    public void visit(Call n) {
        ClassBinding c = (ClassBinding) getCurrentScope().find(Symbol.symbol(n.c), "class");
        MethodBinding m = (MethodBinding) c.getScope().find(Symbol.symbol(n.i.s), "method");

        n.e.accept(this);
        for ( int i = 0; i < n.el.size(); i++ ) {
            n.el.elementAt(i).accept(this);
        }
    
        Bytecode.writeind("invokevirtual " + c.getIdName() + "/" + m.getIdName() + Bytecode.getMethodParams(m));   
    }

    /**
     * This will generate the needed jasmine code for
     * for putting a integer on the stack.
     *
     * Note: if possible it will use iconst for speed improvements
     *
     * @param n     a IntegerLiteral object 
     *
     */
    public void visit(IntegerLiteral n) {
        if (n.i >= -1 && n.i <= 5) {
            Bytecode.writeind(Bytecode.getConstant("iconst", n.i));
        } else {
            Bytecode.writeind("ldc " + n.i);
        }

        incrementStack();
    }

    /**
     * This will generate the needed jasmine code for
     * writing a true to the stack. Which is represented
     * by an integer with value 1
     *
     * @param n     a True object 
     *
     */
    public void visit(True n) {
        Bytecode.writeind("iconst_1");
        incrementStack();
    }

    /**
     * This will generate the needed jasmine code for
     * writing a false to the stack. Which is represented
     * by an integer with value 0
     *
     * @param n     a False object 
     *
     */
    public void visit(False n) {
        Bytecode.writeind("iconst_0");
        incrementStack();
    }

    /**
     * This will generate the needed jasmine code for
     * evaluation and expression and storing it in a 
     * identifier. 
     *
     * @param n     a IdentifierExp object 
     *
     */
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

    /**
     * This will generate the needed jasmine code for
     * for This object.
     *
     * @param n     a This object 
     *
     */
    public void visit(This n) {
        Bytecode.writeind(Bytecode.getConstant("aload", 0));
        incrementStack(2);
    }

    /**
     * This will generate the needed jasmine code for
     * for a new int array.
     *
     * @param n     a NewIntArray object 
     *
     */
    public void visit(NewIntArray n) {	
        n.e.accept(this);
        Bytecode.writeind("newarray int");
        incrementStack(1);
    }

    /**
     * This will generate the needed jasmine code for
     * for a new long array.
     *
     * @param n     a NewLongArray object 
     *
     */
    public void visit(NewLongArray n) {  
        n.e.accept(this);
        Bytecode.writeind("newarray long");
        incrementStack(2);
    }

    /**
     * This will generate the needed jasmine code for
     * for a new object
     *
     * @param n     a NewObject object 
     *
     */
    public void visit(NewObject n) {
        Bytecode.writeind("new '" + n.i.s + "'");
        Bytecode.writeind("dup");
        incrementStack(2);
        Bytecode.writeind("invokespecial " + n.i.s + "/<init>()V");
        decrementStack();
    }

    /**
     * This will generate the needed jasmine code for
     * Not operation, this is done by XOR
     *
     * @param n     a Not object 
     *
     */
    public void visit(Not n) {
        n.e.accept(this);
        Bytecode.writeind("iconst_1");
        incrementStack();
        Bytecode.writeind("ixor");
        decrementStack();
    }

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