package backend.mipsFunction;

import backend.mipsBasicBlock.mipsBasicBlock;
import backend.mipsBasicBlock.mipsBasicBlockBuilder;
import backend.mipsInstruction.mipsInsMove;
import backend.mipsInstruction.mipsInstruction;
import backend.mipsModule;
import backend.mipsRegister;
import backend.symbol.mipsSymbol;
import backend.symbol.mipsSymbolTable;
import middle.llvm.type.llvmArrayType;
import middle.llvm.type.llvmIntegerType;
import middle.llvm.value.basicblock.llvmBasicBlock;
import middle.llvm.value.function.llvmFunc;
import middle.llvm.value.function.llvmParam;
import middle.llvm.value.instructions.llvmInstruction;

import java.util.ArrayList;
import java.util.HashMap;

public class mipsFuncBuilder {
    public llvmFunc func;
    public mipsModule fatherModule;
    public HashMap<String, mipsSymbol> globalVariableMap;//存的是全局变量的名字和mips符号的对应关系

    public mipsSymbolTable symbolTable;//每个函数一个符号表
    public ArrayList<mipsInstruction> mipsInstructions;//这个函数的指令列表
    public mipsBasicBlock moveFromAreq;

    public mipsFuncBuilder(llvmFunc func,mipsModule module,HashMap<String,mipsSymbol> hashMap){
        this.func = func;
        this.fatherModule = module;
        this.globalVariableMap = hashMap;
        this.mipsInstructions = new ArrayList<>();//初始化指令列表
        addGlobal_ParamToSymbolTable(hashMap);
    }

    public void addGlobal_ParamToSymbolTable(HashMap<String,mipsSymbol> hashMap){
        mipsRegister register = new mipsRegister();//寄存器表
        this.symbolTable = new mipsSymbolTable(register);
        /* 将全局变量加入符号表，同时他们不在寄存器中，因此不需要改动寄存器表 */
        for(String name:globalVariableMap.keySet()){
            this.symbolTable.addSymbol(name,globalVariableMap.get(name));
        }
        /* 将函数参数（如果有）加入符号表，并将在寄存器中的写入寄存器表 */
        ArrayList<llvmParam> params = this.func.getParams();
        int length = params.size();
        for(int i=0;i<length;i++){
            llvmParam targetParam = null;
            for(llvmParam param:params){
                if(param.getRank() == i){
                    targetParam = param;
                    break;
                }
            }
            String paramName = targetParam.getName();
            mipsSymbol symbol = null;
            if(i<4){//0~3，可以放在$a0~$a3上面, 8~15, $t0~$t7临时寄存器
                symbol = new mipsSymbol(paramName,30,true,i+8,false);
                register.addSymbol(8+i,symbol);
                mipsInsMove move = new mipsInsMove(i+8,i+4);
                this.mipsInstructions.add(move);
            }else{//在内存中，多出的参数
                boolean isArr = true;
                if(targetParam.getValueType() instanceof llvmIntegerType){
                    isArr = false;
                }
                symbol = new mipsSymbol(paramName,30,false,true,(i-4)*4,false, isArr);
                this.symbolTable.addOffset(4);
            }
            if(targetParam.getValueType() instanceof llvmArrayType){

            }
            this.symbolTable.addSymbol(paramName,symbol);
        }
    }

    public mipsFunc mipsFuncBuilder(){
        mipsFunc funcMips = null;
        if(this.func.getName().equals("main")){
            //main
            funcMips = new mipsFunc(fatherModule,true,this.func.getName(),this.symbolTable);
        }else{//自定义函数
            funcMips = new mipsFunc(fatherModule,false,this.func.getName(),this.symbolTable);
        }

        //首先算上处理参数的指令
        mipsBasicBlock fromArgs = new mipsBasicBlock(funcMips);
        fromArgs.addInstructions(this.mipsInstructions);
        funcMips.addABasicBlock(fromArgs);
        ArrayList<llvmInstruction> instructions = this.func.getInstrutions();
        llvmBasicBlock full = new llvmBasicBlock();
        for(llvmInstruction item:instructions){
            full.addInstruction(item);
        }

        mipsBasicBlockBuilder builder = new mipsBasicBlockBuilder(funcMips,full);
        funcMips.addABasicBlock(builder.genMipsBasicBlock());
        System.out.println("    basicBlock is correct!");
        return funcMips;
    }
}
