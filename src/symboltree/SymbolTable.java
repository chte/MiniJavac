package symboltree;

import java.util.HashMap;

public class SymbolTable{

	public SymbolTable parent;
	// public HashMap<String,

	public SymbolTable(SymbolTable parent){
		this.parent = parent;
	}
}