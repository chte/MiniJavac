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

        /* Get from lookup table */
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

		lookupClass(n.i1);
		lookupVariable(n.i2);
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


		//n.i.accept(this);
		lookupClass(n.i);
		for ( int i = 0; i < n.vl.size(); i++ ) {
			n.vl.elementAt(i).accept(this);
		}
		for ( int i = 0; i < n.ml.size(); i++ ) {
			n.ml.elementAt(i).accept(this);
		}

		endScope();
		return new VoidType();
	}

	private ArrayList<Type> traverseExtensions(ClassDeclExtends n) {
		String className = n.i.s;
		ArrayList<Type> extensions = new ArrayList<Type>();
		HashSet<String> extensionSet = new HashSet<String>();
		Binder classBinder = getCurrentScope().lookup(className, Binder.SymbolType.CLASS);
		extensionSet.add(className);
		while(classBinder != null) {
			if(classBinder.getSymbolType() != Binder.SymbolType.CLASSEXTENDS) {
				break;
			}
			IdentifierType extendsClass = (IdentifierType) classBinder.getExtraTypes().get(0);
			if(extensionSet.contains(extendsClass.s)) {
				error("Circle inheritance detected for class " + className + ".");
				break;
			}
			extensions.add(extendsClass);
			extensionSet.add(extendsClass.s);
			Binder oldClassBinder = classBinder;
			classBinder = getCurrentScope().lookup(extendsClass.s, Binder.SymbolType.CLASS);
			if(classBinder != null) {
				oldClassBinder.getScope().setParent(classBinder.getScope());
			}
		}
		return extensions;
	}

	public Type visit(ClassDeclExtends n) {
		startScope(n);
		lookupClass(n.i);

		SymbolTable extendsScope = null;
		if(getCurrentScope().lookup(n.j.s, Binder.SymbolType.CLASS) == null) {
			error("Extended class identifier " + n.j.s + " was not defined as a class in the scope.");
			extendsScope = getOldScope();
		} else {
			extendsScope = getCurrentScope().lookup(n.j.s, Binder.SymbolType.CLASS).getScope();
		}

		ArrayList<Type> extensions = traverseExtensions(n);
		Binder b = getOldScope().lookup(n.i.s, Binder.SymbolType.CLASS);
		for(Type t : extensions){
			b.addExtraType(t);
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
		//n.i.accept(this);
		lookupVariable(n.i);
		return new VoidType();
	}

	public Type visit(MethodDecl n) {
		startScope(n);

		n.t.accept(this);
		//n.i.accept(this);
		lookupMethod(n.i);
		for ( int i = 0; i < n.fl.size(); i++ ) {
			n.fl.elementAt(i).accept(this);
		}
		for ( int i = 0; i < n.vl.size(); i++ ) {
			n.vl.elementAt(i).accept(this);
		}
		for ( int i = 0; i < n.sl.size(); i++ ) {
			n.sl.elementAt(i).accept(this);
		}
		Type returnType = n.e.accept(this);
		Type expectedReturnType = getCurrentScope().lookup(n.i.s, Binder.SymbolType.METHODRETURN).getType();

		if(!typeEquals(returnType, expectedReturnType)) {
			if(identifierTypeEquals(returnType, expectedReturnType)) {
				if(!hasExtends(expectedReturnType, returnType)) {
					error("Return object type does not extend declared return type.");
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
		//n.i.accept(this);
		lookupVariable(n.i);
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
		Type printType = n.e.accept(this);
		if(!isPrimitiveType(printType)) {
			error("Cannot print anything other than primitive types to stdout.");
		}
		return new VoidType();
	}

	public Type visit(Assign n) {
		//Type varType = n.i.accept(this);
		Type varType = lookupVariable(n.i);
		Type exprType = n.e.accept(this);

		if(!typeEquals(varType, exprType)) {
			if(identifierTypeEquals(varType, exprType)) {
				if(!hasExtends(varType, exprType)) {
					error("Right side object type is not an extension of the left side variable type.");
				}
			} else {
				error("Left and right side of an assignment must be of the same type.");
			}
		}
		return new VoidType();
	}

	public Type visit(ArrayAssign n) {
		//Type identifierType = n.i.accept(this);
		Type identifierType = lookupVariable(n.i);
		Type indexType = n.e1.accept(this);
		Type exprType = n.e2.accept(this);

		if(!(indexType instanceof IntegerType)) {
			error("Array index expression must be of type integer.");
		}

		if(!arrayAssignTypeEquals(identifierType, exprType)) {
			error("Left and right side of an integer array assignment must be of integer or long type.");
		}
		return new VoidType();
	}

	public Type visit(And n) {
		Type leftType = n.e1.accept(this);
		Type rightType = n.e2.accept(this);

		if(!boolTypeEquals(leftType, rightType)) {
			error("Left and right side of an and statement must be of boolean type.");
		}

		return new BooleanType();
	}

	public Type visit(Or n) {
		Type leftType = n.e1.accept(this);
		Type rightType = n.e2.accept(this);

		if(!boolTypeEquals(leftType, rightType)) {
			error("Left and right side of an or statement must be of boolean type.");
		}

		return new BooleanType();
	}

	public Type visit(LessThan n) {
		Type leftType = n.e1.accept(this);
		Type rightType = n.e2.accept(this);

		if(!intTypeEquals(leftType, rightType) && !longTypeEquals(leftType, rightType)) {
			error("Both sides of a less than operation must be of the same type, and of integer or long type.");
		}
		return new BooleanType();
	}

	public Type visit(LessThanOrEqual n) {
		Type leftType = n.e1.accept(this);
		Type rightType = n.e2.accept(this);

		if(!intTypeEquals(leftType, rightType) && !longTypeEquals(leftType, rightType)) {
			error("Both sides of a less or equals operation must be of the same type, and of integer or long type.");
		}
		return new BooleanType();
	}

	public Type visit(GreaterThan n) {
		Type leftType = n.e1.accept(this);
		Type rightType = n.e2.accept(this);

		if(!intTypeEquals(leftType, rightType) && !longTypeEquals(leftType, rightType)) {
			error("Both sides of a greater than operation must be of the same type, and of integer or long type.");
		}
		return new BooleanType();
	}

	public Type visit(GreaterThanOrEqual n) {
		Type leftType = n.e1.accept(this);
		Type rightType = n.e2.accept(this);

		if(!intTypeEquals(leftType, rightType) && !longTypeEquals(leftType, rightType)) {
			error("Both sides of a greater or equals operation must be of the same type, and of integer or long type.");
		}
		return new BooleanType();
	}

	public Type visit(Equal n) {
		Type leftType = n.e1.accept(this);
		Type rightType = n.e2.accept(this);

		if(!typeEquals(leftType, rightType) && !hasExtends(leftType, rightType) && !hasExtends(rightType, leftType)) {
			error("Both sides of an equals operation must be of the same type, and of integer, long or class type.");
		}
		return new BooleanType();
	}

	public Type visit(NotEqual n) {
		Type leftType = n.e1.accept(this);
		Type rightType = n.e2.accept(this);

		if(!typeEquals(leftType, rightType) && !hasExtends(leftType, rightType) && !hasExtends(rightType, leftType)) {
			error("Both sides of a not equals operation must be of the same type, and of integer, long or class type.");
		}
		return new BooleanType();
	}

	public Type visit(Plus n) {
		Type leftType = n.e1.accept(this);
		Type rightType = n.e2.accept(this);

		if(intTypeEquals(leftType, rightType)) {
			return new IntegerType();
		} else if(longTypeEquals(leftType, rightType)) {
			return new LongType();
		} else {
			error("Both sides of a plus operation must be of the same type, and of integer or long type.");
			return new IntegerType();
		}

	}

	public Type visit(Minus n) {
		Type leftType = n.e1.accept(this);
		Type rightType = n.e2.accept(this);

		if(intTypeEquals(leftType, rightType)) {
			return new IntegerType();
		} else if(longTypeEquals(leftType, rightType)) {
			return new LongType();
		} else {
			error("Both sides of a minus operation must be of the same type, and of integer or long type.");
			return new IntegerType();
		}
	}

	public Type visit(Times n) {
		Type leftType = n.e1.accept(this);
		Type rightType = n.e2.accept(this);

		if(intTypeEquals(leftType, rightType)) {
			return new IntegerType();
		} else if(longTypeEquals(leftType, rightType)) {
			return new LongType();
		} else {
			error("Both sides of a multiplication operation must be of the same type, and of integer or long type.");
			return new IntegerType();
		}
	}

	public Type visit(ArrayLookup n) {
		Type arrayType = n.e1.accept(this);
		Type arrayIndexType = n.e2.accept(this);

		if(!(arrayIndexType instanceof IntegerType)) {
			error("Expression inside brackets must be of type integer.");
		}

		if(arrayType instanceof IntArrayType) {
			return new IntegerType();
		} else if(arrayType instanceof LongArrayType) {
			return new LongType();
		} else {
			error("Expression to the left of brackets must of type integer array or long array.");
			return new IntegerType();
		}
	}

	public Type visit(ArrayLength n) {
		Type arrayType = n.e.accept(this);

		if(!((arrayType instanceof IntArrayType) || (arrayType instanceof LongArrayType))) {
			error("Expression must be of array type.");
		}
		return new IntegerType();
	}

	public Type visit(Call n) {
		Type expType = n.e.accept(this);
		if(!(expType instanceof IdentifierType)) {
			error("Expression must be an object type.");
			expType = new IdentifierType("Object");
		}
			//Type methodReturnType = n.i.accept(this); NOT NEEDED, we type check it below

		IdentifierType objectType = (IdentifierType) expType;
		String className = objectType.s;
		SymbolTable classScope = getCurrentScope().lookup(className, Binder.SymbolType.CLASS).getScope();

		Binder methodBinder = classScope.lookup(n.i.s, Binder.SymbolType.METHODRETURN);
		Type methodReturnType = null;

		if(methodBinder == null || methodBinder.getSymbolType() != Binder.SymbolType.METHODRETURN) {
			error("Method " + n.i.s + " does not exist for class " + className + ".");
			methodReturnType = new IntegerType();
		} else {
			methodReturnType = methodBinder.getType();
			ArrayList<Type> paramTypes = methodBinder.getExtraTypes();
			if(paramTypes.size() != n.el.size()) {
				error("Wrong number of arguments for method " + n.i.s + ".");
			}

			for ( int i = 0; i < n.el.size(); i++ ) {
				Type paramType = n.el.elementAt(i).accept(this);
				if(!typeEquals(paramType, paramTypes.get(i))) {
					if(identifierTypeEquals(paramType, paramTypes.get(i))) {
						if(!hasExtends(paramTypes.get(i), paramType)) {
							error("Object type is not an extension of the given parameter type for method " + n.i.s + ".");
						}
					} else {
						error("Parameter types for method call to method " + n.i.s + " does not match.");
						break;
					}
				}
			}
		}
		return methodReturnType;
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
		String identifier = n.s;
		Binder b = getCurrentScope().lookup(identifier);
		Type identifierType = null;
		if(b == null) {
			error("Identifier " + identifier + " not defined in the current scope.");
			identifierType = new IntegerType();
		} else {
			identifierType = b.getType();
		}
		return identifierType;
	}

	public Type visit(This n) {
		return getCurrentScope().classType;
	}

	public Type visit(NewIntArray n) {
		Type indexType = n.e.accept(this);
		if(!(indexType instanceof IntegerType)) {
			error("Expression inside brackets must be of type integer.");
		}
		return new IntArrayType();
	}

	public Type visit(NewLongArray n) {
		Type indexType = n.e.accept(this);
		if(!(indexType instanceof IntegerType)) {
			error("Expression inside brackets must be of type integer.");
		}
		return new LongArrayType();
	}

	public Type visit(NewObject n) {
		//return n.i.accept(this);
		return lookupClass(n.i);
	}

	public Type visit(Not n) {
		Type expType = n.e.accept(this);
		if(!(expType instanceof BooleanType)) {
			error("Expression must be of type Boolean.");
		}
		return new BooleanType();
	}

    public Type visit(Identifier n) {
    	String identifier = n.s;
    	Binder b = getCurrentScope().lookup(identifier);
    	Type identifierType = null;
    	if(b == null) {
			error("Identifier " + identifier + " not defined in the current scope.");
    		identifierType = new IntegerType();
    	} else {
    		identifierType = b.getType();
    	}
    	return identifierType;
    }

	public Type visit(VoidType n) {
		return n;
    }

	private boolean hasExtends(Type varType, Type objType) {
		if(!(varType instanceof IdentifierType) || !(objType instanceof IdentifierType)) {
			return false;
		}
		IdentifierType varObjType = (IdentifierType) varType;
		IdentifierType exprObjType = (IdentifierType) objType;
		Binder b = getCurrentScope().lookup(exprObjType.s, Binder.SymbolType.CLASS);
		if(b == null || b.getExtraTypes() == null) {
			return false;
		}
		ArrayList<Type> extendsTypes = b.getExtraTypes();
		for(int i = 0; i < extendsTypes.size(); i++) {
			IdentifierType extendsType = (IdentifierType) extendsTypes.get(i);
			if(extendsType.s.equals(varObjType.s)) {
				return true;
			}
		}
		return false;
	}

	private boolean intTypeEquals(Type t1, Type t2) {
		if((t1 instanceof IntegerType) && (t2 instanceof IntegerType)) {
			return true;
		}
		return false;
	}

	private boolean arrayTypeEquals(Type t1, Type t2) {
		if((t1 instanceof IntArrayType) && (t2 instanceof IntArrayType)) {
			return true;
		} else if((t1 instanceof LongArrayType) && (t2 instanceof LongArrayType)) {
			return true;
		}
		return false;
	}

	private boolean arrayAssignTypeEquals(Type t1, Type t2) {
		if((t1 instanceof IntArrayType) && (t2 instanceof IntegerType)) {
			return true;
		} else if((t1 instanceof LongArrayType) && (t2 instanceof LongType)) {
			return true;
		}
		return false;
	}

	private boolean identifierTypeEquals(Type t1, Type t2) {
		if((t1 instanceof IdentifierType) && (t2 instanceof IdentifierType)) {
			return true;
		}
		return false;
	}

	private boolean longTypeEquals(Type t1, Type t2) {
		if((t1 instanceof LongType) && (t2 instanceof LongType)) {
			return true;
		}
		return false;
	}

	private boolean boolTypeEquals(Type t1, Type t2) {
		if((t1 instanceof BooleanType) && (t2 instanceof BooleanType)) {
			return true;
		}
		return false;
	}

	private boolean classTypeEquals(Type t1, Type t2) {
		if((t1 instanceof IdentifierType) && (t2 instanceof IdentifierType)) {
			IdentifierType identifierType1 = (IdentifierType) t1;
			IdentifierType identifierType2 = (IdentifierType) t2;
			if(identifierType1.s.equals(identifierType2.s)) {
				return true;
			}
		}
		return false;
	}

	private boolean typeEquals(Type t1, Type t2) {
		if(intTypeEquals(t1, t2) || longTypeEquals(t1, t2) || boolTypeEquals(t1, t2) || arrayTypeEquals(t1, t2) || classTypeEquals(t1, t2)) {
			return true;
		}
		return false;
	}

	private boolean isPrimitiveType(Type t) {
		if((t instanceof IntegerType) || (t instanceof BooleanType) || (t instanceof LongType)) {
			return true;
		}
		return false;
	}

	public Type lookupVariable(Identifier n) {
    	Binder b = getCurrentScope().lookup(n.s, Binder.SymbolType.FIELD);
    	if(b == null) {
			error("Identifier " + n.s + " is not declared as a field in current scope.");
    		return new VoidType();
    	} 
    	return b.getType();
    }

    public Type lookupMethod(Identifier n) {
    	Binder b = getCurrentScope().lookup(n.s, Binder.SymbolType.METHODRETURN);
    	if(b == null) {
			error("Identifier " + n.s + " is not declared as a method in current scope.");
    		return new VoidType();
    	}
   		return b.getType();
    }

    public Type lookupClass(Identifier n) {
    	Binder b = getCurrentScope().lookup(n.s, Binder.SymbolType.CLASS);
    	if(b == null) {
			error("Identifier " + n.s + " is not declared as a class in current scope.");
    		return new VoidType();
    	}
    	return  b.getType();
    }
}