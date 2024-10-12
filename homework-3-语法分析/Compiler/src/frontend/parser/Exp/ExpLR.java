package frontend.parser.Exp;

import frontend.lexer.Token;
import frontend.parser.parserOutput;

public class ExpLR extends PrimaryExpExtend implements parserOutput {
    private Exp exp;
    private Token LPARENT;
    private Token RPARENT;
    public ExpLR(Exp exp,Token LPARENT,Token RPARENT){
        this.exp = exp;
        this.LPARENT = LPARENT;
        this.RPARENT = RPARENT;
    }
    @Override
    public String output() {
        StringBuilder stringBuilder = new StringBuilder();
        if(LPARENT!=null){
            stringBuilder.append(LPARENT.output());
            stringBuilder.append(exp.output());
            stringBuilder.append(RPARENT.output());
        }
        return stringBuilder.toString();
    }
}
