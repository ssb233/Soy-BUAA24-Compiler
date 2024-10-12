package frontend.parser.FuncDef;

import frontend.GlobalParm;
import frontend.lexer.Token;
import frontend.parser.Exp.Exp;
import frontend.parser.Exp.ExpParser;

import java.util.ArrayList;

public class FuncRParamsParser {
    private Exp expFirst=null;
    private ArrayList<Token> opTokens=null;
    private ArrayList<Exp> expArrayList=null;

    public FuncRParams FuncRParamsParser(){
        ExpParser expParser = new ExpParser();
        this.expFirst = expParser.ExpParser();

        if(expFirst == null)return null;//特殊情况，在unaryExp中

        Token token = GlobalParm.getAToken();
        if(token.getTokenTypeName().equals("COMMA")){
            this.opTokens = new ArrayList<Token>();
            this.expArrayList = new ArrayList<Exp>();
        }

        while(token.getTokenTypeName().equals("COMMA")){
            opTokens.add(token);
            expArrayList.add(new ExpParser().ExpParser());
            token = GlobalParm.getAToken();
        }GlobalParm.backAToken();
        FuncRParams funcRParams = new FuncRParams(expFirst, opTokens, expArrayList);
        return funcRParams;
    }
}
