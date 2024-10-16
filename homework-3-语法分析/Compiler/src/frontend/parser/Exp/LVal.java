package frontend.parser.Exp;

import frontend.lexer.Token;
import frontend.parser.Terminal.Ident;
import frontend.parser.parserOutput;

public class LVal extends PrimaryExpExtend implements parserOutput {
    private Ident ident;
    private Exp exp;
    private Token LBRACK;
    private Token RBRACK;
    public LVal(Ident ident, Exp exp,Token LBRACK,Token RBRACK){
        this.exp = exp;
        this.ident = ident;
        this.LBRACK = LBRACK;
        this.RBRACK = RBRACK;
    }
    public Token getRBRACK(){return this.RBRACK;}
    public Token getLBRACK(){return this.LBRACK;}
    public Ident getIdent(){
        return this.ident;
    }
    @Override
    public String output() {
       StringBuilder stringBuilder = new StringBuilder();
       if(ident!=null){
           stringBuilder.append(ident.output());
           if(LBRACK!=null){
               stringBuilder.append(LBRACK.output());
               stringBuilder.append(exp.output());
               stringBuilder.append(RBRACK.output());
           }
       }
       stringBuilder.append("<LVal>\n");
       return stringBuilder.toString();
    }
}
