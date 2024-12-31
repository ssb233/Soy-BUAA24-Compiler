package middle.llvm.value.instructions.other;

import middle.llvm.llvmValue;
import middle.llvm.type.llvmIntegerType;
import middle.llvm.value.function.llvmFuncCnt;
import middle.llvm.value.instructions.llvmInstruction;
import middle.llvm.value.instructions.llvmInstructionType;

public class llvmInsTrunc extends llvmInstruction {
    private llvmValue sourceValue;
    private llvmValue dstValue;
    public llvmInsTrunc(llvmValue sourceValue,llvmValue dstValue){
        super(llvmIntegerType.getI8(), llvmInstructionType.trunc,1);
        this.sourceValue = sourceValue;
        this.dstValue = dstValue;
        this.dstValue.setValueType(llvmIntegerType.getI8());
        if(sourceValue.isConst){
            this.sourceValue.setConst(true);
            this.sourceValue.setConstNum(sourceValue.getConstNum());
        }
    }

    public void setAllName(llvmFuncCnt funcCnt, String funcName){
        this.dstValue.setName("%"+String.valueOf(funcCnt.getCnt()));
    }
    public String llvmOutput(){
        StringBuilder sb = new StringBuilder();
        sb.append(dstValue.getName());
        sb.append(" = trunc i32 ");
//        if(sourceValue.isConst){//有可能传一个常数
//            sb.append(String.valueOf(sourceValue.getConstNum()));
//        }else{
//            sb.append(sourceValue.getName());
//        }
        sb.append(sourceValue.outputConstOrName());
        sb.append(" to i8\n");
        return sb.toString();
    }
    public llvmValue getSourceValue(){
        return this.sourceValue;
    }
    public llvmValue getDstValue(){
        return this.dstValue;
    }
}
