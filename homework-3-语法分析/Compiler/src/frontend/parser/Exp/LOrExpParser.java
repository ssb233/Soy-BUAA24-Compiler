package frontend.parser.Exp;

import frontend.GlobalParm;
import frontend.lexer.Token;

import java.util.ArrayList;

public class LOrExpParser {
    private LAndExp lAndExpFirst=null;
    private ArrayList<Token> opTokens=null;
    private ArrayList<LAndExp> lAndExpArrayList=null;

    public LOrExp LOrExpParser(){
        this.lAndExpFirst = new LAndExpParser().LAndExpParser();
        if(this.lAndExpFirst==null)return null;

        Token token = GlobalParm.getAToken();
        if(token.getTokenTypeName().equals("OR")){
            opTokens = new ArrayList<Token>();
            lAndExpArrayList = new ArrayList<LAndExp>();
        }
        while(token.getTokenTypeName().equals("OR")){
            opTokens.add(token);
            lAndExpArrayList.add(new LAndExpParser().LAndExpParser());
            token = GlobalParm.getAToken();
        }GlobalParm.backAToken();
        LOrExp lOrExp = new LOrExp(lAndExpFirst, opTokens, lAndExpArrayList);
        return lOrExp;
    }
}
