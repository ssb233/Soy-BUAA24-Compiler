package frontend.parser.Decl;

import frontend.lexer.Token;
import frontend.parser.Exp.ConstExp;
import frontend.parser.Terminal.StringConst;
import frontend.parser.parserOutput;

import java.util.ArrayList;

public class ConstInitVal implements parserOutput {
    private ConstExp constExp;
    private ConstExp constExpFirst;
    private ArrayList<Token> opTokens;
    private ArrayList<ConstExp> constExpArrayList;
    private StringConst stringConst;
    private Token LBRACE;
    private Token RBRACE;
    public ConstInitVal(ConstExp constExp, ConstExp constExpFirst, ArrayList<Token> opTokens, ArrayList<ConstExp> constExpArrayList, StringConst stringConst,Token LBRACE,Token RBRACE){
        this.constExp = constExp;
        this.constExpFirst = constExpFirst;
        this.opTokens = opTokens;
        this.constExpArrayList = constExpArrayList;
        this.stringConst = stringConst;
        this.LBRACE = LBRACE;
        this.RBRACE = RBRACE;
    }
    @Override
    public String output() {
        StringBuilder stringBuilder = new StringBuilder();
        if(constExp!=null){
            stringBuilder.append(constExp.output());
        }else if(stringConst!=null){
            stringBuilder.append(stringConst.output());
        }else if(LBRACE!=null){
            stringBuilder.append(LBRACE.output());
            if(constExpFirst!=null){
                stringBuilder.append(constExpFirst.output());
                if(opTokens!=null){
                    int len = opTokens.size();
                    for(int i=0;i<len;i++){
                        stringBuilder.append(opTokens.get(i).output());
                        stringBuilder.append(constExpArrayList.get(i).output());
                    }
                }
            }

            stringBuilder.append(RBRACE.output());
        }
        stringBuilder.append("<ConstInitVal>\n");
        return stringBuilder.toString();
    }

    public ArrayList<Integer> getConstArray(){
        ArrayList<Integer> list = new ArrayList<>();
        if(this.stringConst!=null){
            String str = this.stringConst.getString().substring(1,this.stringConst.getString().length()-1);
            for(char ch:str.toCharArray()){
                list.add(Integer.valueOf((int)ch));
            }
            list.add(Integer.valueOf(0));//字符串末尾的\0
            return list;
        }else if(this.constExpFirst!=null){
            list.add(this.constExpFirst.getValue());
            if(this.constExpArrayList!=null){
                for(ConstExp item:this.constExpArrayList){
                    list.add(item.getValue());
                }
            }
            return list;
        }
        return list;
    }
    public int getConstExp(){
        if(this.constExp!=null){
            return this.constExp.getValue();
        }
        return 0;
    }
    public ArrayList<ConstExp> getConstExpArrayList(){
        ArrayList<ConstExp> constExps = new ArrayList<>();
        if(this.constExpFirst!=null){
            constExps.add(constExpFirst);
            if(this.constExpArrayList!=null){
                for(ConstExp item:this.constExpArrayList){
                    constExps.add(item);
                }
            }
        }
        return constExps;
    }
    public StringConst getStringConst(){
        return this.stringConst;
    }
}
