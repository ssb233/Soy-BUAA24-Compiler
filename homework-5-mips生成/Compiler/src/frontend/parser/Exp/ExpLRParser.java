package frontend.parser.Exp;

import frontend.GlobalParm;
import frontend.lexer.Token;
import middle.error.Error;
import middle.error.ErrorType;
import middle.symbol.SymbolTable;

public class ExpLRParser {
    private Exp exp;
    private Token LPARENT;
    private Token RPARENT;

    private SymbolTable symbolTable;//来自父元素表，不用自己建表

    public ExpLRParser(SymbolTable symbolTable){
        this.symbolTable = symbolTable;
    }

    public ExpLR ExpLRParser(){
        Token token = GlobalParm.getAToken();
        if(token.getTokenTypeName().equals("LPARENT")){
            this.LPARENT = token;
            ExpParser expParser = new ExpParser(this.symbolTable);
            this.exp = expParser.ExpParser();
            token = GlobalParm.getAToken();
            if(token.getTokenTypeName().equals("RPARENT")){
                this.RPARENT = token;
                //有右侧括号
            }else{//无右侧括号，但是它本来应该有的，所以这里是报错
                GlobalParm.backAToken();
                //error, j
                Token fToken = GlobalParm.getCurrentToken();
                Token eToken = new Token(null, "j",fToken.getLineNum(),0);
//                GlobalParm.addParserError(eToken);
                GlobalParm.addError(new Error(ErrorType.J_error, eToken));
                //flag
            }
        }else{
            GlobalParm.backAToken();
            return null;
        }
        ExpLR expLR = new ExpLR(this.exp,this.LPARENT,this.RPARENT);
        return expLR;
    }
}
