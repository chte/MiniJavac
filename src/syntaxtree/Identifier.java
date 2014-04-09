package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;

public class Identifier {
  public String s;
  public int row, col;
  
  public Identifier(String as) { 
    s=as;
  }

  public Identifier(String as, int row, int col) { 
    s=as; this.row=row; this.col=col;
  }


  public void accept(Visitor v) {
    v.visit(this);
  }

  public Type accept(TypeVisitor v) {
    return v.visit(this);
  }

  public String toString(){
    return s;
  }
}
