package symboltree;

import syntaxtree.*;
import java.util.ArrayList;

public class Variable extends Binder{
	public Type type;

	public Variable(Identifier id, Type type, Binder.SymbolType st) { 
		super(type, st); 
		this.id = id;  
	}

    public String toString() {
		return "<" + super.getSymbolType() + " VARIABLE: " + super.getName() +  ">";
	}
}