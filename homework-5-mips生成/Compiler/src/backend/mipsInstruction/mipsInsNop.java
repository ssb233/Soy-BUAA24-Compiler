package backend.mipsInstruction;

import backend.RegisterNameTable;

public class mipsInsNop extends mipsInstruction{
    public mipsInsNop(){
        super("nop");
    }
    public String mipsOutput(){
        StringBuilder sb = new StringBuilder();
        sb.append(this.insName+"\n");

        return sb.toString();
    }
}
