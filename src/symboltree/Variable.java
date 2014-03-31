package symboltree;

import syntaxtree.*;
import java.util.ArrayList;

public class Variable extends Binder{

	/* Input parameter st can be arbitary string instead */
	public Variable(Type type, Binder.SymbolType st) { 
		super(type,st);
	}

	@Override
    public String toString() {
		return "<" + super.getSymbolType() + " VARIABLE: " + super.getName() +  ">";
	}
}