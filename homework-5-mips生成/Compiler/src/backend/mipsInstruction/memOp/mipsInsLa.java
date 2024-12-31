package backend.mipsInstruction.memOp;

import backend.RegisterNameTable;
import backend.mipsInstruction.mipsInstruction;

//la $reg, label
//把label的地址加载到$reg寄存器里面
public class mipsInsLa extends mipsInstruction {
    public int reg;
    public String label;
    public mipsInsLa(int reg, String label){
        super("la");
        this.reg = reg;
        this.label = label;
    }
    public String mipsOutput(){
        StringBuilder sb = new StringBuilder();
        sb.append(this.insName+" ");
        sb.append(RegisterNameTable.getName(reg)+", ");
        sb.append(this.label+"\n");
        return sb.toString();
    }
}
