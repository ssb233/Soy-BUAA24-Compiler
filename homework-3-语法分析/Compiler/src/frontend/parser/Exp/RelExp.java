package frontend.parser.Exp;

import frontend.lexer.Token;
import frontend.parser.parserOutput;

import java.util.ArrayList;

public class RelExp implements parserOutput {
    private AddExp addExpFirst=null;
    private ArrayList<Token> opTokens=null;
    private ArrayList<AddExp> addExpArrayList=null;

    public RelExp(AddExp addExpFirst, ArrayList<Token> opTokens, ArrayList<AddExp> addExpArrayList){
        this.addExpFirst = addExpFirst;
        this.opTokens = opTokens;
        this.addExpArrayList = addExpArrayList;
    }
    @Override
    public String output() {
       StringBuilder stringBuilder = new StringBuilder();
       if(addExpFirst!=null){
           stringBuilder.append(addExpFirst.output());

           if(opTokens!=null){

               int len = opTokens.size();
               for(int i=0;i<len;i++){
                   stringBuilder.append("<RelExp>\n");
                   stringBuilder.append(opTokens.get(i).output());
                   stringBuilder.append(addExpArrayList.get(i).output());
               }
           }
       }
       stringBuilder.append("<RelExp>\n");
       return stringBuilder.toString();
    }
}
