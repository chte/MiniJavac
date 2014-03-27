package symboltree;

import syntaxtree.*;

public class SymbolTreeVisitor implements visitor.DepthFirstVisitor{

	public SymbolTable ROOT;

	public SymbolTreeVisitor(){
		ROOT = new SymbolTable(null);
	}

	//Start the program 
	public void visit(Program n, SymbolTable parent){
		super.visit(n);
	}

	//Main program
	public void visit(MainClass n, SymbolTable parent){
		//Create the new scope with the parent as parent
		SymbolTable child = new SymbolTable(parent);
		//Add the child to the parent
		this.parent.addChild(this.child);
			
		super.visit(n);
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