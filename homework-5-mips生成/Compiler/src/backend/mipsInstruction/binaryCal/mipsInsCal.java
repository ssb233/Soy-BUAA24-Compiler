package backend.mipsInstruction.binaryCal;

import backend.RegisterNameTable;
import backend.mipsInstruction.mipsInstruction;
//这里为两个寄存器计算，结果返回到第三个寄存器，
//calType $reg1, $reg2, $reg3,  reg1 = reg2 cal reg3
public class mipsInsCal extends mipsInstruction {
    public int result;
    public int first;
    public int second;
    public String type;//add，sub, mul, div, mod
    public mipsInsCal(String type){
        super(type);
        this.type = type;
    }
    public mipsInsCal(int result, int first, int second, String type){
        super(type);
        this.type = type;
        this.result = result;
        this.first = first;
        this.second = second;
    }
    public String parseType(int type){
        if(type==1){
            return "add";
        }else if(type==2){
            return "sub";
        }else if(type==3){
            return "mul";
        }else if(type==4){
            return "div";
        }
        return null;
    }
    public String mipsOutput(){
        StringBuilder sb = new StringBuilder();
        sb.append(this.insName+" ");
        sb.append(RegisterNameTable.getName(this.result)+", ");
        sb.append(RegisterNameTable.getName(this.first)+", ");
        sb.append(RegisterNameTable.getName(this.second)+"\n");
        return sb.toString();
    }
}
