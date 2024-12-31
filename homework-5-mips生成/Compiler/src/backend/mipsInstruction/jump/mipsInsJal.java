package backend.mipsInstruction.jump;

import backend.mipsInstruction.mipsInstruction;

public class mipsInsJal extends mipsInstruction {
    public String label;
    public mipsInsJal(String label){
        super("jal");
        this.label = label;
    }

    public String mipsOutput(){
        StringBuilder sb = new StringBuilder();
        sb.append(this.insName+" ");
        sb.append(this.label+"\n");
        return sb.toString();
    }
}
