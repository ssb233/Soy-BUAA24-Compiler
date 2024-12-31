package middle.llvm.value.instructions.terminator;

import middle.llvm.type.llvmLabelType;
import middle.llvm.value.instructions.llvmInstruction;
import middle.llvm.value.instructions.llvmInstructionType;
import middle.llvm.value.instructions.llvmLabel;

public class llvmInsGoto extends llvmInstruction {
    private llvmLabel beginFor;
    public llvmInsGoto(llvmLabel beginFor){
        super(new llvmLabelType(), llvmInstructionType.Goto,0);
        this.beginFor = beginFor;
    }
    public String llvmOutput(){
        StringBuilder sb = new StringBuilder();

        return sb.toString();
    }
}
