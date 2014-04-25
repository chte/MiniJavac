package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;

public class Print extends Statement {
  public Exp e;
  public int row, col;

  public Print(Exp ae) {
    e=ae;
  }

  public Print(Exp ae, int row, int col) {
    e=ae; this.row=row; this.col=col;
  }

  public void accept(Visitor v) {
    v.visit(this);
  }

  public Type accept(TypeVisitor v) {
    return v.visit(this);
  }
}
