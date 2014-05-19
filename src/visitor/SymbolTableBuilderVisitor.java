package visitor;

import syntaxtree.*;
import symboltree.*;

import error.*;
import static error.ErrorObject.*;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Symbol table visitor, that visits each node of the Abstract Syntax tree
 * and stores information about classes and its' members. A symbol table 
 * is created for each scope of the program such as classes, methods, blocks
 * and the program itself. 
 *
 * Each declared class, method and/or variable is stored in the symbol table
 * corresponding to current scope and each symbol table references to its parent
 * scope except "Program" scope which has no parent.
 *
 * The vistor include some basic error checking, such as:
 * <ul>
 * <li> Duplicate classes 
 * <li> Duplicate field variables
 * <li> Duplicate local variables
 * <li> Duplicate method paramters
 * <li> Duplicate methods
 * </ul>
 *
 */

public class SymbolTableBuilderVisitor extends visitor.DepthFirstVisitor{

    public Table program;
    public LinkedList<Table> tableStack;
    public HashMap<Object, Table> scopeLookupTable;
    private ArrayList<CompilerError> errors = new ArrayList<CompilerError>();


    public SymbolTableBuilderVisitor(){
        tableStack = new LinkedList<Table>();
        scopeLookupTable = new HashMap<Object, Table>(); 
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
        return tableStack.peekFirst();
    }

    /**
     * Returns the parent scope of the current scope
     * which the visitor is in. 
     *
     * @return   returns current scope's parent scope 
     *           symbol table. Return null if current 
     *           scope has no parent.
     *
     */
    public Table getParentScope(){
        Table currentScope = tableStack.peekFirst();
        if(currentScope != null && currentScope.hasParent()){
           return currentScope.getParent();
        }else{
           return null;
        }
    }

    /**
     * Returns the scope that the object belongs to.
     *
     * @param   n   reference to a object which that 
     *              belongs to a symbol table
     * @return      returns the symbol table which 
     *              the object belongs to.
     *
     */
    public Table getScope(Object n){
        return scopeLookupTable.get(n);
    }

    /**
     * Begins a new scope, should be used when visiting a,
     * main class, class, method or block. A special 
     * case is (Program). This creates a new symbol table 
     * (Table) and pushes it onto the symbol table stack. 
     * A reference to this table is made by mapping visting
     * object to the table.
     *
     * @param   n           current object that is being visited
     * @param   scopeType   the type of scope, such as CLASS, 
     *                      METHOD or BLOCK, etc.
     * @return              returns the newly created Table.
     *
     */
    public Table startScope(Object n, Table.ScopeType scopeType){
        Table parentScope = tableStack.peekFirst();
        Table currentScope = new Table(parentScope, scopeType);

        /* Build child scope array for easy printing */
        if(parentScope != null) // Except for Program
            parentScope.getChildScopes().add(currentScope);

        /* Add to find table */
        scopeLookupTable.put(n, currentScope);

        /* Push current scope on stack */
        tableStack.push(currentScope);

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
        tableStack.pop();

        /* Return parent scope if needed */
        return tableStack.peekFirst();
    }


    /**
     * This is where the visiting begins. Creates a
     * new symbol table representing top level scope
     * of the program.
     *
     * @param   a Program object which should be top level 
     *          of the Abstract Syntax Tree
     */
    public void visit(Program n){
        /* Add program as current scope */
        startScope(n, Table.ScopeType.PROGRAM);
        super.visit(n);
        endScope();
    }   

    /**
     * Visitation of main class. This is a special case in which
     * also handles the main method of the program. This is a quick
     * and dirty solution which only works in the restricted MiniJava
     * grammar version. 
     *
     * By visiting this node, the visitor has reached a new scope and
     * a new symbol table is created. The visitor traverses the Class 
     * object in the new symbol table.
     *
     * In case of extending this compiler the main method should be 
     * regarded as a method.
     *
     * @param   a MainClass object 
     */
    public void visit(MainClass n){
        /* Visited main class so set up new scope */
        startScope(n, Table.ScopeType.MAIN_CLASS);

        /* Add main class to program scope */
        if(!getParentScope().insert(Symbol.symbol(n.i1.s), new ClassBinding(n.i1, new IdentifierType(n.i1.s), getCurrentScope() )) ){
            error(DUPLICATE_CLASS.at(n.row, n.col, n.i1.s));
        }
        getCurrentScope().setClassType(new IdentifierType(n.i1.s));
        getCurrentScope().insert(Symbol.symbol(n.i2.s), new VariableBinding(n.i2 ,new IdentifierType(n.i2.s), VariableBinding.VariableType.PARAM ));

        /* Set traverse in main class as a new child scope */
        super.visit(n);
        endScope();
    }
    
    /**
     * Visitation of a class that does not extend another class. 
     *
     * By visiting this node, the visitor has reached a new scope and
     * a new symbol table is created with scope type CLASS. The new
     * scope references the old scope as a parent. The visitor traverses 
     * the ClassDeclSimple object storing information in the new symbol
     * table
     *
     * @param   n   a ClassDeclSimple object 
     */
    public void visit(ClassDeclSimple n){
        /* Visited class so set up new scope */
        startScope(n, Table.ScopeType.CLASS);

        /* Add class to scope */
        if(!getParentScope().insert(Symbol.symbol(n.i.s), new ClassBinding(n.i, new IdentifierType(n.i.s), getCurrentScope() ))){
            error(DUPLICATE_CLASS.at(n.row, n.col, n.i.s));
        }

        getCurrentScope().setClassType(new IdentifierType(n.i.s));

        /* Set traverse in class */
        super.visit(n);

        /* End of scope */
        endScope();
    }

    /**
     * Visitation of a class that does extend another class. 
     *
     * By visiting this node, the visitor has reached a new scope and
     * a new symbol table is created with scope type CLASS. The new
     * scope references the old scope as a parent. A reference to 
     * the class it extends is also stored in ClassBinding. The visitor 
     * traverses the ClassDeclExtends object storing information in the 
     * new symbol table. 
     *  
     * @param   n   a ClassDeclExtends object 
     */
    public void visit(ClassDeclExtends n){
        /* Visited class so set up new scope */
        startScope(n, Table.ScopeType.CLASS);

        ClassBinding c = new ClassBinding(n.i, new IdentifierType(n.i.s), getCurrentScope());
        
        /* Add a reference to the class this class extends */
        c.addExtension(new IdentifierType(n.j.s));

        if(!getParentScope().insert(Symbol.symbol(n.i.s), c)){
            error(DUPLICATE_CLASS.at(n.row, n.col, n.i.s));
        }

        getCurrentScope().setClassType(new IdentifierType(n.i.s));
        
        /* Set traverse in class */
        super.visit(n);

        /* End of scope */
        endScope();
    }


    /**
     * Visitation of a method delcaration.
     *
     * Each declaration is checked so it theres not a duplicate 
     * declaration in current scope or any parent scope. If duplicate
     * variable declaration is detected an error is added to the error
     * buffer.
     *
     * @param   n   a VarDecl object 
     */
   public void visit(VarDecl n){
        Table.ScopeType scopeType = getCurrentScope().getScopeType();

        /* If not duplicate variable insert new into scope */
        switch(scopeType){
            case MAIN_CLASS:
                if(!getCurrentScope().insert(Symbol.symbol(n.i.s), new VariableBinding(n.i, n.t,VariableBinding.VariableType.LOCAL)) ){
                    error(DUPLICATE_FIELD.at(n.row, n.col, n.i.s, "mainclass"));
                }
                break;
            case CLASS:
                if(!getCurrentScope().insert(Symbol.symbol(n.i.s), new VariableBinding(n.i, n.t,VariableBinding.VariableType.FIELD)) ){
                    error(DUPLICATE_FIELD.at(n.row, n.col, n.i.s, "class"));
                }
                break;
            case METHOD:
                if(!getCurrentScope().insert(Symbol.symbol(n.i.s), new VariableBinding(n.i, n.t,VariableBinding.VariableType.LOCAL)) ){
                    error(DUPLICATE_LOCAL.at(n.row, n.col, n.i.s, "method"));
                }
                break;
            case BLOCK:
                VariableBinding v = (VariableBinding) getCurrentScope().find(Symbol.symbol(n.i.s), "variable");
                if(v != null){
                    error(DUPLICATE_FIELD.at(n.row, n.col, n.i.s, "block"));
                }

                if(!getCurrentScope().insert(Symbol.symbol(n.i.s), new VariableBinding(n.i, n.t, VariableBinding.VariableType.LOCAL)) ){
                    error(DUPLICATE_LOCAL.at(n.row, n.col, n.i.s, "block"));
                }
                break;
        }

        super.visit(n);
    }


    /**
     * Visitation of a method declaration.
     *
     * By visiting this node, the visitor has reached a new scope and
     * a new symbol table is created with scope type METHOD. The new
     * scope references the old scope as a parent. The visitor traverses 
     * the MethodDecl object storing information in the new symbol table. 
     *
     * Adds the method paramters as variable bindings to the method 
     * binding.
     * 
     * @param   n   a MethodDecl object 
     */
    public void visit(MethodDecl n)
    {
        /* Visited method so set up new scope */
        startScope(n, Table.ScopeType.METHOD);  

        /* Create a new method binding */
        MethodBinding m = new MethodBinding(n.i, n.t, getCurrentScope());

        /* Store formal parameters as local variables method binding */
        for(int i = 0; i < n.fl.size(); i++) {
            Formal f = (Formal) n.fl.elementAt(i);
            VariableBinding v = new VariableBinding(f.i, f.t, VariableBinding.VariableType.PARAM);
            m.addParam(v);
        }

        /* Add method binding to current scope */
        if(!getParentScope().insert(Symbol.symbol(n.i.s), m)){
            error(DUPLICATE_METHOD.at(n.row, n.col, n.i.s));
        }

        /* Set current scope type as parent scope type */
        getCurrentScope().setClassType(getParentScope().getClassType());

        /* Proceed to traverse nodes in method,
           such as formal parameters, local variables
           declarations and statements */
        super.visit(n);

        /* End of scope */
        endScope();
    }

    /**
     * Visitation of a formal parameter in a method declaration.
     *
     * In excess of the visitation in method declarations
     * the formal parameters are also added to current scope
     * for easy accessing.
     * 
     * @param   n   a Formal object 
     */
    public void visit(Formal n)
    {
        /* Add formal parameter to current scope */
        if(!getCurrentScope().insert(Symbol.symbol(n.i.s), new VariableBinding(n.i, n.t, VariableBinding.VariableType.PARAM))){
            error(DUPLICATE_PARAMETERS.at(n.row, n.col, n.i.s));
        }

        super.visit(n);
    }

    public void visit(IntArrayType n)
    {
        super.visit(n);
    }

    public void visit(LongArrayType n)
    {
        super.visit(n);
    }

    public void visit(BooleanType n)
    {
        super.visit(n);
    }

    public void visit(IntegerType n)
    {
        super.visit(n);
    }

    public void visit(LongType n)
    {
        super.visit(n);
    }

    public void visit(IdentifierType n)
    {
        super.visit(n);
    }

    /**
     * Visitation of a block. 
     *
     * By visiting this node, the visitor has reached a new scope and
     * a new symbol table is created with scope type BLOCK. The new
     * scope references the old scope as a parent. The visitor traverses 
     * the Block object storing information in the new symbol table. 
     *  
     * @param   n   a ClassDeclExtends object 
     */
    public void visit(Block n)
    {
        Table parentScope = tableStack.peekFirst();
    
        /* Visited method so set up new scope */
        startScope(n, Table.ScopeType.BLOCK);
        
        /* Add main class to program scope */
        getCurrentScope().setClassType(getParentScope().getClassType());
        super.visit(n);

        /* End of scope */
        endScope();
    }

    public void visit(If n)
    {
        super.visit(n);
    }

    public void visit(IfElse n)
    {
        super.visit(n);
    }

    public void visit(While n)
    {
        super.visit(n);
    }

    public void visit(Print n)
    {
        super.visit(n);
    }

    public void visit(Assign n)
    {
        super.visit(n);
    }

    public void visit(ArrayAssign n)
    {
        super.visit(n);
    }

    public void visit(And n)
    {
        super.visit(n);
    }

    public void visit(Or n)
    {
        super.visit(n);
    }

    public void visit(LessThan n)
    {
        super.visit(n);
    }

    public void visit(GreaterThan n)
    {
        super.visit(n);
    }

    public void visit(LessThanOrEqual n)
    {
        super.visit(n);
    }

    public void visit(GreaterThanOrEqual n)
    {
        super.visit(n);
    }

    public void visit(Equal n)
    {
        super.visit(n);
    }

    public void visit(NotEqual n)
    {
        super.visit(n);
    }

    public void visit(Plus n)
    {
        super.visit(n);
    }

    public void visit(Minus n)
    {
        super.visit(n);
    }

    public void visit(Times n)
    {
        super.visit(n);
    }

    public void visit(ArrayLookup n)
    {
        super.visit(n);
    }

    public void visit(ArrayLength n)
    {
        super.visit(n);
    }

    public void visit(Call n)
    {
        super.visit(n);
    }

    public void visit(IntegerLiteral n)
    {
        super.visit(n);
    }

    public void visit(LongLiteral n)
    {
        super.visit(n);
    }

    public void visit(True n)
    {
        super.visit(n);
    }

    public void visit(False n)
    {
        super.visit(n);
    }

    public void visit(IdentifierExp n)
    {
        super.visit(n);
    }

    public void visit(This n)
    {
        super.visit(n);
    }

    public void visit(NewIntArray n)
    {
        super.visit(n);
    }

    public void visit(NewLongArray n)
    {
        super.visit(n);
    }

    public void visit(NewObject n)
    {
        n.i.accept(this);   
    }

    public void visit(Not n)
    {
        super.visit(n);
    }

    public void visit(Identifier n)
    {
        super.visit(n);
    }

    public void visit(VoidType n)
    {
        super.visit(n);
    }
}