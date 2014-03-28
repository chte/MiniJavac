package symboltree;
import syntaxtree.*;
import java.util.*;

public class SymbolTreeVisitor implements visitor.DepthFirstVisitor{

	public SymbolTable program;
	public LinkedList<SymbolTable> tableStack = new LinkedList<SymbolTable>();
	public HashMap<String, SymbolTable> lookupTable = new HashMap<String, SymbolTable>(); 

	public SymbolTreeBuilder(){
		program = new SymbolTable(null);		
	}

	public void addToStack(SymbolTable st){
		tableStack.addFirst(st);
	}

	public SymbolTable getCurrentScope(){
		return tableStack.getFirst();
	}

	public void startScope(String id){
		SymbolTable newScope = new SymbolTable(tableStack.getFirst());
		lookupTable.put(id, newScope);
		tableStack.addFirst(newScope);
	}

	public SymbolTable endScope(){
		tableStack.removeFirst();
		return tableStack.getFirst();
	}

	//Start the program 
	public void visit(Program n){
		/* Add program as current scope */
		addToStack(program);
		//currentScope = program;
		super.visit(n);
	}	

	//Main program
	public void visit(MainClass n){
		/* Add main class to program scope */
		Symbol mainClassSymbol = Symbol.symbol(n.i1);
		Binder mainClassBinder = new Binder( new IdentifierType(n.i1)), Binder.SymbolType.MAINCLASS );
		mainClassBinder.addExtraType(new IdentifierType(n.i2));
		getCurrentScope().add(mainClassSymbol, mainClassBinder);

		/* Set traverse in main class as a new child scope */
		startScope(n.i1);
		super.visit(n);
		endScope();
	}
    
    public void visit(ClassDeclSimple n)
    {
		/* Set traverse in main class as a new child scope */
		startScope(n.i1);
		super.visit(n);
		endScope();
    }
    
    public void visit(ClassDeclExtends n)
    {


		/* Set traverse in main class as a new child scope */
		startScope(n.i1);
		super.visit(n);
		endScope();
    }
    
    public void visit(VarDecl n)
    {
		/* Add to previous scope */
		Symbol s = Symbol.symbol(n.i);
		Binder b = new Binder( n.t, Binder.SymbolType.LOCAL );
		getCurrentScope().add(s, b);

		/* Set traverse in as a new child scope */
		startScope(n.i1);
		super.visit(n);
		endScope();
    }
    
    public void visit(MethodDecl n)
    {
		/* Add to previous scope */
		Symbol s = Symbol.symbol(n.i);
		Binder b = new Binder( n.t, Binder.SymbolType.METHOD );
		for(int i = 0; i < n.vl.size(); i++){
		b.addExtraType(n.vl.elementAt(i));
		}
		getCurrentScope().add(s, b);

		/* Set traverse in as a new child scope */
		startScope(n.i1);
		super.visit(n);
		endScope();
    }
    
    public void visit(Formal n)
    {
		/* Set traverse in main class as a new child scope */
		startScope(n.i1);
		super.visit(n);
		endScope();
    }
  
    public void visit(Block n)
    {
		/* Set traverse in main class as a new child scope */
		startScope(n.i1);
		super.visit(n);
		endScope();
    }
    
    public void visit(If n)
    {
		/* Set traverse in main class as a new child scope */
		startScope(n.i1);
		super.visit(n);
		endScope();
    }
    
    public void visit(While n)
    {
	preWork(n);
	super.visit(n);
	postWork(n);
    }
    
    public void visit(Print n)
    {
	preWork(n);
	super.visit(n);
	postWork(n);
    }
    
 //    public void visit(Write n)
 //    {
	// preWork(n);
	// super.visit(n);
	// postWork(n);
 //    }
    
    public void visit(Assign n)
    {
	preWork(n);
	super.visit(n);
	postWork(n);
    }
    
    public void visit(ArrayAssign n)
    {
	preWork(n);
	super.visit(n);
	postWork(n);
    }
    
    public void visit(And n)
    {
	preWork(n);
	super.visit(n);
	postWork(n);
    }
    
    public void visit(LessThan n)
    {
	preWork(n);
	super.visit(n);
	postWork(n);
    }
    
    public void visit(Plus n)
    {
	preWork(n);
	super.visit(n);
	postWork(n);
    }
    
    public void visit(Minus n)
    {
	preWork(n);
	super.visit(n);
	postWork(n);
    }
    
    public void visit(Times n)
    {
	preWork(n);
	super.visit(n);
	postWork(n);
    }
    
    public void visit(ArrayLookup n)
    {
	preWork(n);
	super.visit(n);
	postWork(n);
    }
    
    public void visit(ArrayLength n)
    {
	preWork(n);
	super.visit(n);
	postWork(n);
    }
    
    public void visit(Call n)
    {
	preWork(n);
	super.visit(n);
	postWork(n);
    }
    
    public void visit(IntegerLiteral n)
    {
	preWork(n);
	super.visit(n);
	postWork(n);
    }
    
    public void visit(True n)
    {
	preWork(n);
	super.visit(n);
	postWork(n);
    }
    
    public void visit(False n)
    {
	preWork(n);
	super.visit(n);
	postWork(n);
    }
    
    public void visit(IdentifierExp n)
    {
	preWork(n);
	super.visit(n);
	postWork(n);
    }
    
    public void visit(This n)
    {
	preWork(n);
	super.visit(n);
	postWork(n);
    }
    
    public void visit(NewArray n)
    {
	preWork(n);
	super.visit(n);
	postWork(n);
    }
    
    public void visit(NewObject n)
    {
	preWork(n);
	n.i.accept(this);	
	postWork(n);
    }
    
    public void visit(Not n)
    {
	preWork(n);
	super.visit(n);
	postWork(n);
    }
    
    public void visit(Identifier n)
    {
	preWork(n);
	super.visit(n);
	postWork(n);
    }

	private String nodeName(Object o){
		String name = o.getClass().toString();
		int dot = name.lastIndexOf(".");
		if (dot != -1){
			name = name.substring(dot+1);
		}
		return name;	
    }

    private void print(String toPrint){
    	System.out.println("STV: " + toPrint);
    }
}