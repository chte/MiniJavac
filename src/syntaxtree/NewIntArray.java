package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;

public class NewIntArray extends Exp {
    public Exp e;
    public int row, col;

    public NewIntArray(Exp ae) {
        e = ae;
    }

    public NewIntArray(Exp ae, int row, int col) {
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