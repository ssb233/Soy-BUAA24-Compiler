package middle.llvm.value.instructions.memOp;

import middle.llvm.llvmValue;
import middle.llvm.type.llvmArrayType;
import middle.llvm.type.llvmIntegerType;
import middle.llvm.type.llvmType;
import middle.llvm.value.function.llvmFuncCnt;
import middle.llvm.value.instructions.llvmInstruction;
import middle.llvm.value.instructions.llvmInstructionType;

public class llvmInsLoad extends llvmInstruction {
    private llvmType elementType;
    private llvmValue result;
    private llvmValue address;
    private int length;//如果是数组类型，就给个长度
    public boolean isFromGep = false;
    // %7 = load ty, ty* %6
    public llvmInsLoad(llvmType elementType,llvmValue result,llvmValue address){
        super(elementType, llvmInstructionType.load,1);
        this.elementType = elementType;
        this.result =result;
        this.address = address;
        this.result.setValueType(elementType);
    }
    public llvmInsLoad(llvmType elementType,llvmValue result,llvmValue address, int length){
        super(elementType, llvmInstructionType.load,1);
        this.elementType = elementType;
        this.result =result;
        this.address = address;
        this.length = length;
    }
    public void setAllName(llvmFuncCnt funcCnt, String funcName){
        this.result.setName("%"+String.valueOf(funcCnt.getCnt()));
    }

    public String llvmOutput(){
        StringBuilder sb = new StringBuilder();
        sb.append(result.getName());
        sb.append(" = load ");
        if(this.elementType instanceof llvmIntegerType){
            sb.append(this.elementType.llvmOutput());
        }else if(this.elementType instanceof llvmArrayType){
            llvmArrayType arrayType = (llvmArrayType) this.elementType;
            sb.append("["+String.valueOf(this.length)+" x "+arrayType.getElementType().llvmOutput()+"]");
        }else{//指针类型
            sb.append(this.elementType.llvmOutput());
        }
        sb.append(", ");
        if(this.elementType instanceof llvmIntegerType){
            sb.append(this.elementType.llvmOutput());
        }else if(this.elementType instanceof llvmArrayType){
            llvmArrayType arrayType = (llvmArrayType) this.elementType;
            sb.append("["+String.valueOf(this.length)+" x "+arrayType.getElementType().llvmOutput()+"]");
        }else{//指针类型
            sb.append(this.elementType.llvmOutput());
        }
        sb.append("* ");
        sb.append(address.getName());
        sb.append("\n");
        return sb.toString();
    }

    public llvmValue getResult(){
        return this.result;
    }

    public llvmValue getAddress(){
        return this.address;
    }
}
