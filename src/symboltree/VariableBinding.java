package symboltree;

import syntaxtree.*;
import java.util.ArrayList;

public class VariableBinding extends Binder{

	/* Input parameter st can be arbitary string instead */
	public VariableBinding(Identifier id, Type type) { 
		super(id,type);
	}

    public String toString(int level) {
	    StringBuilder sb = new StringBuilder();
	    //For the fields
	    for (int i = 0; i < level; i++) {
	        sb.append("\t");
	    }
    	sb.append("  " + super.getIdName() + "=<VARIABLE: " + super.getTypeName() +  ">");
		return sb.toString();
	}
}