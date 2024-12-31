package frontend.parser.Decl;

import frontend.lexer.Token;
import frontend.parser.Exp.Exp;
import frontend.parser.Terminal.StringConst;
import frontend.parser.parserOutput;

import java.util.ArrayList;

public class InitVal implements parserOutput {
    private Exp exp;
    private Exp expFirst;
    private ArrayList<Token> opTokens;
    private ArrayList<Exp> expArrayList;
    private StringConst stringConst;
    private Token LBRACE;
    private Token RBRACE;

    public InitVal(Exp exp, Exp expFirst, ArrayList<Token> opTokens, ArrayList<Exp> expArrayList, StringConst stringConst,Token LBRACE,Token RBRACE) {
        this.exp = exp;
        this.expFirst = expFirst;
        this.opTokens = opTokens;
        this.expArrayList = expArrayList;
        this.stringConst = stringConst;
        this.LBRACE = LBRACE;
        this.RBRACE = RBRACE;
    }

    @Override
    public String output() {
        StringBuilder stringBuilder = new StringBuilder();
        if(exp!=null){
            stringBuilder.append(exp.output());
        }else if(LBRACE!=null){
            stringBuilder.append(LBRACE.output());
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
            stringBuilder.append(RBRACE.output());
        }else if(stringConst!=null){
            stringBuilder.append(stringConst.output());
        }
        stringBuilder.append("<InitVal>\n");
        return stringBuilder.toString();
    }

    public ArrayList<Integer> getInitArray(){
        ArrayList<Integer> list = new ArrayList<>();
        if(this.stringConst!=null){
            String str = this.stringConst.getString().substring(1,this.stringConst.getString().length()-1);
            for(char ch:str.toCharArray()){
                list.add(Integer.valueOf(ch));
            }
            list.add(0);
        }else if(this.expFirst!=null){
            list.add(this.expFirst.getValue());
            if(this.expArrayList!=null){
                for(Exp item:expArrayList){
                    list.add(item.getValue());
                }
            }
            return list;
        }
        return list;
    }
    public int getInitValue(){
        if(this.exp!=null){
            return this.exp.getValue();
        }
        return 0;
    }
    public Exp getExp(){
        return this.exp;
    }
    public ArrayList<Exp> getExpArrayList(){
        ArrayList<Exp> list = new ArrayList<>();
        if(this.expFirst!=null){
            list.add(this.expFirst);
            if(this.expArrayList!=null){
                for(Exp item:expArrayList){
                    list.add(item);
                }
            }
            return list;
        }
        return list;
    }
    public StringConst getStringConst(){
        return this.stringConst;
    }
}
