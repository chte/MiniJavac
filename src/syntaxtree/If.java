package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;

public class If extends Statement {
  public Exp e;
  public Statement s;

  public If(Exp ae, Statement as1) {
    e=ae; s=as1;
  }

  public void accept(Visitor v) {
    v.visit(this);
  }

  public Type accept(TypeVisitor v) {
    return v.visit(this);
  }
}

