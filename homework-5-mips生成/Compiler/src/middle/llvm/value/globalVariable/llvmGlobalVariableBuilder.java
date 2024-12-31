package middle.llvm.value.globalVariable;

import frontend.GlobalParm;
import frontend.parser.Decl.*;
import frontend.parser.Terminal.Ident;
import middle.llvm.constant.llvmConstantArray;
import middle.llvm.constant.llvmConstantInt;
import middle.llvm.llvmValue;
import middle.llvm.type.llvmArrayType;
import middle.llvm.type.llvmIntegerType;
import middle.symbol.*;

import java.util.ArrayList;

public class llvmGlobalVariableBuilder {
    Decl decl;
    public llvmGlobalVariableBuilder(Decl decl){
        this.decl = decl;
    }

    public ArrayList<llvmGlobalVariable> buildGlobalVariable(){
        ArrayList<llvmGlobalVariable> globalVariables = new ArrayList<>();

        if(decl.isConst()){
            ConstDecl constDecl = decl.getConstDecl();
            BType bType = constDecl.getbType();
            ArrayList<ConstDef> constDefArrayList = new ArrayList<>();
            constDefArrayList = constDecl.getConstDefList();
            for(ConstDef item:constDefArrayList){
                globalVariables.add(buildConstDef(item,bType));
            }
        }else if(decl.isVar()){
            VarDecl varDecl = decl.getVarDecl();
            BType bType = varDecl.getbType();
            ArrayList<VarDef> varDefArrayList = new ArrayList<>();
            varDefArrayList = varDecl.getVarDefList();
            for(VarDef item:varDefArrayList){
                globalVariables.add(buildVarDef(item,bType));
            }
        }
        return globalVariables;
    }

    private llvmGlobalVariable buildConstDef(ConstDef constDef,BType bType){
        Ident ident = constDef.getIdent();
        SymbolTable symbolTable = GlobalParm.getSymbolTable(1);
        Symbol symbol = SymbolTable.searchSymbol(ident.getToken().getTokenValue(),symbolTable);
        llvmValue value = new llvmValue("@"+ident.getToken().getTokenValue());

        int num = parseType(bType,symbol.symbolType);
        SymbolConst symbolConst = (SymbolConst) symbol;
        llvmGlobalVariable globalVariable = new llvmGlobalVariable(true);//const
        globalVariable.setName(ident.getToken().getTokenValue());
        if(num == 1){//constInt
            globalVariable.setValueType(llvmIntegerType.getI32());
            llvmConstantInt constantInt = new llvmConstantInt(llvmIntegerType.getI32(),symbolConst.getInitVal());
            globalVariable.setInitVal(constantInt);
            value.setValueType(llvmIntegerType.getI32());
            value.setConst(true);
            value.setConstNum(symbolConst.getInitVal());
        }else if(num ==2){//constIntArray
            globalVariable.setValueType(new llvmArrayType(llvmIntegerType.getI32()));
            llvmConstantArray constantArray = new llvmConstantArray(new llvmArrayType(llvmIntegerType.getI32()));
            constantArray.setIntegers(symbolConst.getIntegers());
            globalVariable.setInitVal(constantArray);
            value.setValueType(new llvmArrayType(llvmIntegerType.getI32()));
        }else if(num==3){//constChar
            globalVariable.setValueType(llvmIntegerType.getI8());
            llvmConstantInt constantInt = new llvmConstantInt(llvmIntegerType.getI8(),symbolConst.getInitVal());
            globalVariable.setInitVal(constantInt);
            value.setValueType(llvmIntegerType.getI8());
            value.setConst(true);
            value.setConstNum(symbolConst.getInitVal());
        }else if(num==4){//constCharArray
            globalVariable.setValueType(new llvmArrayType(llvmIntegerType.getI8()));
            llvmConstantArray constantArray = new llvmConstantArray(new llvmArrayType(llvmIntegerType.getI8()));
            constantArray.setIntegers(symbolConst.getIntegers());
            globalVariable.setInitVal(constantArray);
            value.setValueType(new llvmArrayType(llvmIntegerType.getI8()));
        }
        symbol.setLLVMValue(value);
        return globalVariable;
    }
    private llvmGlobalVariable buildVarDef(VarDef varDef,BType bType){
        Ident ident = varDef.getIdent();
        SymbolTable symbolTable = GlobalParm.getSymbolTable(1);
        Symbol symbol = SymbolTable.searchSymbol(ident.getToken().getTokenValue(),symbolTable);
        llvmValue value = new llvmValue("@"+ident.getToken().getTokenValue());//作为地址返回，需要设置地址的值为元素类型


        int num = parseType(bType,symbol.symbolType);
        SymbolVar symbolVar = (SymbolVar) symbol;
        llvmGlobalVariable globalVariable = new llvmGlobalVariable(false);//var
        globalVariable.setName(ident.getToken().getTokenValue());
        if(num == 1){//Int
            globalVariable.setValueType(llvmIntegerType.getI32());
            llvmConstantInt constantInt = new llvmConstantInt(llvmIntegerType.getI32(),symbolVar.getInitVal());
            globalVariable.setInitVal(constantInt);
            value.setValueType(llvmIntegerType.getI32());
        }else if(num ==2){//IntArray
            globalVariable.setValueType(new llvmArrayType(llvmIntegerType.getI32()));
            llvmConstantArray constantArray = new llvmConstantArray(new llvmArrayType(llvmIntegerType.getI32()));
            constantArray.setIntegers(symbolVar.getIntegers());
            globalVariable.setInitVal(constantArray);
            value.setValueType(new llvmArrayType(llvmIntegerType.getI32()));
        }else if(num==3){//Char
            globalVariable.setValueType(llvmIntegerType.getI8());
            llvmConstantInt constantInt = new llvmConstantInt(llvmIntegerType.getI8(),symbolVar.getInitVal());
            globalVariable.setInitVal(constantInt);
            value.setValueType(llvmIntegerType.getI8());
        }else if(num==4){//CharArray
            globalVariable.setValueType(new llvmArrayType(llvmIntegerType.getI8()));
            llvmConstantArray constantArray = new llvmConstantArray(new llvmArrayType(llvmIntegerType.getI8()));
            constantArray.setIntegers(symbolVar.getIntegers());
            globalVariable.setInitVal(constantArray);
            value.setValueType(new llvmArrayType(llvmIntegerType.getI8()));
        }
        symbol.setLLVMValue(value);
        return globalVariable;
    }

    private int parseType(BType bType, SymbolType symbolType){
        if(bType.getToken().getTokenTypeName().equals("INTTK")){
            if(symbolType.toString().equals("ConstIntArray")||symbolType.toString().equals("IntArray")){
                return 2;
            }else if(symbolType.toString().equals("ConstInt")||symbolType.toString().equals("Int")){
                return 1;
            }
        }else if(bType.getToken().getTokenTypeName().equals("CHARTK")){
            if(symbolType.toString().equals("ConstCharArray")||symbolType.toString().equals("CharArray")){
                return 4;
            }else if(symbolType.toString().equals("ConstChar")||symbolType.toString().equals("Char")){
                return 3;
            }
        }
        return 0;
    }
}
