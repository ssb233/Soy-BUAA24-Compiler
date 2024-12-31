package middle.symbol;

import frontend.lexer.Token;
import middle.llvm.llvmValue;

import java.util.ArrayList;

public class Symbol {
    public SymbolType symbolType;//符号类型
    public int tableId;//属于哪个符号表
    public Token token;
    public int cntNum;//临时变量编号
    public llvmValue value;//需要维护这个来查找寄存器
    public Symbol(SymbolType symbolType, int tableId, Token token){
        this.symbolType = symbolType;
        this.tableId = tableId;
        this.token = token;
    }
    public String getStringName(){
        return this.token.getTokenValue();
    }
    public int getType(){//1代表int或者char还有函数的返回值，比如charFunc和intFunc，2代表intArr， 3代表charArr, 4代表const
        if(this.symbolType.toString().equals("Char")||this.symbolType.toString().equals("Int")
                ||this.symbolType.toString().equals("CharFunc")||this.symbolType.toString().equals("IntFunc")
                ||this.symbolType.toString().equals("ConstChar")||this.symbolType.toString().equals("ConstInt")){
            return 1;
        }else if(this.symbolType.toString().equals("IntArray")){
            return 2;
        }else if(this.symbolType.toString().equals("CharArray")){
            return 3;
        }else if(this.symbolType.toString().equals("VoidFunc")){
            return 0;
        }else{//   const
            return 4;
        }
    }

    public String getTmpName(){
        return "%"+String.valueOf(this.cntNum);
    }
    public void setLLVMValue(llvmValue value){
        this.value = value;
    }
}
