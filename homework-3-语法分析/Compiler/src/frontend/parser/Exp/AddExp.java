package frontend.parser.Exp;

import frontend.lexer.Token;
import frontend.parser.parserOutput;

import java.util.ArrayList;

public class AddExp implements parserOutput {
    private MulExp mulExpFirst=null;
    private ArrayList<Token> opTokens=null;
    private ArrayList<MulExp> mulExpArrayList=null;

    public AddExp(MulExp mulExpFirst, ArrayList<Token> opTokens, ArrayList<MulExp> mulExpArrayList){
        this.mulExpFirst = mulExpFirst;
        this.opTokens = opTokens;
        this.mulExpArrayList = mulExpArrayList;
    }
    @Override
    public String output() {
        StringBuilder stringBuilder = new StringBuilder();
        if(mulExpFirst!=null){
            stringBuilder.append(mulExpFirst.output());

            if(opTokens!=null){

                int len = opTokens.size();
                for(int i=0;i<len;i++){
                    stringBuilder.append("<AddExp>\n");
                    stringBuilder.append(opTokens.get(i).output());
                    stringBuilder.append(mulExpArrayList.get(i).output());
                }
            }
        }
        stringBuilder.append("<AddExp>\n");
        return stringBuilder.toString();
    }
}
