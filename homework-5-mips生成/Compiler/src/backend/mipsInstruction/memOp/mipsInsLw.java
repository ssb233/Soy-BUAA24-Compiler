package backend.mipsInstruction.memOp;

import backend.RegisterNameTable;
import backend.mipsInstruction.mipsInstruction;
/**
 * Mips lw : Load Word 加载字
 * R[rt] = Mem[GPR[rs] + sign_ext(offset)]
 */
//lw $target, offset($base)
public class mipsInsLw extends mipsInstruction {
    public int target;//目标寄存器
    public int base;
    public int offset;
    public mipsInsLw(int target,int base,int offset){
        super("lw");
        this.target = target;
        this.base = base;
        this.offset = offset;
    }
    public String mipsOutput(){
        StringBuilder sb = new StringBuilder();
        sb.append(this.insName+" ");
        sb.append(RegisterNameTable.getName(target)+", ");
        sb.append(String.valueOf(this.offset)+"("+RegisterNameTable.getName(base)+")\n");
        return sb.toString();
    }
}
