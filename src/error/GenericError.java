public class GenericError extends java.lang.Error
{
    private String msg;
    public GenericError(String s) { 
	msg = "Internal compiler error!\n" + s;
    }
    public String toString() { 
	return msg; 
    }
}
