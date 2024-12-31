package middle.symbol;

import frontend.parser.Decl.BType;
import frontend.parser.FuncDef.FuncType;

public enum SymbolType {
    ConstChar,
    ConstInt,
    ConstCharArray,
    ConstIntArray,
    Char,
    Int,
    CharArray,
    IntArray,

    VoidFunc,
    CharFunc,
    IntFunc;
    //0-var, int/char   array
    //1-const, int/char  array
    //2-func, void/int/char
    public String toString(){
        return this.name().toString();
    }
    public static SymbolType getSymbolType(int CorV, BType bType, int arrayFlag){//var或者const
        if(arrayFlag==0){//不是array
            if(CorV == 0){//var
                if(bType.getToken().getTokenTypeName().equals("INTTK")){
                    return SymbolType.Int;
                }else if(bType.getToken().getTokenTypeName().equals("CHARTK")){
                    return SymbolType.Char;
                }
            }else if(CorV == 1){//const
                if(bType.getToken().getTokenTypeName().equals("INTTK")){
                    return SymbolType.ConstInt;
                }else if(bType.getToken().getTokenTypeName().equals("CHARTK")){
                    return SymbolType.ConstChar;
                }
            }
        }else{//array
            if(CorV == 0){//var
                if(bType.getToken().getTokenTypeName().equals("INTTK")){
                    return SymbolType.IntArray;
                }else if(bType.getToken().getTokenTypeName().equals("CHARTK")){
                    return SymbolType.CharArray;
                }
            }else if(CorV == 1){//const
                if(bType.getToken().getTokenTypeName().equals("INTTK")){
                    return SymbolType.ConstIntArray;
                }else if(bType.getToken().getTokenTypeName().equals("CHARTK")){
                    return SymbolType.ConstCharArray;
                }
            }
        }

        return null;
    }
    public static SymbolType getFuncSymbolType(FuncType funcType){
        if(funcType.getToken().getTokenTypeName().equals("VOIDTK")){
            return SymbolType.VoidFunc;
        }else if(funcType.getToken().getTokenTypeName().equals("INTTK")){
            return SymbolType.IntFunc;
        }else if(funcType.getToken().getTokenTypeName().equals("CHARTK")){
            return SymbolType.CharFunc;
        }
        return null;
    }
}
