package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;

public class Assign extends Statement {
  public Identifier i;
  public Exp e;
  public int row, col;

  public Assign(Identifier ai, Exp ae) {
    i=ai; e=ae; 
  }

  public Assign(Identifier ai, Exp ae, int row, int col) {
    i=ai; e=ae; this.row=row; this.col=col;
  }


  public void accept(Visitor v) {
    v.visit(this);
  }

  public Type accept(TypeVisitor v) {
    return v.visit(this);
  }
}

