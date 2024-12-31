package middle.symbol;

import frontend.lexer.Token;
import frontend.parser.Decl.ConstInitVal;
import frontend.parser.Exp.ConstExp;

import java.util.ArrayList;

public class SymbolConst extends Symbol{
    private int initVal;
    private ArrayList<Integer> integers;
    private int length;
    private int type;//1单个值，2数组
    public ConstInitVal constInitVal;
    public SymbolConst(SymbolType symbolType, int tableId, Token token, ConstExp constExp, ConstInitVal constInitVal){
        super(symbolType,tableId,token);
        this.constInitVal = constInitVal;
        if(isArray(symbolType)==1){//数组
            this.type = 2;
            this.length = constExp.getValue();
            if(constInitVal!=null){
                this.integers = constInitVal.getConstArray();
                int len = this.integers.size();
                if(len<length){
                    for(int i=0;i+len<length;i++){
                        integers.add(0);
                    }
                }
            }
        }else{//单个值
            this.type=1;
            if(constInitVal!=null){
                this.initVal = constInitVal.getConstExp();
            }
        }
    }

    private int isArray(SymbolType symbolType){//数组返回1
        if(symbolType.toString().equals("ConstCharArray")||symbolType.toString().equals("ConstIntArray")){
            return 1;
        }else return 0;
    }
    public int getLength(){
        return this.length;
    }
    public int getInitVal(){
        return this.initVal;
    }
    public int getIndexValue(int index){
        return this.integers.get(index);
    }
    public ArrayList<Integer> getIntegers(){return this.integers;}
    public int getType2(){
        return this.type;
    }
}
