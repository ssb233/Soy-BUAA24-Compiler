package middle.llvm;

import frontend.parser.CompUnit;
import frontend.parser.Decl.Decl;
import frontend.parser.FuncDef.FuncDef;
import frontend.parser.MainFuncDef;
import middle.llvm.value.function.llvmFunc;
import middle.llvm.value.function.llvmFuncBuilder;
import middle.llvm.value.globalVariable.llvmGlobalVariable;
import middle.llvm.value.globalVariable.llvmGlobalVariableBuilder;
import middle.symbol.SymbolTable;

import java.util.ArrayList;

public class llvmBuilder {
    private CompUnit compUnit;//拿到前端的编译单元，进行llvm的构建
    private llvmModule module;
    public llvmBuilder(CompUnit compUnit){
        this.compUnit = compUnit;
        this.module = new llvmModule();
    }

    public llvmModule buildLlvmModule(){
        //先解析全局变量Decl
        if(this.compUnit.getDecls()!=null){
            for(Decl decl: this.compUnit.getDecls()){
                llvmGlobalVariableBuilder globalVariableBuilder = new llvmGlobalVariableBuilder(decl);
                ArrayList<llvmGlobalVariable> globalVariables = globalVariableBuilder.buildGlobalVariable();
                for(llvmGlobalVariable item:globalVariables){
                    if(item!=null){
                        this.module.addllvmGlobalVariable(item);
                    }
                }
            }
        }

        //然后解析所有的函数
        if(this.compUnit.getFuncDefs()!=null){
            for(FuncDef funcDef:this.compUnit.getFuncDefs()){
                SymbolTable symbolTable = funcDef.getCurrentSymbolTable();
                llvmFuncBuilder funcBuilder = new llvmFuncBuilder(symbolTable,funcDef,this.module);
                llvmFunc func = funcBuilder.llvmFuncDefBuilder();
                func.removeIns();//ret后的指令需要删除掉一部分
                this.module.addllvmFunc(func);
            }
        }

        //最后解析Main函数
        MainFuncDef mainFuncDef = this.compUnit.getMainFuncDef();
        SymbolTable symbolTable = mainFuncDef.getSymbolTable();
        llvmFuncBuilder funcBuilder = new llvmFuncBuilder(symbolTable,mainFuncDef,this.module);
        llvmFunc func = funcBuilder.llvmMainFuncBuilder();
        func.removeIns();//ret后的指令需要删除掉一部分
        this.module.addllvmFunc(func);



        return this.module;
    }
}
