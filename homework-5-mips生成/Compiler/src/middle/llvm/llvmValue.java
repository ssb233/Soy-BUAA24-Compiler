package middle.llvm;

import middle.llvm.type.llvmType;

import java.util.ArrayList;
import java.util.LinkedList;

public class llvmValue implements llvmOutput{

    private llvmType valueType = null;//value的类型
    private String name;//该value的名字
    private LinkedList<llvmUse> uses;
    public boolean isConst = false;//如果是true，那么就是确定值的常量，那么在指令中需要使用具体值
    private int constNum;
    public llvmValue(){

    }
    public llvmValue(llvmType type){
        this.valueType = type;
    }
    public llvmValue(llvmType type,String name){
        this.valueType = type;
        this.name = name;
    }
    public llvmValue(String name){
        this.name = name;
    }
    public llvmValue(llvmType valueType,int constNum){
        this.valueType = valueType;
        this.constNum = constNum;
        this.isConst =true;
    }
    public void setValueType(llvmType valueType){
        this.valueType = valueType;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getName(){return this.name;}
    public void addUse(llvmUse use){
        this.uses.add(use);
    }
    public void removeUse(llvmUse use){
        this.uses.removeIf(h->h.equals(use));
    }
    public llvmType getValueType(){
        return this.valueType;
    }
    public void setConst(boolean isConst){
        this.isConst = isConst;
    }
    public int getConstNum(){
        return this.constNum;
    }
    public void setConstNum(int num){
        this.constNum = num;
    }
    @Override
    public String llvmOutput() {
        return null;
    }
    public String outputConstOrName(){
        if(this.isConst){
            return String.valueOf(this.constNum);
        }else{
            return this.name;
        }
    }
    public String getMipsName(){
        if(this.isConst){
            return String.valueOf(this.constNum);
        }else{
            return this.name;
        }
    }
}
