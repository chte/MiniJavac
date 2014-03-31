package symboltree;

import syntaxtree.*;
import java.util.ArrayList;

public class Binder {  
    public enum SymbolType {
        CLASS, 
        CLASS_EXTENDS, 
        METHOD_RETURN,
        LOCAL, 
        PARAM, 
        FIELD
    }
    private Identifier id;
    private Type type;
    private Binder.SymbolType st;
    private ArrayList<Type> extraTypes; // If extra parameters exist like method
    private SymbolTable scope;

    public Binder(Type type) {
      this.type = type;
      this.st = st;
      this.extraTypes = null;
      this.scope = null;
    }

    public Binder(Type type, Binder.SymbolType st) {
      this.type = type;
      this.st = st;
      this.extraTypes = null;
      this.scope = null;
    }

    public Binder(Type type, Binder.SymbolType st, SymbolTable scope) {
      this.type = type;
      this.st = st;
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

    public Type getExtension() {
      if(st == Binder.SymbolType.CLASS_EXTENDS){
        return extraTypes.get(0);
      }else{
        return null;
      }
    }

    public Binder.SymbolType getSymbolType() {
      return st;
    }

    public SymbolTable getScope() {
      return scope;
    }

    public Type getType() {
      return type;
    }

    public String getName() {
      String[] parts = this.type.getClass().getName().split("\\.");
      return parts[parts.length-1];
    }

    public String getName(Type t) {
      String[] parts = t.getClass().getName().split("\\.");
      return parts[parts.length-1];
    }


    @Override
    public String toString() {
      String binding = "<" + st.toString() + ": " + getName();
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
