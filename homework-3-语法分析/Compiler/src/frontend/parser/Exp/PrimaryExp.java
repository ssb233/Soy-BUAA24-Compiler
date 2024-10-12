package frontend.parser.Exp;

import frontend.lexer.Token;
import frontend.parser.parserOutput;

import java.util.ArrayList;

public class PrimaryExp implements parserOutput {
    private PrimaryExpExtend primaryExpExtend;

    public PrimaryExp(PrimaryExpExtend primaryExpExtend){
        this.primaryExpExtend = primaryExpExtend;
    }
    @Override
    public String output() {
        StringBuilder stringBuilder = new StringBuilder();
        if(primaryExpExtend!=null){
            stringBuilder.append(primaryExpExtend.output());
        }
        stringBuilder.append("<PrimaryExp>\n");
        return stringBuilder.toString();
    }
}
