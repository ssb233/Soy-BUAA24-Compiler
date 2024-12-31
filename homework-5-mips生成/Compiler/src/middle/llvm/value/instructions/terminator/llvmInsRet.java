package middle.llvm.value.instructions.terminator;

import middle.llvm.llvmValue;
import middle.llvm.type.llvmType;
import middle.llvm.type.llvmVoidType;
import middle.llvm.value.instructions.llvmInstruction;
import middle.llvm.value.instructions.llvmInstructionType;

public class llvmInsRet extends llvmInstruction {
    private llvmType retType;//返回类型,I32/I8
    private llvmValue retValue;//返回值
    //ret ty value;ret void;
    public llvmInsRet(llvmType retType, llvmValue retValue){//这里指令自身的值使用返回值的值
        super(retValue.getValueType(), llvmInstructionType.ret,1);
        this.retType = retType;
        this.retValue = retValue;
    }
    public llvmInsRet(llvmType retType){//返回空
        super(llvmVoidType.getVoidType(),llvmInstructionType.ret,0);
        this.retType = retType;
    }

    public String llvmOutput(){
        StringBuilder sb = new StringBuilder();
        sb.append("ret ");
        if(retValue!=null){
            sb.append(retType.llvmOutput()+" ");
            if(retValue.isConst){
                sb.append(String.valueOf(retValue.getConstNum()));
            }else{
                sb.append(retValue.getName());
            }
        }else{
            sb.append(retType.llvmOutput());
        }
        sb.append("\n");
        return sb.toString();
    }
    public boolean isVoidType(){
        if(this.retType instanceof  llvmVoidType){
            return true;
        }
        return false;
    }
    public llvmValue getRetValue(){
        return this.retValue;
    }
}
