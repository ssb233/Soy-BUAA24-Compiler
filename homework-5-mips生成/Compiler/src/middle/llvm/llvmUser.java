package middle.llvm;

import middle.llvm.type.llvmType;

import java.util.ArrayList;
import java.util.LinkedList;

//属于优化公共子表达式部分，暂行不管
//四元式(运算符, 操作数 1, 操作数 2, 结果)
//numOp描述运算符
//useArrayList存储使用的所有操作数
//%result = add i32 %a, %b
//%result作为计算结果，使用name存
//I32作为继承而来的llvmType
public class llvmUser extends llvmValue implements llvmOutput{
    private int numOp;//操作数数量
    private LinkedList<llvmUse> useArrayList;//user追溯其使用的所有的value

    public llvmUser(llvmType type){
        super(type);
    }
    public llvmUser(llvmType type,String name){
        super(type,name);
    }
    public llvmUser(llvmType type,int numOp){
        super(type);
        this.numOp = numOp;
    }
    public void setOperand(llvmValue value, int index){
        for(llvmUse use:this.useArrayList){
            if(use.getOprandRank() == index){
                use.getValue().removeUse(use);
                use.setValue(value);
                value.addUse(use);
                return;
            }
        }
        llvmUse newUse = new llvmUse(value,this,index);
        this.useArrayList.add(newUse);
    }

    public String llvmOutput(){
        return null;
    }
}
