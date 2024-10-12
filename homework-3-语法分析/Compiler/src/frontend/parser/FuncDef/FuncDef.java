package frontend.parser.FuncDef;

import frontend.lexer.Token;
import frontend.parser.Terminal.Ident;
import frontend.parser.parserOutput;

public class FuncDef implements parserOutput {
    private FuncType funcType;
    private Ident ident;
    private FuncFParams funcFParams;
    private Block block;
    private Token LPARENT;
    private Token RPARENT;
    public FuncDef(FuncType funcType, Ident ident, FuncFParams funcFParams, Block block,Token LPARENT,Token RPARENT){
        this.funcType = funcType;
        this.ident = ident;
        this.funcFParams = funcFParams;
        this.block = block;
        this.LPARENT = LPARENT;
        this.RPARENT = RPARENT;
    }
    @Override
    public String output() {
        StringBuilder stringBuilder = new StringBuilder();
        if(funcType!=null){
            stringBuilder.append(funcType.output());
            stringBuilder.append(ident.output());
            stringBuilder.append(LPARENT.output());
            if(funcFParams!=null){
                stringBuilder.append(funcFParams.output());
            }
            stringBuilder.append(RPARENT.output());
            stringBuilder.append(block.output());
        }
        stringBuilder.append("<FuncDef>\n");
        return stringBuilder.toString();
    }
}
