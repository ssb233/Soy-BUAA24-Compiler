package frontend.parser.FuncDef;

import frontend.lexer.Token;
import frontend.parser.Decl.BType;
import frontend.parser.Decl.BTypeParser;
import frontend.parser.Terminal.Ident;
import frontend.parser.parserOutput;

public class FuncFParam implements parserOutput {
    private BType btype;
    private Ident ident;
    private Token LBRACK;
    private Token RBRACK;
    public FuncFParam(BType btype, Ident ident,Token LBRACK,Token RBRACK){
        this.btype = btype;
        this.ident = ident;
        this.LBRACK = LBRACK;
        this.RBRACK = RBRACK;
    }
    @Override
    public String output() {
        StringBuilder stringBuilder = new StringBuilder();
        if(btype!=null){
            stringBuilder.append(btype.output());
            stringBuilder.append(ident.output());
            if(LBRACK!=null){
                stringBuilder.append(LBRACK.output());
                stringBuilder.append(RBRACK.output());
            }
        }
        stringBuilder.append("<FuncFParam>\n");
        return stringBuilder.toString();
    }
}
