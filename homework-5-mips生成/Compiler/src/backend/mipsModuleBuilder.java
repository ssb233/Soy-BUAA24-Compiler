package backend;

import backend.mipsFunction.mipsFuncBuilder;
import backend.mipsInstruction.memOp.mipsInsSw;
import backend.mipsInstruction.mipsInsLi;
import backend.symbol.mipsSymbol;
import middle.llvm.llvmModule;
import middle.llvm.type.llvmArrayType;
import middle.llvm.type.llvmIntegerType;
import middle.llvm.type.llvmType;
import middle.llvm.value.function.llvmFunc;
import middle.llvm.value.globalVariable.llvmGlobalVariable;

import java.util.ArrayList;
import java.util.HashMap;

public class mipsModuleBuilder {
    public llvmModule llvmModule;
    public mipsModuleBuilder(llvmModule llvmModule){
        this.llvmModule = llvmModule;
    }
    //构建mipsModule
    public mipsModule mipsModuleBuilder(){
        mipsModule mipsModule = new mipsModule();
        //加载全局变量
        HashMap<String, mipsSymbol> globalVariableMap = new HashMap<>();
        ArrayList<llvmGlobalVariable> globalVariables = this.llvmModule.globalVariableArrayList;

        int gpOffset = 0;
        //使用$24不断进行li 和 sw
        for(llvmGlobalVariable item:globalVariables){
            llvmType type = item.getValueType();
            int typeNum = parseGlobalVariableType(type);
            if(typeNum == 3||typeNum == 4){//单个值
                int initVal = item.getSingleInitVal();
                if(initVal!=0){//为0的变量不添加这些指令了
                    mipsInsLi li = new mipsInsLi(24,initVal);
                    mipsInsSw sw = new mipsInsSw(24,28,gpOffset);
                    mipsModule.addInsToGlobalIns(li);
                    mipsModule.addInsToGlobalIns(sw);
                }
                mipsSymbol symbol = new mipsSymbol('@'+ item.getName(),28,gpOffset);
                globalVariableMap.put(symbol.name,symbol);
                gpOffset+=4;
            }else if(typeNum==1|| typeNum==2){//数组值
                ArrayList<Integer> initVal = item.getArrInitVal();
                int len = initVal.size();
                mipsSymbol symbol = new mipsSymbol('@' +item.getName(),28,false,-1,true,gpOffset,false,false,len, true);
                globalVariableMap.put(symbol.name,symbol);
                for(Integer it:initVal){
                    if(it!=0){//只存入非0值
                        mipsInsLi li = new mipsInsLi(24,it);
                        mipsInsSw sw = new mipsInsSw(24,28,gpOffset);
                        mipsModule.addInsToGlobalIns(li);
                        mipsModule.addInsToGlobalIns(sw);
                    }
                    gpOffset+=4;
                }
            }
        }

        //生成函数
        ArrayList<llvmFunc> llvmFuncs = this.llvmModule.funcArrayList;
        int cnt = 0;
        for(llvmFunc func:llvmFuncs){
            mipsFuncBuilder funcBuilder = new mipsFuncBuilder(func,mipsModule,globalVariableMap);
            mipsModule.addFunction(funcBuilder.mipsFuncBuilder());
            System.out.println("func"+String.valueOf(cnt++)+"is correct!");
        }

        return mipsModule;
    }

    public int parseGlobalVariableType(llvmType type){
        if(type instanceof llvmArrayType){
            llvmArrayType tmp = (llvmArrayType) type;
            llvmIntegerType eleType = (llvmIntegerType) tmp.getElementType();
            if(eleType.getName().equals("i8")){
                return 1;
            }else if(eleType.getName().equals("i32")){
                return 2;
            }
        }else if(type instanceof llvmIntegerType){
            llvmIntegerType tmp = (llvmIntegerType) type;
            if(tmp.getName().equals("i8")){
                return 3;
            }else if(tmp.getName().equals("i32")){
                return 4;
            }
        }
        return 0;
    }
}
