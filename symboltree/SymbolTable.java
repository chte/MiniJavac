package symboltree;

import java.util.HashMap;
import java.util.ArrayList;

public class SymbolTable extends HashMap<Symbol, Binding> {
    public static final String VAR_SALT = "$var", METHOD_SALT = "$method", CLASS_SALT = "$class";

    public enum ScopeType {
        PROGRAM, MAINCLASS, CLASS, METHOD, BLOCK;
    }

    private SymbolTable.ScopeType scopeType;
    public IdentifierType classType;


	public HashMap<Symbol, Binding> symbols;
	public ArrayList<SymbolTable> childScopes;
    public SymbolTable parent;

	public SymbolTable(SymbolTable parent){
        super();
		this parent = parent;
		this.childScopes = new ArrayList<SymbolTable>();
	}


    /**
     * Add type to symbol table
     *
     * @param name Name of the scope.
     * @return Scope object, or null if there's no such scope (class).
     */
    public void add(Symbol name, Binder type) {
        symbols.put(name, type);
    }

   
    /**
     * Returns scope from symbol table
     *
     * @param name Name of the scope.
     * @return Scope object, or null if there's no such scope (class).
     */
    public Symbol get(Symbol name) {
        return symbols.get(name);
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


}