package backend.mipsInstruction.compare;

import backend.RegisterNameTable;
import backend.mipsInstruction.mipsInstruction;

public class mipsInsCompare extends mipsInstruction {
    public int first;
    public int second;
    public String label;
    public mipsInsCompare(int first,int second,String label,String name){
        super(name);
        this.label = label;
        this.first = first;
        this.second = second;
    }
    public String mipsOutput(){
        StringBuilder sb = new StringBuilder();
        sb.append(this.insName+" ");
        sb.append(RegisterNameTable.getName(this.first)+", ");
        sb.append(RegisterNameTable.getName(this.second)+", ");
        sb.append(this.label+"\n");
        return sb.toString();
    }
}
