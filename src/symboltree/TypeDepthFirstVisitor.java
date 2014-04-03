package symboltree;

import visitor.*;
import syntaxtree.*;
import symboltree.*;
import error.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.LinkedList;

public class TypeDepthFirstVisitor implements TypeVisitor
{
	public CompilerErrorMsg error;
	public HashMap<Object, SymbolTable> scopeLookupTable;
	public LinkedList<SymbolTable> tableStack = new LinkedList<SymbolTable>();

	public void error(String message) {
        error = new CompilerErrorMsg(System.out, "COMPILER ERROR: " + message);
        error.flush();
    }

	public SymbolTable getCurrentScope(){
		return tableStack.peekFirst();
	}

    public SymbolTable getOldScope(){
    	if(tableStack.size() > 1){
			return tableStack.get(1);
		}
		return null;
    }

	public SymbolTable startScope(Object n){
        SymbolTable parentScope = tableStack.peekFirst();

        /* Get from find table */
		SymbolTable currentScope = scopeLookupTable.get(n);

		/* Push current scope on stack */
		tableStack.addFirst(currentScope);

        return parentScope;
	}

	public SymbolTable endScope(){
		/* Pop first on stack */
		tableStack.pollFirst();

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
		checkVar(n.i2);
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

		ArrayList<Type> extensions = checkExtensions(n);
		Class c = (Class) getOldScope().find(Symbol.symbol(n.i.s));
		for(Type t : extensions){
			c.addExtension(t);
		}

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
		checkVar(n.i);
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
		Type rt = n.e.accept(this); /* Return type */
		Type mrt = getCurrentScope().find(Symbol.symbol(n.i.s)).getType(); /* Method rt */

		if(!checkTypeEquals(rt, mrt)) {
			if(checkIdentifierEquals(rt, mrt)) {
				if(!classReferencesEquals(rt, mrt)) {
					error("Type mismatch: return type does not extend method return type.");
				}
			} else {
				error("Return types of method call does not match.");
			}
		}

		endScope();
		return new VoidType();
	}

	public Type visit(Formal n) {
		n.t.accept(this);

		checkVar(n.i);
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
		n.e.accept(this);
		n.s1.accept(this);
		n.s2.accept(this);
		return new VoidType();
	}

	public Type visit(If n) {
		n.e.accept(this);
		n.s.accept(this);
		return new VoidType();
	}

	public Type visit(While n) {
		n.e.accept(this);
		n.s.accept(this);
		return new VoidType();
	}

	public Type visit(Print n) {
		Type t = n.e.accept(this);
		if(!checkPrimitive(t)) {
			error("Type cannot be resolved to long, int or boolean.");
		}
		return new VoidType();
	}

	public Type visit(Assign n) {

		Type varType = checkVar(n.i);
		Type exprType = n.e.accept(this);

		if(!checkTypeEquals(varType, exprType)) {
			if(checkIdentifierEquals(varType, exprType)) {
				if(!classReferencesEquals(varType, exprType)) {
					error("Right side object type is not an extension of the left side variable type.");
				}
			} else {
				error("Left and right side of an assignment must be of the same type.");
			}
		}
		return new VoidType();
	}

	public Type visit(ArrayAssign n) {

		Type it = checkVar(n.i);
		Type indexType = n.e1.accept(this);
		Type exprType = n.e2.accept(this);

		if(!(indexType instanceof IntegerType)) {
			error("Array index expression must be of type integer.");
		}

		if(!checkArrayAssignEquals(it, exprType)) {
			error("Left and right side of an integer array assignment must be of integer or long type.");
		}
		return new VoidType();
	}

	public Type visit(And n) {
		Type lhs = n.e1.accept(this);
		Type rhs = n.e2.accept(this);

		if(!checkBoolEquals(lhs, rhs)) {
			error("Incompatible operand types. Left hand side and right hand side must be of type boolean.");
		}

		return new BooleanType();
	}

	public Type visit(Or n) {
		Type lhs = n.e1.accept(this);
		Type rhs = n.e2.accept(this);

		if(!checkBoolEquals(lhs, rhs)) {
			error("Incompatible operand types. Left hand side and right hand side must be of type boolean.");
		}

		return new BooleanType();
	}

	public Type visit(LessThan n) {
		Type lhs = n.e1.accept(this);
		Type rhs = n.e2.accept(this);

		if(!checkIntEquals(lhs, rhs) && !checkLongEquals(lhs, rhs)) {
			error("Incompatible operand types. Left hand side and right hand side must either integer or long.");
		}
		return new BooleanType();
	}

	public Type visit(LessThanOrEqual n) {
		Type lhs = n.e1.accept(this);
		Type rhs = n.e2.accept(this);

		if(!checkIntEquals(lhs, rhs) && !checkLongEquals(lhs, rhs)) {
			error("Incompatible operand types. Left hand side and right hand side must either integer or long.");
		}
		return new BooleanType();
	}

	public Type visit(GreaterThan n) {
		Type lhs = n.e1.accept(this);
		Type rhs = n.e2.accept(this);

		if(!checkIntEquals(lhs, rhs) && !checkLongEquals(lhs, rhs)) {
			error("Incompatible operand types. Left hand side and right hand side must either integer or long.");
		}
		return new BooleanType();
	}

	public Type visit(GreaterThanOrEqual n) {
		Type lhs = n.e1.accept(this);
		Type rhs = n.e2.accept(this);

		if(!checkIntEquals(lhs, rhs) && !checkLongEquals(lhs, rhs)) {
			error("Incompatible operand types. Left hand side and right hand side must either integer or long.");
		}
		return new BooleanType();
	}

	public Type visit(Equal n) {
		Type lhs = n.e1.accept(this);
		Type rhs = n.e2.accept(this);

		/* Equality of classes must be checked by reference */
		if(!checkTypeEquals(lhs, rhs) && !classReferencesEquals(lhs, rhs) && !classReferencesEquals(rhs, lhs)) {
			error("Incompatible operand types. Left hand side and right hand side must either integer, long or class type.");
		}
		return new BooleanType();
	}

	public Type visit(NotEqual n) {
		Type lhs = n.e1.accept(this);
		Type rhs = n.e2.accept(this);

		if(!checkTypeEquals(lhs, rhs) && !classReferencesEquals(lhs, rhs) && !classReferencesEquals(rhs, lhs)) {
			error("Incompatible operand types. Left hand side and right hand side must either integer, long or class type.");
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
		}else if( (lhs instanceof IntegerType && rhs instanceof LongType) || 
				  (lhs instanceof LongType && rhs instanceof IntegerType) ){
			error("The operator + is undefined for unmatched operands, long, long or int, int.");
			return new VoidType();
		} else {
			error("The operator + is undefined for other arguments than integer or long type.");
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
			error("The operator * is undefined for unmatched operands, long, long or int, int.");
			return new VoidType();		
		} else {
			error("The operator * is undefined for other arguments than integer or long type.");
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
			error("The operator * is undefined for unmatched operands, long, long or int, int.");
			return new VoidType();
		} else {
			error("The operator * is undefined for other arguments than integer or long type.");
			return new VoidType();
		}
	}

	public Type visit(ArrayLookup n) {
		Type arrayType = n.e1.accept(this);
		Type arrayIndexType = n.e2.accept(this);

		if(!(arrayIndexType instanceof IntegerType)) {
			error("Type mismatch: cannot convert to int.");
		}

		if(arrayType instanceof IntArrayType) {
			return new IntegerType();
		} else if(arrayType instanceof LongArrayType) {
			return new LongType();
		} else {
			error("Left hand side of brackets cannot be resolved to a type of long or int.");
			return new IntegerType();
		}
	}

	public Type visit(ArrayLength n) {
		Type arrayType = n.e.accept(this);

		if(!((arrayType instanceof IntArrayType) || (arrayType instanceof LongArrayType))) {
			error("The left-hand side of an assignment must be an array.");
		}
		return new IntegerType();
	}

	public Type visit(Call n) {
		/* Left hand side expression */
		Type lhs = n.e.accept(this);

		if(!(lhs instanceof IdentifierType)) {
			error("The left-hand side of an assignment must be a variable.");
			lhs = new IdentifierType("Object");
		}

		/* Left hand side is a identifier */
		IdentifierType it = (IdentifierType) lhs;
		String className = it.s;
		SymbolTable classScope = getCurrentScope().find(Symbol.symbol(className)).getScope();

		/* Left hand side is a identifier but check its existance*/
		if(classScope == null){
			error(className + "cannot be resolved");
		}

		/* Extract right hand side */
		Method m = (Method) classScope.find(Symbol.symbol(n.i.s));

		if(m == null) {
			error("The method " + n.i.s + " is undefined for the type " + className + ".");
			return new VoidType();
		} else {
			ArrayList<Variable> paramTypes = m.getParams();
			if(paramTypes.size() != n.el.size()) {
				error("The method" + n.i.s + " in type "  + className + " is not applicable for the arguments provided.");
			}

			for ( int i = 0; i < n.el.size(); i++ ) {
				Type paramType = n.el.elementAt(i).accept(this);
				if(!checkTypeEquals(paramType, paramTypes.get(i).getType())) {
					error("The method" + n.i.s + " in type "  + className + " is not applicable for the arguments provided.");
					break;
				}
			}
			return m.getType();
		}
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
		Binder b = getCurrentScope().find(Symbol.symbol(n.s));
        if(b == null) {
	        b = getCurrentScope().find(Symbol.symbol(n.s));        
        }
        if(b == null) {
        	b = getCurrentScope().find(Symbol.symbol(n.s));
        }

		if(b == null) {
			error(n.s + " cannot be resolved to a variable.");
			return new IntegerType();
		} 
		return b.getType();
	}

	public Type visit(This n) {
		return getCurrentScope().classType;
	}

	public Type visit(NewIntArray n) {
		Type indexType = n.e.accept(this);
		if(!(indexType instanceof IntegerType)) {
			error("Type mismatch: cannot convert type to int.");
		}
		return new IntArrayType();
	}

	public Type visit(NewLongArray n) {
		Type indexType = n.e.accept(this);
		if(!(indexType instanceof IntegerType)) {
			error("Type mismatch: cannot convert type to int.");
		}
		return new LongArrayType();
	}

	public Type visit(NewObject n) {

		return checkClass(n.i);
	}

	public Type visit(Not n) {
		Type expType = n.e.accept(this);
		if(!(expType instanceof BooleanType)) {
			error("Incompatible operand types.");
		}
		return new BooleanType();
	}

    public Type visit(Identifier n) {
		Binder b = getCurrentScope().find(Symbol.symbol(n.s));
        if (b == null) {
	        b = getCurrentScope().find(Symbol.symbol(n.s));        
        }
        if (b == null) {
        	b = getCurrentScope().find(Symbol.symbol(n.s));
        }

		if(b == null) {
			error(n.s + " cannot be resolved to a variable.");
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
		Class c = (Class) getCurrentScope().find(Symbol.symbol(classLhs.s));
		IdentifierType classLhsExtension = (IdentifierType) c.getExtension();

		if(c == null || classLhsExtension == null) {
			return false;
		}
		if(classLhsExtension.s.equals(classRhs.s)) {
			return true;
		}
		
		return false;
	}

    public Type checkClass(Identifier n) {
    	Binder b = getCurrentScope().find(Symbol.symbol(n.s));
    	if(b == null) {
			error(n.s + " cannot be resolved to a variable in this scope.");
    		return new VoidType();
    	}
    	return  b.getType();
    }

	public Type checkVar(Identifier n) {
    	Binder b = getCurrentScope().find(Symbol.symbol(n.s));
    	if(b == null) {
			error(n.s + " cannot be resolved to a variable in this scope.");
    		return new VoidType();
    	} 
    	return b.getType();
    }
	public Type checkMethod(Identifier n) {
    	Binder b = getCurrentScope().find(Symbol.symbol(n.s));
    	if(b == null) {
			error(n.s + " cannot be resolved to a variable in this scope.");
    		return new VoidType();
    	} 
    	return b.getType();
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

	private ArrayList<Type> checkExtensions(ClassDeclExtends n) {
		String className = n.i.s;
		ArrayList<Type> extensions = new ArrayList<Type>();
		HashSet<String> visited = new HashSet<String>();
		Class currentClass = (Class) getCurrentScope().find(Symbol.symbol(className));
		visited.add(className);

		/* Traverse while extented classes still exist */
		while(currentClass != null && currentClass.hasExtension()) {
			IdentifierType classExtension = (IdentifierType) currentClass.getExtension();

			if(visited.contains(classExtension.s)) {
				error("Cyclic inheritance involving " + className + ".");
				break;
			}else{
				visited.add(classExtension.s);				
			}

			/* Update current class */
			currentClass = (Class) getCurrentScope().find(Symbol.symbol(classExtension.s));

			/* lastly update list */
			extensions.add(classExtension);
		}
		return extensions;
	}



}