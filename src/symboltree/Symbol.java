package symboltree;

import syntaxtree.Type;

public class Symbol {  
    private static HashMap<String, Symbol> dictionary = new HashMap<String, Symbol>();
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
		int saltIndex = name.indexOf("$");
		return name.substring(0, saltIndex);
	}
}