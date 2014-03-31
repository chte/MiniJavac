package symboltree;

import syntaxtree.*;
import java.util.ArrayList;

public class Binder {  
    private Type type;
    private Symbol.SymbolType symbolType;
    private ArrayList<Type> extraTypes; // If extra parameters exist like method
    private SymbolTable scope;

    public Binder(Type type, Symbol.SymbolType symbolType) {
      this.type = type;
      this.symbolType = symbolType;
      this.extraTypes = null;
      this.scope = null;
    }

    public Binder(Type type, Symbol.SymbolType symbolType, SymbolTable scope) {
      this.type = type;
      this.symbolType = symbolType;
      this.extraTypes = null;
      this.scope = scope;
    }

    public void addExtraType(Type t){
      if(extraTypes == null){
        extraTypes = new java.util.ArrayList<Type>();
      }
      extraTypes.add(t);
    }

    public ArrayList<Type> getExtraTypes() {
      return extraTypes;
    }

    public Symbol.SymbolType getSymbolType() {
      return symbolType;
    }

    public SymbolTable getScope() {
      return scope;
    }

    public Type getType() {
      return type;
    }

    private String getName(Type t) {
      String[] parts = t.getClass().getName().split("\\.");
      return parts[parts.length-1];
    }

    @Override
    public String toString() {
      String binding = "<" + symbolType.toString() + ": " + getName(type);
      if(extraTypes != null) {
        binding += ", MethodParameters: " + printArrayList(extraTypes);
      }
      binding += ">";

      return binding;
    }

    private String printArrayList(ArrayList<Type> list) {
      String types = "{";
      for(int i = 0; i < list.size(); i++) {
        types += getName(list.get(i));
        if(i < list.size() - 1) {
          types += ", ";
        }
      }
      types += "}";
      return types;
    } 

}
