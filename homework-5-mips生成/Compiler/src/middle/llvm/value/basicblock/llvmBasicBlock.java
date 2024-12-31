package middle.llvm.value.basicblock;

import middle.llvm.llvmValue;
import middle.llvm.value.function.llvmFuncCnt;
import middle.llvm.value.instructions.llvmInstruction;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class llvmBasicBlock extends llvmValue {
    private String name;//基本块的名字
    private ArrayList<llvmInstruction> instructions= new ArrayList<>();
    public llvmBasicBlock(){

    }


    public void addAllInstructions(ArrayList<llvmInstruction> llvmInstructions){
        if(llvmInstructions!=null){
            for(llvmInstruction item:llvmInstructions){
                this.instructions.add(item);
            }
        }
    }
    public void addInstruction(llvmInstruction instruction){
        if(instruction!=null){
            this.instructions.add(instruction);
        }
    }
    public String llvmOutput(){
        StringBuilder sb = new StringBuilder();
        for(llvmInstruction item:instructions){
            sb.append(item.llvmOutput());
        }
        return sb.toString();
    }
    public void setAllName(llvmFuncCnt funcCnt, String funcName){
        if(this.instructions!=null){
            for(llvmInstruction ins:this.instructions){
                ins.setAllName(funcCnt,funcName);
            }
        }
    }
    public ArrayList<llvmInstruction> getInstructions(){
        ArrayList<llvmInstruction> list = new ArrayList<>();
        if(this.instructions!=null){
            for(llvmInstruction item:this.instructions){
                list.add(item);
            }
        }
        return list;
    }
}
