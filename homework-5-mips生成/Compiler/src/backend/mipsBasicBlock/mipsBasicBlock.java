package backend.mipsBasicBlock;

import backend.mipsFunction.mipsFunc;
import backend.mipsInstruction.mipsInstruction;
import backend.mipsOutput;
import backend.symbol.mipsSymbolTable;

import java.util.ArrayList;

public class mipsBasicBlock implements mipsOutput {
    public mipsFunc function;
    public ArrayList<mipsInstruction> instructions;
    public mipsSymbolTable table;

    public mipsBasicBlock(mipsFunc func){
        this.function = func;
        this.instructions = new ArrayList<>();
        this.table = function.table;
    }

    public void addInstructions(ArrayList<mipsInstruction> ins){
        if(ins!=null){
            for(mipsInstruction item:ins){
                this.instructions.add(item);
            }
        }
    }
    public void addAIns(mipsInstruction ins){
        if(ins!=null){
            this.instructions.add(ins);
        }
    }

    @Override
    public String mipsOutput() {
        StringBuilder sb = new StringBuilder();
        for(mipsInstruction ins:instructions){
            if(ins!=null){
                sb.append(ins.mipsOutput());
            }
        }
        return sb.toString();
    }
}
