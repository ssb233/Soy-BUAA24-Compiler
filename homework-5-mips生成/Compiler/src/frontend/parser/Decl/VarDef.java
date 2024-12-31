package frontend.parser.Decl;

import frontend.lexer.Token;
import frontend.parser.Exp.ConstExp;
import frontend.parser.Terminal.Ident;
import frontend.parser.parserOutput;

public class VarDef implements parserOutput {
    private Ident ident;
    private ConstExp constExp;
    private InitVal initVal;
    private Token LBRACK;
    private Token RBRACK;
    private Token ASSIGN;
    public VarDef(Ident ident, ConstExp constExp, InitVal initVal,Token LBRACK,Token RBRACK,Token ASSIGN){
        this.ident = ident;
        this.constExp = constExp;
        this.initVal = initVal;
        this.LBRACK = LBRACK;
        this.RBRACK = RBRACK;
        this.ASSIGN = ASSIGN;
    }
    @Override
    public String output() {
        StringBuilder stringBuilder = new StringBuilder();
        if(ident!=null){
            stringBuilder.append(ident.output());
        }
        if(LBRACK!=null){
            stringBuilder.append(LBRACK.output());
            stringBuilder.append(constExp.output());
            stringBuilder.append(RBRACK.output());
        }
        if(ASSIGN!=null){
            stringBuilder.append(ASSIGN.output());
            stringBuilder.append(initVal.output());
        }
        stringBuilder.append("<VarDef>\n");
        return stringBuilder.toString();
    }

    public Ident getIdent(){
        return this.ident;
    }
}
