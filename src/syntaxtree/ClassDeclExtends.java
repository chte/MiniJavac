package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;

public class ClassDeclExtends extends ClassDecl {
  public Identifier i;
  public Identifier j;
  public VarDeclList vl;  
  public MethodDeclList ml;
  public int row, col;
  public ClassDeclExtends(Identifier ai, Identifier aj, VarDeclList avl, MethodDeclList aml) {
    i=ai; j=aj; vl=avl; ml=aml;
  }

  public ClassDeclExtends(Identifier ai, Identifier aj, VarDeclList avl, MethodDeclList aml, int row, int col) {
    i=ai; j=aj; vl=avl; ml=aml; this.row=row; this.col=col;
  }

  public void accept(Visitor v) {
    v.visit(this);
  }

  public Type accept(TypeVisitor v) {
    return v.visit(this);
  }
}
