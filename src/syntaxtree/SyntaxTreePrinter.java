package syntaxtree;

/**
 * Pretty printing of an Abstract Syntax Tree.
 * Visit each node of the tree and calls its
 * parent DepthFirstVisitor to in a depth first
 * manner exhaust before stepping into next node.
 *
 * preWork() and postWork() does to pretty indentation
 * as well printing identifier name. 
 *
 */

public class SyntaxTreePrinter extends visitor.DepthFirstVisitor 
{
	int level = 0;
	java.io.PrintStream out;
    public int row, col;


    /**
     * Constructor of SyntaxTreePrinter
     *
     * @param  	o 	a PrintStream which the printer should write to
     *
     */
	public SyntaxTreePrinter(java.io.PrintStream o) 
	{
		out=o;
	}

    /**
     * Constructor of SyntaxTreePrinter
     *
     * @param  	o 		a PrintStream which the printer should write to
     * @param 	row 	current row in code
     * @param 	col 	current col in code
     *
     */
	public SyntaxTreePrinter(java.io.PrintStream o, int row, int col) {
        out = o;
        this.row = row;
        this.col = col;
    }

    /**
     * Begins the visit of the program
     *
     * @param  	o 	program of which the syntax printer should print
     *
     */
	public void visit(Program n)
	{
		preWork(n);
		super.visit(n);
		postWork(n);
	}

	public void visit(MainClass n)
	{
		preWork(n);
		super.visit(n);
		postWork(n);
	}

	public void visit(ClassDeclSimple n)
	{
		preWork(n);
		super.visit(n);
		postWork(n);
	}

	public void visit(ClassDeclExtends n)
	{
		preWork(n);
		super.visit(n);
		postWork(n);
	}

	public void visit(Equal n)
	{
		preWork(n);
		super.visit(n);
		postWork(n);
	}


	public void visit(GreaterThan n)
	{
		preWork(n);
		super.visit(n);
		postWork(n);
	}

	public void visit(GreaterThanOrEqual n)
	{
		preWork(n);
		super.visit(n);
		postWork(n);
	}


	public void visit(VarDecl n)
	{
		preWork(n);
		super.visit(n);
		postWork(n);
	}

	public void visit(VoidType n) {
		preWork(n);
		super.visit(n);
		postWork(n);
	}

	public void visit(MethodDecl n)
	{
		preWork(n);
		super.visit(n);
		postWork(n);
	}

	public void visit(Formal n)
	{
		preWork(n);
		super.visit(n);
		postWork(n);
	}

	public void visit(IntArrayType n)
	{
		preWork(n);
		super.visit(n);
		postWork(n);
	}

	public void visit(BooleanType n)
	{
		preWork(n);
		super.visit(n);
		postWork(n);
	}

	public void visit(IntegerType n)
	{
		preWork(n);
		super.visit(n);
		postWork(n);
	}

	public void visit(IdentifierType n)
	{
		preWork(n);
		super.visit(n);
		postWork(n);
	}

	public void visit(Block n)
	{
		preWork(n);
		super.visit(n);
		postWork(n);
	}

	public void visit(If n)
	{
		preWork(n);
		super.visit(n);
		postWork(n);
	}

	public void visit(IfElse n)
	{
		preWork(n);
		super.visit(n);
		postWork(n);
	}

	public void visit(While n)
	{
		preWork(n);
		super.visit(n);
		postWork(n);
	}

	public void visit(Print n)
	{
		preWork(n);
		super.visit(n);
		postWork(n);
	}

	public void visit(Assign n)
	{
		preWork(n);
		super.visit(n);
		postWork(n);
	}

	public void visit(ArrayAssign n)
	{
		preWork(n);
		super.visit(n);
		postWork(n);
	}

	public void visit(ArrayLookup n)
	{
		preWork(n);
		super.visit(n);
		postWork(n);
	}

	public void visit(ArrayLength n)
	{
		preWork(n);
		super.visit(n);
		postWork(n);
	}

	public void visit(And n)
	{
		preWork(n);
		super.visit(n);
		postWork(n);
	}

	public void visit(LessThan n)
	{
		preWork(n);
		super.visit(n);
		postWork(n);
	}

	public void visit(LessThanOrEqual n)
	{
		preWork(n);
		super.visit(n);
		postWork(n);
	}


	public void visit(Plus n)
	{
		preWork(n);
		super.visit(n);
		postWork(n);
	}

	public void visit(Minus n)
	{
		preWork(n);
		super.visit(n);
		postWork(n);
	}

	public void visit(Times n)
	{
		preWork(n);
		super.visit(n);
		postWork(n);
	}

	public void visit(Call n)
	{
		preWork(n);
		super.visit(n);
		postWork(n);
	}

	public void visit(IntegerLiteral n)
	{
		preWork(n);
		super.visit(n);
		postWork(n);
	}

	public void visit(True n)
	{
		preWork(n);
		super.visit(n);
		postWork(n);
	}

	public void visit(False n)
	{
		preWork(n);
		super.visit(n);
		postWork(n);
	}

	public void visit(IdentifierExp n)
	{
		preWork(n);
		super.visit(n);
		postWork(n);
	}

	public void visit(This n)
	{
		preWork(n);
		super.visit(n);
		postWork(n);
	}

	public void visit(NotEqual n)
	{
		preWork(n);
		super.visit(n);
		postWork(n);
	}


	public void visit(NewIntArray n)
	{
		preWork(n);
		super.visit(n);
		postWork(n);
	}

	public void visit(NewLongArray n)
	{
		preWork(n);
		super.visit(n);
		postWork(n);
	}

	public void visit(NewObject n)
	{
		preWork(n);
		n.i.accept(this);	
		postWork(n);
	}

	public void visit(Not n)
	{
		preWork(n);
		super.visit(n);
		postWork(n);
	}

	public void visit(Identifier n)
	{
		preWork(n);
		super.visit(n);
		postWork(n);
	}

	public void visit(LongLiteral n) 
	{
		preWork(n);
		super.visit(n);
		postWork(n);
	}


    /**
     * Indents the code
     */
	String indent() 
	{
		String t = "";	
		for (int i = 0; i < level; ++i) t += "  ";
			return t;	
	}

    /**
     * Extracts the object name, i.e. the name of the node in
     * the AST.
     *
     * @param  	o 	object that the name should be extracted from
     */
	String nodeName(Object o) 
	{
		String name = o.getClass().toString();
		int dot = name.lastIndexOf(".");
		if (dot != -1) name = name.substring(dot+1);
		return name;	
	}


    /**
     * Does the prework before visiting the node. Indenting,
     * printing the name of the object and increases the level
     * of indentation
     *
     * @param  	n 	object that that is being visited
     */
	void preWork(Object n) 
	{
	String name = nodeName(n);
	out.println(indent() + "(" + name);
		++level;
	}

    /**
     * Does the postwork after visiting the node. Deindenting,
     * and decreases the level of indentation
     *
     * @param  	n 	object that that is being visited
     */
	void postWork(Object n) 
	{
		--level;	
		out.println(indent() + ")");
	}

	void preWork(Identifier n) 
	{
		String name = nodeName(n);
		out.println(indent() + "(" + name + 
			"[ " + n.s + " ]");
		++level;
	}

	void preWork(IdentifierExp n) 
	{
		String name = nodeName(n);
		out.println(indent() + "(" + name + 
			"[ " + n.s + " ]");
		++level;
	}

	void preWork(IdentifierType n) 
	{
		String name = nodeName(n);
		out.println(indent() + "(" + name + 
			"[ " + n.s + " ]");
		++level;
	}

	void preWork(IntegerLiteral n) 
	{
		String name = nodeName(n);
		out.println(indent() + "(" + name + 
			"[ " + n.i + " ]");
		++level;
	}

	void preWork(LongLiteral n) 
	{
		String name = nodeName(n);
		out.println(indent() + "(" + name + 
			"[ " + n.i + " ]");
		++level;
	}

	void preWork(Call n) 
	{
		String name = nodeName(n);
		out.println(indent() + "(" + name + 
			"[ " + n.i + " ]");
		++level;
	}

}
