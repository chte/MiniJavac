package error;

/*
 * Internal package for handling errors
 *
 * An compile error holds information what row and colomn
 * the error occour on and what type of error it was
 *
 */
public class CompilerError
{
    //Private variable that defines an CompileError
	private ErrorObject err;
    private Object[] args;
    private int row;
    private int col;

    //Contructor for creating a new error
    public CompilerError(int row, int col, ErrorObject err, Object... args) {
        this.row = row;
        this.col = col;
		this.args = args;
		this.err = err;
    }

    //Get the error message for this error
    public String getMsg() {
		return (args.length == 0) ? err.getMsg() : String.format(err.getMsg(), args);
    }

    @Override 
    public String toString() {
        if(row == 0 && col == 0){
           return "Internal error: " + getMsg();
        }else{
    	   return "Encountered error at [line: " + row + ", column: " + col + "] " + getMsg();
        }
    }
}
