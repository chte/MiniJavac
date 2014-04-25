package error;

public class CompilerError
{
	private ErrorObject err;
    private Object[] args;
    private int row;
    private int col;

    public CompilerError(int row, int col, ErrorObject err, Object... args) {
        this.row = row;
        this.col = col;
		this.args = args;
		this.err = err;
    }

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
