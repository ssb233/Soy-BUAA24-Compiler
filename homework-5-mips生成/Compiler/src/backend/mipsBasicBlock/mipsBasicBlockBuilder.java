package backend.mipsBasicBlock;

import backend.mipsFunction.mipsFunc;
import backend.mipsInstruction.*;
import backend.mipsInstruction.memOp.mipsInsLa;
import middle.llvm.llvmValue;
import middle.llvm.value.basicblock.llvmBasicBlock;
import middle.llvm.value.instructions.llvmInstruction;
import middle.llvm.value.instructions.other.llvmInsCall;

import java.util.ArrayList;

public class mipsBasicBlockBuilder {
    mipsFunc MipsFunc;
    llvmBasicBlock LLVMBlock;
    public mipsBasicBlockBuilder(mipsFunc func,llvmBasicBlock llvmBasicBlock){
        this.MipsFunc = func;
        this.LLVMBlock = llvmBasicBlock;
    }

    public mipsBasicBlock genMipsBasicBlock(){
        mipsBasicBlock block = new mipsBasicBlock(this.MipsFunc);
        ArrayList<llvmInstruction> LLVMIns = this.LLVMBlock.getInstructions();
        int length = LLVMIns.size();

        for(int i=0;i<length;i++){
            llvmInstruction IRins = LLVMIns.get(i);
            //如果为函数调用，而且为putch,多个字符合并输出
            if(IRins instanceof llvmInsCall){
                llvmInsCall callIns = (llvmInsCall) IRins;
                if(callIns.getLibFunc() == 4){//putch
                    StringBuilder stringBuilder = new StringBuilder();
                    llvmInstruction tmp = IRins;
                    boolean needBuild = false;
                    while(tmp instanceof llvmInsCall && ((llvmInsCall) tmp).getLibFunc()==4 && ((llvmInsCall)tmp).getPutParam().isConst){
                        llvmValue ch = ((llvmInsCall)tmp).getPutParam();
                        stringBuilder.append(Character.valueOf((char)ch.getConstNum()));
                        i++;
                        needBuild = true;
                        if(i>=length)break;
                        tmp = LLVMIns.get(i);
                    }
                    if(needBuild==true){
                        i--;
                        int count = AscCnt.getcnt();
                        Asciiz asciiz = new Asciiz(stringBuilder.toString(),count);
                        /* 需要li $v0, 4和la $a0, label，为了安全，将$a0挪到$v1，结束后再挪回来 */
                        this.MipsFunc.module.addAsciiz(asciiz);
                        ArrayList<mipsInstruction> outputIns = new ArrayList<>();
                        mipsInsMove move = new mipsInsMove(3, 4);
                        mipsInsLi li = new mipsInsLi(2,4);
                        mipsInsLa la = new mipsInsLa(4, asciiz.name);
                        mipsInsSyscall syscall = new mipsInsSyscall();
                        mipsInsMove move1 = new mipsInsMove(4, 3);
                        outputIns.add(move);
                        outputIns.add(li);
                        outputIns.add(la);
                        outputIns.add(syscall);
                        outputIns.add(move1);
                        block.addInstructions(outputIns);

                        System.out.println("     "+IRins.llvmOutput()+"is correct!");
                        continue;
                    }
                }
            }
            //到这都要正常处理指令
            mipsInstructionBuilder builder = new mipsInstructionBuilder(block,IRins);
            block.addInstructions(builder.genMipsInstruction());
            System.out.println("     "+IRins.llvmOutput()+"is correct!");
        }
        return block;
    }
}
