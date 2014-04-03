package symboltree;

import syntaxtree.*;
import java.util.ArrayList;

public class Binder {  
    public enum SymbolType {
        CLASS, 
        EXTENDS, 
        RETURN,
        LOCAL, 
        PARAM, 
        FIELD
    }
    private Identifier id;
    private Type type;
    private Binder.SymbolType st;
    private SymbolTable scope;

    public Binder(Type type, Binder.SymbolType st) {
      this.type = type;
      this.st = st;
      this.scope = null;
    }

    public Binder(Type type, Binder.SymbolType st, SymbolTable scope) {
      this.type = type;
      this.st = st;
      this.scope = scope;
    }

    public Binder(Type type, SymbolTable scope) {
      this.type = type;
      this.st = st;
      this.scope = scope;
    }

    public void setSymbolType(Binder.SymbolType st) {
      this.st = st;
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


}
