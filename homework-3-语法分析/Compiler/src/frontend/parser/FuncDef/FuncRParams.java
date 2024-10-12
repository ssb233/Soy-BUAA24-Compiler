package frontend.parser.FuncDef;

import frontend.lexer.Token;
import frontend.parser.Exp.Exp;
import frontend.parser.parserOutput;

import java.util.ArrayList;

public class FuncRParams implements parserOutput {
    private Exp expFirst=null;
    private ArrayList<Token> opTokens=null;
    private ArrayList<Exp> expArrayList=null;

    public FuncRParams(Exp expFirst, ArrayList<Token> opTokens, ArrayList<Exp> expArrayList){
        this.expFirst = expFirst;
        this.opTokens = opTokens;
        this.expArrayList = expArrayList;
    }
    @Override
    public String output() {
        StringBuilder stringBuilder = new StringBuilder();
        if(expFirst!=null){
            stringBuilder.append(expFirst.output());
            if(opTokens!=null){
                int len = opTokens.size();
                for(int i=0;i<len;i++){
                    stringBuilder.append(opTokens.get(i).output());
                    stringBuilder.append(expArrayList.get(i).output());
                }
            }
        }
        stringBuilder.append("<FuncRParams>\n");
        return stringBuilder.toString();
    }
}
