package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;

public class Block extends Statement {
  public VarDeclList vl;
  public StatementList sl;

  public Block(VarDeclList avl, StatementList asl) {
    vl = avl; sl = asl;
  }

  public Block(StatementList asl) {
    vl = new VarDeclList(); sl = asl; 
  }

  public void accept(Visitor v) {
    v.visit(this);
  }

  public Type accept(TypeVisitor v) {
    return v.visit(this);
  }
}

