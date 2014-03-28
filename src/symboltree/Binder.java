package symboltree;

import syntaxtree.Type;

public class Binder {  
    public enum SymbolType {
      CLASS, 
      CLASSEXTENDS, 
      MAINCLASS, 
      METHOD,
      LOCAL, 
      PARAM, 
      FIELD 
    }

    public Type value;
    public SymbolType symbolType;
    private java.util.ArrayList<Type> extraTypes; // If extra parameters exist like method

    public Binder(Type t, SymbolType s) {
        this.type = t;
        this.symbolType = s;
    }

    public void addExtraType(Type t){
      if(extraTypes == null){
        extraTypes = new java.util.ArrayList<Type>();
      }
      extraTypes.add(t);
    }
}
