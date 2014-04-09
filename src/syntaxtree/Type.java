package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;

public abstract class Type {
    public  boolean equals(Type tp)
    {
	return getClass().equals(tp.getClass());
    }
    
    public  String toString(Type tp)
    {
      String[] parts = getClass().getName().split("\\.");
      return parts[parts.length-1];
    }  

    public abstract void accept(Visitor v);
    public abstract Type accept(TypeVisitor v);
}
