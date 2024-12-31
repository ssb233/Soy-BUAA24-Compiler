package backend;

import backend.mipsFunction.mipsFunc;
import backend.mipsInstruction.Asciiz;
import backend.mipsInstruction.jump.mipsInsJ;
import backend.mipsInstruction.mipsInsLi;
import backend.mipsInstruction.mipsInsNop;
import backend.mipsInstruction.mipsInstruction;

import java.util.ArrayList;

public class mipsModule implements mipsOutput{
    public ArrayList<mipsInstruction> globalIns;//全局变量的相关指令
    public ArrayList<mipsFunc> functions;//所有函数
    public ArrayList<Asciiz> asciizs;//全局的字符串常量
    public mipsInsLi li;
    public mipsInsJ jMain;
    public mipsInsNop nop;
    public mipsModule(){
        this.globalIns = new ArrayList<>();
        this.functions = new ArrayList<>();
        this.asciizs = new ArrayList<>();
        this.li = new mipsInsLi(30,0x10040000);
        this.jMain = new mipsInsJ("main");
        this.nop = new mipsInsNop();
    }


    public void addInsToGlobalIns(mipsInstruction ins){
        this.globalIns.add(ins);
    }
    public void addFunction(mipsFunc func){
        this.functions.add(func);
    }
    public void addAsciiz(Asciiz asciiz){
        this.asciizs.add(asciiz);
    }

    @Override
    public String mipsOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append("#常量\n.data\n");
        for(Asciiz item:asciizs){
            sb.append(item.mipsOutput());
        }
        //.text
        sb.append("#text代码段\n.text\n");
        sb.append(li.mipsOutput());
        //全局变量写入内存
        for(mipsInstruction instruction:globalIns){
            sb.append(instruction.mipsOutput());
        }

        //跳转到main函数
        sb.append("#跳到main\n");
        sb.append("j main\n");
        sb.append(nop.mipsOutput());
        //导出main在内的函数
        for(mipsFunc func:functions){
            sb.append(func.mipsOutput());
        }
        return sb.toString();
    }
}
