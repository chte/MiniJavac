package visitor;

import syntaxtree.*;
import symboltree.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.LinkedList;

import error.*;
import static error.ErrorObject.*;


public class TypeDepthFirstVisitor implements TypeVisitor
{
	public HashMap<Object, Table> scopeLookupTable;
	public LinkedList<Table> tableStack = new LinkedList<Table>();
    private ArrayList<CompilerError> errors  = new ArrayList<CompilerError>();


    public void error(final CompilerError err) {
        errors.add(err);
    }

    public boolean hasErrors(){
        return !errors.isEmpty();
    }

    public ArrayList<CompilerError> getErrors(){
        return errors;
    }


	public Table getCurrentScope(){
		return tableStack.peekFirst();
	}

    public Table getOldScope(){
		return tableStack.get(1);
    }

	public void enterScope(Table scope){
		/* Push current scope on stack */
		tableStack.push(scope);
	}

	public void leaveScope(){
		/* Push current scope on stack */
		tableStack.pop();
	}


	public Table startScope(Object n){
        Table parentScope = tableStack.peekFirst();

		/* Push current scope on stack */
		tableStack.push(scopeLookupTable.get(n));

        return parentScope;
	}

	public Table endScope(){
		/* Pop first on stack */
		tableStack.pop();

		/* Return parent scope if needed */
		return tableStack.peekFirst();
	}    

	public Type visit(Program n) {
		tableStack.addFirst(scopeLookupTable.get(n));

		n.m.accept(this);
		for ( int i = 0; i < n.cl.size(); i++ ) {
			n.cl.elementAt(i).accept(this);
		}
		return new VoidType();
	}

	public Type visit(MainClass n) {
		startScope(n);

		checkClass(n.i1);
		checkVariable(n.i2);

		for ( int i = 0; i < n.vdl.size(); i++ ) {
			n.vdl.elementAt(i).accept(this);
		}
		for ( int i = 0; i < n.sl.size(); i++ ) {
			n.sl.elementAt(i).accept(this);
		}

		endScope();
		return new VoidType();
	}

	public Type visit(ClassDeclSimple n) {
		startScope(n);

		checkClass(n.i);
		for ( int i = 0; i < n.vl.size(); i++ ) {
			n.vl.elementAt(i).accept(this);
		}
		for ( int i = 0; i < n.ml.size(); i++ ) {
			n.ml.elementAt(i).accept(this);
		}

		endScope();
		return new VoidType();
	}

	public Type visit(ClassDeclExtends n) {
		startScope(n);
		checkClass(n.i);
		checkExtensions(n);
		// ClassBinding c = (ClassBinding) getOldScope().find(Symbol.symbol(n.i.s), "class");

		// System.out.println("Class " + n.i.s);
		// for(Type t : extensions){
		// 	System.out.println("Found ext: " + ((IdentifierType) t).s);
		// 	c.addExtension(t);
		// }

		for ( int i = 0; i < n.vl.size(); i++ ) {
			n.vl.elementAt(i).accept(this);
		}
		for ( int i = 0; i < n.ml.size(); i++ ) {
			n.ml.elementAt(i).accept(this);
		}
		
		endScope();
		return new VoidType();
	}


	public Type visit(VarDecl n) {
		n.t.accept(this);
		checkVariable(n.i);

		return new VoidType();
	}

	public Type visit(MethodDecl n) {
		startScope(n);

		n.t.accept(this);
		checkMethod(n.i);

		for ( int i = 0; i < n.fl.size(); i++ ) {
			n.fl.elementAt(i).accept(this);
		}
		for ( int i = 0; i < n.vl.size(); i++ ) {
			n.vl.elementAt(i).accept(this);
		}
		for ( int i = 0; i < n.sl.size(); i++ ) {
			n.sl.elementAt(i).accept(this);
		}
		Type actualReturnType = n.e.accept(this); /* Return type */
		Type expectedReturnType = getCurrentScope().find(Symbol.symbol(n.i.s), "method").getType(); /* MethodBinding rt */

		if(!checkTypeEquals(actualReturnType, expectedReturnType)) {
			if(checkIdentifierEquals(actualReturnType, expectedReturnType)) {
				if(!classReferencesEquals(expectedReturnType, actualReturnType)) {
					error(INVALID_INHERTICANCE.at(n.row, n.col));
				}
			} else {
				error(UNMATCHED_RETURNTYPE.at(n.row, n.col));
			}
		}

		endScope();
		return new VoidType();
	}

	public Type visit(Formal n) {
		n.t.accept(this);
		checkVariable(n.i);

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

		/* VarDecl is extension */
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
		if (!(t instanceof BooleanType)){
			error(INVALID_UNARY_OP.at(n.row, n.col, t));
		}
		n.s1.accept(this);
		n.s2.accept(this);
		return new VoidType();
	}

	public Type visit(If n) {
		Type t = n.e.accept(this);
		if (!(t instanceof BooleanType)){
			error(INVALID_UNARY_OP.at(n.row, n.col, t));
		}
		n.s.accept(this);
		return new VoidType();
	}

	public Type visit(While n) {
		Type t = n.e.accept(this);
		if (!(t instanceof BooleanType)){
			error(INVALID_UNARY_OP.at(n.row, n.col, t));
		}
		n.s.accept(this);
		return new VoidType();
	}

	public Type visit(Print n) {
		Type t = n.e.accept(this);
		if(!checkPrimitive(t)) {
			error(INVALID_UNARY_OP.at(n.row, n.col, t));
		}
		return new VoidType();
	}

	public Type visit(Assign n) {

		Type lhs = checkVariable(n.i);
		Type rhs = n.e.accept(this);

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

	public Type visit(ArrayAssign n) {

		Type lhs = checkVariable(n.i);
		Type rhs = n.e2.accept(this);
		Type exp = n.e1.accept(this);
		if(!(exp instanceof IntegerType)) {
			error(INVALID_ARRAY_INDEX.at(n.row, n.col, exp));
		}

		if(!checkArrayAssignEquals(lhs, rhs)) {
			error(INVALID_ARRAY_ASSIGN.at(n.row, n.col, rhs, lhs));
		}
		return new VoidType();
	}

	public Type visit(And n) {
		Type lhs = n.e1.accept(this);
		Type rhs = n.e2.accept(this);

		if(!checkBoolEquals(lhs, rhs)) {
			error(INVALID_BINARY_OP.at(n.row, n.col, lhs, rhs, "&&"));
		}

		return new BooleanType();
	}

	public Type visit(Or n) {
		Type lhs = n.e1.accept(this);
		Type rhs = n.e2.accept(this);

		if(!checkBoolEquals(lhs, rhs)) {
			error(INVALID_BINARY_OP.at(n.row, n.col, lhs, rhs, "||"));
		}

		return new BooleanType();
	}

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

	public Type visit(Plus n) {
		Type lhs = n.e1.accept(this);
		Type rhs = n.e2.accept(this);

		if(checkIntEquals(lhs, rhs)) {
			return new IntegerType();
		} else if(checkLongEquals(lhs, rhs)) {
			return new LongType();
		} else if( (lhs instanceof IntegerType && rhs instanceof LongType) || 
				  (lhs instanceof LongType && rhs instanceof IntegerType) ){
			//error("The operator + is undefined for unmatched operands, long, long or int, int.");
			return new LongType();
		} else {
			error(INVALID_BINARY_OP.at(n.row, n.col, lhs, rhs, "+"));
			return new VoidType();
		}
	}

	public Type visit(Minus n) {
		Type lhs = n.e1.accept(this);
		Type rhs = n.e2.accept(this);

		if(checkIntEquals(lhs, rhs)) {
			return new IntegerType();
		} else if(checkLongEquals(lhs, rhs)) {
			return new LongType();
		} else if( (lhs instanceof IntegerType && rhs instanceof LongType) || 
				   (lhs instanceof LongType && rhs instanceof IntegerType) ){
			//error("The operator * is undefined for unmatched operands, long, long or int, int.");
			return new LongType();		
		} else {
			error(INVALID_BINARY_OP.at(n.row, n.col, lhs, rhs, "-"));
			return new VoidType();
		}
	}

	public Type visit(Times n) {
		Type lhs = n.e1.accept(this);
		Type rhs = n.e2.accept(this);

		if(checkIntEquals(lhs, rhs)) {
			return new IntegerType();
		} else if(checkLongEquals(lhs, rhs)) {
			return new LongType();
		} else if( (lhs instanceof IntegerType && rhs instanceof LongType) || 
				   (lhs instanceof LongType && rhs instanceof IntegerType) ){
			//error("The operator * is undefined for unmatched operands, long, long or int, int.");
			return new LongType();
		} else {
			error(INVALID_BINARY_OP.at(n.row, n.col, lhs, rhs, "*"));
			return new VoidType();
		}
	}

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

	public Type visit(ArrayLength n) {
		Type lhs = n.e.accept(this);

		if(!((lhs instanceof IntArrayType) || (lhs instanceof LongArrayType))) {
			error(INVALID_ARRAY_LENGTH.at(n.row, n.col, lhs));
		}
		return new IntegerType();
	}

	public Type visit(Call n) {
		/* Left hand side expression */
		Type lhs = n.e.accept(this);

		if(!(lhs instanceof IdentifierType)) {
			error(INVALID_CALL.at(n.row, n.col, lhs));
			lhs = new IdentifierType("Object");
		}

		n.c = ((IdentifierType) lhs).s;

		/* Left hand side is a identifier so check that method exists*/
		ClassBinding c = (ClassBinding) getCurrentScope().find(Symbol.symbol(((IdentifierType) lhs).s), "class");
		MethodBinding m = (MethodBinding) c.getScope().find(Symbol.symbol(n.i.s), "method");
	

		if(m == null) {
			// System.out.println(c.toString(0));

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
		if(m == null){
			error(NOT_IN_SCOPE.at(n.row, n.col, n.i.s));
			return new VoidType();
		}

		IdentifierType extension = c.getExtension();

		ArrayList<VariableBinding> paramTypes = m.getParams();
		if(paramTypes.size() != n.el.size()) {
			error(UNMATCHED_ARGUMENT_SIZE.at(n.row, n.col, n.el.size(), paramTypes.size()));
		}

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

	public Type visit(NewObject n) {

		return checkClass(n.i);
	}

	public Type visit(Not n) {
		Type expType = n.e.accept(this);
		if(!(expType instanceof BooleanType)) {
			error(INVALID_UNARY_OP.at(n.row, n.col, expType));
		}
		return new BooleanType();
	}

    public Type visit(Identifier n) {
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

	private boolean classReferencesEquals(Type t1, Type t2) {
		if(!(t1 instanceof IdentifierType) || !(t2 instanceof IdentifierType)) {
			return false;
		}
		IdentifierType classLhs = (IdentifierType) t1;
		IdentifierType classRhs = (IdentifierType) t2;
		ClassBinding c = (ClassBinding) getCurrentScope().find(Symbol.symbol(classRhs.s), "class");

		if(c == null || !c.hasExtension()) {
			return false;
		}
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

    public Type checkClass(Identifier n) {
    	ClassBinding c = (ClassBinding) getCurrentScope().find(Symbol.symbol(n.s), "class");
    	if(c == null) {
			error(NOT_IN_SCOPE.at(n.row, n.col, n.s));
    		return new IntegerType();
    	}
    	return  c.getType();
    }

    public Type checkMethod(Identifier n) {
    	MethodBinding m = (MethodBinding) getCurrentScope().find(Symbol.symbol(n.s), "method");
    	if(m == null) {
			error(NOT_IN_SCOPE.at(n.row, n.col, n.s));
    		return new IntegerType();
    	}
    	return  m.getType();
    }

    public Type checkVariable(Identifier n) {
    	VariableBinding v = (VariableBinding) getCurrentScope().find(Symbol.symbol(n.s), "variable");
    	if(v == null) {
			error(NOT_IN_SCOPE.at(n.row, n.col, n.s));
    		return new VoidType();
    	}
    	return v.getType();
    }

	private boolean checkIntEquals(Type t1, Type t2) {
		if((t1 instanceof IntegerType) && (t2 instanceof IntegerType)) {
			return true;
		}
		return false;
	}

	private boolean checkArrayEquals(Type t1, Type t2) {
		if((t1 instanceof IntArrayType) && (t2 instanceof IntArrayType)) {
			return true;
		}
		if((t1 instanceof LongArrayType) && (t2 instanceof LongArrayType)) {
			return true;
		}
		return false;
	}

	private boolean checkArrayAssignEquals(Type t1, Type t2) {
		if((t1 instanceof IntArrayType) && (t2 instanceof IntegerType)) {
			return true;
		}
		if((t1 instanceof LongArrayType) && (t2 instanceof LongType)) {
			return true;
		}
		return false;
	}

	private boolean checkIdentifierEquals(Type t1, Type t2) {
		if((t1 instanceof IdentifierType) && (t2 instanceof IdentifierType)) {
			return true;
		}
		return false;
	}

	private boolean checkLongEquals(Type t1, Type t2) {
		if((t1 instanceof LongType) && (t2 instanceof LongType)) {
			return true;
		}
		return false;
	}

	private boolean checkBoolEquals(Type t1, Type t2) {
		if((t1 instanceof BooleanType) && (t2 instanceof BooleanType)) {
			return true;
		}
		return false;
	}

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

	private boolean checkTypeEquals(Type t1, Type t2) {
		if(checkIntEquals(t1, t2) || checkLongEquals(t1, t2) || checkBoolEquals(t1, t2) || checkArrayEquals(t1, t2) || checkClassEquals(t1, t2)) {
			return true;
		}
		return false;
	}

	private boolean checkPrimitive(Type t) {
		if((t instanceof IntegerType) || (t instanceof BooleanType) || (t instanceof LongType)) {
			return true;
		}
		return false;
	}

	private void checkExtensions(ClassDeclExtends n) {
		HashSet<String> visitedClasses = new HashSet<String>();
		ArrayList<Type> extensions = new ArrayList<Type>();

		/* Add current traversed class as visited */
		ClassBinding currentClass = (ClassBinding) getCurrentScope().find(Symbol.symbol(n.i.s),"class");
		visitedClasses.add(n.i.s);

		/* Traverse while extented classes still exist */
		while(currentClass != null) {
			if(!currentClass.hasExtension()){
				return;
			}

			IdentifierType classExtension = currentClass.getExtension();
			if(visitedClasses.contains(classExtension.s)) {
				error(CYCLIC_INHERTICANCE.at(n.row, n.col, classExtension.s));
				System.exit(1);
				return;
			}

			ClassBinding extendedClass = currentClass;
			currentClass = (ClassBinding) getCurrentScope().find(Symbol.symbol(classExtension.s), "class");
			if(currentClass != null){
				extendedClass.getScope().parent = currentClass.getScope();
			}
			visitedClasses.add(classExtension.s);

		}
		return;
	}



}