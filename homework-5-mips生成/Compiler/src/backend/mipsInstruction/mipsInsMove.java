package backend.mipsInstruction;

import backend.RegisterNameTable;

public class mipsInsMove extends mipsInstruction{
    public int left;
    public int right;

    //move $left, $right    left = right
    public mipsInsMove(int left,int right){
        super("move");
        this.left = left;
        this.right = right;
    }
    public String mipsOutput(){
        StringBuilder sb = new StringBuilder();
        sb.append(this.insName+" ");
        sb.append(RegisterNameTable.getName(left)+", ");
        sb.append(RegisterNameTable.getName(right)+"\n");
        return sb.toString();
    }
}
