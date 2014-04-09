package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;

public class Program {
  public MainClass m;
  public ClassDeclList cl;
  public int row, col;
  
  public Program(MainClass am, ClassDeclList acl) {
    m=am; cl=acl; 
  }

  public Program(MainClass am, ClassDeclList acl, int row, int col) {
    m=am; cl=acl; this.row=row; this.col=col;
  }

  public void accept(Visitor v) {
    v.visit(this);
  }

  public Type accept(TypeVisitor v) {
    return v.visit(this);
  }
}
