package frontend.parser.Decl;

import frontend.lexer.Token;
import frontend.parser.Exp.ConstExp;
import frontend.parser.Exp.RelExp;
import frontend.parser.Terminal.Ident;
import frontend.parser.parserOutput;

public class ConstDef implements parserOutput{
    private Ident ident;
    private ConstExp constExp;
    private ConstInitVal constInitVal;
    private Token LBRACK;
    private Token RBRACK;
    private Token ASSIGN;
    public ConstDef(Ident ident, ConstExp constExp, ConstInitVal constInitVal, Token LBRACK,Token RBRACK,Token ASSIGN){
        this.ident = ident;
        this.constExp = constExp;
        this.constInitVal = constInitVal;
        this.LBRACK = LBRACK;
        this.RBRACK = RBRACK;
        this.ASSIGN = ASSIGN;
    }
    @Override
    public String output() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(ident.output());
        if(LBRACK!=null){
            stringBuilder.append(LBRACK.output());
            stringBuilder.append(constExp.output());
            stringBuilder.append(RBRACK.output());
        }
        stringBuilder.append(ASSIGN.output());
        stringBuilder.append(constInitVal.output());
        stringBuilder.append("<ConstDef>\n");
        return stringBuilder.toString();
    }

    public Ident getIdent(){
        return this.ident;
    }
}
