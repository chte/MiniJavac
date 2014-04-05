package symboltree;

import syntaxtree.*;
import java.util.ArrayList;

public class MethodBinding extends Binder{
  private ArrayList<VariableBinding> params; // If extra parameters exist like method

	/* Input parameter st can be arbitary string instead */
  public MethodBinding(Identifier id, Type type, Table scope) { 
    super(id, type, scope);
  }

  public void addParam(VariableBinding v){
    if(params == null){
      params = new java.util.ArrayList<VariableBinding>();
    }
    params.add(v);
  }

  public ArrayList<VariableBinding> getParams() {
    if(params == null){
      return new ArrayList<VariableBinding>();
    }
    return params;
  }

 
  public String toString(int level) {
    StringBuilder sb = new StringBuilder();
    //For the fields
    for (int i = 0; i < level; i++) {
        sb.append("\t");
    }

    sb.append("  " + super.getIdName() + "=<" + "RETURN TYPE: " + super.getTypeName());

		if(getParams().size() > 0) {
  		sb.append(", PARAMS: " + printParams());
    }
    sb.append(">");

    return sb.toString();
  }

  private String printParams() {
    String types = "{";
    for(int i = 0; i < params.size(); i++) {
      types += params.get(i).toString(0);
      if(i < params.size() - 1) {
        types += ", ";
      }
    }
    types += "  }";
    return types;
  } 

}