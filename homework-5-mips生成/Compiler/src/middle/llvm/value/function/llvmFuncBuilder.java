package middle.llvm.value.function;

import frontend.GlobalParm;
import frontend.parser.FuncDef.FuncDef;
import frontend.parser.FuncDef.FuncFParam;
import frontend.parser.FuncDef.FuncFParams;
import frontend.parser.MainFuncDef;
import frontend.parser.Terminal.Ident;
import middle.llvm.llvmModule;
import middle.llvm.llvmValue;
import middle.llvm.type.*;
import middle.llvm.value.basicblock.llvmBasicBlock;
import middle.llvm.value.basicblock.llvmBasicBlockBuilder;
import middle.llvm.value.instructions.llvmLabel;
import middle.llvm.value.instructions.terminator.llvmInsRet;
import middle.symbol.*;

import java.util.ArrayList;

public class llvmFuncBuilder {
    private SymbolTable symbolTable;//当前函数的符号表
    private FuncDef funcDef;
    private llvmModule module;
    private MainFuncDef mainFuncDef;//区分main函数和其它函数
    private llvmFuncCnt funcCnt;//函数变量名计数器

    public llvmFuncBuilder(SymbolTable symbolTable, FuncDef funcDef,llvmModule module){//用于一般函数定义
        this.module = module;
        this.mainFuncDef = null;
        this.symbolTable = symbolTable;
        this.funcDef = funcDef;
        this.funcCnt = new llvmFuncCnt();
    }

    public llvmFuncBuilder(SymbolTable symbolTable, MainFuncDef mainFuncDef,llvmModule module){//用于main函数
        this.module = module;
        this.funcDef = null;
        this.symbolTable = symbolTable;
        this.mainFuncDef = mainFuncDef;
        this.funcCnt = new llvmFuncCnt();
    }

    public llvmFunc llvmFuncDefBuilder(){//解析定义的函数
        //先构建函数的返回类型,只有funcDef需要考虑返回类型
        llvmType retType;
        if(this.funcDef.getFuncType().getToken().getTokenTypeName().equals("INTTK")){
            retType = llvmIntegerType.getI32();
        }else if(this.funcDef.getFuncType().getToken().getTokenTypeName().equals("CHARTK")){
            retType = llvmIntegerType.getI8();
        }else{//一定是void类型
            retType = llvmVoidType.getVoidType();
        }

        //构建参数列表
        ArrayList<llvmParam> params = new ArrayList<>();//用于函数的参数
        ArrayList<llvmType> paramsType = new ArrayList<>();//参数类型列表，用于构建函数类型
        FuncFParams funcFParams = this.funcDef.getFuncFParams();
        ArrayList<Symbol> paramsSymbol= null;
        if(funcFParams!=null){
            SymbolTable symbolTable1 = GlobalParm.getSymbolTable(1);//拿到全局符号表
            SymbolFunc symbolFunc = (SymbolFunc) SymbolTable.searchSymbol(this.funcDef.getName(),symbolTable1);
            paramsSymbol = symbolFunc.getParamList();//拿到参数列表的符号列表
            int site = 0;
            for(Symbol symbol:paramsSymbol){
                llvmType type = getParamType(symbol);
                paramsType.add(type);
                llvmParam param = new llvmParam(type,site);
                symbol.setLLVMValue(param);
                //param就该存那个result，那个值，那个寄存器
                params.add(param);
                site++;
            }
        }

        //函数的类型
        llvmFuncType funcType = new llvmFuncType(retType,paramsType);

        llvmFunc func = new llvmFunc(funcType,params,this.funcDef.getName(),this.module,this.funcCnt);

        llvmBasicBlock firstBlock = new llvmBasicBlock();
        llvmLabel firstLabel = new llvmLabel();
        firstBlock.addInstruction(firstLabel);
        func.addBasicBlock(firstBlock);
//        this.funcCnt.getCnt();//这里进入函数块前要加一个

        //基本块
        llvmBasicBlockBuilder blockBuilder = new llvmBasicBlockBuilder(this.symbolTable,this.funcCnt,this.funcDef.getBlock(),null,null,paramsSymbol,retType);
        ArrayList<llvmBasicBlock> blocks = blockBuilder.getBasicBlocks();
        for(llvmBasicBlock item: blocks){
            func.addBasicBlock(item);
        }
        //有的void函数末尾没有ret
        llvmBasicBlock retVoidBlock = new llvmBasicBlock();
        llvmInsRet retVoid = new llvmInsRet(llvmVoidType.getVoidType());
        retVoidBlock.addInstruction(retVoid);
        func.addBasicBlock(retVoidBlock);

        return func;
    }
    public llvmFunc llvmMainFuncBuilder(){//解析main函数
        //返回类型为I32
        llvmType retType = llvmIntegerType.getI32();
        //参数列表为空
        ArrayList<llvmType> paramsType = new ArrayList<>();

        llvmFuncType funcType = new llvmFuncType(retType,paramsType);//函数类型
        llvmFunc func = new llvmFunc(funcType,"main",this.module,this.funcCnt);
        this.funcCnt.getCnt();//这里进入函数块前要加一个
        //基本块
        llvmBasicBlockBuilder blockBuilder = new llvmBasicBlockBuilder(this.symbolTable,this.funcCnt,this.mainFuncDef.getBlock(),null,null, null,retType);
        ArrayList<llvmBasicBlock> blocks = blockBuilder.getBasicBlocks();
        for(llvmBasicBlock item: blocks){
            func.addBasicBlock(item);
        }
        return func;
    }

    private llvmType getParamType(Symbol symbol){
        SymbolVar symbolVar = (SymbolVar) symbol;
        int num = symbolVar.parseSymbolType();
        switch (num){
            case 1://int
                return llvmIntegerType.getI32();
            case 2://char
                return llvmIntegerType.getI8();
            case 3://intArr
                return new llvmArrayType(llvmIntegerType.getI32());
            case 4://charArr
                return new llvmArrayType(llvmIntegerType.getI8());
        }
        return null;
    }
    private void  addSymbolName(Symbol symbol){
        symbol.cntNum = this.funcCnt.getCnt();//分配几号临时变量%i
    }
}
