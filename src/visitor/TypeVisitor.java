package visitor;

import syntaxtree.*;

public interface TypeVisitor
{
	public Type visit(And x);
	public Type visit(ArrayAssign x);
	public Type visit(ArrayLength x);
	public Type visit(ArrayLookup x);
	public Type visit(Assign x);
	public Type visit(Block x);
	public Type visit(BooleanType x);
	public Type visit(Call x);
	public Type visit(ClassDecl x);
	public Type visit(ClassDeclExtends x);
	public Type visit(ClassDeclList x);
	public Type visit(ClassDeclSimple x);
	public Type visit(Exp x);
	public Type visit(ExpList x);
	public Type visit(False x);
	public Type visit(Formal x );
	public Type visit(FormalList x);	
	public Type visit(Identifier x);
	public Type visit(IdentifierExp x);
	public Type visit(IdentifierType x);
	public Type visit(If x);
	public Type visit(IntArrayType x);
	public Type visit(IntegerLiteral x);
	public Type visit(IntegerType x);
	public Type visit(LessThan x);
	public Type visit(MainClass x);
	public Type visit(MethodDecl x);
	public Type visit(MethodDeclList x);
	public Type visit(Minus x);
	public Type visit(NewArray x);
	public Type visit(NewObject x);
	public Type visit(Not x);
	public Type visit(Plus x);
	public Type visit(Print x);
	public Type visit(Program x);
	public Type visit(Statement x);
	public Type visit(StatementList x);
	public Type visit(SyntaxTreePrinter x);
	public Type visit(This x);
	public Type visit(Times x);
	public Type visit(True x);
	public Type visit(Type x);
	public Type visit(VarDecl x);
	public Type visit(VarDeclList x);
	public Type visit(While x);
}