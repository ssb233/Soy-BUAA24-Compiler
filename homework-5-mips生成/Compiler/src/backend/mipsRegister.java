package backend;

import backend.mipsBasicBlock.mipsBasicBlock;
import backend.mipsInstruction.memOp.mipsInsLw;
import backend.mipsInstruction.memOp.mipsInsSw;
import backend.mipsInstruction.mipsInstruction;
import backend.symbol.mipsSymbol;
import backend.symbol.mipsSymbolTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class mipsRegister {
    public mipsSymbolTable symbolTable;
    public HashMap<Integer, Boolean> hasValues;//寄存器编号是否有值，即是否被使用
    public HashMap<Integer,mipsSymbol> regs;//寄存器编号与symbol的映射
    public int regNum = 32;
    public Stack<Integer> SregUse = new Stack<>();//s寄存器使用先后顺序
    public int tempPtr = 8;//指向最旧的寄存器的指针
    public mipsRegister(){
        this.hasValues = new HashMap<>();
        this.regs = new HashMap<>();
        for (int i = 0; i < this.regNum; i++) {
            if (isSReg(i) || isTmpReg(i) || isAReg(i) || isVReg(i) || isRaReg(i)) {
                // 可以被程序分配的寄存器 or 不知道有没有值的寄存器
                this.hasValues.put(i, false);
            } else {
                // 不可以被分配的寄存器
                this.hasValues.put(i, true);
            }
        }
    }
    public mipsRegister(mipsSymbolTable symbolTable){
        this.symbolTable = symbolTable;

    }

    public void setSymbolTable(mipsSymbolTable symbolTable){
        this.symbolTable = symbolTable;
    }
    //添加寄存器到符号的映射
    public void addSymbol(int reg, mipsSymbol symbol){
        this.regs.put(reg,symbol);
        this.hasValues.put(reg,true);
    }
    //判断是否未临时寄存器，$t系列
    public boolean isTmpReg(int site){
        if ((8 <= site && site <= 15) || // t0-t7
                (24 <= site && site <= 25)) { // t8-t9
            return true;
        } else {
            return false;
        }
    }
    /* 判断是否为$s寄存器 */
    public boolean isSReg(int site) {
        if (16 <= site && site <= 22) { // s0-s7
            return true;
        } else {
            return false;
        }
    }
    //判断是否为$v
    public boolean isVReg(int site){
        if (2 <= site && site <= 3) {
            return true;
        } else {
            return false;
        }
    }
    //判断是否为$Ra
    public boolean isRaReg(int site){
        return site==31;
    }
    //判断是否为$a
    public boolean isAReg(int site){
        if (4 <= site && site <= 7) {
            return  true;
        } else {
            return false;
        }
    }
    //获取一个空闲寄存器编号
    public int getFreeReg(boolean isTmp){
        for(int i=0;i<regNum;i++){
            if(this.isTmpReg(i)&& isTmp){
                //如果有未赋值的直接用
                if(!this.hasValues.get(i)){
                    return i;
                }else{
                    /* 如果有已经被use的临时变量的寄存器也可以直接使用 */
                    mipsSymbol symbol = this.regs.get(i);//获取i的符号
                    if(symbol.isTmp && symbol.isUsed){
                        return i;
                    }
                }
            }else if(this.isSReg(i)&&!isTmp ){
                /* 为局部变量寻找寄存器 */
                /* 如果有未赋值的寄存器可以直接使用 */
                if (!this.hasValues.get(i)) {
                    return i;
                } else if (!this.regs.get(i).isInReg) {
                    return i;
                }
            }
        }
        return -1;//没找到
    }
    public int getReg(boolean isTemp, mipsSymbol symbol, mipsBasicBlock basicBlock){
        //如果isTemp为真，从$t0~$t7找
        int freeReg = getFreeReg(isTemp);
        if(freeReg!=-1){
            if(!isTemp){
                /* 找到了空闲寄存器，将寄存器编号压入use栈 */
                this.SregUse.push(freeReg);
            }
            /* 修改MipsSymbol状态 */
            symbol.isInReg = true;//在寄存器里面
            symbol.regIndex = freeReg;
            this.hasValues.put(freeReg,true);
            this.regs.put(freeReg,symbol);
            /* 写入寄存器 */
            if(symbol.haveRam){
                readMemToReg(freeReg,symbol,basicBlock);
            }
            if(freeReg == this.tempPtr){
                this.tempPtr = (this.tempPtr +1 +32)%32;
            }
            return freeReg;
        }else{
            /* 没有找到空闲寄存器，说明需要将某个寄存器写入内存或做其他操作 */
            if(isTemp){
                /* 找到一个最旧的t寄存器 */
                int ret = getOldestTempReg(basicBlock);
                /* 修改MipsSymbol状态 */
                symbol.isInReg = true;
                symbol.regIndex = ret;
                this.hasValues.put(ret, true);
                this.regs.put(ret,symbol);
                return ret;
            }else{
                /* 弹出最旧的$s寄存器 */
                int OldestReg = this.SregUse.pop();
                mipsSymbol OldestSymbol = this.regs.get(OldestReg);
                if(OldestSymbol.haveRam){
                    //已分配内存
                    if(OldestSymbol.isInReg){
                        writeBackToMem(OldestSymbol,basicBlock);
                    }
                }else{
                    //需要分配内存
                    if(OldestSymbol.isInReg){
                        allocRamForSymbol(OldestSymbol);
                        writeBackToMem(OldestSymbol,basicBlock);
                    }
                }
                /* 修改被弹出符号状态 */
                OldestSymbol.isInReg = false;
                OldestSymbol.regIndex = -1;
                /* 修改寄存器表状态 */
                this.regs.put(OldestReg,symbol);
                this.SregUse.push(OldestReg);
                /* 修改新加入符号状态 */
                symbol.isInReg = true;
                symbol.regIndex = OldestReg;
                /* 如果新加入符号在内存有数据，则应读回 */
                if(symbol.haveRam){
                    readMemToReg(OldestReg,symbol,basicBlock);
                }
                if(OldestReg == this.tempPtr){
                    this.tempPtr = (this.tempPtr + 1 + 32) % 32;
                }
                return OldestReg;
            }
        }
    }
    //从内存读取变量，读到寄存器里面
    public void readMemToReg(int reg, mipsSymbol symbol,mipsBasicBlock basicBlock){
        mipsInsLw lw = new mipsInsLw(reg,symbol.base,symbol.offset);
        basicBlock.addAIns(lw);
    }
    public int getOldestTempReg(mipsBasicBlock basicBlock){
        //到这说明没有闲置寄存器
        while (true){
            /* 为临时变量寻找寄存器 */
            if(this.isTmpReg(this.tempPtr)){
                mipsSymbol symbol = this.regs.get(this.tempPtr);
                if(!symbol.haveRam){/* 没有对应内存空间需要先申请 */
                    allocRamForSymbol(symbol);
                }
                writeBackToMem(symbol,basicBlock);
                //将symbol写入内存，将他所属的寄存器空闲出来
                symbol.isInReg = false;//移除寄存器
                this.tempPtr = (this.tempPtr + 1 + 32) % 32;
                return symbol.regIndex;
            }else{
                this.tempPtr = (this.tempPtr + 1 + 32) % 32;
            }
        }
    }
    //申请一片空间 /* 申请空间只可能是局部变量 */
    public void allocRamForSymbol(mipsSymbol symbol){
        int FPoffset = this.symbolTable.Offset;
        symbol.offset = FPoffset+4;
        this.symbolTable.addOffset(8);
        symbol.haveRam = true;
    }
    /* 写回可能是全局变量或局部变量 */
    public void writeBackToMem(mipsSymbol symbol,mipsBasicBlock basicBlock){
        int reg = symbol.regIndex;
        int base = symbol.base;
        int off = symbol.offset;
        mipsInsSw sw = new mipsInsSw(reg,base,off);
        basicBlock.addAIns(sw);
    }

    /**
     * 将寄存器中的值全部存回内存
     * 主要用于跳转语句的跳转行为不一定执行带来的寄存器视图的差别
     */
    public ArrayList<mipsInstruction> writeBackAllRegs(){
        ArrayList<mipsInstruction> ret = new ArrayList<>();
        for(int i=0;i<this.regNum;i++){
            //$t
            if(this.isTmpReg(i)){
                if(hasValues.get(i)){
                    //t内有值
                    mipsSymbol symbol = regs.get(i);
                    if(!symbol.isTmp||(symbol.isTmp&&!symbol.isUsed)){
                        /* t寄存器内的值不是临时变量 */
                        /* 或t寄存器内的值是临时变量但还没用过 */
                        /* 此时应当写回内存 */
                        if(!symbol.haveRam){
                            /* 如果该变量没有内存中对应的空间，应当先为其申请 */
                            allocRamForSymbol(symbol);
                        }
                        mipsInsSw sw = new mipsInsSw(i,symbol.base,symbol.offset);
                        symbol.isInReg = false;
                        symbol.regIndex = -1;
                        ret.add(sw);
                    }
                    hasValues.put(i,false);
                }
            }//$s
            else if(this.isSReg(i)){
                if(hasValues.get(i)){
                    mipsSymbol symbol = regs.get(i);
                    if(!symbol.isInReg){
                        /* 不在寄存器中则不写回 */
                        continue;
                    }
                    if(!symbol.haveRam){
                        allocRamForSymbol(symbol);
                    }
                    mipsInsSw sw = new mipsInsSw(i,symbol.base,symbol.offset);
                    symbol.isInReg = false;
                    symbol.regIndex = -1;
                    ret.add(sw);
                }
                hasValues.put(i,false);
            }
            else if(this.isVReg(i)||this.isRaReg(i)||this.isAReg(i)){
                if(hasValues.get(i)){
                    mipsSymbol symbol = regs.get(i);
                    if(!symbol.haveRam){
                        allocRamForSymbol(symbol);
                    }
                    mipsInsSw sw = new mipsInsSw(i,symbol.base,symbol.offset);
                    symbol.isInReg = false;
                    symbol.regIndex = -1;
                    ret.add(sw);
                }
                hasValues.put(i,false);
            }
        }
        while(!SregUse.empty()){
            this.SregUse.pop();
        }
        tempPtr = 8;
        return ret;
    }
    public boolean isInReg(int regNum){
        return this.hasValues.get(regNum);
    }
    public HashMap<Integer, Boolean> cloneHasValues() {
        HashMap<Integer, Boolean> newHasValues = new HashMap<>();
        for (Integer index : this.hasValues.keySet()) {
            newHasValues.put(index, this.hasValues.get(index));
        }
        return newHasValues;
    }
    public HashMap<Integer, mipsSymbol> cloneRegs() {
        HashMap<Integer, mipsSymbol> newRegs = new HashMap<>();
        for (Integer index : this.regs.keySet()) {
            String name = this.regs.get(index).name;
            // newRegs.put(index, this.regs.get(index).cloneMipsSymbol());
            mipsSymbol symbol = this.symbolTable.getSymbol(name);
            if (symbol == null) {
                newRegs.put(index, this.regs.get(index).cloneMipsSymbol());
            } else {
                newRegs.put(index, this.symbolTable.getSymbol(name));
            }
        }
        return newRegs;
    }
    public Stack<Integer> cloneSregUse() {
        Stack<Integer> ret = new Stack<>();
        for (Integer index : this.SregUse) {
            ret.push(index);
        }
        return ret;
    }
    public mipsInstruction writeBackPublic(mipsSymbol symbol){
        mipsInsSw sw = new mipsInsSw(symbol.regIndex,symbol.base,symbol.offset);
        return sw;
    }
}
