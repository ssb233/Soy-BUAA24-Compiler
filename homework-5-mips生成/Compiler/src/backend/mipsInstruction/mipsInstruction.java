package backend.mipsInstruction;

import backend.mipsOutput;

public class mipsInstruction implements mipsOutput {
    public String insName;//指令名称


    public mipsInstruction(String insName){
        this.insName = insName;
    }

    @Override
    public String mipsOutput() {
        return null;
    }
}
