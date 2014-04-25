package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;

public class LongLiteral extends Exp {
    public long i;
    public int row, col;

    public LongLiteral(long ai) {
        i = ai;
    }

    public LongLiteral(long ai, int row, int col) {
        i = ai; this.row=row; this.col=col;
    }

    public void accept(Visitor v) {
        v.visit(this);
    }

    public Type accept(TypeVisitor v) {
        return v.visit(this);
    }
}