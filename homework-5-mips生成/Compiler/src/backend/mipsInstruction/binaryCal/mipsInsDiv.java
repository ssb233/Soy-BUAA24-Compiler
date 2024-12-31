package backend.mipsInstruction.binaryCal;

import backend.RegisterNameTable;

public class mipsInsDiv extends mipsInsCal{
    public mipsInsDiv(int result, int first, int second){
        super(result,first,second,"div");
    }
    public String mipsOutput(){
        StringBuilder sb = new StringBuilder();
        sb.append(this.insName+" ");
        if(this.result!=-1){
            sb.append(RegisterNameTable.getName(this.result)+", ");
        }

        sb.append(RegisterNameTable.getName(this.first)+", ");
        sb.append(RegisterNameTable.getName(this.second)+"\n");
        return sb.toString();
    }
}
