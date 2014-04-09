package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;

public class IdentifierExp extends Exp {
  public String s;
  public int row, col;

  public IdentifierExp(String as) { 
      s=as;
  }

  public IdentifierExp(String as, int row, int col) { 
      s=as; this.row=row; this.col=col;
  }


  public void accept(Visitor v) {
    v.visit(this);
  }

  public Type accept(TypeVisitor v) {
    return v.visit(this);
  }
}
