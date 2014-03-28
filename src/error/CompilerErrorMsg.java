package error;

public class CompilerErrorMsg
{
    private java.io.PrintStream out;
    private String msg;

    public CompilerErrorMsg(java.io.PrintStream o, String m) {
	out = o;
	msg = m;
    }

    public void flush() {
	out.println(msg);
    }
}
