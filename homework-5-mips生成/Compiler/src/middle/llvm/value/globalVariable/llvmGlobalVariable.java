package middle.llvm.value.globalVariable;

import middle.llvm.constant.llvmConstant;
import middle.llvm.constant.llvmConstantArray;
import middle.llvm.constant.llvmConstantInt;
import middle.llvm.llvmOutput;
import middle.llvm.llvmUser;
import middle.llvm.type.llvmType;

import java.util.ArrayList;

public class llvmGlobalVariable extends llvmUser implements llvmOutput {
    private boolean isConst;
    private llvmConstant initVal;//初始值
    private llvmType valueType;//变量的类型

    public llvmGlobalVariable(){
        super(null);

    }
    public llvmGlobalVariable(boolean isConst){
        super(null);
        this.isConst = isConst;
    }

    public void setInitVal(llvmConstant constant){
        this.initVal = constant;
    }

    public void setValueType(llvmType type){
        this.valueType = type;
    }

    public String llvmOutput(){
        StringBuilder sb = new StringBuilder();
        sb.append("@"+this.getName());
        sb.append(" = dso_local global ");
        sb.append(this.initVal.llvmOutput());
        sb.append('\n');
        return sb.toString();
    }
    public int getSingleInitVal(){
        if(this.initVal instanceof llvmConstantInt){
            llvmConstantInt tmp = (llvmConstantInt) this.initVal;
            return tmp.getInitVal();
        }
        return 0;
    }
    public ArrayList<Integer> getArrInitVal(){
        if(this.initVal instanceof llvmConstantArray){
            llvmConstantArray tmp = (llvmConstantArray) this.initVal;
            return tmp.getIntegers();
        }
        return null;
    }
    public llvmType getValueType(){
        return this.valueType;
    }



}
