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
 * Bytecode emitter visitor, that visit each of the nodes in 
 * Abstract Syntax tree and generate the corresponding Jasmin 
 * machine code for the JVM.
 *
 * The Jasmin code is buffered until a class is completely traversed.
 *
 * When done the code is written down to file on disk.
 *
 * @see Bytecode.java for more information about the counters for the JVM
 */
public class BytecodeEmitterVisitor implements Visitor {
    private int stackSize = 1;
    private int maxStackSize = 0;
    public LinkedList<Table> stack = new LinkedList<Table>();
    private ArrayList<CompilerError> errors  = new ArrayList<CompilerError>();
    public HashMap<Object, Table> scopeLookupTable;
    public TypeDepthFirstVisitor typeChecker;
    public int offset = 0;

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
     * will be set to 0. This is required for each class.
     *
     */
    public void clearStack(){
        /* Increment stack size tracker by one */
        stackSize = maxStackSize = 0;
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
     * The stack can never become negative, if it would happen
     * and error is buffered.
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
     * Begins the next scope of the code. 
     * When a visiting a object, that object is
     * used to lookup which scope it belongs to.
     * The scope pushed onto this visitors table stack.
     * 
     *
     * @see  This method uses a lookup table generated
     *       with the symbol table builder, 
     *       see visitor.SymbolTableBuilderVisitor
     *
     * @param   n   current object that is being visited
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
     * Ends the current scope of the code by popping
     * current scope from stack, this should be invoked 
     * when visitation of the object is finished. 
     *
     * @param   n   current object that is being visited
     *
     */
    public Table endScope(){
        /* Pop first on stack */
        stack.pop();

        /* Return parent scope if needed */
        return stack.peekFirst();
    }   

    /**
     * This is where the visiting begins. Enters the
     * retrieved from scope lookup table and initializes
     * required code bytecode for a Java program.
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
     * By visiting this node, the visitor enters corresponding
     * scope from generated by SymbolTableBuilder and generate the 
     * necessary Jasmin code for the main method.
     *
     * In case of extending this compiler the main method should be 
     * regarded and handled as a method.
     *
     * @param   a MainClass object 
     */
    public void visit(MainClass n) {
        startScope(n);

        /* Set filename as class name */
        Bytecode.setClassName(n.i1.s);

        /* The class must inherit from Java Object */
        Bytecode.directive(".class public " + n.i1.s);
        Bytecode.directive(".super java/lang/Object");
        Bytecode.newline();
        Bytecode.comment("standard initializer (calls java.lang.Object's initializer)");
        Bytecode.standardConstructor("java/lang/Object");
        Bytecode.newline();
        Bytecode.comment("main() - main method follows");
        Bytecode.directive(".method public static main([Ljava/lang/String;)V");

        /* Reset stack */
        clearStack();

        n.i1.accept(this);
        n.i2.accept(this);

        offset = 0;
        VariableBinding v;
        for (int i = 0; i < n.vdl.size() ;++i){
            n.vdl.elementAt(i).accept(this);
            v = (VariableBinding) getCurrentScope().find(Symbol.symbol(n.vdl.elementAt(i).i.s), "variable");
            v.setStackOffset(++offset);
            if(isLongType(v.getType())){
                offset++;
            }
        }

        for(int i = 0; i < n.sl.size(); ++i){
            n.sl.elementAt(i).accept(this);
        }

        /* Limit locals to variable declaration plus parameters (1) */
        Bytecode.writeind(".limit locals " + (offset + 1));
        Bytecode.writeind(".limit stack " + maxStackSize);

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

        /* Set filename as class name */
        Bytecode.setClassName(n.i.s);

        Bytecode.directive(".class public '" + n.i.s + "'");
        Bytecode.directive(".super java/lang/Object");
       
        n.i.accept(this);

        /* Reset offset to zero */
        int offset = 0;
        VariableBinding v;

        /* Iterate through variable declaration list 
           and store index offsets correctly. Long constants
           require two offsets since requires 64 bit
           in memory */
        for (int i = 0; i < n.vl.size() ;++i){
            n.vl.elementAt(i).accept(this);
            v = (VariableBinding) getCurrentScope().find(Symbol.symbol(n.vl.elementAt(i).i.s), "variable");
            
            /* Store offset in VariableBinding */
            v.setStackOffset(++offset);

            /* If variable was of type long
               then increase offset by one */
            if(isLongType(v.getType())) offset++;

            /* Since it is declared as field in classes */
            Bytecode.writeind(".field public '" + n.vl.elementAt(i).i.s + "' " + Bytecode.descriptor(n.vl.elementAt(i).t));
        }


        Bytecode.newline();

        /* Class inherits from Java Object */
        Bytecode.standardConstructor("java/lang/Object");

        /* Accept method declarations */
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

        /* Set filename as class name */
        Bytecode.setClassName(n.i.s);

        Bytecode.directive(".class public '" + n.i.s + "'");
        Bytecode.directive(".super '" + n.j.s + "'");
        n.i.accept(this);
        
        /* Reset offset to zero */
        offset = 0;
        VariableBinding v;
        /* Iterate through variable declaration list 
           and store index offsets correctly. Long constants
           require two offsets since requires 64 bit
           in memory */
        for (int i = 0; i < n.vl.size() ;++i){
            n.vl.elementAt(i).accept(this);
            v = (VariableBinding) getCurrentScope().find(Symbol.symbol(n.vl.elementAt(i).i.s), "variable");
            
            /* Store offset in VariableBinding */
            v.setStackOffset(++offset);

            /* If variable was of type long
               then increase offset by one */
            if(isLongType(v.getType())) offset++;

            /* Since it is declared as field in classes */
            Bytecode.writeind(".field public '" + n.vl.elementAt(i).i.s + "' " + Bytecode.descriptor(n.vl.elementAt(i).t));
        }

        Bytecode.newline();

        /* Since it inherits from another class 
           the identifier of that class must
           be used. It inherits Java Object through
           the extension */
        Bytecode.standardConstructor(n.j.s);

        /* Accept method declarations */
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

        /* Get method binding in scope */
        MethodBinding m = (MethodBinding) getCurrentScope().find(Symbol.symbol(n.i.s), "method");
        Bytecode.directive(".method public " + n.i.s + Bytecode.getMethodParams(m));

        /* Reset stack */
        clearStack();

        offset = 0;
        VariableBinding v;
        boolean prev_long = false;
        
        /* Iterate through formal list and store
           index offsets correctly. Long constants
           require two offsets since requires 64 bit
           in memory */
        for ( int i = 0; i < n.fl.size(); i++ ) {
            n.fl.elementAt(i).accept(this);		
            v = (VariableBinding) getCurrentScope().find(Symbol.symbol(n.fl.elementAt(i).i.s), "variable");

            /* If current type is Long then increase long_locals
               so that "limit locals" continues to have correct size,
               and set prev_long to true so this invariant still is correct */
            prev_long = isLongType(v.getType()) ? true : false;
  
            /* Lastly, store offset in VariableBinding */
            v.setStackOffset(++offset);

            /* If formal was of type long
               then increase offset by one */
            if(prev_long) offset++;

        }

        n.i.accept(this);

        /* Iterate through formal list and store
           index offsets correctly. Long constants
           require two offsets since requires 64 bit
           in memory */
        for (int i = 0; i < n.vl.size() ;++i){
            n.vl.elementAt(i).accept(this);
            v = (VariableBinding) getCurrentScope().find(Symbol.symbol(n.vl.elementAt(i).i.s), "variable");
        
            /* If current type is Long then increase long_locals
               so that "limit locals" continues to have correct size,
               and set prev_long to true so this invariant still is correct */
            prev_long = isLongType(v.getType()) ? true : false;

            /* Lastly, store offset in VariableBinding */
            v.setStackOffset(++offset);     

            /* If previous formal was of type long
               then increase offset one */
            if(prev_long) offset++;


        }

        /* Traverse statements */
        for ( int i = 0; i < n.sl.size(); i++ ) {
            n.sl.elementAt(i).accept(this);		
        }
        n.e.accept(this);

        /* Check method return type write corresponding
           bytecode */
        if (isReferenceType(m.getType())){
            Bytecode.writeind("areturn");
        } else if (isLongType(m.getType())){
            Bytecode.writeind("lreturn");
        } else {
            Bytecode.writeind("ireturn");
        }

        /* Limit locals */
        Bytecode.writeind(".limit locals " + (offset + 1) );
        Bytecode.writeind(".limit stack " + maxStackSize);
        
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

        /* Setup branching labels, in this case 
           no next label is required since if it 
           is false we can just fall through */
        String trueLabel = Bytecode.label("IF_TRUE");
        String falseLabel = Bytecode.label("IF_FALSE");
        
        /* If value is zero jump to false label,
           otherwise fall through to true label */
        Bytecode.writeind("ifeq " + falseLabel);

        /* True label, accept statement */
        Bytecode.write(trueLabel + ":");
        n.s.accept(this);

        /* False label, fall through and
           continue the with the code as 
           normal */
        Bytecode.write(falseLabel + ":");
    }

    /**
     * This will generate the needed Jasmin code for
     * simple if else statement
     *
     * @param n     a IfElse object 
     *
     */
    public void visit(IfElse n) {
        n.e.accept(this);

        /* Setup branching labels */
        String trueLabel = Bytecode.label("IF_TRUE");
        String falseLabel = Bytecode.label("IF_FALSE");
        String nextLabel = Bytecode.label("IF_NEXT");
        
        /* If value is zero jump to false label,
           otherwise fall through to true label */
        Bytecode.writeind("ifeq " + falseLabel);

        /* True label */
        Bytecode.write(trueLabel + ":");
        n.s1.accept(this);

        /* Jump directly to next label */
        Bytecode.writeind("goto " + nextLabel);

        /* False label */
        Bytecode.write(falseLabel + ":");
        n.s2.accept(this);

        /* Next label, from here code is
           executed as normal */
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
        /* Setup branching labels */
        String startLabel = Bytecode.label("WHILE");
        String endLabel = Bytecode.label("END_WHILE");

        /* Set start label before comparator */
        Bytecode.write(startLabel + ":");

        /* Accept expression to be evaluated */
        n.e.accept(this);

        /* If it evaluates to false jump to
           end label, otherwise fall through */
        Bytecode.writeind("ifeq " + endLabel);

        /* Expression evaluated to true, so
           accept statements */
        n.s.accept(this);

        /* Go back to start label */
        Bytecode.writeind("goto " + startLabel);

        /* Evaluated to false, ignore contents
           of loop and continue with rest of
           the code */
        Bytecode.write(endLabel + ":");
    }

    /**
     * This will generate the needed Jasmin code to
     * print the variable to the console.
     *
     * Depending on the type, different Jasmin code
     * is generated, only primitive types are accepted.
     *
     * @param   n   a Print object 
     *
     */
    public void visit(Print n) {
        /* Enter current scope in type checker
           to retrieve expression type */
        typeChecker.enterScope(getCurrentScope());
        Type t = n.e.accept(typeChecker);

        /* Get PrintStream as IO */
        Bytecode.writeind("getstatic java/lang/System/out Ljava/io/PrintStream;");
        incrementStack();

        /* Now accept expression as regular */
        n.e.accept(this);

        /* Depending on type write invoke corresponding
           Java toString method */
        if(Bytecode.descriptor(t).equals("Z")){
            Bytecode.writeind("invokestatic java/lang/Boolean/toString(" +  Bytecode.descriptor(t) + ")Ljava/lang/String;");
        }else if(Bytecode.descriptor(t).equals("J")){
            Bytecode.writeind("invokestatic java/lang/Long/toString(" +  Bytecode.descriptor(t) + ")Ljava/lang/String;");
        }else{
            Bytecode.writeind("invokestatic java/lang/Integer/toString(" +  Bytecode.descriptor(t) + ")Ljava/lang/String;");
        }

        /* Invoke PrintStream's println */
        Bytecode.writeind("invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V");
        decrementStack();
    }

    /**
     * This will generate the needed Jasmin code for
     * assigning a value to variable. This function 
     * handles conversion from int to long, however 
     * not the other way around. This requires the
     * locals counter to be incremented by one since
     * long requires the double amount of stack space.
     *
     * Note: special handling for the long type since 
     *       it requires larger stack space than an 
     *       normal int and requires.
     *
     * @param n     a Assign object 
     *
     */
    public void visit(Assign n) {
        n.i.accept(this);

        /* Get variable from current scope */
        VariableBinding v = (VariableBinding) getCurrentScope().find(Symbol.symbol(n.i.s), "variable");

        /* If variable is of LOCAL or PARAM type do a regular store,
           else the variable is a field and putfield is requried */
        if (v.getVariableType() == VariableBinding.VariableType.LOCAL || v.getVariableType() == VariableBinding.VariableType.PARAM) {
            
            /* Accept the expression */
            n.e.accept(this);

            /* Enter the type checker in current scope,
               and retrieve the expression type */
            typeChecker.enterScope(getCurrentScope());
            Type t = n.e.accept(typeChecker);

            /* If left hand side is of type long and right hand
               side of type int, then convert int to long */
            if(isLongType(v.getType()) && isIntegerType(t)){
                Bytecode.writeind("i2l");
                incrementStack();
            }

            Bytecode.writeind(Bytecode.store(v.getType(), v.getStackOffset()));
            decrementStack();

         }else if(v.getVariableType() == VariableBinding.VariableType.FIELD) {
            
            /* Load object reference (class) which 
               field variable is declared in onto
               stack */
            Bytecode.writeind("aload 0");

            incrementStack();
            
            /* Accept the expression, i.e., value */
            n.e.accept(this);

           /* Enter the type checker in current scope,
              and retrieve the expression type */
            typeChecker.enterScope(getCurrentScope());
            Type t = n.e.accept(typeChecker);

           /* If left hand side is of type long and right hand
              side of type int, then convert int to long */
            if(isLongType(v.getType()) && isIntegerType(t)){
                Bytecode.writeind("i2l");
                incrementStack();
            }

            /* Store to field variable, object reference 
               is index 0 which is the class which the 
               method resides in */
            Bytecode.writeind("putfield '" + Bytecode.getClassName() + "/" + n.i.s + "' " + Bytecode.descriptor(v.getType()));
            decrementStack(2);
        }
    }

    /**
     * This will generate the needed Jasmin code for
     * assigning an array with a value of either int 
     * or long type.
     *
     * Note: special handling for the long type since 
     *       it requires larger stack space than an 
     *       normal int and requires.
     *
     * @param n    a ArrayAssign object 
     *
     */
    public void visit(ArrayAssign n) {
        n.i.accept(this);

        /* Get variable from current scope */
        VariableBinding v = (VariableBinding) getCurrentScope().find(Symbol.symbol(n.i.s), "variable");

        /* If variable is of LOCAL or PARAM type do a regular load,
           else the variable is a FIELD getfield is requried */
        if(v.getVariableType() == VariableBinding.VariableType.FIELD) {
            /* Load object reference (class) which
               field variable resides in onto stack */
            Bytecode.writeind("aload 0");

            /* Consume object reference and load field variable onto stack */
            Bytecode.writeind("getfield '" + Bytecode.getClassName() + "/" + n.i.s + "' " + Bytecode.descriptor(v.getType()));
        } else{
            /* Load LOCAL / PARAM variable onto stack */
            Bytecode.writeind(Bytecode.load(v.getType(), v.getStackOffset()));
        }

        if(isLongType(v.getType())){
            incrementStack(2);
        }else{
            incrementStack(1);
        }

        /* Accept expressions */
        n.e1.accept(this);  
        n.e2.accept(this);


       /* Enter the type checker in current scope,
          and retrieve the expression type */
        typeChecker.enterScope(getCurrentScope());
        Type t2 = n.e2.accept(typeChecker);

        /* Store variable, use corresponding store
           instruction depending on type */
        if(isLongArrayType(v.getType()) && isLongType(t2)){
            Bytecode.writeind("lastore");
            decrementStack(2);
        }else if(isLongArrayType(v.getType()) && isIntegerType(t2)){
            /* If RHS is of LongArrayType and LHS is IntegerType then convert 
               Integer to Long */
            Bytecode.writeind("i2l"); 
            incrementStack();
            Bytecode.writeind("lastore");
            decrementStack(2);
        }
        else{
            Bytecode.writeind("iastore");
            decrementStack(1);
        }

    }

    /**
     * This will generate the needed Jasmin code for
     * looking up either a long array or and int array
     *
     * @param n     a ArrayLookup object 
     *
     */
    public void visit(ArrayLookup n) {

        n.e1.accept(this);  
        n.e2.accept(this);

       /* Enter the type checker in current scope,
          and retrieve the expression type */
        typeChecker.enterScope(getCurrentScope());
        Type t1 = n.e1.accept(typeChecker);

        /* Instruction depending on Array Type */
        if(isLongArrayType(t1)){
            Bytecode.writeind("laload");
        }else{
            Bytecode.writeind("iaload");
        }
    }

    /**
     * This will generate the needed Jasmin code for
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
     * This will generate the needed Jasmin code for
     * "and" operation. Keep in mind that it uses lazy 
     * evaluation.
     *
     * @param n     a And object 
     *
     */
    public void visit(And n) {
       /* Enter the type checker in current scope,
          and retrieve the expression type */
        typeChecker.enterScope(getCurrentScope());
        Type bool = n.e1.accept(typeChecker);

        /* Make sure LHS evaluates to boolean type */
        if(isBooleanType(bool)){
            /* Accept LHS expressions */
            n.e1.accept(this);
        }

        /* Setup branching labels */
        String trueLabel = Bytecode.label("IF_TRUE");
        String falseLabel = Bytecode.label("IF_FALSE");
        String nextLabel = Bytecode.label("IF_NEXT");
        
        /* If value is zero jump to false label,
           otherwise fall through to true label */
        Bytecode.writeind("ifeq " + falseLabel);
        
        Bytecode.write(trueLabel + ":");
            /* If true, no lazy evaluation.
               check that RHS is of boolean type */
            bool = n.e2.accept(typeChecker);
                if(isBooleanType(bool)){
                /* Accept RHS expressions */
                n.e2.accept(this);
            }
        /* Jump to next label */
        Bytecode.writeind("goto " + nextLabel);

        Bytecode.write(falseLabel + ":");
            /* If false, then we lazy evaluated
               and skip evaluating RHS */
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
     * This will generate the needed Jasmin code comparision
     * operators such as Lesser Than, Greater Than, etc.
     *
     * @param   t     char object, 'l' for long
     * @param   op    String operator, how to do the comparison
     *
     */
    public void eval(char t, String op){
        /* Set up branch labels */
        String trueLabel = Bytecode.label("TRUE_LABEL");
        String endLabel = Bytecode.label("NEXT_LABEL");
        String instr = "";

        /* If long type, then do a long
           compare, else regular integer
           compare */
        if(t == 'l'){
            Bytecode.writeind("lcmp");
            decrementStack(2);  //Since it consumes two long and pushes
                                //another long on stack
            instr = "if"+op;
        }else {
            instr = "if_" + t + "cmp" + op;
        }

        /* If evaluated to true jump to true label,
           otherwise fall trough */
        Bytecode.writeind(instr + " " + trueLabel);

        /* If false write integer 0 onto stack */
        Bytecode.writeind("iconst_0");

        Bytecode.writeind("goto " + endLabel);

        Bytecode.write(trueLabel + ":");

        /* If true write integer 1 onto stack */
        Bytecode.writeind("iconst_1");

        Bytecode.write(endLabel + ":");

        /* Since we are writing to max stack limit */
        incrementStack();
    }

    /**
     * This will generate the needed Jasmin code for
     * the result of the two expressions. 
     *
     * Note: The stack get increased because of the
     *       int to long convertion
     *
     * @param e1    a Exp object
     * @param e2    a Exp object
     *
     * @return      return what the two expression becomes
     *
     */
    public char accept(Exp e1, Exp e2){
       /* Enter the type checker in current scope,
          and retrieve the expression type */
        typeChecker.enterScope(getCurrentScope());
        Type t1 = e1.accept(typeChecker);
        Type t2 = e2.accept(typeChecker);

        if( !isLongType(t1) && isLongType(t2)){
            e1.accept(this);
            Bytecode.writeind("i2l");  //One was not long so increment stack by one
            incrementStack();
            e2.accept(this);
            return 'l';
        }else if( isLongType(t1) && !isLongType(t2) ) {
            e1.accept(this);
            e2.accept(this);
            Bytecode.writeind("i2l");  //One was not long so increment stack by one
            incrementStack();
            return 'l';
        } else if( isLongType(t1) && isLongType(t2) ) {
            e1.accept(this);
            e2.accept(this);
            return 'l';
        } else if( isIdentifierType(t1) && isIdentifierType(t2) ) {
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
       /* Enter the type checker in current scope,
          and retrieve the expression type */
        typeChecker.enterScope(getCurrentScope());
        Type bool = n.e1.accept(typeChecker);

        /* Make sure LHS evaluates to boolean type */
        if(isBooleanType(bool)){
            /* Accept LHS expressions */
            n.e1.accept(this);
        }

        /* Setup branching labels */
        String trueLabel = Bytecode.label("IF_TRUE");
        String falseLabel = Bytecode.label("IF_FALSE");
        String nextLabel = Bytecode.label("IF_NEXT");
        

        /* If value is zero jump to false label,
           otherwise fall through to true label */
        Bytecode.writeind("ifeq " + falseLabel);

        Bytecode.write(trueLabel + ":");
        /* If true, then we lazy evaluate
           and skip evaluating RHS */
        Bytecode.writeind("iconst_1");
        
        /* Jump to next label */
        Bytecode.writeind("goto " + nextLabel);

        Bytecode.write(falseLabel + ":");
            /* If false, then no lazy evaluation,
               check if RHS of boolean type */
            bool = n.e2.accept(typeChecker);
        if(isBooleanType(bool)){
                /* Accept RHS expressions */
                n.e2.accept(this);
            }

        Bytecode.write(nextLabel + ":");
    }


    /**
     * This will generate the needed Jasmin code for
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
     * This will generate the needed Jasmin code for
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
        if(t == 'l'){ //Type long
            Bytecode.writeind("lmul");
            decrementStack(2);
        }else if( t == 'i' ){ //Type int
            Bytecode.writeind("imul");
            decrementStack();
        }
    }

    /**
     * This will generate the needed Jasmin code for
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
     * This will generate the needed Jasmin code for
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
     * This will generate the needed Jasmin code for
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
     * This will generate the needed Jasmin code for
     * evaluation of identifier expression and storing 
     * it in a identifier. 
     *
     * @param n     a IdentifierExp object 
     *
     */
    public void visit(IdentifierExp n) {
        VariableBinding v = (VariableBinding) getCurrentScope().find(Symbol.symbol(n.s), "variable");
        
        /* If variable is of LOCAL or PARAM type do a regular load,
           else the variable is a FIELD getfield is requried */
        if (v.getVariableType() == VariableBinding.VariableType.LOCAL || v.getVariableType() == VariableBinding.VariableType.PARAM) {
            Bytecode.writeind(Bytecode.load(v.getType(), v.getStackOffset()));
         }else if(v.getVariableType() == VariableBinding.VariableType.FIELD ) {
            /* Get object reference (class) that the field
               variable resides in and push onto stack*/
            Bytecode.writeind("aload 0");

            /* Load field variable */
            Bytecode.writeind("getfield '" + Bytecode.getClassName() + "/" + n.s + "' " + Bytecode.descriptor(v.getType()));
        }

        if( isLongType(v.getType()) ){
            incrementStack(2);
        }else{
            incrementStack(1);
        }
    }

    /**
     * This will generate the needed Jasmin code for
     * for "This" object.
     *
     * @param n     a This object 
     *
     */
    public void visit(This n) {
        Bytecode.writeind(Bytecode.getConstant("aload", 0));
        incrementStack(2);
    }

    /**
     * This will generate the needed Jasmin code for
     * for a NewIntArray.
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
     * This will generate the needed Jasmin code for
     * for a NewLongArray.
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
        /* Invoke special, consumes NEW from operand
           stack so the reference is duplicated in
           advance */
        Bytecode.writeind("dup");
        incrementStack(2);
        Bytecode.writeind("invokespecial " + n.i.s + "/<init>()V");
        decrementStack();
    }

    /**
     * This will generate the needed jasmine code for
     * Not operation, this is done by using XOR
     *
     * @param n     a Not object 
     *
     */
    public void visit(Not n) {
        /* Accept expression that is
           to be "inverted" */
        n.e.accept(this);
        /* Push integer 1 on stack */
        Bytecode.writeind("iconst_1");
        incrementStack();

        /* XOR integer 1 with evaluated expression */
        Bytecode.writeind("ixor");
        decrementStack();
    }

    public void visit(Identifier n) {
    }

    public void visit(VoidType n) {
    }

  /* Check that it is not of primitive type */
  private boolean isReferenceType(Type t) {
    return !(t instanceof IntegerType || t instanceof LongType || t instanceof BooleanType);
  }

  /* Check that it is long type */
  private boolean isLongType(Type t) {
    return (t instanceof LongType);
  }

  /* Check that it is long type */
  private boolean isIntegerType(Type t) {
    return (t instanceof IntegerType);
  }

  private boolean isBooleanType(Type t) {
    return (t instanceof BooleanType);
  }  

  private boolean isLongArrayType(Type t) {
    return (t instanceof LongArrayType);
  }  

  private boolean isIdentifierType(Type t) {
    return (t instanceof IdentifierType);
  } 
}