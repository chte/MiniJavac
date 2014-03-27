package visitor;

import syntaxtree.*;

public interface Visitor
{
	public void visit(And x);
	public void visit(ArrayAssign x);
	public void visit(ArrayLength x);
	public void visit(ArrayLookup x);
	public void visit(Assign x);
	public void visit(Block x);
	public void visit(BooleanType x);
	public void visit(Call x);
	//public void visit(ClassDecl x);
	public void visit(ClassDeclExtends x);
	// public void visit(ClassDeclList x);
	public void visit(ClassDeclSimple x);
	public void visit(Equal x);
	//public void visit(Exp x);
	//public void visit(ExpList x);
	public void visit(False x);
	public void visit(Formal x );
	// public void visit(FormalList x);	
	public void visit(GreaterThan x);
	public void visit(GreaterThanOrEqual x);
	public void visit(Identifier x);
	public void visit(IdentifierExp x);
	public void visit(IdentifierType x);
	public void visit(If x);
	public void visit(IntArrayType x);
	public void visit(IntegerLiteral x);
	public void visit(IntegerType x);
	public void visit(LessThan x);
	public void visit(LessThanOrEqual x);
	public void visit(MainClass x);
	public void visit(MethodDecl x);
	// public void visit(MethodDeclList x);
	public void visit(Minus x);
	public void visit(NewArray x);
	public void visit(NewObject x);
	public void visit(Not x);
	public void visit(NotEqual x);
	public void visit(Or x);
	public void visit(Plus x);
	public void visit(Print x);
	public void visit(Program x);
	//public void visit(Statement x);
	// public void visit(StatementList x);
	//public void visit(SyntaxTreePrinter x);
	public void visit(This x);
	public void visit(Times x);
	public void visit(True x);
	//public void visit(Type x);
	public void visit(VarDecl x);
	// public void visit(VarDeclList x);
	public void visit(While x);
}