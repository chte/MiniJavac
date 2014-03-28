package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;

public class NewLongArray extends Exp {
    public Exp e;
    public int row, col;

    public NewLongArray(Exp ae) {
        e = ae;
    }

    public NewLongArray(Exp ae, int row, int col) {
        e = ae;
        this.row = row;
        this.col = col;
    }

    public void accept(Visitor v) {
        v.visit(this);
    }

    public Type accept(TypeVisitor v) {
        return v.visit(this);
    }
}