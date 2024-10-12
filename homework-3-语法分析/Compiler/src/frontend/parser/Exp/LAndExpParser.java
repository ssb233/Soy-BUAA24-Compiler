package frontend.parser.Exp;

import frontend.GlobalParm;
import frontend.lexer.Token;

import java.util.ArrayList;

public class LAndExpParser {
    private EqExp eqExpFirst=null;
    private ArrayList<Token> opTokens=null;
    private ArrayList<EqExp> eqExpArrayList=null;

    public LAndExp LAndExpParser(){
        EqExpParser eqExpParser = new EqExpParser();
        this.eqExpFirst = eqExpParser.EqExpParser();
        if(this.eqExpFirst==null)return null;

        Token token = GlobalParm.getAToken();
        if(token.getTokenTypeName().equals("AND")){
            opTokens = new ArrayList<Token>();
            eqExpArrayList = new ArrayList<EqExp>();
        }
        while(token.getTokenTypeName().equals("AND")){
            opTokens.add(token);
            eqExpArrayList.add(new EqExpParser().EqExpParser());
            token = GlobalParm.getAToken();
        }GlobalParm.backAToken();
        LAndExp lAndExp = new LAndExp(eqExpFirst, opTokens, eqExpArrayList);
        return lAndExp;
    }
}
