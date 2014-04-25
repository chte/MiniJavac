package symboltree;

import syntaxtree.*;
import java.util.ArrayList;

public class ClassBinding extends Binder{
  private ArrayList<Type> inherits = new ArrayList<Type>(); // If extra parameters exist like method
  private IdentifierType extension = null;

	/* Input parameter st can be arbitary string instead */
  public ClassBinding(Identifier id, Type type, Table scope) { 
    super(id, type, Binder.SymbolType.CLASS, scope);
  }

  public void addExtension(Type t){
    super.setSymbolType(Binder.SymbolType.EXTENDS);
    extension = (IdentifierType) t;
  }

  public IdentifierType getExtension() {
    return extension;
  }

  public boolean hasExtension() {
		if(extension != null) {
      return true;
    }else{
    	return false;
    }
  }

  public String toString(int level) {
    StringBuilder sb = new StringBuilder();
        //For the fields
    for (int i = 0; i < level; i++) {
        sb.append("\t");
    }

    sb.append("  " + super.getIdName() +"=<" + ((super.getSymbolType() == Binder.SymbolType.EXTENDS) ? "CLASS" + " " + super.getSymbolType() : super.getSymbolType()) + ": " + super.getTypeName());

		if(hasExtension()) {
  		sb.append(", Extension: " + super.getTypeName(extension)); // NOTERDEN KAN HA FLERA
    }
    sb.append(">");

    return sb.toString();
  }

}