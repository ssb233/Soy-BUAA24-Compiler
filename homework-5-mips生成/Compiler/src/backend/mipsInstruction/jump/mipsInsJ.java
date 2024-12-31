package backend.mipsInstruction.jump;

import backend.RegisterNameTable;
import backend.mipsInstruction.mipsInstruction;

public class mipsInsJ extends mipsInstruction {
    public String label;
    public mipsInsJ(String label){
        super("j");
        this.label = label;
    }
    public String mipsOutput(){
        StringBuilder sb = new StringBuilder();
        sb.append(this.insName+" ");
        sb.append(this.label+"\n");
        return sb.toString();
    }
}
