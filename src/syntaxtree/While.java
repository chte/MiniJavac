package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;

public class While extends Statement {
  public Exp e;
  public Statement s;
  public int row, col;

  public While(Exp ae, Statement as) {
    e=ae; s=as; 
  }

  public While(Exp ae, Statement as, int row, int col) {
    e=ae; s=as; this.row=row; this.col=col;
  }

  public void accept(Visitor v) {
    v.visit(this);
  }

  public Type accept(TypeVisitor v) {
    return v.visit(this);
  }
}

