package middle.error;

import frontend.lexer.Token;

public class Error {
    private ErrorType errorType;
    private int lineNum;
    private Token eToken;

    public Error(ErrorType errorType, Token eToken){
        this.errorType = errorType;
        this.eToken = eToken;
        this.lineNum = this.eToken.getLineNum();
    }
    public int getLineNum(){
        return this.lineNum;
    }
    public String getErrorTypeString(){
        return this.errorType.getErrorCh();
    }
}
