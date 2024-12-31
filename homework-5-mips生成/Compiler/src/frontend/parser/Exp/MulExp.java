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

    public int getType(){
        if(unaryExpFirst!=null){
            return unaryExpFirst.getType();
        }else return 0;

    }

    public int getValue(){
        if(this.opTokens==null){
            if(this.unaryExpFirst!=null)return this.unaryExpFirst.getValue();
        }else{
            int value = this.unaryExpFirst.getValue();
            int len = opTokens.size();
            for(int i=0;i<len;i++){
                int tmp = this.unaryExpArrayList.get(i).getValue();
                String op = this.opTokens.get(i).getTokenValue();
                if(op.equals("*")){
                    value *= tmp;
                }else if(op.equals("/")){
                    value /=tmp;
                }else if(op.equals("%")){
                    value%=tmp;
                }
            }
            return value;
            //这里来会不会有除以0，%0的情况啊
        }
        return 0;
    }
    public UnaryExp getUnaryExpFirst(){
        return this.unaryExpFirst;
    }
    public ArrayList<UnaryExp> getUnaryExpArrayList(){
        return this.unaryExpArrayList;
    }
    public ArrayList<Token> getOpTokens(){
        return this.opTokens;
    }
}
