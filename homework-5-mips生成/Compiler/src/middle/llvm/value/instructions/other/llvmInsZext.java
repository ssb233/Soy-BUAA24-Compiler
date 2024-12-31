package middle.llvm.value.instructions.other;

import middle.llvm.llvmValue;
import middle.llvm.type.llvmIntegerType;
import middle.llvm.value.function.llvmFuncCnt;
import middle.llvm.value.instructions.llvmInstruction;
import middle.llvm.value.instructions.llvmInstructionType;

public class llvmInsZext extends llvmInstruction {
    private llvmValue result;
    private llvmValue source;
    public llvmInsZext(llvmValue result,llvmValue source){
        super(llvmIntegerType.getI32(), llvmInstructionType.zext,1);
        this.result = result;
        this.source = source;
        this.result.setValueType(llvmIntegerType.getI32());
    }
    public void setAllName(llvmFuncCnt funcCnt, String funcName){
        this.result.setName("%"+String.valueOf(funcCnt.getCnt()));
    }
    public String llvmOutput(){
        StringBuilder sb = new StringBuilder();
        sb.append(result.getName());
        sb.append(" = zext ");
        if(source!=null){
            sb.append(source.getValueType().llvmOutput());
            sb.append(" ");
            sb.append(source.getName());
        }else{
            System.out.println("Zext instruction src is null!");
        }

        sb.append(" to i32\n");
        return sb.toString();
    }
    public llvmValue getResult(){
        return this.result;
    }
    public llvmValue getSource(){
        return this.source;
    }
}
