package backend.mipsInstruction;

import backend.RegisterNameTable;

public class mipsInsAddi extends mipsInstruction{
    public int dst;
    public int src;
    public int imm;
    public mipsInsAddi(int dst,int src,int imm){
        super("addiu");
        this.dst =dst;
        this.src = src;
        this.imm =imm;
    }
    public String mipsOutput(){
        StringBuilder sb = new StringBuilder();
        sb.append(this.insName+" ");
        sb.append(RegisterNameTable.getName(dst) + ", ");
        sb.append(RegisterNameTable.getName(src) + ", ");
        sb.append(String.valueOf(imm) + "\n");
        return sb.toString();
    }
}
