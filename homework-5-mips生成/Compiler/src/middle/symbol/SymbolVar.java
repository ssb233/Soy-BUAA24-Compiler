package middle.symbol;

import frontend.lexer.Token;
import frontend.parser.Decl.InitVal;
import frontend.parser.Exp.ConstExp;

import java.util.ArrayList;

public class SymbolVar extends Symbol{
    private int initVal;
    private ArrayList<Integer> integers;
    private int length;
    private int type;//1变量，2数组
    public boolean isInit;
    public InitVal init;
    public SymbolVar(SymbolType symbolType, int tableId, Token token){//函数形参走这里
        super(symbolType,tableId,token);
        type = 0;
    }
    public SymbolVar(SymbolType symbolType, int tableId, Token token, ConstExp constExp, InitVal initVal){//其它所有变量定义走这里
        super(symbolType,tableId,token);
        this.init = initVal;
        if(tableId==1){//全局变量
            this.isInit = true;
            if(isArray(symbolType)==1){//数组情况
                this.type = 2;
                this.length = constExp.getValue();
                this.integers = null;//null代表没有初始化
                int preSize = 0;
                if(initVal!=null){//有初值
                    this.integers = initVal.getInitArray();
                    int len = this.integers.size();
                    if(len<length){
                        for(int i=0;i+len<length;i++){
                            integers.add(0);
                        }
                    }
                }else {//没有初值而且是全局变量
                    this.integers = new ArrayList<>();
                    for(int i=0;i<this.length;i++){
                        this.integers.add(0);
                    }
                }
            }else{//变量
                this.isInit = false;
                this.type = 1;
                if(initVal!=null){
                    this.initVal = initVal.getInitValue();
                }else{
                    this.initVal = 0;//全局变量初始化为0
                }
            }
        }else{//局部变量
            if(isArray(symbolType)==1){
                this.type = 2;
                this.length = constExp.getValue();
            }else{
                this.type=1;
            }
        }


    }
    private int isArray(SymbolType symbolType){//数组返回1
        if(symbolType.toString().equals("CharArray")||symbolType.toString().equals("IntArray")){
            return 1;
        }else return 0;
    }
    public int getInitVal(){
        return this.initVal;
    }
    public int getIndexValue(int index){
        return this.integers.get(index);
    }
    public ArrayList<Integer> getIntegers(){
        return this.integers;
    }

    public int parseSymbolType(){
        if(this.symbolType.toString().equals("Int")){
            return 1;
        }else if(this.symbolType.toString().equals("Char")){
            return 2;
        }else if(this.symbolType.toString().equals("IntArray")){
            return 3;
        }else if(this.symbolType.toString().equals("CharArray")){
            return 4;
        }
        return 0;
    }

    public int getType2(){
        return this.type;
    }
    public int getLength(){
        return this.length;
    }
}
