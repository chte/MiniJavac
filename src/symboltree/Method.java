package symboltree;

import syntaxtree.*;
import java.util.ArrayList;

public class Method extends Binder{
  private ArrayList<Variable> params; // If extra parameters exist like method

	/* Input parameter st can be arbitary string instead */
	public Method(Type type, Binder.SymbolType st, SymbolTable scope) { 
		super(type, st, scope);
	}

  public void addParam(Variable v){
    if(params == null){
      params = new java.util.ArrayList<Variable>();
    }
    params.add(v);
  }

  public ArrayList<Variable> getParams() {
    return params;
  }

  public boolean hasParams() {
		if(params != null) {
      return true;
    }else{
    	return false;
    }
  }
  @Override
  public String toString() {
    String binding = "<" + "METHOD " + super.getSymbolType() + ": " + super.getName();

		if(hasParams()) {
  		binding += ", MethodParameters: " + printParams();
    }
    binding += ">";

    return binding;
  }

  private String printParams() {
    String types = "{";
    for(int i = 0; i < params.size(); i++) {
      types += params.get(i);
      if(i < params.size() - 1) {
        types += ", ";
      }
    }
    types += "}";
    return types;
  } 

}