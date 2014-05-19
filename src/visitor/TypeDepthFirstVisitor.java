package visitor;

import syntaxtree.*;
import symboltree.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.LinkedList;

import error.*;
import static error.ErrorObject.*;

/**
 * Type checker visitor visits each node of the Abstract Syntax Tree
 * and uses the generated symbol tables from visitor.SymbolTableBuilderVisitor
 * to do a final check that all references, declarations, inheritances, etc.,
 * follows the MiniJava grammar.
 *
 * @see The vistor include some major error checking, for error
 *		messages see error.ErrorObject
 *
 * @see The vistor implements TypeVisitor so each visit returns a (Type)
 *
 * @see The visitor is heavily dependent that the Symbol Tables are correctly
 *		built. See, visitor.SymbolTableBuilderVisitor
 */


public class TypeDepthFirstVisitor implements TypeVisitor
{
	public HashMap<Object, Table> scopeLookupTable;
	public LinkedList<Table> tableStack = new LinkedList<Table>();
    private ArrayList<CompilerError> errors  = new ArrayList<CompilerError>();

    /**
     * Appends an error to an error buffer 
     *
     * @param   err    error message wrapped in a CompilerError object 
     *
     */
    public void error(final CompilerError err) {
        errors.add(err);
    }

    /**
     * Checks for any buffered errors. 
     *
     * @return   returns true if errors exist, false otherwise.
     *
     */
    public boolean hasErrors(){
        return !errors.isEmpty();
    }

    /**
     * Returns the buffered errors as a list.
     *
     * @return   returns an ArrayList of CompilerError objects
     *
     */
    public ArrayList<CompilerError> getErrors(){
        return errors;
    }


    /**
     * Returns current scope which the visitor is in,
     * by peeking in the table stack structure.
     *
     * @return   returns a symbol table of current scope.
     *
     */
	public Table getCurrentScope(){
		return tableStack.peekFirst();
	}


    /**
     * This function forces the visitor to enter
     * a scope. 
     *
     * @see This function is not used by this visitor 
     *		but instead by the visitor.BytecodeEmitterVisitor
     *
     * @param   scope       scope that this visitor should be forced
     *						to enter. 
     *
     */
	public void enterScope(Table scope){
		/* Push current scope on stack */
		tableStack.push(scope);
	}

    /**
     * This function forces the visitor to leave
     * a scope. 
     *
     * @see This function is not used by this visitor 
     *		but instead by the visitor.BytecodeEmitterVisitor
     *
     */
	public void leaveScope(){
		/* Push current scope on stack */
		tableStack.pop();
	}

    /**
     * Begins the next scope of the code. 
     * When a visiting a object, that object is
     * used to lookup which scope it belongs to.
     * The scope pushed onto this visitors table stack.
     * 
     *
     * @see  This method uses a lookup table generated
     * 		 with the symbol table builder, 
     * 		 see visitor.SymbolTableBuilderVisitor
	 *
     * @param   n 	current object that is being visited
     *
     */
	public void startScope(Object n){
		/* Push current scope on stack */
		tableStack.push(scopeLookupTable.get(n));
	}

    /**
     * Ends the current scope of the code by popping
     * current scope from stack, this should be invoked 
     * when visitation of the object
     * is finished. 
	 *
     * @param   n 	current object that is being visited
     *
     */
	public void endScope(){
		/* Pop first on stack */
		tableStack.pop();
	}    


	/**
 	 * Visitation of a Program declaration.
     *
     * This is a special case representing the scope
     * which classes are defined in. This is where
     * the visitor normally begins. 
     *
     * @param  	n 	a Program object 
	 */ 
	public Type visit(Program n) {
		/* Start of program scope */
		startScope(n);

		n.m.accept(this);

		/* Traverse class declaration list */
		for ( int i = 0; i < n.cl.size(); i++ ) {
			n.cl.elementAt(i).accept(this);
		}

		/* End of program scope */
		endScope();

		return new VoidType();
	}

	/**
 	 * Visitation of a main class declaration.
     *
     * Checks that the main class and main method is 
     * declared aswell as type checking variable declarations 
     * and statements. 
     *
     * @param  	n 	a MainClass object 
	 */ 
	public Type visit(MainClass n) {
		startScope(n);

		/* Check that main class exists */
		checkClass(n.i1);

		/* Check that main method argument (args) exists */
		checkVariable(n.i2);

		/* Traverse variable declaration list */
		for ( int i = 0; i < n.vdl.size(); i++ ) {
			n.vdl.elementAt(i).accept(this);
		}

		/* Traverse statement list */
		for ( int i = 0; i < n.sl.size(); i++ ) {
			n.sl.elementAt(i).accept(this);
		}

		endScope();
		return new VoidType();
	}

	/**
 	 * Visitation of a class declaration without extension.
     *
     * Checks that the class is declared aswell as type checking 
     * variable and method list declarations. 
     *
     * @param  	n 	a ClassDeclSimple object 
	 */ 
	public Type visit(ClassDeclSimple n) {
		startScope(n);

		/* Check that class exists */
		checkClass(n.i);

		/* Traverse variable declaration list */
		for ( int i = 0; i < n.vl.size(); i++ ) {
			n.vl.elementAt(i).accept(this);
		}

		/* Traverse method declaration list */
		for ( int i = 0; i < n.ml.size(); i++ ) {
			n.ml.elementAt(i).accept(this);
		}

		endScope();
		return new VoidType();
	}

	/**
 	 * Visitation of a class declaration with extension.
     *
     * Checks that the class is declared. The method checks
     * it's extension and build correct parent structure. Thus,
     * the extension scope is included when type checking variable
     * and method list declarations. 
     *
     * @see 		the cycle inheritance check building of correct 
     *				parent scope structure is done in another method,
     *				see checkExtensions(). 
     *
     * @param  	n 	a ClassDeclExtends object 
	 */ 
	public Type visit(ClassDeclExtends n) {
		startScope(n);

		/* Check class exists */
		checkClass(n.i);

		/* Traverse extensions */
		checkExtensions(n);

		/* Traverse variable declaration list */
		for ( int i = 0; i < n.vl.size(); i++ ) {
			n.vl.elementAt(i).accept(this);
		}

		/* Traverse method declaration list */
		for ( int i = 0; i < n.ml.size(); i++ ) {
			n.ml.elementAt(i).accept(this);
		}
		
		endScope();
		return new VoidType();
	}

	/**
 	 * Visitation of a variable declaration.
     *
     * Checks that the variable is declared and that it is not
     * referencing to a non-existand class.
     *
     * @param  	n 	a VarDecl object 
	 */ 
	public Type visit(VarDecl n) {
		n.t.accept(this);

		/* Check that variable is declared,
		   and reachable in scope */
		checkVariable(n.i);

		/* If declaration is of a IdentifierType, i.e. class
		   check so that the class actually exists */
		if(n.t instanceof IdentifierType && !getCurrentScope().findObject((IdentifierType) n.t)){
			error(NO_SUCH_CLASS.at(n.row, n.col, ((IdentifierType) n.t).s));
		}

		return new VoidType();
	}


	/**
 	 * Visitation of a method declaration.
     *
     * Traverses its' formal and variable declarations and statements.
     * Checks that the method exists and that the expected and actual 
     * return type are the same.
     *
     * @param  	n 	a MethodDecl object 
	 */ 
	public Type visit(MethodDecl n) {
		startScope(n);

		n.t.accept(this);

		/* Check that method exists and
		   is reachable in current scope */
		checkMethod(n.i);

		/* Traverse formal declaration list */
		for ( int i = 0; i < n.fl.size(); i++ ) {
			n.fl.elementAt(i).accept(this);
		}

		/* Traverse variable declaration list */
		for ( int i = 0; i < n.vl.size(); i++ ) {
			n.vl.elementAt(i).accept(this);
		}

		/* Traverse statements list */
		for ( int i = 0; i < n.sl.size(); i++ ) {
			n.sl.elementAt(i).accept(this);
		}

		/* Actual return type */
		Type actualReturnType = n.e.accept(this); 
		/* Expected return type */
		Type expectedReturnType = getCurrentScope().find(Symbol.symbol(n.i.s), "method").getType(); /* MethodBinding rt */

		/* Check that return type and expected return type are the same */
		if(!checkTypeEquals(actualReturnType, expectedReturnType)) {
			/* If not, check if both are identifiers 
			   and that they are referencing same class  */
			if(!classReferencesEquals(expectedReturnType, actualReturnType)) {
				error(UNMATCHED_RETURNTYPE.at(n.row, n.col));
			} 
		}

		endScope();
		return new VoidType();
	}

	public Type visit(Formal n) {
		n.t.accept(this);

		/* Check that variable is declared,
		   and reachable in scope */
		checkVariable(n.i);

		/* Check that identifier types
		   references to defines classes */
		if(n.t instanceof IdentifierType && !getCurrentScope().findObject((IdentifierType) n.t)){
			error(NO_SUCH_CLASS.at(n.row, n.col, ((IdentifierType) n.t).s));
		}
		return n.t;
	}

	public Type visit(IntArrayType n) {
		return n;
	}

	public Type visit(LongArrayType n) {
		return n;
	}

	public Type visit(BooleanType n) {
		return n;
	}

	public Type visit(IntegerType n) {
		return n;
	}

	public Type visit(LongType n) {
		return n;
	}

	public Type visit(IdentifierType n) {
		return n;
	}

	public Type visit(Block n) {
		startScope(n);

		/* Traverse variable declarations and statements */
		for ( int i = 0; i < n.vl.size(); i++ ) {
			n.vl.elementAt(i).accept(this);
		}
		for ( int i = 0; i < n.sl.size(); i++ ) {
			n.sl.elementAt(i).accept(this);
		}

		endScope();
		return new VoidType();
	}

	public Type visit(IfElse n) {
		Type t = n.e.accept(this);

		/* Check that expression is of boolean types */
		if (!(t instanceof BooleanType)){
			error(INVALID_UNARY_OP.at(n.row, n.col, t));
		}

		/* Traverse statements */
		n.s1.accept(this);
		n.s2.accept(this);
		return new VoidType();
	}

	public Type visit(If n) {
		Type t = n.e.accept(this);

		/* Check that expression is of boolean types */
		if (!(t instanceof BooleanType)){
			error(INVALID_UNARY_OP.at(n.row, n.col, t));
		}

		/* Traverse statement */
		n.s.accept(this);
		return new VoidType();
	}

	public Type visit(While n) {
		Type t = n.e.accept(this);

		/* Check that expression is of boolean types */
		if (!(t instanceof BooleanType)){
			error(INVALID_UNARY_OP.at(n.row, n.col, t));
		}

		/* Traverse statement */
		n.s.accept(this);
		return new VoidType();
	}

	public Type visit(Print n) {
		Type t = n.e.accept(this);

		/* Check that input is of primitive types */
		if(!((t instanceof IntegerType) || (t instanceof BooleanType) || (t instanceof LongType))) {
			error(INVALID_UNARY_OP.at(n.row, n.col, t));
		}

		return new VoidType();
	}

	/*
	 * This method check that assign is between compatible types. 
	 * If right hand side and left hand side are identifier types,
	 * check that both are of same class or referencing same
	 * parent class.
	 */
	public Type visit(Assign n) {

		/* Check that left hand variable is declared 
		   in current or parent scopes */
		Type lhs = checkVariable(n.i);
		Type rhs = n.e.accept(this);

		/* Check that they are of same type */
		if(!checkTypeEquals(lhs, rhs)) {
			if(checkIdentifierEquals(lhs, rhs)) {
				if(!classReferencesEquals(lhs, rhs)) { /* Check by reference */
					error(INVALID_CLASS_REFERENCE.at(n.row, n.col, ((IdentifierType) rhs).s, ((IdentifierType) lhs).s));
				}
			} else {
				if(lhs instanceof LongType && rhs instanceof IntegerType ){
					return new LongType();
				}
				error(INVALID_ASSIGN.at(n.row, n.col, rhs, lhs));
			}
		}
		return new VoidType();
	}

	/*
	 * This method check that array assign is between compatible types. 
	 */
	public Type visit(ArrayAssign n) {

		/* Check that left hand variable is declared 
		   in current or parent scopes */
		Type lhs = checkVariable(n.i);
		Type rhs = n.e2.accept(this);
		Type exp = n.e1.accept(this);

		/* Check that expression is of integer type */
		if(!(exp instanceof IntegerType)) {
			error(INVALID_ARRAY_INDEX.at(n.row, n.col, exp));
		}

		/* Check that the array assign types are
		   are compatible */
		if(!checkArrayAssignEquals(lhs, rhs)) {
			error(INVALID_ARRAY_ASSIGN.at(n.row, n.col, rhs, lhs));
		}
		return new VoidType();
	}

	/* Checks compatible conversion */
	private boolean checkArrayAssignEquals(Type t1, Type t2) {
		if((t1 instanceof IntArrayType) && (t2 instanceof IntegerType)) {
			return true;
		}
		if((t1 instanceof LongArrayType) && (t2 instanceof LongType)) {
			return true;
		}
		if((t1 instanceof LongArrayType) && (t2 instanceof IntegerType)) {
			return true;
		}
		return false;
	}

	/* Check that both left hand side and right hand side
	 * evaluates to a boolean expression */
	public Type visit(And n) {
		Type lhs = n.e1.accept(this);
		Type rhs = n.e2.accept(this);

		if(!checkBoolEquals(lhs, rhs)) {
			error(INVALID_BINARY_OP.at(n.row, n.col, lhs, rhs, "&&"));
		}

		return new BooleanType();
	}

	/* Check that both left hand side and right hand side
	 * evaluates to a boolean expression */
	public Type visit(Or n) {
		Type lhs = n.e1.accept(this);
		Type rhs = n.e2.accept(this);

		if(!checkBoolEquals(lhs, rhs)) {
			error(INVALID_BINARY_OP.at(n.row, n.col, lhs, rhs, "||"));
		}

		return new BooleanType();
	}

	/**
	 * Check that both left hand side and right hand side
	 * evaluates to compatible types such as int - int, or
	 * long - long, or even int - long, long - int 
	 */
	public Type visit(LessThan n) {
		Type lhs = n.e1.accept(this);
		Type rhs = n.e2.accept(this);

		if(!checkIntEquals(lhs, rhs) && !checkLongEquals(lhs, rhs)) {
			if( (lhs instanceof IntegerType && rhs instanceof LongType) || 
			 	(lhs instanceof LongType && rhs instanceof IntegerType) ){
				//error("The operator + is undefined for unmatched operands, long, long or int, int.");
				return new BooleanType();
			}
			error(INVALID_BINARY_OP.at(n.row, n.col, lhs, rhs, "<"));
		}
		return new BooleanType();
	}

	/**
	 * Check that both left hand side and right hand side
	 * evaluates to compatible types such as int - int, or
	 * long - long, or even int - long, long - int 
	 */
	public Type visit(LessThanOrEqual n) {
		Type lhs = n.e1.accept(this);
		Type rhs = n.e2.accept(this);

		if(!checkIntEquals(lhs, rhs) && !checkLongEquals(lhs, rhs)) {
			if( (lhs instanceof IntegerType && rhs instanceof LongType) || 
			 	(lhs instanceof LongType && rhs instanceof IntegerType) ){
				//error("The operator + is undefined for unmatched operands, long, long or int, int.");
				return new BooleanType();
			}
			error(INVALID_BINARY_OP.at(n.row, n.col, lhs, rhs, "<="));
		}
		return new BooleanType();
	}

	/**
	 * Check that both left hand side and right hand side
	 * evaluates to compatible types such as int - int, or
	 * long - long, or even int - long, long - int 
	 */
	public Type visit(GreaterThan n) {
		Type lhs = n.e1.accept(this);
		Type rhs = n.e2.accept(this);

		if(!checkIntEquals(lhs, rhs) && !checkLongEquals(lhs, rhs)) {
			if( (lhs instanceof IntegerType && rhs instanceof LongType) || 
			 	(lhs instanceof LongType && rhs instanceof IntegerType) ){
				//error("The operator + is undefined for unmatched operands, long, long or int, int.");
				return new BooleanType();
			}
			error(INVALID_BINARY_OP.at(n.row, n.col, lhs, rhs, ">"));
		}
		return new BooleanType();
	}

	/**
	 * Check that both left hand side and right hand side
	 * evaluates to compatible types such as int - int, or
	 * long - long, or even int - long, long - int 
	 */
	public Type visit(GreaterThanOrEqual n) {
		Type lhs = n.e1.accept(this);
		Type rhs = n.e2.accept(this);

		if(!checkIntEquals(lhs, rhs) && !checkLongEquals(lhs, rhs)) {
			if( (lhs instanceof IntegerType && rhs instanceof LongType) || 
			 	(lhs instanceof LongType && rhs instanceof IntegerType) ){
				//error("The operator + is undefined for unmatched operands, long, long or int, int.");
				return new BooleanType();
			}
			error(INVALID_BINARY_OP.at(n.row, n.col, lhs, rhs, ">="));
		}
		return new BooleanType();
	}

	/**
	 * Check that both left hand side and right hand side
	 * evaluates to compatible types such as int - int, or
	 * long - long, or even int - long, long - int.
	 *
	 * If types are not of a long or int, check if both
	 * are identifier types and that they references 
	 * the same class. 
	 */
	public Type visit(Equal n) {
		Type lhs = n.e1.accept(this);
		Type rhs = n.e2.accept(this);

		/* Equality of classes must be checked by reference aswell */
		if(!checkTypeEquals(lhs, rhs) && !classReferencesEquals(lhs, rhs) && !classReferencesEquals(rhs, lhs)) {
			if( (lhs instanceof IntegerType && rhs instanceof LongType) || 
			 	(lhs instanceof LongType && rhs instanceof IntegerType) ){
				//error("The operator + is undefined for unmatched operands, long, long or int, int.");
				return new BooleanType();
			}else{
				error(INVALID_BINARY_OP.at(n.row, n.col, lhs, rhs, "=="));
			}
		}
		return new BooleanType();
	}

	/**
	 * Check that both left hand side and right hand side
	 * evaluates to compatible types such as int - int, or
	 * long - long, or even int - long, long - int.
	 *
	 * If types are not of a long or int, check if both
	 * are identifier types and that they references 
	 * the same class. 
	 */
	public Type visit(NotEqual n) {
		Type lhs = n.e1.accept(this);
		Type rhs = n.e2.accept(this);

		/* Equality of classes must be checked by reference aswell */
		if(!checkTypeEquals(lhs, rhs) && !classReferencesEquals(lhs, rhs) && !classReferencesEquals(rhs, lhs)) {
			if( (lhs instanceof IntegerType && rhs instanceof LongType) || 
			 	(lhs instanceof LongType && rhs instanceof IntegerType) ){
				//error("The operator + is undefined for unmatched operands, long, long or int, int.");
				return new BooleanType();
			}else{
				error(INVALID_BINARY_OP.at(n.row, n.col, lhs, rhs, "!="));
			}
		}
		return new BooleanType();
	}

	/**
	 * Check that both left hand side and right hand side
	 * evaluates to compatible types such as int - int, or
	 * long - long, or even int - long, long - int. 
	 */
	public Type visit(Plus n) {
		Type lhs = n.e1.accept(this);
		Type rhs = n.e2.accept(this);

		if(checkIntEquals(lhs, rhs)) {
			return new IntegerType();
		} else if(checkLongEquals(lhs, rhs)) {
			return new LongType();
		} else if( (lhs instanceof IntegerType && rhs instanceof LongType) || 
				  (lhs instanceof LongType && rhs instanceof IntegerType) ){
			return new LongType();
		} else {
			error(INVALID_BINARY_OP.at(n.row, n.col, lhs, rhs, "+"));
			return new VoidType();
		}
	}

	/**
	 * Check that both left hand side and right hand side
	 * evaluates to compatible types such as int - int, or
	 * long - long, or even int - long, long - int. 
	 */
	public Type visit(Minus n) {
		Type lhs = n.e1.accept(this);
		Type rhs = n.e2.accept(this);

		if(checkIntEquals(lhs, rhs)) {
			return new IntegerType();
		} else if(checkLongEquals(lhs, rhs)) {
			return new LongType();
		} else if( (lhs instanceof IntegerType && rhs instanceof LongType) || 
				   (lhs instanceof LongType && rhs instanceof IntegerType) ){
			return new LongType();		
		} else {
			error(INVALID_BINARY_OP.at(n.row, n.col, lhs, rhs, "-"));
			return new VoidType();
		}
	}

	/**
	 * Check that both left hand side and right hand side
	 * evaluates to compatible types such as int - int, or
	 * long - long, or even int - long, long - int. 
	 */
	public Type visit(Times n) {
		Type lhs = n.e1.accept(this);
		Type rhs = n.e2.accept(this);

		if(checkIntEquals(lhs, rhs)) {
			return new IntegerType();
		} else if(checkLongEquals(lhs, rhs)) {
			return new LongType();
		} else if( (lhs instanceof IntegerType && rhs instanceof LongType) || 
				   (lhs instanceof LongType && rhs instanceof IntegerType) ){
			return new LongType();
		} else {
			error(INVALID_BINARY_OP.at(n.row, n.col, lhs, rhs, "*"));
			return new VoidType();
		}
	}

	/**
	 * Check that right hand side, i.e., index is of
	 * integer type.
	 */
	public Type visit(ArrayLookup n) {
		Type lhs = n.e1.accept(this);
		Type rhs = n.e2.accept(this);

		if(!(rhs instanceof IntegerType)) {
			error(INVALID_ARRAY_LOOKUP.at(n.row, n.col, rhs));
		}

		if(lhs instanceof IntArrayType) {
			return new IntegerType();
		} else if(lhs instanceof LongArrayType) {
			return new LongType();
		} else {
			error(INVALID_ARRAY_INDEX.at(n.row, n.col, lhs));
			return new VoidType();
		}
	}

	/**
	 * Check that length is called on a integer
	 * or long array.
	 */
	public Type visit(ArrayLength n) {
		Type lhs = n.e.accept(this);

		if(!((lhs instanceof IntArrayType) || (lhs instanceof LongArrayType))) {
			error(INVALID_ARRAY_LENGTH.at(n.row, n.col, lhs));
		}
		return new IntegerType();
	}

	/**
	 * This method type checks following for a method call:
	 *
	 * <ul>
	 *	 <li> that the left-hand side is of an IdentifierType, i.e., a class
	 *	 <li> that the class exists
	 *   <li> that the called method exists in class or is reachable through 
	 * 		  current scope through inheritance
	 *	 <li> that the parameter size correspondes to the called method's 
	 *		  parameter size
	 *   <li> that the parameter types correspondes to the called method's
	 *		  parameter types 
	 * </ul>
	 *
	 */
	public Type visit(Call n) {
		/* Left hand side expression */
		Type lhs = n.e.accept(this);

		/* Left hand side must be of an identifier type */
		if(!(lhs instanceof IdentifierType)) {
			error(INVALID_CALL.at(n.row, n.col, lhs));
			lhs = new IdentifierType("Object");
		}

		/* Store class of left-hand side in Call object,
		   makes things easier in BytecodeEmitterVisitor.
		   See visit(Call n) in the BytecodeEmitterVisitor */
		n.c = ((IdentifierType) lhs).s;

		/* Left hand side is a identifier so check that class exists */
		ClassBinding c = (ClassBinding) getCurrentScope().find(Symbol.symbol(((IdentifierType) lhs).s), "class");
		/* Get the method binding from this class scope */
		MethodBinding m = (MethodBinding) c.getScope().find(Symbol.symbol(n.i.s), "method");
	
		/* If the method was not found check its extensions */
		if(m == null) {
			IdentifierType extension = c.getExtension();
			while(extension != null){
				c = (ClassBinding) getCurrentScope().find(Symbol.symbol(extension.s), "class");			
				extension = c.getExtension();
				m = (MethodBinding) c.getScope().find(Symbol.symbol(n.i.s), "method");
				
				if(m != null){
					break;
				}
			}
		}
		/* If the method was not in extensions it does not
		   exist or is not in scope */
		if(m == null){
			error(NOT_IN_SCOPE.at(n.row, n.col, n.i.s));
			return new VoidType();
		}

		/* Check that the method is provided we correct
		   amount of parameters */
		ArrayList<VariableBinding> paramTypes = m.getParams();
		if(paramTypes.size() != n.el.size()) {
			error(UNMATCHED_ARGUMENT_SIZE.at(n.row, n.col, n.el.size(), paramTypes.size()));
		}

		/* Check that the method is provided we correct
		   parameters types, if identifier types, also
		   check extensions */
		for ( int i = 0; i < n.el.size(); i++ ) {
			Type argumentType = n.el.elementAt(i).accept(this);
			Type formalType = paramTypes.get(i).getType();
			if(!checkTypeEquals(argumentType, formalType)) {
				if(checkIdentifierEquals(argumentType, formalType)){
					if(!classReferencesEquals(formalType, argumentType)) {
						error(INVALID_INHERTICANCE.at(n.row, n.col));
					}
				} else{
						error(UNMATCHED_ARGUMENT_TYPES.at(n.row, n.col, formalType, argumentType));
				}
			}
		}
		return m.getType();

	}

	public Type visit(IntegerLiteral n) {
		return new IntegerType();
	}

	public Type visit(LongLiteral n) {
		return new LongType();
	}

	public Type visit(True n) {
		return new BooleanType();
	}

	public Type visit(False n) {
		return new BooleanType();
	}

	/* Check identifier expression if it exists
	 * or is reachable in current scope */
	public Type visit(IdentifierExp n) {
		Binder b =  getCurrentScope().find(Symbol.symbol(n.s));

		if(b == null) {
			error(NOT_IN_SCOPE.at(n.row, n.col, n.s));
			return new IntegerType();
		} 
		return b.getType();

	}

	public Type visit(This n) {
		return getCurrentScope().getClassType();
	}

	public Type visit(NewIntArray n) {
		Type indexType = n.e.accept(this);
		
		/* Check that index is of integer type */
		if(!(indexType instanceof IntegerType)) {
			error(UNMATCHED_TYPE.at(n.row, n.col, indexType, "int"));
		}
		return new IntArrayType();
	}

	public Type visit(NewLongArray n) {
		Type indexType = n.e.accept(this);
		if(!(indexType instanceof IntegerType)) {
			error(UNMATCHED_TYPE.at(n.row, n.col, indexType, "long"));
		}
		return new LongArrayType();
	}

	/* Check that the class actually exists */
	public Type visit(NewObject n) {

		return checkClass(n.i);
	}


	public Type visit(Not n) {
		Type expType = n.e.accept(this);

		/* Check that expression is of boolean type */
		if(!(expType instanceof BooleanType)) {
			error(INVALID_UNARY_OP.at(n.row, n.col, expType));
		}
		return new BooleanType();
	}

    public Type visit(Identifier n) {
		/* Check that identifier is declared in and reachable scope */
		Binder b = getCurrentScope().find(Symbol.symbol(n.s));

		if(b == null) {
			error(NOT_IN_SCOPE.at(n.row, n.col, n.s));
			return new IntegerType();
		} 
		return b.getType();
    }

	public Type visit(VoidType n) {
		return n;
    }


    /* Helper methods for type checking follows */

    /**
     * This method checks so that both types are of
     * identifier type. That both references to same 
     * class or parent class by checking extensions.
     *
     * @param 	t1 	identifier to be checked
     * @param 	t2  identifier to be checked
     *
     * @return 	returns true if both identifiers are
     * 			referencing to same identifier type,
     *			otherwise false.
     */
	private boolean classReferencesEquals(Type t1, Type t2) {
		/* Check that both are of identifier type */
		if(!(t1 instanceof IdentifierType) || !(t2 instanceof IdentifierType)) {
			return false;
		}
		IdentifierType classLhs = (IdentifierType) t1;
		IdentifierType classRhs = (IdentifierType) t2;

		/* Fetch right hand side class */
		ClassBinding c = (ClassBinding) getCurrentScope().find(Symbol.symbol(classRhs.s), "class");

		/* Check that it exists and has extensions */
		if(c == null || !c.hasExtension()) {
			return false;
		}

		/* For each extension of right hand side check if any has
		   same reference as left hand side class */
		IdentifierType extension = c.getExtension();
		while(extension != null){
			if(classLhs.equals(extension)) {
				return true;
			}
			c = (ClassBinding) getCurrentScope().find(Symbol.symbol(extension.s), "class");			
			extension = c.getExtension();
		}
		
		return false;
	}


    /**
     * This method checks that class is reachable/exists
     * in current scope.
     *
     * @param 	n 	identifier of the class to be checked
     *
     * @return 	returns the type of the identifier
     *
     */
    public Type checkClass(Identifier n) {
    	ClassBinding c = (ClassBinding) getCurrentScope().find(Symbol.symbol(n.s), "class");
    	if(c == null) {
			error(NOT_IN_SCOPE.at(n.row, n.col, n.s));
    		return new VoidType();
    	}
    	return  c.getType();
    }

    /**
     * This method checks that the method is reachable/exists
     * in current scope.
     *
     * @param 	n 	identifier of the method to be checked
     *
     * @return 	returns the type of the identifier
     *
     */
    public Type checkMethod(Identifier n) {
    	MethodBinding m = (MethodBinding) getCurrentScope().find(Symbol.symbol(n.s), "method");
    	if(m == null) {
			error(NOT_IN_SCOPE.at(n.row, n.col, n.s));
    		return new VoidType();
    	}
    	return  m.getType();
    }

    /**
     * This method checks that the variable is reachable/exists
     * in current scope.
     *
     * @param 	n 	identifier of the variable to be checked
     *
     * @return 	returns the type of the identifier
     *
     */
    public Type checkVariable(Identifier n) {
    	VariableBinding v = (VariableBinding) getCurrentScope().find(Symbol.symbol(n.s), "variable");
    	if(v == null) {
			error(NOT_IN_SCOPE.at(n.row, n.col, n.s));
    		return new VoidType();
    	}
    	return v.getType();
    }

    /**
     * This method checks both inputs t1 and t2 are of
     * integer type.
     *
     * @param 	t1 	type to be checked
     * @param 	t2 	type to be checked
     *
     * @return 	returns the true if both are integer types, 
     * 			otherwise false.
     *
     */
	private boolean checkIntEquals(Type t1, Type t2) {
		if((t1 instanceof IntegerType) && (t2 instanceof IntegerType)) {
			return true;
		}
		return false;
	}

    /**
     * This method checks both inputs t1 and t2 are of
     * integer or long array type.
     *
     * @param 	t1 	type to be checked
     * @param 	t2 	type to be checked
     *
     * @return 	returns the true if both are of integer or
     *			long array types, otherwise false.
     *
     */
	private boolean checkArrayEquals(Type t1, Type t2) {
		if((t1 instanceof IntArrayType) && (t2 instanceof IntArrayType)) {
			return true;
		}
		if((t1 instanceof LongArrayType) && (t2 instanceof LongArrayType)) {
			return true;
		}
		return false;
	}

    /**
     * This method checks both inputs t1 and t2 are of
     * identifier type.
     *
     * @param 	t1 	type to be checked
     * @param 	t2 	type to be checked
     *
     * @return 	returns the true if both are identifier types, 
     * 			otherwise false.
     *
     */
	private boolean checkIdentifierEquals(Type t1, Type t2) {
		if((t1 instanceof IdentifierType) && (t2 instanceof IdentifierType)) {
			return true;
		}
		return false;
	}

    /**
     * This method checks both inputs t1 and t2 are of
     * long type.
     *
     * @param 	t1 	type to be checked
     * @param 	t2 	type to be checked
     *
     * @return 	returns the true if both are long types, 
     * 			otherwise false.
     *
     */
	private boolean checkLongEquals(Type t1, Type t2) {
		if((t1 instanceof LongType) && (t2 instanceof LongType)) {
			return true;
		}
		return false;
	}

    /**
     * This method checks both inputs t1 and t2 are of
     * boolean type.
     *
     * @param 	t1 	type to be checked
     * @param 	t2 	type to be checked
     *
     * @return 	returns the true if both are boolean types, 
     * 			otherwise false.
     *
     */
	private boolean checkBoolEquals(Type t1, Type t2) {
		if((t1 instanceof BooleanType) && (t2 instanceof BooleanType)) {
			return true;
		}
		return false;
	}

    /**
     * This method checks both inputs t1 and t2 are of
     * identifier type and that they are the same class.
     *
     * @param 	t1 	type to be checked
     * @param 	t2 	type to be checked
     *
     * @return 	returns the true if both are the same class, 
     * 			otherwise false.
     *
     */
	private boolean checkClassEquals(Type t1, Type t2) {
		if((t1 instanceof IdentifierType) && (t2 instanceof IdentifierType)) {
			IdentifierType it1 = (IdentifierType) t1;
			IdentifierType it2 = (IdentifierType) t2;
			if(it1.s.equals(it2.s)) {
				return true;
			}
		}
		return false;
	}

    /**
     * This method checks that t1 and t2 both are of
     * either:
     * <ul>
     *   <li> long type
     *   <li> integer type
     * 	 <li> boolean type
     *   <li> long (or) integer array type
     *   <li> both are of same class
     * </ul>
     *
     * @see checkIntEquals(), checkLongEquals(), checkBoolEquals(), checkArrayEquals(), checkClassEquals()
     *
     * @param 	t1 	type to be checked
     * @param 	t2 	type to be checked
     *
     * @return 	returns the true if both are the same, 
     * 			otherwise false.
     *
     */
	private boolean checkTypeEquals(Type t1, Type t2) {
		if(checkIntEquals(t1, t2) || checkLongEquals(t1, t2) || checkBoolEquals(t1, t2) || checkArrayEquals(t1, t2) || checkClassEquals(t1, t2)) {
			return true;
		}
		return false;
	}


    /**
     * The method recursively visit extension and checks so no 
     * cyclic inheritance is involved. It also build up a parent
     * scope relation in such way that a class extension is a parent
     * to the class extending the class.
     *
     * @important 	System.exit(1) is called if cyclic inheritance is detected,
     * 				otherwise the type checker gets stuck in a infinite loop. 
     *				As a side effect all errors will not be printed.
     * 
     * @param 	n 	a class object with extensions
     *
     */
	private void checkExtensions(ClassDeclExtends n) {
		HashSet<String> visitedClasses = new HashSet<String>();
		ArrayList<Type> extensions = new ArrayList<Type>();

		/* Add current traversed class as visited */
		ClassBinding currentClass = (ClassBinding) getCurrentScope().find(Symbol.symbol(n.i.s),"class");
		visitedClasses.add(n.i.s);


		/* Traverse while extended classes still exist */
		while(currentClass != null) {
			if(!currentClass.hasExtension()){
				return;
			}

			/* Get current class's extension */
			IdentifierType classExtension = currentClass.getExtension();

			/* If extension has been visited before,
			   then we have a cyclic inheritance */
			if(visitedClasses.contains(classExtension.s)) {
				error(CYCLIC_INHERTICANCE.at(n.row, n.col, classExtension.s));
				System.exit(1);
				return;
			}

			/* Temporarily store current class */
			ClassBinding extendedClass = currentClass;

			/* Check that current class's extension is reachable in scope */
			currentClass = (ClassBinding) getCurrentScope().find(Symbol.symbol(classExtension.s), "class");
			if(currentClass != null){
				/* Add extension's scope as parent scope to class extending */
				extendedClass.getScope().parent = currentClass.getScope();
			}else{
				error(NO_SUCH_CLASS.at(n.row, n.col, classExtension.s));	
				
			}
			/* Mark as visited */
			visitedClasses.add(classExtension.s);

		}
		return;
	}



}