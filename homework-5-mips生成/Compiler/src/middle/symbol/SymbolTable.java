package middle.symbol;


import frontend.GlobalParm;
import middle.error.Error;
import middle.error.ErrorType;

import java.util.ArrayList;
import java.util.HashMap;

public class SymbolTable {
    public int id;
    public int fatherId;
    public ArrayList<Symbol> directory;
    public ArrayList<SymbolTable> subTables;

    public SymbolTable(int id, int fatherId) {
        this.id = id;
        this.fatherId = fatherId;
        this.directory = new ArrayList<>();
        this.subTables = new ArrayList<>();
    }

    public int isRepeated(String name){
        for(Symbol item: this.directory){
            if(item.token.getTokenValue().equals(name)){
                return 1;//有重复名字
            }
        }
        return 0;
    }
    public void addASymbol(Symbol symbol) {
        String name = symbol.getStringName();
        int repeat = isRepeated(name);
        if (repeat == 0) {//没有重复
            this.directory.add(symbol);
        } else {//变量名出现重复//同一级
            GlobalParm.addError(new Error(ErrorType.B_error, symbol.token));
        }
    }

    public void addASymbolTable(SymbolTable symbolTable) {
        int id = symbolTable.id;
        int flag = 0;
        for (SymbolTable item : this.subTables) {
            if (this.id == id) {
                System.out.println("子符号表重复添加");
                flag = 1;
            }
        }
        if (flag == 0) {
            this.subTables.add(symbolTable);
        }
    }

    public static Symbol searchSymbol(String name, SymbolTable symbolTable) {
        SymbolTable iter = symbolTable;
        int id;
        int repeat;//1代表找到了
        while (iter!=null && iter.isRepeated(name) == 0 && iter.id != 1) {
            id = iter.fatherId;
            iter = GlobalParm.getSymbolTable(id);
        }//跳出情况，iter为空，找到了，或者id为1了
        if(iter!=null){
            if(iter.isRepeated(name)==1){
                for(Symbol item: iter.directory){
                    if(item.token.getTokenValue().equals(name)){
                        return item;//找到了就返回
                    }
                }
            }else return null;
        }
        return null;
    }
    public static Symbol llvmSearchSymbol(String name, SymbolTable symbolTable){
        SymbolTable iter = symbolTable;
        int id;
        int repeat;//1代表找到了
        while (iter!=null && iter.repeatOrDefine(name) && iter.id != 1) {
            id = iter.fatherId;
            iter = GlobalParm.getSymbolTable(id);
        }//跳出情况，iter为空，找到了，或者id为1了
        if(iter!=null){
            if(iter.isRepeated(name)==1){
                for(Symbol item: iter.directory){
                    if(item.token.getTokenValue().equals(name)){
                        return item;//找到了就返回
                    }
                }
            }else return null;
        }
        return null;
    }
    private boolean repeatOrDefine(String name){
        for(Symbol item: this.directory){
            if(item.token.getTokenValue().equals(name)){
                if(item.value!=null){
                    return false;//只有同名字而且value已经有了的才能修改
                }
            }
        }
        return true;
    }
}
