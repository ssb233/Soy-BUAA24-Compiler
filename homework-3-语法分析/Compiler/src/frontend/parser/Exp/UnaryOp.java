package frontend.parser.Exp;

import frontend.lexer.Token;
import frontend.parser.parserOutput;

public class UnaryOp implements parserOutput {
    private Token token;
    public UnaryOp(Token token){
        this.token = token;
    }

    @Override
    public String output() {
        StringBuilder stringBuilder = new StringBuilder();
        if(token !=null){
            stringBuilder.append(token.output());
        }
        stringBuilder.append("<UnaryOp>\n");
        return stringBuilder.toString();
    }
}
