package frontend.parser.Terminal;

import frontend.lexer.Token;
import frontend.parser.parserOutput;

public class Ident implements parserOutput {
    private Token token;
    public Ident(Token token){
        this.token = token;
    }

    @Override
    public String output() {
        StringBuilder stringBuilder = new StringBuilder();
        if(token !=null){
            stringBuilder.append(token.output());
        }
        return stringBuilder.toString();
    }
}
