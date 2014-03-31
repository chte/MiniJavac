package symboltree;

import syntaxtree.*;
import java.util.ArrayList;

public class Class extends Binder{
  private Type extension; // If extra parameters exist like method

	/* Input parameter st can be arbitary string instead */
  public Class(Type type, SymbolTable scope) { 
    super(type, Binder.SymbolType.CLASS, scope);
  }

  public void addExtension(Type t){
    super.setSymbolType(Binder.SymbolType.EXTENDS);
    this.extension = t;
  }

  public Type getExtension() {
    return extension;
  }

  public boolean hasExtension() {
		if(extension == null) {
      return false;
    }else{
    	return true;
    }
  }
  @Override
  public String toString() {
    String binding = "<" + ((super.getSymbolType() == Binder.SymbolType.EXTENDS) ? "CLASS" + " " + super.getSymbolType() : super.getSymbolType()) + ": " + super.getName();

		if(hasExtension()) {
  		binding += ", Extension: " + super.getName(extension);
    }
    binding += ">";

    return binding;
  }

}