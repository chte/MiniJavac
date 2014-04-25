package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;

public class Call extends Exp {
  public Exp e;
  public String c=null; // class of e
    
  public Identifier i;
  public ExpList el;

  public int row, col;
  
  public Call(Exp ae, Identifier ai, ExpList ael) {
    e=ae; i=ai; el=ael;
  }

  public Call(Exp ae, Identifier ai, ExpList ael, int row, int col) {
    e=ae; i=ai; el=ael; this.row=row; this.col=col;
  }

  public void accept(Visitor v) {
    v.visit(this);
  }

  public Type accept(TypeVisitor v) {
    return v.visit(this);
  }
}
