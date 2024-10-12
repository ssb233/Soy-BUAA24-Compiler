package frontend.parser.Decl;

import frontend.GlobalParm;
import frontend.lexer.Token;

import java.util.ArrayList;

public class ConstDeclParser {
    private Token CONSTTK;
    private BType bType;
    private ConstDef constDefFirst;
    private ArrayList<Token> opTokens;
    private ArrayList<ConstDef> constDefArrayList;
    private Token SEMICN;

    public ConstDecl ConstDeclParser(){
        Token token = GlobalParm.getAToken();
        if(token.getTokenTypeName().equals("CONSTTK")){
            this.CONSTTK = token;
            BTypeParser bTypeParser = new BTypeParser();
            this.bType = bTypeParser.BTypeParser();

            ConstDefParser constDefParser = new ConstDefParser();
            this.constDefFirst = constDefParser.ConstDefParser();

            token = GlobalParm.getAToken();
            if(token.getTokenTypeName().equals("COMMA")){
                this.opTokens = new ArrayList<Token>();
                this.constDefArrayList = new ArrayList<ConstDef>();
            }
            while(token.getTokenTypeName().equals("COMMA")){
                this.opTokens.add(token);
                this.constDefArrayList.add(new ConstDefParser().ConstDefParser());
                token =GlobalParm.getAToken();
            }
            //GlobalParm.backAToken();
            if(token.getTokenTypeName().equals("SEMICN")){
                this.SEMICN = token;
            }else{
                GlobalParm.backAToken();
                //error, i型，缺少分号
                Token fToken = GlobalParm.getCurrentToken();
                Token eToken = new Token(null,"i",fToken.getLineNum(),0);
                GlobalParm.addParserError(eToken);
            }
        }else{
            GlobalParm.backAToken();
            return null;//第一个词就不匹配
        }
        ConstDecl constDecl = new ConstDecl(this.bType, this.constDefFirst, this.opTokens, this.constDefArrayList,this.CONSTTK,this.SEMICN);
        return constDecl;
    }
}
