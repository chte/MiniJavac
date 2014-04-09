package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;

public class VarDecl {
  public Type t;
  public Identifier i;
  public int row, col;
  
  public VarDecl(Type at, Identifier ai) {
    t=at; i=ai;
  }

  public VarDecl(Type at, Identifier ai, int row, int col) {
    t=at; i=ai; this.row=row; this.col=col;
  }


  public void accept(Visitor v) {
    v.visit(this);
  }

  public Type accept(TypeVisitor v) {
    return v.visit(this);
  }
}
