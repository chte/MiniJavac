package symboltree;

import syntaxtree.*;
import java.util.ArrayList;

public class Variable extends Binder{
	public Type type;

	/* Input parameter st can be arbitary string instead */
	public Variable(Identifier id, Type type, Binder.SymbolType st) { 
		super(type, st); 
	}

    public String toString() {
		return "<" + super.getSymbolType() + " VARIABLE: " + super.getName() +  ">";
	}
}