package symboltree;

import syntaxtree.*;
import java.util.HashMap;
import java.util.ArrayList;

public class SymbolTable extends HashMap<Symbol, Binder> {



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
    public Binder lookup(String symbol, Binder.SymbolType symbolType) {
        String scopeType = getAffix(symbolType);
        Symbol s = Symbol.symbol(symbol + scopeType);

        SymbolTable currentScope = this;
        Binder binding = currentScope.get(s);
        if (binding != null) {
            return binding;
        }
        while (currentScope.hasParent()) {
            currentScope = currentScope.parent;
            binding = currentScope.get(s);
            if (binding != null) {
                return binding;
            }
        }
        return null;
    }


    public Binder lookup(String sym) {
        Binder b = null;
        b = lookup(sym, Binder.SymbolType.FIELD);
        if (b != null) {
            return b;
        }
        b = lookup(sym, Binder.SymbolType.METHODRETURN);
        if (b != null) {
            return b;
        }
        b = lookup(sym, Binder.SymbolType.CLASS);
        if (b != null) {
            return b;
        }

        return null;
    }


    /**
     * Add type to symbol table
     *
     * @param name Name of the scope.
     * @return Scope object, or null if there's no such scope (class).
     */
    public boolean insert(String sym, Binder b) {
        Symbol s = Symbol.symbol(sym + getAffix(b.getSymbolType()));

        if (get(s) == null) {
            //s does not exist in the innermost scope
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

        appendLevels(sb, level);
        sb.append(getScopeType().toString() + "\n");
        appendLevels(sb, level);
        sb.append("(\n");

        //For the fields
        appendLevels(sb, level + 1);
        sb.append(super.toString() + "\n");

        String scope = sb.toString();
        for (SymbolTable child : getChildScopes()) {
            sb.append(child.toString(level + 1));
        }

        appendLevels(sb, level);
        sb.append(")\n");
        return sb.toString();
    }

    private void appendLevels(StringBuilder sb, int level) {
        for (int i = 0; i < level; i++) {
            sb.append("    ");
        }
    }

    public static final String VAR_AFFIX = "$var", METHOD_AFFIX = "$method", CLASS_AFFIX = "$class";

    public enum ScopeType {
        PROGRAM, MAINCLASS, CLASS, METHOD, BLOCK;
    }

    private String getAffix(Binder.SymbolType symbolType) {
        String affix = "";
        if (symbolType == Binder.SymbolType.CLASS || symbolType == Binder.SymbolType.CLASSEXTENDS) {
            affix = CLASS_AFFIX;
        } else if (symbolType == Binder.SymbolType.LOCAL || symbolType == Binder.SymbolType.PARAM || symbolType == Binder.SymbolType.FIELD) {
            affix = VAR_AFFIX;
        } else if (symbolType == Binder.SymbolType.METHODRETURN) {
            affix = METHOD_AFFIX;
        }
        return affix;
    }


}