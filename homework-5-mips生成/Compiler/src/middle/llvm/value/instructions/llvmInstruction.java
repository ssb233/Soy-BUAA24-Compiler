package middle.llvm.value.instructions;

import middle.llvm.llvmOutput;
import middle.llvm.llvmUser;
import middle.llvm.type.llvmType;
import middle.llvm.value.basicblock.llvmBasicBlock;
import middle.llvm.value.function.llvmFuncCnt;

public class llvmInstruction extends llvmUser implements llvmOutput {
    private llvmInstructionType instructionType;//指令类型
    private llvmBasicBlock basicBlock;//所属基本块

    public llvmInstruction(llvmType type, llvmInstructionType instructionType,int numOp) {
        super(type,numOp);
        this.instructionType = instructionType;
    }

    public String llvmOutput(){
        return null;
    }
    public void setAllName(llvmFuncCnt funcCnt, String funcName){

    }
    public llvmInstructionType getInstructionType(){
        return this.instructionType;
    }
}
