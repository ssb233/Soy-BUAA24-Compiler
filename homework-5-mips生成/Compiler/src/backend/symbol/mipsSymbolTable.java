package backend.symbol;

import backend.mipsBasicBlock.mipsBasicBlock;
import backend.mipsInstruction.memOp.mipsInsLw;
import backend.mipsRegister;

import java.util.HashMap;

public class mipsSymbolTable {
    public mipsRegister register;//每个符号表对应有一个寄存器表
    public HashMap<String, mipsSymbol> symbolHashMap;
    public int Offset;//当前已经使用的内存相对于fp的偏移
    public mipsSymbolTable(mipsRegister register){
        this.symbolHashMap = new HashMap<>();
        this.register = register;
        this.register.setSymbolTable(this);
    }


    public void addSymbol(String name,mipsSymbol symbol){
        this.symbolHashMap.put(name,symbol);
    }
    public void addOffset(int off){
        this.Offset+=off;
    }

    /* 获取LLVM IR变量对应的符号的寄存器 */
    public int getRegIndex(String name, mipsBasicBlock basicBlock, boolean isLoad){
        mipsSymbol symbol = this.symbolHashMap.get(name);
        //在寄存器中直接返回
        if(symbol.isInReg){
            return symbol.regIndex;
        }else{
            //分配一个寄存器给他
            int freeReg = this.register.getReg(symbol.isTmp,symbol,basicBlock);
            //是否加载到寄存器里面
            if(!symbol.isInReg&&isLoad){
                mipsInsLw lw = new mipsInsLw(freeReg,symbol.base,symbol.offset);
                basicBlock.addAIns(lw);
            }
            return freeReg;
        }
    }
    //根据名字获取符号
    public mipsSymbol getSymbol(String name){
        if(this.symbolHashMap.containsKey(name)){
            return this.symbolHashMap.get(name);
        }else{
            System.out.println("error in mipsSymbolTable, no search!");
            return null;
        }
    }

    public HashMap<String,mipsSymbol> cloneSymbols(){
        HashMap<String,mipsSymbol> newSymbol = new HashMap<>();
        for(String index:this.symbolHashMap.keySet()){
            newSymbol.put(index,this.symbolHashMap.get(index).cloneMipsSymbol());
        }
        return newSymbol;
    }
}
