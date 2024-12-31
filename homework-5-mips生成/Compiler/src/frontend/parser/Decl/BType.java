package frontend.parser.Decl;

import frontend.lexer.Token;
import frontend.parser.parserOutput;

public class BType implements parserOutput {
    private Token token;
    public BType(Token token){
        this.token = token;
    }
    public Token getToken(){
        return this.token;
    }
    @Override
    public String output() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(token.output());
        return stringBuilder.toString();
    }
}
