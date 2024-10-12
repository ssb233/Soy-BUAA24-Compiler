package frontend.parser.Exp;

import frontend.lexer.Token;
import frontend.parser.parserOutput;

import java.util.ArrayList;

public class MulExp implements parserOutput {
    private UnaryExp unaryExpFirst=null;
    private ArrayList<Token> opTokens=null;
    private ArrayList<UnaryExp> unaryExpArrayList=null;
    public MulExp(UnaryExp unaryExp, ArrayList<Token> opTokens, ArrayList<UnaryExp> unaryExpArrayList){
        this.unaryExpFirst = unaryExp;
        this.opTokens = opTokens;
        this.unaryExpArrayList = unaryExpArrayList;
    }
    @Override
    public String output() {
        StringBuilder stringBuilder = new StringBuilder();
        if(unaryExpFirst!=null){
            stringBuilder.append(unaryExpFirst.output());

            if(opTokens!=null){

                int len = opTokens.size();
                for(int i=0;i<len;i++){
                    stringBuilder.append("<MulExp>\n");
                    stringBuilder.append(opTokens.get(i).output());
                    stringBuilder.append(unaryExpArrayList.get(i).output());
                }
            }
        }
        stringBuilder.append("<MulExp>\n");
        return stringBuilder.toString();
    }
}
