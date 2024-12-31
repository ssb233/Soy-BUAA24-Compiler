package middle.llvm.value.instructions.memOp;

import middle.llvm.llvmValue;
import middle.llvm.type.llvmPointerType;
import middle.llvm.type.llvmType;
import middle.llvm.value.instructions.llvmInstruction;
import middle.llvm.value.instructions.llvmInstructionType;

public class llvmInsStore extends llvmInstruction {
    private llvmType valueType;//存元素的类型
    private llvmValue value;//存的值
    private int num;//村的值
    private llvmValue pointer;//目标地址
    public boolean isFromGep = false;
    //store <ty> <value> , ptr <pointer>, store i32 2, i32* %3
    public llvmInsStore(llvmType valueType, llvmValue value, llvmValue pointer){
        super(new llvmPointerType(valueType), llvmInstructionType.store,2);
        this.valueType = valueType;
        this.value = value;
        this.pointer = pointer;
    }
    public llvmInsStore(llvmType valueType, int num, llvmValue pointer){
        super(new llvmPointerType(valueType), llvmInstructionType.store,2);
        this.valueType = valueType;
        this.num = num;
        this.pointer = pointer;
    }
    public String llvmOutput(){
        StringBuilder sb = new StringBuilder();
        sb.append("store ");
        sb.append(valueType.llvmOutput()+" ");
        if(this.value!=null){
            if(this.value.isConst){
                sb.append(String.valueOf(this.value.getConstNum()));
            }else{
                sb.append(value.getName());
            }

        }else{
            sb.append(String.valueOf(num));
        }
        sb.append(", "+valueType.llvmOutput()+"* "+pointer.getName());
        sb.append("\n");
        return sb.toString();
    }
    public llvmValue getValue(){
        return this.value;
    }
    public llvmValue getPointer(){
        return this.pointer;
    }
    public int getNum(){
        return this.num;
    }
}
