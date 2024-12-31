package backend.mipsInstruction;

import backend.RegisterNameTable;

//li: load immediate加载立即数，最多32位
//li $8, 1000
public class mipsInsLi extends mipsInstruction{
    public int regNum;//寄存器编号
    public int immediateNum;//立即数
    public mipsInsLi(int regNum, int immediateNum){
        super("li");
        this.regNum = regNum;
        this.immediateNum = immediateNum;
    }

    public String mipsOutput(){
        StringBuilder sb = new StringBuilder();
        sb.append(this.insName+" ");
        sb.append(RegisterNameTable.getName(regNum) + ", ");
        if (immediateNum >= 0) {
            sb.append("0x" + Integer.toHexString(this.immediateNum) + "\n");
        } else {
            sb.append(this.immediateNum + "\n");
        }
        return sb.toString();
    }

}
