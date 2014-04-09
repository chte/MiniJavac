package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;

public class IntegerLiteral extends Exp {
  public int i;
  public int row, col;

  public IntegerLiteral(int ai) {
    i=ai;
  }

  public IntegerLiteral(int ai, int row, int col) {
    i=ai; this.row=row; this.col=col;
  }

  public void accept(Visitor v) {
    v.visit(this);
  }

  public Type accept(TypeVisitor v) {
    return v.visit(this);
  }
}
