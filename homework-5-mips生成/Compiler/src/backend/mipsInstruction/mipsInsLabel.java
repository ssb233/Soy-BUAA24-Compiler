package backend.mipsInstruction;

import backend.RegisterNameTable;

public class mipsInsLabel extends mipsInstruction{
    public String name;
    public mipsInsLabel(String name,String funcName){
        super(name);
    }

    public String mipsOutput(){
        StringBuilder sb = new StringBuilder();
        sb.append(this.insName+":\n");
        return sb.toString();
    }
}
