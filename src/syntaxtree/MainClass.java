package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;

public class MainClass {
  public Identifier i1,i2;
  public StatementList sl;
  public VarDeclList vdl;
  public int row, col;
  
  public MainClass(Identifier ai1, Identifier ai2, VarDeclList avdl, StatementList asl) {
    i1=ai1; i2=ai2; vdl=avdl; sl=asl;
  }

  public MainClass(Identifier ai1, Identifier ai2, VarDeclList avdl, StatementList asl, int row, int col) {
    i1=ai1; i2=ai2; vdl=avdl; sl=asl; this.row=row; this.col=col;
  }
  public void accept(Visitor v) {
    v.visit(this);
  }

  public Type accept(TypeVisitor v) {
    return v.visit(this);
  }
}

