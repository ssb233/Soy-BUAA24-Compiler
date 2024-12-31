package middle.llvm.value.instructions;

import middle.llvm.type.llvmLabelType;
import middle.llvm.type.llvmType;
import middle.llvm.value.function.llvmFuncCnt;

import java.util.ArrayList;

public class llvmLabel extends llvmInstruction{

    public llvmLabel(String name) {
        super(new llvmLabelType(), llvmInstructionType.Label, 0);
        this.setName(name);
    }
    public llvmLabel(){
        super(new llvmLabelType(), llvmInstructionType.Label, 0);
    }
    public void setAllName(llvmFuncCnt funcCnt,String funcName){
        this.setName(funcName+"_soybean_"+String.valueOf(funcCnt.getCnt()));
    }
    public String llvmOutput(){
        StringBuilder sb= new StringBuilder();
//        String str = this.getName().substring(1,this.getName().length());
        String str = this.getName();
        sb.append(str+":");
        sb.append("\n");
        return sb.toString();
    }

}
