package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;

public class NewObject extends Exp {
  public Identifier i;
  public int row, col;

  public NewObject(Identifier ai) {
    i=ai;
  }

  public NewObject(Identifier ai, int row, int col) {
    i=ai; this.row=row; this.col=col;
  }

  public void accept(Visitor v) {
    v.visit(this);
  }

  public Type accept(TypeVisitor v) {
    return v.visit(this);
  }
}
