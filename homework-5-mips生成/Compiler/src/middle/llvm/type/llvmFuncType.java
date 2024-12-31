package middle.llvm.type;

import middle.llvm.llvmValue;

import java.util.ArrayList;

public class llvmFuncType extends llvmType{
    private llvmType retType;//函数的返回类型，void, int ,char
    private ArrayList<llvmType> paramsType;//形参类型列表
    public llvmFuncType(llvmType retType,ArrayList<llvmType> paramsType){
        this.retType = retType;
        this.paramsType = paramsType;
    }
    public llvmType getRetType(){
        return retType;
    }
}
