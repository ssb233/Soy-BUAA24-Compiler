package backend.mipsInstruction.memOp;

import backend.RegisterNameTable;
import backend.mipsInstruction.mipsInstruction;

public class mipsInsMfhi extends mipsInstruction {
    public int result;
    public mipsInsMfhi(int result){
        super("mfhi");
        this.result = result;
    }
    public String mipsOutput(){
        StringBuilder sb = new StringBuilder();
        sb.append(this.insName+" ");
        sb.append(RegisterNameTable.getName(result)+"\n");
        return sb.toString();
    }
}
