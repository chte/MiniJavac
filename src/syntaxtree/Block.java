package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;

public class Block extends Statement {
  public StatementList sl;
  public VarDeclList vl;

  public Block(VarDeclList avl, StatementList asl) {
    vl = avl; sl = asl;
  }

  public Block(StatementList asl) {
    sl=asl;
  }

  public void accept(Visitor v) {
    v.visit(this);
  }

  public Type accept(TypeVisitor v) {
    return v.visit(this);
  }
}

