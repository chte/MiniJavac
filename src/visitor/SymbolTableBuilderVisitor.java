package visitor;
import syntaxtree.*;
import symboltree.*;
import error.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.LinkedList;

public class SymbolTableBuilderVisitor extends visitor.DepthFirstVisitor{

    public Table program;
    public LinkedList<Table> tableStack;
    public HashMap<Object, Table> scopeLookupTable;
    public CompilerErrorMsg error;
    public CompilerErrorMsg warning;
    public ArrayList<String> classes;

    public SymbolTableBuilderVisitor(){
        tableStack = new LinkedList<Table>();
        scopeLookupTable = new HashMap<Object, Table>(); 
        classes = new ArrayList<String>();
    }


    public void error(String message) {
        error = new CompilerErrorMsg(System.out, "COMPILE SYMBOL ERROR: " + message);
        error.flush();
    }

    public void warning(String message) {
        warning = new CompilerErrorMsg(System.out, "COMPILE SYMBOL WARNING: " + message);
        warning.flush();
    }


    public Table getCurrentScope(){
        return tableStack.peekFirst();
    }

    public Table getParentScope(){
        Table currentScope = tableStack.peekFirst();
        if(currentScope != null && currentScope.hasParent()){
           return currentScope.getParent();
        }else{
           return null;
        }
    }

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

    public Table endScope(){
        /* Pop first on stack */
        tableStack.pop();

        /* Return parent scope if needed */
        return tableStack.peekFirst();
    }

    //Start the program 
    public void visit(Program n){
        /* Add program as current scope */
        startScope(n, Table.ScopeType.PROGRAM);
        super.visit(n);
    }   

    //Main program
    public void visit(MainClass n){
        classes.add(n.i1.s);

        /* Visited main class so set up new scope */
        startScope(n, Table.ScopeType.MAIN_CLASS);

        /* Add main class to program scope */
        if(!getParentScope().insert(Symbol.symbol(n.i1.s), new ClassBinding(n.i1, new IdentifierType(n.i1.s), getCurrentScope() )) ){
            error(n.i1.s + " was already defined in scope.");
        }
        getCurrentScope().setClassType(new IdentifierType(n.i1.s));
        getCurrentScope().insert(Symbol.symbol(n.i2.s), new VariableBinding(n.i2 ,new IdentifierType(n.i2.s) ));

        /* Set traverse in main class as a new child scope */
        super.visit(n);
        endScope();
    }
    
    public void visit(ClassDeclSimple n){
        classes.add(n.i.s);
        /* Visited class so set up new scope */
        startScope(n, Table.ScopeType.CLASS);

        /* Add class to scope */
        if(!getParentScope().insert(Symbol.symbol(n.i.s), new ClassBinding(n.i, new IdentifierType(n.i.s), getCurrentScope() ))){
            error(n.i.s + " was already defined in scope.");   
        }

        getCurrentScope().setClassType(new IdentifierType(n.i.s));

        /* Set traverse in main class as a new child scope */
        super.visit(n);

        /* End of scope */
        endScope();
    }

    public void visit(ClassDeclExtends n){
        /* Visited class so set up new scope */
        startScope(n, Table.ScopeType.CLASS);

        ArrayList<Type> extensions = new ArrayList<Type>();
        ClassBinding c = new ClassBinding(n.i, new IdentifierType(n.i.s), getCurrentScope());
        c.addExtension(new IdentifierType(n.j.s));

        if(!getParentScope().insert(Symbol.symbol(n.i.s), c)){
            error(n.i.s + " was already defined in scope.");
        }

        getCurrentScope().classType = new IdentifierType(n.i.s);

        super.visit(n);

        /* End of scope */
        endScope();
    }

   public void visit(VarDecl n){
        Table.ScopeType scopeType = getCurrentScope().getScopeType();

        /* If not duplicate variable insert new into scope */
        switch(scopeType){
            case MAIN_CLASS:
                if(!getCurrentScope().insert(Symbol.symbol(n.i.s), new VariableBinding(n.i, n.t)) ){
                    error("Field identifier " + n.i.s + " was already defined in the main class.");
                }
                break;
            case CLASS:
                if(!getCurrentScope().insert(Symbol.symbol(n.i.s), new VariableBinding(n.i, n.t)) ){
                    error("Field identifier " + n.i.s + " was already defined in the class scope.");  
                }
                break;
            case METHOD:
                if(!getCurrentScope().insert(Symbol.symbol(n.i.s), new VariableBinding(n.i, n.t)) ){
                    error("Local identifier " + n.i.s + " was already defined in the method scope.");
                }
                break;
            case BLOCK:
                if(!getCurrentScope().insert(Symbol.symbol(n.i.s), new VariableBinding(n.i, n.t)) ){
                    error("Local identifier " + n.i.s + " was already defined in the scope.");
                }
                break;
        }

        super.visit(n);
    }

    public void visit(MethodDecl n)
    {
        /* Visited method so set up new scope */
        startScope(n, Table.ScopeType.METHOD);  

        MethodBinding m = new MethodBinding(n.i, n.t, getCurrentScope());
        for(int i = 0; i < n.fl.size(); i++) {
            VariableBinding v = new VariableBinding(n.fl.elementAt(i).i, n.fl.elementAt(i).t);
            m.addParam(v);
        }

        if(!getParentScope().insert(Symbol.symbol(n.i.s), m)){
            warning("Duplicate method names " + n.i.s + ".");
        }

        getCurrentScope().setClassType(getParentScope().getClassType());

        super.visit(n);

        /* End of scope */
        endScope();
    }

    public void visit(Formal n)
    {
        if(!getCurrentScope().insert(Symbol.symbol(n.i.s), new VariableBinding(n.i, n.t))){
            error("Duplicate parameter " + n.i.s + ".");
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

    public void visit(Block n)
    {
        /* Visited method so set up new scope */
        startScope(n, Table.ScopeType.BLOCK);
        
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