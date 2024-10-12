package frontend.parser.Exp;

import frontend.lexer.Token;
import frontend.parser.parserOutput;

public class Exp implements parserOutput {
    private AddExp addExp;
    public Exp(AddExp addExp){
        this.addExp = addExp;
    }

    @Override
    public String output() {
        StringBuilder stringBuilder = new StringBuilder();
        if(addExp!=null){
            stringBuilder.append(addExp.output());
        }
        stringBuilder.append("<Exp>\n");
        return stringBuilder.toString();
    }
}
