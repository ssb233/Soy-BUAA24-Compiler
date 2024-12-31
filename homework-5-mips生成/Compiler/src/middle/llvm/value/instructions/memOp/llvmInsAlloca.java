package middle.llvm.value.instructions.memOp;

import middle.llvm.llvmValue;
import middle.llvm.type.llvmArrayType;
import middle.llvm.type.llvmPointerType;
import middle.llvm.type.llvmType;
import middle.llvm.value.function.llvmFuncCnt;
import middle.llvm.value.instructions.llvmInstruction;
import middle.llvm.value.instructions.llvmInstructionType;

import java.util.ArrayList;

public class llvmInsAlloca extends llvmInstruction {
    private llvmType valueType;//分配的值的类型,
    private llvmValue value;//操作的值
    private int length;//如果是数组，看长度
    //valueType为指针类型或者int类型
    public llvmInsAlloca(llvmType valueType,llvmValue value) {//传入分配内存的值的类型,pointer, i8/i32
        //指令自身是指针类型，分配的值类型，操作数为0
        //result = alloca valueType;
        super(new llvmPointerType(valueType),llvmInstructionType.alloca,0);
        this.valueType = valueType;
        this.value = value;//操作的对象，也就是左边的那个寄存器
        this.value.setValueType(valueType);
        this.length = 0;
        //%x = alloca ...
    }
    //下面的valueTYpe为arrayType（i8/i32)
    public llvmInsAlloca(llvmType valueType,llvmValue value, int length){//这里是i32/i8
        //result = alloca [length * valueTYpe]
        super(new llvmPointerType(valueType),llvmInstructionType.alloca,0);
        this.valueType = valueType;
        this.value = value;//操作的对象，也就是左边的那个寄存器
        this.value.setValueType(valueType);
        this.length = length;
    }
    public void setAllName(llvmFuncCnt funcCnt, String funcName){
        this.value.setName("%"+String.valueOf(funcCnt.getCnt()));
    }

    public llvmValue getResult(){
        return this.value;//左操作数，也就是结果
    }
    public String llvmOutput(){
        StringBuilder sb = new StringBuilder();
        sb.append(this.value.getName());
        sb.append(" = alloca ");
        if(length!=0){//alloca [3 x i32]
            sb.append("["+String.valueOf(length)+" x ");
            if(this.valueType instanceof llvmArrayType){
                llvmArrayType arrayType = (llvmArrayType) this.valueType;
                sb.append(arrayType.getElementType().llvmOutput());
            }
            sb.append("]");
        }else{
            sb.append(this.valueType.llvmOutput());
        }
        sb.append("\n");
        return sb.toString();
    }
    public llvmType getSelfAllocaValueType(){
        return this.valueType;
    }
    public int getLength(){
        return this.length;
    }

}
