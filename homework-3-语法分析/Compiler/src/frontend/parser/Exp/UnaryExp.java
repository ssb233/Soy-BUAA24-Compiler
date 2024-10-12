package frontend.parser.Exp;

import frontend.lexer.Token;
import frontend.parser.FuncDef.FuncRParams;
import frontend.parser.Terminal.Ident;
import frontend.parser.parserOutput;

public class UnaryExp implements parserOutput {
    private PrimaryExp primaryExp=null;
    private Ident ident=null;
    private FuncRParams funcRparams=null;
    private UnaryOp unaryOp=null;
    private UnaryExp unaryExp=null;
    private Token LPARENT;
    private Token RPARENT;
    public UnaryExp(PrimaryExp primaryExp, Ident ident, FuncRParams funcRparams, UnaryOp unaryOp, UnaryExp unaryExp,Token LPARENT,Token RPARENT){
        this.primaryExp = primaryExp;
        this.ident = ident;
        this.funcRparams = funcRparams;
        this.unaryOp = unaryOp;
        this.unaryExp = unaryExp;
        this.LPARENT = LPARENT;
        this.RPARENT = RPARENT;
    }
    @Override
    public String output() {
        StringBuilder stringBuilder = new StringBuilder();
        if(primaryExp!=null){
            stringBuilder.append(primaryExp.output());
        }else if(unaryOp!=null){
            stringBuilder.append(unaryOp.output());
            stringBuilder.append(unaryExp.output());
        }else if(ident!=null){
            stringBuilder.append(ident.output());
            stringBuilder.append(LPARENT.output());
            if(funcRparams!=null)stringBuilder.append(funcRparams.output());
            stringBuilder.append(RPARENT.output());
        }
        stringBuilder.append("<UnaryExp>\n");
        return stringBuilder.toString();
    }
}
