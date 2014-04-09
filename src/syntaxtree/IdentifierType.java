package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;

public class IdentifierType extends Type {
    public String s;
    public int row, col;
    
    public boolean equals(Type tp)
    {
    	if (! (tp instanceof IdentifierType) ) return false;
    	return ((IdentifierType)tp).s.equals(s);
    }
    
    public IdentifierType(String as) {
      s=as;
    }

    public IdentifierType(String as, int row, int col) {
      s=as; this.row=row; this.col=col;
    }


    public void accept(Visitor v) {
      v.visit(this);
    }

    public Type accept(TypeVisitor v) {
      return v.visit(this);
    }
}
