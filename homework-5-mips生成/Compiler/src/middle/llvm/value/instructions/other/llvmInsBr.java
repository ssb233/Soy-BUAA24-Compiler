package middle.llvm.value.instructions.other;

import middle.llvm.llvmValue;
import middle.llvm.type.llvmIntegerType;
import middle.llvm.value.instructions.llvmInstruction;
import middle.llvm.value.instructions.llvmInstructionType;
import middle.llvm.value.instructions.llvmLabel;

public class llvmInsBr extends llvmInstruction {
    private llvmValue value;
    private llvmLabel label1;
    private llvmLabel label2;
    private llvmLabel dst;
    //br label dst;
    //br i1 %value, label %label1, label %label2
    public llvmInsBr(llvmValue value,llvmLabel label1, llvmLabel label2){
        super(llvmIntegerType.getI1(), llvmInstructionType.br,1);
        this.value = value;
        this.label1 = label1;
        this.label2 = label2;
        this.dst = null;
    }
    public llvmInsBr(llvmLabel dst){
        super(llvmIntegerType.getI1(), llvmInstructionType.br,0);
        this.dst = dst;
    }
    public String llvmOutput(){
        StringBuilder sb = new StringBuilder();
        if(this.label1!=null){
            sb.append("br "+this.getValueType().llvmOutput()+" ");
            if(this.value.isConst){
                sb.append(String.valueOf(this.value.getConstNum()));
            }else{
                sb.append(this.value.getName());
            }
            sb.append(", label "+label1.getName()+", label "+label2.getName());
        }else{
            sb.append("br label "+this.dst.getName());
        }
        sb.append("\n");
        return sb.toString();
    }
    public llvmValue getValue(){
        return this.value;
    }
    public llvmLabel getDst(){
        return this.dst;
    }
    public llvmLabel getLabel1(){
        return this.label1;
    }
    public llvmLabel getLabel2(){
        return this.label2;
    }
}
