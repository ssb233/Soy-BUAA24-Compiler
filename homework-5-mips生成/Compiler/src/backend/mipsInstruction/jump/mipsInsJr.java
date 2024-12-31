package backend.mipsInstruction.jump;

import backend.RegisterNameTable;
import backend.mipsInstruction.mipsInstruction;

public class mipsInsJr extends mipsInstruction {
    public int retReg;//跳转的寄存器
    public mipsInsJr(int retReg){
        super("jr");
        this.retReg = retReg;
    }
    public String mipsOutput(){
        StringBuilder sb = new StringBuilder();
        sb.append(this.insName+" ");
        sb.append(RegisterNameTable.getName(retReg) +"\n");
        return sb.toString();
    }
}
