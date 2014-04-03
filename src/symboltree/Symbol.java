package symboltree;

import syntaxtree.Type;

public class Symbol {  
    public enum SymbolType {
        CLASS, 
        CLASS_EXTENDS, 
        METHOD_RETURN,
        LOCAL, 
        PARAM, 
        FIELD
    }

    private static java.util.HashMap<String, Symbol> dictionary = new java.util.HashMap<String, Symbol>();
    private String name;

    private Symbol(String n) { 
    	this.name = n; 
    }

    public static Symbol symbol(String n) {
    	String u = n.intern();
    	Symbol s = (Symbol) dictionary.get(u);
    	if(s == null) {
    		s = new Symbol(u); 
    		dictionary.put(u, s); 
    	}
    	return s;
    }

	@Override
	public String toString() {
		return name;
	}
}