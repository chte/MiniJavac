package symboltree;

import syntaxtree.*;
import java.util.Iterator;
import java.util.*;
import java.util.HashMap;
import java.util.ArrayList;

public class Table {

    public enum ScopeType {
        PROGRAM,MAIN_CLASS, CLASS, METHOD, BLOCK;
    }

    private Table.ScopeType scopeType;
    public IdentifierType classType;
    public ArrayList<Table> childScopes;
    public Table parent;
    public HashMap<Symbol, ClassBinding> classes;
    public HashMap<Symbol, MethodBinding> methods;
    public HashMap<Symbol, VariableBinding> variables;


    /** 
     *
     * Initializes the HashMaps for classes, methods, and variables.
     *
     * @param   parent      a reference to parent scope, null if no parent exists
     * @param   scopeType   The type of the scope, such @see
     *
     */
    public Table(Table parent, Table.ScopeType scopeType){
        super();
        this.parent = parent;
        this.scopeType = scopeType;
        this.childScopes = new ArrayList<Table>();
        classes = new HashMap<Symbol, ClassBinding>();
        methods = new HashMap<Symbol, MethodBinding>();
        variables = new HashMap<Symbol, VariableBinding>();
    }

    /** 
     *
     * Performs a lookup in a specific HashMap of current
     * scope and parent scopes, by calling findexclusive
     * recursively. It stops when sought binder is found
     * or if theres no more parent scopes to visit, i.e
     * (Program) which parent scope is null.
     * 
     * @param   s       a symbol with a identifier to sought binder 
     * @param   bType   the binder type sought to find
     *
     * @return          A binding, return null if none is found
     *
     */
    public Binder find(Symbol s, String bType) {
        Table currentScope = this;
        Binder binding = currentScope.findexclusive(s, bType);
        if(binding != null){
            return binding;
        }

        while (currentScope.hasParent()) {
            currentScope = currentScope.getParent();
            binding = currentScope.findexclusive(s, bType);
            if (binding != null) {
                return binding;
            }
        }
        return null;
    }


    /**
     * 
     * Performs a lookup in every HashMap of current
     * scope and parent scopes.
     * 
     * @param   s   a symbol with a identifier to sought binder 
     * @return      a binding, return null if none is found
     *
     */
    public Binder find(Symbol s) {
        VariableBinding v = (VariableBinding) find(s, "variable");
        if (v != null) return v;
    
        MethodBinding m = (MethodBinding) find(s, "method");
        if (m != null) return m;

        ClassBinding c = (ClassBinding) find(s, "class");
        if (c != null) return c;

        return null;
    }


    /** 
     *
     * Performs a lookup in a specific HashMap of current scope.
     * 
     * @param   s   a symbol with a identifier to sought binder 
     * @return      a binding, return null if none is found
     *
     */
    public Binder findexclusive(Symbol s, String bType){
        try {
            if(bType.equals("variable")){
                VariableBinding v = this.variables.get(s);
                if (v != null) return v;
            }   
            if(bType.equals("method")){
                MethodBinding m = this.methods.get(s);
                if (m != null) return m;
            }
            if(bType.equals("class")){
                ClassBinding c = this.classes.get(s);
                if (c != null) return c;
            }
            return null;
        } catch( Exception e ){
            return null;
        }
    }

    /**
     * Add mapping of symbol to binding
     *
     * @param   s    Symbol which should be bound to binding
     * @param   b    Binder which should be stored
     * @return       Return true if added successfully, else false.
     *
     */
    public boolean insert(Symbol s, Binder b) {
        if(b instanceof ClassBinding){
            if(classes.containsKey(s)){
                return false;
            } else{
                classes.put(s,(ClassBinding) b);
                return true;
            }
        }
        if(b instanceof MethodBinding){
            if(methods.containsKey(s)){
                return false;
            } else{
                methods.put(s,(MethodBinding) b);
                return true;
            }   
        }
        if(b instanceof VariableBinding){
            if(variables.containsKey(s)){
                return false;
            } else{
                variables.put(s,(VariableBinding) b);
                return true;
            }
        }
        return false;
    }

    /**
     * Searches through current scope and parent scope
     * to see class with IdentifierType t exists
     *
     * @param   identifierType    Identifier type of the class sought to check
     * @return                    Return true if found, else false.
     *
     */
    public boolean findObject(IdentifierType t) {
        Table currentScope = this;

        for(ClassBinding c : classes.values()){
            if( ((IdentifierType) c.getType()).s.equals(t.s)) {
                return true;
            }
        }

        while (currentScope.hasParent()) {
            currentScope = currentScope.getParent();
            boolean found = currentScope.findObject(t);
            if (found) {
                return true;
            }
        }
        return false;
    }



    /**
    * Returns parent scope from current table
    *
    * @return Scope object, or null if there's no such scope (class).
    */
    public Table getParent() {
        return parent;
    }


    /**
    * Returns parent scope from current table
    *
    * @return Scope object, or null if there's no such scope (class).
    */
    public void setClassType(IdentifierType classType) {
        this.classType = classType;
    }


    public IdentifierType getClassType() {
        return classType;
    }


    public Table.ScopeType getScopeType() {
        return scopeType;
    }

   public ArrayList<Table> getChildScopes() {
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


        /* CLASSES*/
        if( classes.entrySet().size() != 0){
            for (int i = 0; i < level; i++) {
                sb.append("\t");
            }
            sb.append("Classes:\n");
        }
        for(Map.Entry<Symbol,ClassBinding> c : classes.entrySet())
            sb.append(c.getValue().toString(level) + "\n");
        
        /* METHODS */
        if( methods.entrySet().size() != 0){
            for (int i = 0; i < level; i++) {
                sb.append("\t");
            }
            sb.append("Methods:\n");
        }
        for(Map.Entry<Symbol,MethodBinding> m : methods.entrySet())
            sb.append(m.getValue().toString(level) + "\n");

        /* VARIABLES */
        if( variables.entrySet().size() != 0){
            for (int i = 0; i < level; i++) {
                sb.append("\t");
            }
            sb.append("Variables:\n");
        }
        for(Map.Entry<Symbol, VariableBinding> v : variables.entrySet())
            sb.append(v.getValue().toString(level) + "\n");

        String scope = sb.toString();
        for (Table child : getChildScopes()) {
            sb.append(child.toString(level + 1));
        }

        for (int i = 0; i < level; i++) {
            sb.append("\t");
        }

        sb.append(")\n");
        return sb.toString();
    }



}