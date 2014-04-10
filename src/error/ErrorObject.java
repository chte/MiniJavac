package error;

public enum ErrorObject {

    /*
    * Symbol table 
    */
    DUPLICATE_CLASS			    (100, "'%s' was already defined in scope."),
    DUPLICATE_FIELD			    (101, "field identifier '%s' was already defined in the '%s' scope."),
    DUPLICATE_LOCAL			    (102, "local identifier '%s' was already defined in the '%s' scope."),
    DUPLICATE_METHOD		    (103, "duplicate method names '%s'"),
    DUPLICATE_PARAMETERS	    (104, "duplicate parameter names '%s'"),

    INVALID_INHERTICANCE        (200, "object type does not extend declared object type."),
    INVALID_TYPE                (201, "type cannot be resolved to long, int or boolean."),
    INVALID_CLASS_REFERENCE     (202, "'%s' does not inherit '%s'."),
    INVALID_ASSIGN              (203, "'%s' cannot be assigned to '%s'."),
    INVALID_ARRAY_INDEX         (204, "'%s' cannot be array index of type int."),
    INVALID_ARRAY_ASSIGN        (205, "'%s' cannot be assigned to array type '%s'"),
    INVALID_ARRAY_LENGTH        (206, "'%s' cannot be resolved to an array type of long or int."),
    INVALID_ARRAY_LOOKUP        (207, "type mismatch: cannot convert '%s' to array index of type int."),
    INVALID_UNARY_OP            (208, "invalid operand type: '%s' for operator '%s'."),
    INVALID_BINARY_OP           (209, "invalid operand types: '%s' and '%s' for operator '%s'."),
    INVALID_CALL                (210, "invalid call: '%s' is not an object."),
    UNMATCHED_ARGUMENT_SIZE     (211, "argument size does not match, expected '%s' but '%s' was provided."),
    UNMATCHED_ARGUMENT_TYPES    (212, "argument types does not match, expected '%s' but '%s' was provided."),
    UNMATCHED_RETURNTYPE        (213, "return types of method call does not match."),
    UNMATCHED_TYPE              (214, "type mismatch: cannot convert '%s' to type '%s'."),

    NOT_IN_SCOPE                (215, "'%s' is not reachable in scope"),
    CYCLIC_INHERTICANCE         (216, "cyclic inheritance involving '%s'."),

    DEBUG_ERROR_NOT_FOUND       (300, "Cannot find object '%s'.");



    private final String msg;
    private final int type;

    private ErrorObject(int type, String msg) {
    	this.type = type;
    	this.msg = msg;
    }

    public int getType(){
        return this.type;
    }

    public String getMsg(){
        return this.msg;
    }

    public CompilerError at(int row, int col, Object... args){
        return new CompilerError(row, col, this, args);
    }
}
