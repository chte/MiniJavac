package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;

public class MainClass {
  public Identifier i1,i2;
  public StatementList sl;
  public VarDeclList vdl;

  public MainClass(Identifier ai1, Identifier ai2, VarDeclList avdl, StatementList asl) {
    i1=ai1; i2=ai2; vdl=avdl; sl=asl;
  }

  public void accept(Visitor v) {
    v.visit(this);
  }

  public Type accept(TypeVisitor v) {
    return v.visit(this);
  }
}

