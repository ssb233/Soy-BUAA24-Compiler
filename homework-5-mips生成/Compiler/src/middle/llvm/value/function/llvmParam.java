package middle.llvm.value.function;

import middle.llvm.llvmOutput;
import middle.llvm.llvmValue;
import middle.llvm.type.llvmType;

public class llvmParam extends llvmValue implements llvmOutput {
    private int rank;//参数的位置，第几个
    public llvmParam(llvmType type, int rank){
        super(type);
        this.rank = rank;
    }
    public void setAllName(llvmFuncCnt funcCnt){
        this.setName("%"+String.valueOf(funcCnt.getCnt()));
    }
    public int getRank(){
        return this.rank;
    }
}
