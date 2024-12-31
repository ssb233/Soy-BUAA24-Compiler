package backend.mipsInstruction;

import backend.RegisterNameTable;

public class mipsInsMacr extends mipsInstruction{
    int reg;
    public mipsInsMacr(int reg){
        super("putchar");
        this.reg =reg;
    }
    public String mipsOutput(){
        StringBuilder sb= new StringBuilder();
        sb.append(this.insName+"("+ RegisterNameTable.getName(reg)+")\n");
        return sb.toString();
    }
}
