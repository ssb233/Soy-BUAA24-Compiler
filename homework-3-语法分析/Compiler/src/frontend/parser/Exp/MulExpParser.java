package frontend.parser.Exp;

import frontend.GlobalParm;
import frontend.lexer.Token;

import java.util.ArrayList;

public class MulExpParser {
    private UnaryExp unaryExpFirst=null;
    private ArrayList<Token> opTokens=null;
    private ArrayList<UnaryExp> unaryExpArrayList=null;

    public MulExp MulExpParser(){
        UnaryExpParser unaryExpParser = new UnaryExpParser();
        this.unaryExpFirst = unaryExpParser.UnaryExpParser();
        if(unaryExpFirst==null)return null;

        Token token = GlobalParm.getAToken();
        if(token.getTokenTypeName().equals("MULT")||token.getTokenTypeName().equals("DIV")||token.getTokenTypeName().equals("MOD")){
            this.opTokens = new ArrayList<Token>();
            this.unaryExpArrayList = new ArrayList<UnaryExp>();
        }
        while(token.getTokenTypeName().equals("MULT")||token.getTokenTypeName().equals("DIV")||token.getTokenTypeName().equals("MOD")){
            opTokens.add(token);
            unaryExpArrayList.add(new UnaryExpParser().UnaryExpParser());
            token = GlobalParm.getAToken();
        }GlobalParm.backAToken();
        MulExp mulExp = new MulExp(unaryExpFirst,opTokens,unaryExpArrayList);
        return mulExp;
    }
}
