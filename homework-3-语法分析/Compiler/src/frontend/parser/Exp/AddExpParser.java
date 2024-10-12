package frontend.parser.Exp;

import frontend.GlobalParm;
import frontend.lexer.Token;

import java.util.ArrayList;

public class AddExpParser {
    private MulExp mulExpFirst=null;
    private ArrayList<Token> opTokens=null;
    private ArrayList<MulExp> mulExpArrayList=null;

    public AddExp AddExpParser(){
        MulExpParser mulExpParser = new MulExpParser();
        this.mulExpFirst = mulExpParser.MulExpParser();
        if(this.mulExpFirst==null)return null;

        Token token = GlobalParm.getAToken();
        if(token.getTokenTypeName().equals("PLUS")||token.getTokenTypeName().equals("MINU")){
            opTokens = new ArrayList<Token>();
            mulExpArrayList = new ArrayList<MulExp>();
        }
        while(token.getTokenTypeName().equals("PLUS")||token.getTokenTypeName().equals("MINU")){
            opTokens.add(token);
            mulExpArrayList.add(new MulExpParser().MulExpParser());
            token = GlobalParm.getAToken();
        }GlobalParm.backAToken();
        AddExp addExp = new AddExp(mulExpFirst, opTokens, mulExpArrayList);
        return addExp;
    }
}
