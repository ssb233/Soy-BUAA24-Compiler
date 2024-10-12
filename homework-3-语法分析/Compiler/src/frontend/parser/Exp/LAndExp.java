package frontend.parser.Exp;

import frontend.lexer.Token;
import frontend.parser.parserOutput;

import java.util.ArrayList;

public class LAndExp implements parserOutput {
    private EqExp eqExpFirst=null;
    private ArrayList<Token> opTokens=null;
    private ArrayList<EqExp> eqExpArrayList=null;
    public LAndExp(EqExp eqExpFirst, ArrayList<Token> opTokens, ArrayList<EqExp> eqExpArrayList){
        this.eqExpFirst = eqExpFirst;
        this.opTokens = opTokens;
        this.eqExpArrayList = eqExpArrayList;
    }
    @Override
    public String output() {
        StringBuilder stringBuilder = new StringBuilder();
        if(eqExpFirst!=null){
            stringBuilder.append(eqExpFirst.output());

            if(opTokens!=null){

                int len = opTokens.size();
                for(int i=0;i<len;i++){
                    stringBuilder.append("<LAndExp>\n");
                    stringBuilder.append(opTokens.get(i).output());
                    stringBuilder.append(eqExpArrayList.get(i).output());
                }
            }
        }
        stringBuilder.append("<LAndExp>\n");
        return stringBuilder.toString();
    }
}
