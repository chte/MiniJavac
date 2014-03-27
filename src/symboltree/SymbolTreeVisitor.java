package symboltree;

import syntaxtree.*;

public class SymbolTreeVisitor implements Visitor{

	public SymbolTable ROOT;

	public SymbolTreeVisitor(){
		ROOT = new SymbolTable(null);
	}

	public void visit(Program n){
		
	}	
}