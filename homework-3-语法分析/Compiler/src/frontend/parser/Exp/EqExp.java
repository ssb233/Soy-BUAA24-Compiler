package frontend.parser.Exp;

import frontend.lexer.Token;
import frontend.parser.parserOutput;

import java.util.ArrayList;

public class EqExp implements parserOutput {
    private RelExp relExpFirst=null;
    private ArrayList<Token> opTokens=null;
    private ArrayList<RelExp> relExpArrayList=null;

    public EqExp(RelExp relExpFirst, ArrayList<Token> opTokens, ArrayList<RelExp> relExpArrayList){
        this.relExpFirst = relExpFirst;
        this.opTokens = opTokens;
        this.relExpArrayList = relExpArrayList;
    }
    @Override
    public String output() {
        StringBuilder stringBuilder = new StringBuilder();
        if(relExpFirst!=null){
            stringBuilder.append(relExpFirst.output());

            if(opTokens!=null){

                int len = opTokens.size();
                for(int i=0;i<len;i++){
                    stringBuilder.append("<EqExp>\n");
                    stringBuilder.append(opTokens.get(i).output());
                    stringBuilder.append(relExpArrayList.get(i).output());
                }
            }
        }
        stringBuilder.append("<EqExp>\n");
        return stringBuilder.toString();
    }
}
