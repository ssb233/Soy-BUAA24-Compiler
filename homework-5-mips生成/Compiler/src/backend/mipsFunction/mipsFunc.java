package backend.mipsFunction;

import backend.mipsBasicBlock.mipsBasicBlock;
import backend.mipsInstruction.jump.mipsInsJr;
import backend.mipsInstruction.mipsInsLi;
import backend.mipsInstruction.mipsInsSyscall;
import backend.mipsModule;
import backend.mipsOutput;
import backend.symbol.mipsSymbolTable;

import java.util.ArrayList;

public class mipsFunc implements mipsOutput {
    public String name;
    public boolean isMainFunc;
    public ArrayList<mipsBasicBlock> blocks = new ArrayList<>();
    public mipsModule module;
    public mipsSymbolTable table;

    public mipsFunc(mipsModule module){
        this.module = module;
    }
    public mipsFunc(mipsModule module,boolean isMainFunc, String name, mipsSymbolTable table){
        this.module = module;
        this.isMainFunc = isMainFunc;
        this.name = name;
        this.table = table;
    }

    public void addABasicBlock(mipsBasicBlock basicBlock){
        if(basicBlock!=null){
            this.blocks.add(basicBlock);
        }
    }
    public void addBasicBlocks(ArrayList<mipsBasicBlock> blocks){
        if(blocks!=null){
            for(mipsBasicBlock item:blocks){
                this.blocks.add(item);
            }
        }
    }

    @Override
    public String mipsOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append("#----------"+this.name+"函数开始--------\n");
        sb.append(this.name+":\n");
        for(mipsBasicBlock block:this.blocks){
            sb.append(block.mipsOutput());
        }
//        if(this.isMainFunc){
//            /* 当main函数时，需要结束程序 */
//            /* 将10装入$v0即$2 */
//            mipsInsLi li = new mipsInsLi(2,10);
//            sb.append(li.mipsOutput());
//            mipsInsSyscall syscall = new mipsInsSyscall();
//            sb.append(syscall.mipsOutput());
//        }else{
//            mipsInsJr jr = new mipsInsJr(31);
//            sb.append(jr);
//        }
        sb.append("#*************"+this.name+"函数结束************\n");
        return sb.toString();

    }
}
