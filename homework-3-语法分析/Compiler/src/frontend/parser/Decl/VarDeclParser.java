package frontend.parser.Decl;

import frontend.GlobalParm;
import frontend.lexer.Token;

import java.util.ArrayList;

public class VarDeclParser {
    private BType bType;
    private VarDef varDefFirst;
    private ArrayList<Token> opTokens;
    private ArrayList<VarDef> varDefArrayList;
    private Token SEMICN;
    public VarDecl VarDeclParser(){
        BTypeParser bTypeParser = new BTypeParser();
        this.bType = bTypeParser.BTypeParser();

        if(bType == null)return null;//第一个不匹配

        VarDefParser varDefParser = new VarDefParser();
        this.varDefFirst = varDefParser.VarDefParser();

        Token token = GlobalParm.getAToken();
        if(token.getTokenTypeName().equals("COMMA")){
            this.opTokens = new ArrayList<Token>();
            this.varDefArrayList = new ArrayList<VarDef>();
        }
        while(token.getTokenTypeName().equals("COMMA")){
            this.opTokens.add(token);
            this.varDefArrayList.add(new VarDefParser().VarDefParser());
            token = GlobalParm.getAToken();
        }GlobalParm.backAToken();
        token = GlobalParm.getAToken();
        if(token.getTokenTypeName().equals("SEMICN")){
            this.SEMICN = token;
        }else{
            GlobalParm.backAToken();
            //error, i型，缺少分号
            Token fToken = GlobalParm.getCurrentToken();
            Token eToken = new Token(null,"i",fToken.getLineNum(),0);
            GlobalParm.addParserError(eToken);
        }
        VarDecl varDecl = new VarDecl(this.bType, this.varDefFirst, this.opTokens, this.varDefArrayList,this.SEMICN);
        return varDecl;
    }
}
