package frontend.parser.Decl;

import frontend.GlobalParm;
import frontend.lexer.Token;
import frontend.parser.Exp.ConstExp;
import frontend.parser.Exp.ConstExpParser;
import frontend.parser.Terminal.Ident;
import frontend.parser.Terminal.IdentParser;

public class VarDefParser {
    private Ident ident;
    private ConstExp constExp;
    private InitVal initVal;
    private Token LBRACK;
    private Token RBRACK;
    private Token ASSIGN;

    public VarDef VarDefParser(){
        IdentParser identParser = new IdentParser();
        this.ident = identParser.IdentParser();
        if(this.ident ==null)return null;//第一个不匹配

        Token token = GlobalParm.getAToken();
        if(token.getTokenTypeName().equals("LBRACK")){
            this.LBRACK = token;
            ConstExpParser constExpParser = new ConstExpParser();
            this.constExp = constExpParser.ConstExpParser();
            token = GlobalParm.getAToken();
            if(token.getTokenTypeName().equals("RBRACK")){
                this.RBRACK = token;
                //有右括号，正确
            }else {
                GlobalParm.backAToken();
                //error, k型，无右侧中括号
                Token fToken = GlobalParm.getCurrentToken();
                Token eToken = new Token(null, "k",fToken.getLineNum(),0);
                GlobalParm.addParserError(eToken);
            }
        }else GlobalParm.backAToken();//无左括号
        token = GlobalParm.getAToken();
        if(token.getTokenTypeName().equals("ASSIGN")){//有等于号
            this.ASSIGN = token;
            InitValParser initValParser = new InitValParser();
            this.initVal = initValParser.InitValParser();
        }else GlobalParm.backAToken();

        VarDef varDef = new VarDef(this.ident, this.constExp, this.initVal,this.LBRACK,this.RBRACK,this.ASSIGN);
        return varDef;
    }
}
