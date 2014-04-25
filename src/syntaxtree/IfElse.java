package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;

public class IfElse extends Statement {
  public Exp e;
  public Statement s1,s2;
  public int row, col;

  public IfElse(Exp ae, Statement as1, Statement as2) {
    e=ae; s1=as1; s2=as2;
  }

  public IfElse(Exp ae, Statement as1, Statement as2, int row, int col) {
    e=ae; s1=as1; s2=as2; this.row=row; this.col=col;
  }


  public void accept(Visitor v) {
    v.visit(this);
  }

  public Type accept(TypeVisitor v) {
    return v.visit(this);
  }
}

