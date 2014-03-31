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

	public SymbolTableBuilder(){
		tableStack = new LinkedList<SymbolTable>();
		scopeLookupTable = new HashMap<Object, SymbolTable>(); 
	}


    public void error(String message) {
        errormsg = new CompilerErrorMsg(System.out, "COMPILE ERROR: " + message);
        errormsg.flush();
    }

    public void warning(String message) {
        errormsg = new CompilerErrorMsg(System.out, "COMPILE WARNING: " + message);
        errormsg.flush();
    }

    public Type checkClass(Identifier n) {
        Binder b = getCurrentScope().find(n.s, Symbol.SymbolType.CLASS);
        if(b == null) {
            error(n.s + " cannot be resolved to a variable in this scope.");
            return new VoidType();
        }
        return  b.getType();
    }

    public Type checkVar(Identifier n) {
        Binder b = getCurrentScope().find(n.s, Symbol.SymbolType.FIELD);
        if(b == null) {
            error(n.s + " cannot be resolved to a variable in this scope.");
            return new VoidType();
        } 
        return b.getType();
    }
    public Type checkMethod(Identifier n) {
        Binder b = getCurrentScope().find(n.s, Symbol.SymbolType.METHOD_RETURN);
        if(b == null) {
            error(n.s + " cannot be resolved to a variable in this scope.");
            return new VoidType();
        } 
        return b.getType();
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
        if(getCurrentScope().find(n.i1.s, Symbol.SymbolType.CLASS) != null) {
            error(n.i1.s + " was already defined in scope.");
        }

        /* Visited main class so set up new scope */
        startScope(n, SymbolTable.ScopeType.MAIN_CLASS);

        /* Add main class to program scope */
        getParentScope().insert(n.i1.s, new Binder( new IdentifierType(n.i1.s), Symbol.SymbolType.CLASS, getCurrentScope() ));
        getCurrentScope().classType = new IdentifierType(n.i1.s);


        getCurrentScope().insert(n.i2.s, new Binder( new IdentifierType(n.i2.s), Symbol.SymbolType.PARAM ));
        getParentScope().getChildScopes().add(getCurrentScope());

		/* Set traverse in main class as a new child scope */
		super.visit(n);
		endScope();
	}
    
    public void visit(ClassDeclSimple n)
    {
        if(getCurrentScope().find(n.i.s, Symbol.SymbolType.CLASS) != null) {
            error(n.i.s + " was already defined in scope.");
        }

        /* Visited class so set up new scope */
        startScope(n, SymbolTable.ScopeType.CLASS);

        /* Add class to scope */
        getParentScope().insert(n.i.s, new Binder( new IdentifierType(n.i.s), Symbol.SymbolType.CLASS, getCurrentScope() ));
        getCurrentScope().classType = new IdentifierType(n.i.s);
        getParentScope().getChildScopes().add(getCurrentScope());

        /* Set traverse in main class as a new child scope */
        super.visit(n);

        /* End of scope */
        endScope();
    }

    public void visit(ClassDeclExtends n)
    {
        if(getCurrentScope().find(n.i.s, Symbol.SymbolType.CLASS_EXTENDS) != null) {
            error(n.i.s + " was already defined in scope.");
        }

        /* Visited class so set up new scope */
        startScope(n, SymbolTable.ScopeType.CLASS);

        ArrayList<Type> extensions = new ArrayList<Type>();
        Binder b =  new Binder(new IdentifierType(n.i.s), Symbol.SymbolType.CLASS_EXTENDS, getCurrentScope());
        b.addExtraType(new IdentifierType(n.j.s));

        getParentScope().insert(n.i.s, b);
        getCurrentScope().classType = new IdentifierType(n.i.s);
        getParentScope().getChildScopes().add(getCurrentScope());

        super.visit(n);

        /* End of scope */
        endScope();
    }

   public void visit(VarDecl n)
    {
        SymbolTable.ScopeType scopeType = getCurrentScope().getScopeType();
        Binder duplicateVarDecl = getCurrentScope().find(n.i.s, Symbol.SymbolType.FIELD);
        if(duplicateVarDecl != null) {
            Symbol.SymbolType duplicateDeclType = duplicateVarDecl.getSymbolType();
            error("Duplicate local variable " + n.i.s + ". Identfier already defined in defined in class scope.");
        }   

        /* If not duplicate variable insert new into scope */
        switch(scopeType){
            case MAIN_CLASS:
                getCurrentScope().insert(n.i.s, new Binder(n.t, Symbol.SymbolType.LOCAL));
                break;
            case CLASS:
                getCurrentScope().insert(n.i.s, new Binder(n.t, Symbol.SymbolType.FIELD));
                break;
            case METHOD:
            case BLOCK:
                getCurrentScope().insert(n.i.s, new Binder(n.t, Symbol.SymbolType.LOCAL));
                break;
        }

        super.visit(n);
    }

    public void visit(MethodDecl n)
    {
        Binder duplicateMethodDecl = getCurrentScope().find(n.i.s, Symbol.SymbolType.METHOD_RETURN);
        if(duplicateMethodDecl != null) {
            warning("Duplicate method names" + n.i.s + ".");
        }

        /* Visited method so set up new scope */
        startScope(n, SymbolTable.ScopeType.METHOD);  
        Binder b = new Binder(n.t, Symbol.SymbolType.METHOD_RETURN, getCurrentScope());
        for(int i = 0; i < n.fl.size(); i++) {
            b.addExtraType(n.fl.elementAt(i).t);
        }
        getParentScope().insert(n.i.s, b);
        getCurrentScope().setClassType(getParentScope().getClassType());
        getParentScope().getChildScopes().add(getCurrentScope());

        super.visit(n);

        /* End of scope */
        endScope();
    }

    public void visit(Formal n)
    {
        Binder duplicate = getCurrentScope().find(n.i.s, Symbol.SymbolType.PARAM);
        if(duplicate != null && duplicate.getSymbolType() == Symbol.SymbolType.PARAM) {
            error("Duplicate parameter " + n.i.s + ".");
        }

        getCurrentScope().insert(n.i.s, new Binder(n.t, Symbol.SymbolType.PARAM));

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