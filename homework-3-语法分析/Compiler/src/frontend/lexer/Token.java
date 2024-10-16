package frontend.lexer;

import frontend.parser.parserOutput;

public class Token implements parserOutput {
    private TokenType tokenType;
    private String tokenValue;
    private int lineNum;

    private long numValue;

    public Token(TokenType tokenType, String tokenValue, int lineNum, long numValue){
        this.tokenType = tokenType;
        this.tokenValue = tokenValue;//在错误情况下记录错误码，正确情况下记录字符串
        this.lineNum = lineNum;
        this.numValue = numValue;
    }
    public String getTokenTypeName(){
        return tokenType.toString();
    }
    public long getNumValue(){return numValue;}
    public String getTokenValue(){return tokenValue;}
    public int getLineNum(){return lineNum;}

    public void setNumValue(long flag){
        this.numValue = flag;
    }

    @Override
    public String output() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.getTokenTypeName()+" "+this.getTokenValue()+'\n');
        return stringBuilder.toString();
    }
}
