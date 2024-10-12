package frontend.parser.Exp;

import frontend.GlobalParm;
import frontend.lexer.Token;

import java.util.ArrayList;

public class RelExpParser {
    private AddExp addExpFirst=null;
    private ArrayList<Token> opTokens=null;
    private ArrayList<AddExp> addExpArrayList=null;

    public RelExp RelExpParser(){
        AddExpParser addExpParser = new AddExpParser();
        this.addExpFirst = addExpParser.AddExpParser();
        if(this.addExpFirst ==null)return null;

        Token token = GlobalParm.getAToken();
        if(token.getTokenTypeName().equals("LEQ")||token.getTokenTypeName().equals("LSS")||token.getTokenTypeName().equals("GEQ")||token.getTokenTypeName().equals("GRE")){
            opTokens = new ArrayList<Token>();
            addExpArrayList = new ArrayList<AddExp>();
        }
        while(token.getTokenTypeName().equals("LEQ")||token.getTokenTypeName().equals("LSS")||token.getTokenTypeName().equals("GEQ")||token.getTokenTypeName().equals("GRE")){
            opTokens.add(token);
            addExpArrayList.add(new AddExpParser().AddExpParser());
            token = GlobalParm.getAToken();
        }GlobalParm.backAToken();
        RelExp relExp = new RelExp(addExpFirst, opTokens, addExpArrayList);
        return relExp;
    }
}
