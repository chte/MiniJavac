package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;

public class LessThanOrEqual extends Exp {
  public Exp e1,e2;
  public int row, col;
  
  public LessThanOrEqual(Exp ae1, Exp ae2) {
    e1=ae1; e2=ae2;
  }

  public LessThanOrEqual(Exp ae1, Exp ae2, int row, int col) {
    e1=ae1; e2=ae2; this.row=row; this.col=col;
  }


  public void accept(Visitor v) {
    v.visit(this);
  }

  public Type accept(TypeVisitor v) {
    return v.visit(this);
  }
}
