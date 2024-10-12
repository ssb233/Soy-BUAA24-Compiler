package frontend.parser.FuncDef;

import frontend.GlobalParm;
import frontend.lexer.Token;
import frontend.parser.Decl.BType;
import frontend.parser.Decl.BTypeParser;
import frontend.parser.Terminal.Ident;
import frontend.parser.Terminal.IdentParser;

public class FuncFParamParser {
    private BType btype;
    private Ident ident;
    private Token LBRACK;
    private Token RBRACK;
    public FuncFParam FuncParamParser(){
        BTypeParser bTypeParser = new BTypeParser();
        this.btype = bTypeParser.BTypeParser();
        if(this.btype==null)return null;

        IdentParser identParser = new IdentParser();
        this.ident = identParser.IdentParser();

        Token token = GlobalParm.getAToken();
        if(token.getTokenTypeName().equals("LBRACK")){//有左侧中括号
            this.LBRACK = token;
            token = GlobalParm.getAToken();
            if(!token.getTokenTypeName().equals("RBRACK")){//无右侧中括号
                GlobalParm.backAToken();
                //error, k型
                Token fToken = GlobalParm.getCurrentToken();
                Token eToken = new Token(null, "k", fToken.getLineNum(),0);
                GlobalParm.addParserError(eToken);
            }else {
                this.RBRACK = token;
            }
        }else GlobalParm.backAToken();//无左侧中括号
        FuncFParam funcFParam = new FuncFParam(this.btype, this.ident,this.LBRACK,this.RBRACK);
        return funcFParam;
    }
}
