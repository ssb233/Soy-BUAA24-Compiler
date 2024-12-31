package frontend.parser.Exp;

import frontend.lexer.Token;
import frontend.parser.parserOutput;

import java.util.ArrayList;

public class LOrExp implements parserOutput {
    private LAndExp lAndExpFirst=null;
    private ArrayList<Token> opTokens=null;
    private ArrayList<LAndExp> lAndExpArrayList=null;

    public LOrExp(LAndExp lAndExpFirst, ArrayList<Token> opTokens, ArrayList<LAndExp> lAndExpArrayList){
          this.lAndExpFirst = lAndExpFirst;
          this.opTokens = opTokens;
          this.lAndExpArrayList = lAndExpArrayList;
    }
    @Override
    public String output() {
        StringBuilder stringBuilder = new StringBuilder();
        if(lAndExpFirst!=null){
            stringBuilder.append(lAndExpFirst.output());

            if(opTokens!=null){

                int len = opTokens.size();
                for(int i=0;i<len;i++){
                    stringBuilder.append("<LOrExp>\n");
                    stringBuilder.append(opTokens.get(i).output());
                    stringBuilder.append(lAndExpArrayList.get(i).output());
                }
            }
        }
        stringBuilder.append("<LOrExp>\n");
        return stringBuilder.toString();
    }
    public ArrayList<LAndExp> getlAndExpArrayList(){
        ArrayList<LAndExp> lAndExps = new ArrayList<>();
        if(lAndExpFirst!=null){
            lAndExps.add(lAndExpFirst);
            if(this.lAndExpArrayList!=null){
                for(LAndExp item:this.lAndExpArrayList){
                    lAndExps.add(item);
                }
            }
        }
        return lAndExps;
    }
}
