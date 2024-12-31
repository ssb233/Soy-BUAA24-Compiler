package frontend.parser.Exp;

import frontend.GlobalParm;
import frontend.lexer.Token;
import frontend.parser.Terminal.Ident;
import middle.error.Error;
import middle.error.ErrorType;
import middle.symbol.SymbolTable;

public class LValParser{
    private Exp exp;
    private Ident ident;
    private Token LBRACK;
    private Token RBRACK;

    public SymbolTable symbolTable;//来自父元素

    public LValParser(SymbolTable symbolTable){
        this.symbolTable = symbolTable;
    }

    public LVal LValParser(){
        Token token = GlobalParm.getAToken();
        if(token.getTokenTypeName().equals("IDENFR")){
            this.ident = new Ident(token);//遇到终结符直接收入
            LVal.checkIdentExist(this.symbolTable,this.ident);
            token = GlobalParm.getAToken();
            if(token.getTokenTypeName().equals("LBRACK")){
                this.LBRACK = token;
                ExpParser expParser = new ExpParser(this.symbolTable);
                this.exp = expParser.ExpParser();
                token = GlobalParm.getAToken();
                if(token.getTokenTypeName().equals("RBRACK")){
                    this.RBRACK = token;
                }else{
                    GlobalParm.backAToken();
                    //error记录，缺少右侧中括号
                    Token fToken = GlobalParm.getCurrentToken();
                    Token eToken = new Token(null, "k",fToken.getLineNum(),0);
//                    GlobalParm.addParserError(eToken);
                    GlobalParm.addError(new Error(ErrorType.K_error, eToken));
                    //flag
                }
            }else{//就是单纯的Ident，没有[]
                GlobalParm.backAToken();
            }
        }else{
            GlobalParm.backAToken();
            return null;//第一个ident都不匹配
        }

        LVal lval = new LVal(this.ident,this.exp,this.LBRACK,this.RBRACK, this.symbolTable);



        return lval;
    }
}
