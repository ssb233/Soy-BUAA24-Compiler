package frontend.lexer;

public class Token {
    private TokenType tokenType;
    private String tokenValue;
    private int lineNum;

    private long numValue;

    public Token(TokenType tokenType, String tokenValue, int lineNum, long numValue){
        this.tokenType = tokenType;
        this.tokenValue = tokenValue;
        this.lineNum = lineNum;
        this.numValue = numValue;
    }
    public String getTokenTypeName(){
        return tokenType.toString();
    }
    public long getNumValue(){return numValue;}
    public String getTokenValue(){return tokenValue;}
    public int getLineNum(){return lineNum;}

}
