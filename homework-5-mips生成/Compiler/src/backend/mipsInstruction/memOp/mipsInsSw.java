package backend.mipsInstruction.memOp;

import backend.RegisterNameTable;
import backend.mipsInstruction.mipsInstruction;

//sw: save word
//sw $v1, 8($s0)
//$v1为原本值在的寄存器，$s0为内存地址基值，8为偏移
public class mipsInsSw extends mipsInstruction {
    public int srcReg;
    public int baseReg;
    public int offset;
    public mipsInsSw(int srcReg, int baseReg, int offset){
        super("sw");
        this.srcReg = srcReg;
        this.baseReg = baseReg;
        this.offset = offset;
    }

    public String mipsOutput(){
        StringBuilder sb = new StringBuilder();
        sb.append(this.insName+" ");
        sb.append(RegisterNameTable.getName(srcReg)+", ");
        sb.append(String.valueOf(offset)+"("+RegisterNameTable.getName(baseReg)+")\n");
        return sb.toString();
    }

}
