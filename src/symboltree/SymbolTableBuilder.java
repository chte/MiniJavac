package symboltree;
import syntaxtree.*;
import error.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.LinkedList;

public class SymbolTableBuilder extends visitor.DepthFirstVisitor{

	public SymbolTable program;
	public LinkedList<SymbolTable> tableStack;
	public HashMap<Object, SymbolTable> scopeLookupTable;
    public CompilerErrorMsg errormsg;
    public ArrayList<String> classes;

	public SymbolTableBuilder(){
		tableStack = new LinkedList<SymbolTable>();
		scopeLookupTable = new HashMap<Object, SymbolTable>(); 
        classes = new ArrayList<String>();
	}


    public void error(String message) {
        errormsg = new CompilerErrorMsg(System.out, "COMPILE ERROR: " + message);
        errormsg.flush();
    }

    public void warning(String message) {
        errormsg = new CompilerErrorMsg(System.out, "COMPILE WARNING: " + message);
        errormsg.flush();
    }


	public SymbolTable getCurrentScope(){
		return tableStack.peekFirst();
	}

    public SymbolTable getParentScope(){
        SymbolTable currentScope = tableStack.peekFirst();
        if(currentScope != null && currentScope.hasParent()){
           return currentScope.getParent();
        }else{
           return null;
        }
    }

	public SymbolTable startScope(Object n, SymbolTable.ScopeType scopeType){
        SymbolTable parentScope = tableStack.peekFirst();
		SymbolTable currentScope = new SymbolTable(parentScope, scopeType);

		/* Add to find table */
		scopeLookupTable.put(n, currentScope);

		/* Push current scope on stack */
		tableStack.addFirst(currentScope);

        return parentScope;
	}

	public SymbolTable endScope(){
		/* Pop first on stack */
		tableStack.pollFirst();

		/* Return parent scope if needed */
		return tableStack.peekFirst();
	}

	//Start the program 
	public void visit(Program n){
		/* Add program as current scope */
		startScope(n, SymbolTable.ScopeType.PROGRAM);
		super.visit(n);
	}	

	//Main program
	public void visit(MainClass n){
        classes.add(n.i1.s);
        if(getCurrentScope().find(Symbol.symbol(n.i1.s)) != null) {
            error(n.i1.s + " was already defined in scope.");
        }

        /* Visited main class so set up new scope */
        startScope(n, SymbolTable.ScopeType.MAIN_CLASS);

        /* Add main class to program scope */
        getParentScope().insert(Symbol.symbol(n.i1.s), new Class( new IdentifierType(n.i1.s), getCurrentScope() ));
        getCurrentScope().setClassType(new IdentifierType(n.i1.s));

        getCurrentScope().insert(Symbol.symbol(n.i2.s), new Variable( new IdentifierType(n.i2.s), Binder.SymbolType.PARAM ));
        getParentScope().getChildScopes().add(getCurrentScope());

		/* Set traverse in main class as a new child scope */
		super.visit(n);
		endScope();
	}
    
    public void visit(ClassDeclSimple n){
        classes.add(n.i.s);
        if(getCurrentScope().find(Symbol.symbol(n.i.s)) != null) {
            error(n.i.s + " was already defined in scope.");
        }

        /* Visited class so set up new scope */
        startScope(n, SymbolTable.ScopeType.CLASS);

        /* Add class to scope */
        getParentScope().insert(Symbol.symbol(n.i.s), new Class( new IdentifierType(n.i.s), getCurrentScope() ));
        getCurrentScope().setClassType(new IdentifierType(n.i.s));
        getParentScope().getChildScopes().add(getCurrentScope());

        /* Set traverse in main class as a new child scope */
        super.visit(n);

        /* End of scope */
        endScope();
    }

    public void visit(ClassDeclExtends n){
        if(getCurrentScope().find(Symbol.symbol(n.i.s)) != null) {
            error(n.i.s + " was already defined in scope.");
        }
        /* Visited class so set up new scope */
        startScope(n, SymbolTable.ScopeType.CLASS);


        ArrayList<Type> extensions = new ArrayList<Type>();
        Class c = new Class(new IdentifierType(n.i.s), getCurrentScope());
        c.addExtension(new IdentifierType(n.j.s));


        getParentScope().insert(Symbol.symbol(n.i.s), c);


        getCurrentScope().classType = new IdentifierType(n.i.s);
        getParentScope().getChildScopes().add(getCurrentScope());

        super.visit(n);

        /* End of scope */
        endScope();
    }

   public void visit(VarDecl n){
        SymbolTable.ScopeType scopeType = getCurrentScope().getScopeType();
        if(getCurrentScope().find(Symbol.symbol(n.i.s)) != null) {
            error("Duplicate local variable " + n.i.s + ".");
        }   

        /* If not duplicate variable insert new into scope */
        switch(scopeType){
            case MAIN_CLASS:
                getCurrentScope().insert(Symbol.symbol(n.i.s), new Variable(n.t,  Binder.SymbolType.LOCAL));
                break;
            case CLASS:
                getCurrentScope().insert(Symbol.symbol(n.i.s), new Variable(n.t, Binder.SymbolType.FIELD));
                break;
            case METHOD:
            case BLOCK:
                getCurrentScope().insert(Symbol.symbol(n.i.s), new Variable(n.t, Binder.SymbolType.PARAM));
                break;
        }

        super.visit(n);
    }

    public void visit(MethodDecl n)
    {
        if(getCurrentScope().find(Symbol.symbol(n.i.s)) != null) {
            warning("Duplicate method names" + n.i.s + ".");
        }

        Method m = new Method(n.t, Binder.SymbolType.RETURN, getCurrentScope());
        for(int i = 0; i < n.fl.size(); i++) {
            m.addParam(new Variable(n.fl.elementAt(i).t, Binder.SymbolType.PARAM));
        }
        getCurrentScope().insert(Symbol.symbol(n.i.s), m);

        /* Visited method so set up new scope */
        startScope(n, SymbolTable.ScopeType.METHOD);  

        getCurrentScope().setClassType(getParentScope().getClassType());
        getParentScope().getChildScopes().add(getCurrentScope());

        super.visit(n);

        /* End of scope */
        endScope();
    }

    public void visit(Formal n)
    {
        Variable duplicate = (Variable) getCurrentScope().find(Symbol.symbol(n.i.s));
        if(duplicate != null && duplicate.getSymbolType() == Binder.SymbolType.PARAM) {
            error("Duplicate parameter " + n.i.s + ".");
        }

        getCurrentScope().insert(Symbol.symbol(n.i.s), new Variable(n.t, Binder.SymbolType.PARAM));

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
        startScope(n, SymbolTable.ScopeType.BLOCK);
        
        getCurrentScope().setClassType(getParentScope().getClassType());
        getParentScope().getChildScopes().add(getCurrentScope());

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