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
        MAIN, 
        FIELD
    }
    private Identifier id;
    private Type type;
    private Binder.SymbolType st;
    private Table scope;

    public Binder(Identifier id, Type type) {
      this.id = id;
      this.type = type;
      this.st = st;
      this.scope = null;
    }

    public Binder(Identifier id, Type type, Table scope) {
      this.id = id;
      this.type = type;
      this.st = st;
      this.scope = scope;
    }

    public Binder(Identifier id, Type type, Binder.SymbolType st, Table scope) {
      this.id = id;      
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

    public Table getScope() {
      return scope;
    }

    public Type getType() {
      return type;
    }

    public String getIdName() {
      return id.s;
    }

    public String getTypeName() {
      String[] parts = this.type.getClass().getName().split("\\.");
      return parts[parts.length-1];
    }

    public String getTypeName(Type t) {
      String[] parts = t.getClass().getName().split("\\.");
      return parts[parts.length-1];
    }


}
