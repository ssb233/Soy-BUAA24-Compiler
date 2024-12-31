package middle.llvm.value.instructions.binary;

import middle.llvm.llvmValue;
import middle.llvm.type.llvmIntegerType;
import middle.llvm.type.llvmType;
import middle.llvm.value.function.llvmFuncCnt;
import middle.llvm.value.instructions.llvmInstruction;
import middle.llvm.value.instructions.llvmInstructionType;

public class llvmInsBinary extends llvmInstruction {
    private llvmValue first;
    private llvmValue second;
    private llvmValue result;
    private llvmInstructionType type;//二元操作类型
    public llvmInsBinary(llvmValue first,llvmValue second,llvmValue result,llvmInstructionType type){//默认类型，I32
        super(llvmIntegerType.getI32(),type,2);
        this.first = first;
        this.second = second;
        this.result = result;
        this.type = type;
    }
    public llvmInsBinary(llvmValue first, llvmValue second, llvmValue result, llvmInstructionType type, llvmType elementType){//指定类型
        super(elementType,type,2);
        this.first = first;
        this.second = second;
        this.result = result;
        this.type = type;
    }
    public void setAllName(llvmFuncCnt funcCnt, String funcName){
        this.result.setName("%"+String.valueOf(funcCnt.getCnt()));
    }
    //<result> = add <ty> <op1>, <op2> +
    //<result> = sub <ty> <op1>, <op2> -
    //<result> = mul <ty> <op1>, <op2> *,  <result> = shl <ty> <op1>, <op2>左移
    //<result> = sdiv <ty> <op1>, <op2>有符号除法, div /
    //<result> = srem <ty> <op1>, <op2>, mod %

    //ne , eq都使用i32
    public String llvmOutput(){
        StringBuilder sb = new StringBuilder();
        sb.append(result.getName()+" = ");
        if(type.toString().equals(llvmInstructionType.add.toString())){
            sb.append("add");
        }else if(type.toString().equals(llvmInstructionType.sub.toString())){
            sb.append("sub");
        }else if(type.toString().equals(llvmInstructionType.mul.toString())){
            sb.append("mul");
        }else if(type.toString().equals(llvmInstructionType.sdiv.toString())){
            sb.append("sdiv");
        }else if(type.toString().equals(llvmInstructionType.srem.toString())){
            sb.append("srem");
        }else if(type.toString().equals(llvmInstructionType.eq.toString())){
            sb.append("icmp eq");
        }else if(type.toString().equals(llvmInstructionType.ne.toString())){
            sb.append("icmp ne");
        }else if(type.toString().equals(llvmInstructionType.sgt.toString())){
            sb.append("icmp sgt");
        }else if(type.toString().equals(llvmInstructionType.sge.toString())){
            sb.append("icmp sge");
        }else if(type.toString().equals(llvmInstructionType.slt.toString())){
            sb.append("icmp slt");
        }else if(type.toString().equals(llvmInstructionType.sle.toString())){
            sb.append("icmp sle");
        }
        sb.append(" i32 ");
//        if(first.isConst){
//            sb.append(first.getConstNum());
//        }else sb.append(first.getName());
        sb.append(first.outputConstOrName());
        sb.append(" ,");
//        if(second.isConst){
//            sb.append(second.getConstNum());
//        }else sb.append(second.getName());
        sb.append(second.outputConstOrName());
        sb.append("\n");
        return sb.toString();
    }

    public llvmValue getFirst(){
        return this.first;
    }
    public llvmValue getSecond(){
        return this.second;
    }
    public llvmValue getResult(){
        return this.result;
    }
    public llvmInstructionType getType(){
        return this.type;
    }

}
