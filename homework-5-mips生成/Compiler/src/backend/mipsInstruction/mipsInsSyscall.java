package backend.mipsInstruction;

import backend.RegisterNameTable;

public class mipsInsSyscall extends mipsInstruction{
    public mipsInsSyscall(){
        super("syscall");
    }
    public String mipsOutput(){
        StringBuilder sb = new StringBuilder();
        sb.append(this.insName+"\n");
        return sb.toString();
    }
}
