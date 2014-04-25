package symboltree;

import syntaxtree.*;
import java.util.ArrayList;

public class VariableBinding extends Binder{
    public enum VariableType {
        LOCAL, 
        PARAM,
        FIELD
    }

    private VariableType vt;
    private int so;

	public VariableBinding(Identifier id, Type t) { 
		super(id,t);
	}
	public VariableBinding(Identifier id, Type t, VariableType vt) { 
		super(id,t); this.vt = vt;
	}

	public VariableType getVariableType(){
		return this.vt;
	}

	public void setStackOffset(int so){
		this.so = so;
	}

	public String getStackOffset(){
		return ""+this.so;
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