package frontend.parser.Decl;

import frontend.GlobalParm;
import frontend.lexer.Token;
import frontend.parser.Exp.ConstExp;
import frontend.parser.Exp.ConstExpParser;
import frontend.parser.Terminal.Ident;
import frontend.parser.Terminal.IdentParser;

public class ConstDefParser {
    private Ident ident;
    private ConstExp constExp;
    private ConstInitVal constInitVal;
    private Token LBRACK;
    private Token RBRACK;
    private Token ASSIGN;

    public ConstDef ConstDefParser() {
        IdentParser identParser = new IdentParser();
        this.ident = identParser.IdentParser();
        if(this.ident ==null)return null;//第一个就不匹配

        Token token = GlobalParm.getAToken();
        if (token.getTokenTypeName().equals("LBRACK")) {
            this.LBRACK = token;
            ConstExpParser constExpParser = new ConstExpParser();
            this.constExp = constExpParser.ConstExpParser();
            Token token2 = GlobalParm.getAToken();
            if (token2.getTokenTypeName().equals("RBRACK")) {
                this.RBRACK = token2;
            } else {
                GlobalParm.backAToken();
                //error，k型错误，右侧中括号
                Token fToken = GlobalParm.getCurrentToken();
                Token eToken = new Token(null,"k", fToken.getLineNum(),0);
                GlobalParm.addParserError(eToken);
            }
        } else GlobalParm.backAToken();//没有左括号，
        token = GlobalParm.getAToken();//=号
        if(token.getTokenTypeName().equals("ASSIGN")){
            this.ASSIGN = token;
        }else GlobalParm.backAToken();
        ConstInitValParser constInitValParser = new ConstInitValParser();
        this.constInitVal = constInitValParser.ConstInitValParser();

        ConstDef constDef = new ConstDef(this.ident, this.constExp, this.constInitVal,this.LBRACK, this.RBRACK,this.ASSIGN);
        return constDef;
    }
}
