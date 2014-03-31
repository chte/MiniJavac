package symboltree;

import syntaxtree.*;
import java.util.HashMap;
import java.util.ArrayList;

public class SymbolTable extends HashMap<Symbol, Binder> {

    public enum ScopeType {
        PROGRAM, 
        MAIN_CLASS, 
        CLASS, 
        METHOD, 
        BLOCK;
    }

    private SymbolTable.ScopeType scopeType;
    public IdentifierType classType;
	public ArrayList<SymbolTable> childScopes;
    public SymbolTable parent;

	public SymbolTable(SymbolTable parent, SymbolTable.ScopeType scopeType){
        super();
		this.parent = parent;
        this.scopeType = scopeType;
		this.childScopes = new ArrayList<SymbolTable>();
	}

    /* Lookup in symbol table */
    public Binder find(String symbol, Symbol.SymbolType symbolType) {
        String scopeType = getAffix(symbolType);
        Symbol s = Symbol.symbol(symbol + scopeType);

        SymbolTable currentScope = this;
        Binder binding = currentScope.get(s);
        if (binding != null) {
            return binding;
        }
        while (currentScope.hasParent()) {
            currentScope = currentScope.getParent();
            binding = currentScope.get(s);
            if (binding != null) {
                return binding;
            }
        }
        return null;
    }

    /**
     * Add mapping of symbol to binding
     *
     * @param name Symbol name and a binding.
     * @return Return true if added successfully, else false.
     */
    public boolean insert(String sym, Binder b) {
        Symbol s = Symbol.symbol(sym + getAffix(b.getSymbolType()));

        if (get(s) == null) {
            put(s, b);
            return true;
        } else {
            return false;
        }
    }

    /**
    * Returns parent scope from current table
    *
    * @param name Name of the scope.
    * @return Scope object, or null if there's no such scope (class).
    */
    public SymbolTable getParent() {
        return parent;
    }

    public void setParent(SymbolTable parent) {
        this.parent = parent;
    }


    public void setClassType(IdentifierType classType) {
        this.classType = classType;
    }


    public IdentifierType getClassType() {
        return classType;
    }


    public SymbolTable.ScopeType getScopeType() {
        return scopeType;
    }

   public ArrayList<SymbolTable> getChildScopes() {
        return childScopes;
    }

    /**
    * Checks if this scope belong to parent scope
    *
    * @return Return true if scope belongs to parent scope, else false
    */
    public boolean hasParent() {
        return (parent != null) ? true : false;
    }

    public String toString(int level) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < level; i++) {
            sb.append("\t");
        }
        sb.append(getScopeType().toString() + "\n");

        for (int i = 0; i < level; i++) {
            sb.append("\t");
        }

        sb.append("(\n");

        //For the fields
        for (int i = 0; i < level; i++) {
            sb.append("\t");
        }

        sb.append(super.toString() + "\n");

        String scope = sb.toString();
        for (SymbolTable child : getChildScopes()) {
            sb.append(child.toString(level + 1));
        }

        for (int i = 0; i < level; i++) {
            sb.append("\t");
        }

        sb.append(")\n");
        return sb.toString();
    }

    public static final String VAR_AFFIX = ":variable", METHOD_AFFIX = ":method", CLASS_AFFIX = ":class";


    private String getAffix(Symbol.SymbolType symbolType) {
        String affix = "";
        switch(symbolType){
            case CLASS:
            case CLASS_EXTENDS:
                return CLASS_AFFIX;
            case LOCAL:
            case PARAM:
            case FIELD:
                return VAR_AFFIX;
            case METHOD_RETURN:
                return METHOD_AFFIX;
            default:
                return "";
            
        }
    }


}