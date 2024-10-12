package frontend.parser.FuncDef;

import frontend.lexer.Token;
import frontend.parser.parserOutput;

public class FuncType implements parserOutput {
    private Token token;
    public FuncType(Token token){
        this.token = token;
    }
    @Override
    public String output() {
        StringBuilder stringBuilder = new StringBuilder();
        if(token!=null){
            stringBuilder.append(token.output());
        }
        stringBuilder.append("<FuncType>\n");
        return stringBuilder.toString();
    }
}
