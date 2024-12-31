package middle.llvm.value.instructions;

import frontend.lexer.Token;
import frontend.parser.Decl.*;
import frontend.parser.Exp.*;
import frontend.parser.FuncDef.BlockItem;
import frontend.parser.FuncDef.FuncRParams;
import frontend.parser.Terminal.Ident;
import frontend.parser.Terminal.StringConst;
import middle.llvm.llvmValue;
import middle.llvm.type.llvmArrayType;
import middle.llvm.type.llvmIntegerType;
import middle.llvm.type.llvmType;
import middle.llvm.value.basicblock.llvmBasicBlock;
import middle.llvm.value.function.llvmFunc;
import middle.llvm.value.function.llvmFuncCnt;
import middle.llvm.value.instructions.binary.llvmInsBinary;
import middle.llvm.value.instructions.memOp.llvmInsAlloca;
import middle.llvm.value.instructions.memOp.llvmInsGetElement;
import middle.llvm.value.instructions.memOp.llvmInsLoad;
import middle.llvm.value.instructions.memOp.llvmInsStore;
import middle.llvm.value.instructions.other.llvmInsBr;
import middle.llvm.value.instructions.other.llvmInsCall;
import middle.llvm.value.instructions.other.llvmInsTrunc;
import middle.llvm.value.instructions.other.llvmInsZext;
import middle.llvm.value.instructions.terminator.llvmInsRet;
import middle.symbol.*;


import java.util.ArrayList;

public class llvmInstructionBuilder {
    private SymbolTable symbolTable;//指令的构建需要符号表
    private llvmFuncCnt funcCnt;//需要计数器为寄存器计数
    private BlockItem blockItem;
    private llvmBasicBlock basicBlock;//当前基本块
    private llvmInstructionType instructionType;
    private ArrayList<llvmInstruction> instructions;//最终返回的指令列表

    private llvmType retType;//最外层的函数的返回类型，需要继承过来
    private llvmLabel beginForLabel;
    private llvmLabel endForLabel;

    public llvmInstructionBuilder(SymbolTable symbolTable, llvmFuncCnt funcCnt, llvmBasicBlock basicBlock, llvmLabel beginForLabel,llvmLabel endForLabel,llvmType retType) {
        this.symbolTable = symbolTable;
        this.funcCnt = funcCnt;
        this.basicBlock = basicBlock;
        this.beginForLabel = beginForLabel;
        this.endForLabel = endForLabel;
        this.retType = retType;
        this.instructions = new ArrayList<>();
    }

    public llvmInstructionBuilder(SymbolTable symbolTable, llvmFuncCnt funcCnt, llvmBasicBlock basicBlock, BlockItem blockItem, llvmLabel beginForLabel,llvmLabel endForLabel,llvmType retType) {
        this(symbolTable, funcCnt, basicBlock, beginForLabel, endForLabel, retType);
        this.blockItem = blockItem;
//        this.instructions = new ArrayList<>();
    }

    //判断blockItem的类型
    /*
    4:constDecl
    5:varDecl
    6:stmtAssign: lval = exp
    7:stmtBreak: break/
    8:stmtContinue: continue/
    9:stmtReturn: return [exp]
    10:stmtGetint: getint()
    11:stmtGetChar: getchar()
    12: stmtExp;
    13：printf
    * */
    public ArrayList<llvmInstruction> getInstructions() {
        //根据传入的blockItem来决定不同的类别
        int typeCode = checkBlockItemType(this.blockItem);
        //这里的类别只可能是上述类别
        if (typeCode == 4) {//4:constDecl
            Decl decl = this.blockItem.getDecl();
            ConstDecl constDecl = decl.getConstDecl();
            getInsFromConstDecl(constDecl);
        } else if (typeCode == 5) {//5:varDecl
            VarDecl varDecl = this.blockItem.getDecl().getVarDecl();
            getInsFromVarDecl(varDecl);
        } else if (typeCode == 6) {//6:stmtAssign: lval = exp
            LVal lval = this.blockItem.getStmt().getlVal();
            Exp rightExp = this.blockItem.getStmt().getRightExp();
            getInsFromStmtAssign(lval,rightExp);
        } else if (typeCode == 7) {//7:stmtBreak: break/
            getInsFromBreak();
        } else if (typeCode == 8) {//8:stmtContinue: continue/
            getInsFromContinue();
        } else if (typeCode == 9) {//9:stmtReturn: return [exp]
            Exp retExp = this.blockItem.getStmt().getReturnExp();
            getInsFromReturn(retExp);
        } else if (typeCode == 10) {// 10:stmtGetint: getint()
            LVal lVal = this.blockItem.getStmt().getlVal();
            getInsFromGetInt(lVal);
        } else if (typeCode == 11) {//11:stmtGetChar: getchar()
            LVal lVal = this.blockItem.getStmt().getlVal();
            getInsFromGetChar(lVal);
        } else if (typeCode == 12) {//12: stmtExp;
            Exp exp = this.blockItem.getStmt().getExp();
            getInsFromExp(exp);
        } else if (typeCode == 13) {//13：printf
            StringConst stringConst = this.blockItem.getStmt().getStringConst();
            ArrayList<Exp> exps = this.blockItem.getStmt().getPrintExpList();
            getInsFromPrint(stringConst,exps);
        } else {
            System.out.println("error, typecode out of expected");
        }
        return this.instructions;
    }
    public ArrayList<llvmInstruction> getDirectInstructions(){
        return this.instructions;
    }

    //数值常量不用变指令，放到符号表里面查找就行，直接拿常量，但是数组需要指令
    private void getInsFromConstDecl(ConstDecl constDecl){//4:constDecl
        ArrayList<ConstDef> constDefs = constDecl.getConstDefList();
        for(ConstDef item:constDefs){//对于每一个constDef
            //先区分是数组还是单个值
            Ident ident = item.getIdent();
            Symbol symbol = SymbolTable.searchSymbol(ident.getToken().getTokenValue(),this.symbolTable);
            SymbolConst symbolConst = (SymbolConst) symbol;
            if(symbolConst.getType2()==1){//单个值
                //这里什么都不用干，对于常量的单个数值，只需要在用的时候拿常数就行
                //因为提前构建好了符号表，所以不用加入符号表了
                llvmValue value = new llvmValue(llvmIntegerType.getI32(),symbolConst.getInitVal());
                symbolConst.setLLVMValue(value);
            }else if(symbolConst.getType2()==2){//数组
//                if(symbolConst.cntNum == 0){
//                    symbolConst.cntNum = this.funcCnt.getCnt();
//                }
                llvmType valueType = null;
                llvmType elementType = null;
                if(symbolConst.symbolType.toString().equals("ConstIntArray")){
                    valueType = new llvmArrayType(llvmIntegerType.getI32());
                    elementType = llvmIntegerType.getI32();
                }else if(symbolConst.symbolType.toString().equals("ConstCharArray")){
                    valueType = new llvmArrayType(llvmIntegerType.getI8());
                    elementType = llvmIntegerType.getI8();
                }
                llvmValue  allocaResult= new llvmValue();
                llvmInsAlloca alloca = new llvmInsAlloca(valueType,allocaResult,symbolConst.getLength());
                //数组%result = alloca [length x i8/i32]
                //%address = get [length x i8/i32], [length x i8/i32]* %result, i32 0 , i32 0;
                llvmValue getEleResult = new llvmValue();
                llvmInsGetElement getElementptr = new llvmInsGetElement(valueType,getEleResult,allocaResult,symbolConst.getLength());
                this.instructions.add(alloca);
                this.instructions.add(getElementptr);
                symbolConst.setLLVMValue(getEleResult);
                //对于每个位置都要赋值，但是由于是数组，需要先调用getElement

                if(symbolConst.constInitVal!=null){
                    StringConst stringConst = symbolConst.constInitVal.getStringConst();
                    ArrayList<ConstExp> constExps = symbolConst.constInitVal.getConstExpArrayList();
                    if(stringConst!=null){
                        String str = stringConst.getString().substring(1,stringConst.getString().length()-1);
                        int site = 0;
                        for(char ch:str.toCharArray()){
                            llvmValue GEPresult = new llvmValue();//作为左侧操作数
                            llvmInsGetElement getElement = new llvmInsGetElement(elementType,GEPresult,getEleResult,site);
                            site++;
                            llvmInsStore store = new llvmInsStore(elementType,ch,GEPresult);
                            this.instructions.add(getElement);
                            this.instructions.add(store);
                        }
                        for(;site<symbolConst.getLength();site++){
                            llvmValue GEPresult = new llvmValue();//作为左侧操作数
                            llvmInsGetElement getElement = new llvmInsGetElement(elementType,GEPresult,getEleResult,site);
                            llvmInsStore store = new llvmInsStore(elementType,0,GEPresult);
                            this.instructions.add(getElement);
                            this.instructions.add(store);
                        }
                    }else{
                        int site = 0;
                        for(ConstExp it:constExps){//对于每个表达式
                            llvmValue expResult = getInsFromConstExp(it);//最后肯定是%result = balabala，然后传出来store ty %result , ty* %pointer
                            if(isIntegerI32(expResult.getValueType())&&isIntegerI8(elementType)){
                                llvmValue tmp = new llvmValue();
                                llvmInsTrunc trunc = new llvmInsTrunc(expResult,tmp);
                                this.instructions.add(trunc);
                                expResult = tmp;
                            }
                            llvmValue GEPresult = new llvmValue();//作为左侧操作数
                            llvmInsGetElement getElement = new llvmInsGetElement(elementType,GEPresult,getEleResult,site);
                            llvmInsStore store = new llvmInsStore(elementType,expResult,GEPresult);
                            this.instructions.add(getElement);
                            this.instructions.add(store);
                            site++;
                        }
                    }
                }

            }
        }
    }
    //变量定义指令
    private void getInsFromVarDecl(VarDecl varDecl){//5:varDecl
        ArrayList<VarDef> varDefs = varDecl.getVarDefList();
        for(VarDef item:varDefs){
            Ident ident = item.getIdent();
            Symbol symbol = SymbolTable.searchSymbol(ident.getToken().getTokenValue(),this.symbolTable);
            SymbolVar symbolVar = (SymbolVar) symbol;
            if(symbolVar.getType2()==1){//单个值
                //先alloca，再store
                llvmType valueType = null;
                if(symbolVar.symbolType.toString().equals("Int")){
                    valueType = llvmIntegerType.getI32();
                }else if(symbolVar.symbolType.toString().equals("Char")){
                    valueType = llvmIntegerType.getI8();
                }
//                if(symbolVar.cntNum==0){
//                    symbolVar.cntNum = this.funcCnt.getCnt();
//                }
                llvmValue allocaResult = new llvmValue();
                llvmInsAlloca alloca = new llvmInsAlloca(valueType,allocaResult);
                symbolVar.setLLVMValue(allocaResult);
                this.instructions.add(alloca);
                if(symbolVar.init!=null){//有初始值,但是是一个表达式，需要从表达式去解析
                    Exp exp = symbolVar.init.getExp();
                    llvmValue expResult = getInsFromExp(exp);//最后肯定是%result = balabala，然后传出来store ty %result , ty* %pointer
                    //exp一定是i32，需要看这里定义的是否为char
                    if(isIntegerI32(expResult.getValueType())&&isIntegerI8(valueType)){
                        llvmValue tmp = new llvmValue();
                        llvmInsTrunc trunc = new llvmInsTrunc(expResult,tmp);
                        this.instructions.add(trunc);
                        expResult = tmp;
                    }
                    llvmInsStore store = new llvmInsStore(valueType,expResult,allocaResult);
                    this.instructions.add(store);
                }
            }else if(symbolVar.getType2()==2){//数组
                //先alloc，然后每个元素再初始化
//                if(symbolVar.cntNum == 0){
//                    symbolVar.cntNum = this.funcCnt.getCnt();
//                }
                llvmType valueType = null;
                llvmType elementType = null;
                if(symbolVar.symbolType.toString().equals("IntArray")){
                    valueType = new llvmArrayType(llvmIntegerType.getI32());
                    elementType = llvmIntegerType.getI32();
                }else if(symbolVar.symbolType.toString().equals("CharArray")){
                    valueType = new llvmArrayType(llvmIntegerType.getI8());
                    elementType = llvmIntegerType.getI8();
                }
                llvmValue  allocaResult= new llvmValue();
                llvmInsAlloca alloca = new llvmInsAlloca(valueType,allocaResult,symbolVar.getLength());
                //数组%result = alloca [length x i8/i32]
                //%address = get [length x i8/i32], [length x i8/i32]* %result, i32 0 , i32 0;
                llvmValue getEleResult = new llvmValue();
                llvmInsGetElement getElementptr = new llvmInsGetElement(valueType,getEleResult,allocaResult,symbolVar.getLength());
                this.instructions.add(alloca);
                this.instructions.add(getElementptr);
                symbolVar.setLLVMValue(getEleResult);

//                symbolVar.setLLVMValue(allocaResult);

                //对于每个位置都要赋值，但是由于是数组，需要先调用getElement
                if(symbolVar.init!=null){//有初始化
                    StringConst stringConst = symbolVar.init.getStringConst();
                    ArrayList<Exp> exps = symbolVar.init.getExpArrayList();
                    if(stringConst!=null){//字符串初始化
                        String str = stringConst.getString().substring(1,stringConst.getString().length()-1);
                        int site = 0;
                        for(char ch:str.toCharArray()){
                            llvmValue GEPresult = new llvmValue();//作为左侧操作数
                            llvmInsGetElement getElement = new llvmInsGetElement(elementType,GEPresult,getEleResult,site);
                            site++;
                            llvmInsStore store = new llvmInsStore(elementType,ch,GEPresult);
                            this.instructions.add(getElement);
                            this.instructions.add(store);
                        }
                        for(;site<symbolVar.getLength();site++){
                            llvmValue GEPresult = new llvmValue();//作为左侧操作数
                            llvmInsGetElement getElement = new llvmInsGetElement(elementType,GEPresult,getEleResult,site);
                            llvmInsStore store = new llvmInsStore(elementType,0,GEPresult);
                            this.instructions.add(getElement);
                            this.instructions.add(store);
                        }
                    }else{
                        int site = 0;
                        for(Exp it:exps){//对于每个表达式
                            llvmValue expResult = getInsFromExp(it);//最后肯定是%result = balabala，然后传出来store ty %result , ty* %pointer
                            if(isIntegerI32(expResult.getValueType())&&isIntegerI8(elementType)){
                                llvmValue tmp = new llvmValue();
                                llvmInsTrunc trunc = new llvmInsTrunc(expResult,tmp);
                                this.instructions.add(trunc);
                                expResult = tmp;
                            }
                            llvmValue GEPresult = new llvmValue();//作为左侧操作数
                            llvmInsGetElement getElement = new llvmInsGetElement(elementType,GEPresult,getEleResult,site);
                            llvmInsStore store = new llvmInsStore(elementType,expResult,GEPresult);
                            this.instructions.add(getElement);
                            this.instructions.add(store);
                            site++;
                        }
                    }
                }
            }
        }
    }
    public void getInsFromStmtAssign(LVal lval, Exp rightExp){//6:stmtAssign: lval = exp
        //先取exp的值，再store
        llvmValue expResult = getInsFromExp(rightExp);
        //store ty %result, ty* pointer
        llvmValue lvalResult = getInsFromLval(lval);
        llvmType elementType = null;
        Symbol symbol = SymbolTable.llvmSearchSymbol(lval.getIdent().getToken().getTokenValue(),this.symbolTable);
        if(symbol.symbolType.toString().equals("Int")||symbol.symbolType.toString().equals("IntArray")){
            elementType = llvmIntegerType.getI32();
        }else if(symbol.symbolType.toString().equals("Char")||symbol.symbolType.toString().equals("CharArray")){
            elementType = llvmIntegerType.getI8();
        }
        if(isIntegerI8(elementType)&&isIntegerI32(expResult.getValueType())){//将exp的结果转化为i8再赋值
            llvmValue result = new llvmValue();
            llvmInsTrunc trunc = new llvmInsTrunc(expResult,result);
            this.instructions.add(trunc);
            expResult = result;
        }
        llvmInsStore store = new llvmInsStore(elementType,expResult,lvalResult);
        this.instructions.add(store);
    }
    private void getInsFromBreak(){//7:stmtBreak: break/
        llvmInsBr brToEnd = new llvmInsBr(this.endForLabel);
        this.instructions.add(brToEnd);
//        //跳转语句的出现意味着下一个一定得是新的基本块，因此给一个新标签
//        llvmLabel newLabel = new llvmLabel();
//        this.instructions.add(newLabel);
    }
    private void getInsFromContinue(){//8:stmtContinue: continue/
        llvmInsBr brToForStmt2 = new llvmInsBr(this.beginForLabel);
        this.instructions.add(brToForStmt2);
//        //跳转语句的出现意味着下一个一定得是新的基本块，因此给一个新标签
//        llvmLabel newLabel = new llvmLabel();
//        this.instructions.add(newLabel);
    }
    private void getInsFromReturn(Exp retExp){//9:stmtReturn: return [exp],exp可选，这里我们确保一定有返回值，否则不需要ret指令
        //return exp;需要先有exp的计算结果
        if(retExp!=null){
            llvmValue expResult = getInsFromExp(retExp);
            //exp返回的类型一定是i32,看ret是否要i8
            llvmValue dstResult = expResult;
            if(this.retType instanceof llvmIntegerType){
                llvmIntegerType type = (llvmIntegerType) this.retType;
                if(type.getName().equals("i8")){//需要转i8
                    dstResult = new llvmValue(llvmIntegerType.getI8());
                    llvmInsTrunc trunc = new llvmInsTrunc(expResult,dstResult);
                    this.instructions.add(trunc);
                }
            }
            llvmInsRet ret = new llvmInsRet(this.retType,dstResult);
            this.instructions.add(ret);
        }else{//return null
            llvmInsRet ret = new llvmInsRet(this.retType);
            this.instructions.add(ret);
        }

    }
    private void getInsFromGetInt(LVal lval){// 10:stmtGetint: lval = getint()
        //%3 = call i32 @getint()
        //    store i32 %3, i32* %1
        llvmValue callResult = new llvmValue();
        llvmInsCall call = new llvmInsCall(llvmIntegerType.getI32(),1,callResult);
        this.instructions.add(call);
        llvmValue lvalResult = getInsFromLval(lval);
        llvmInsStore store = new llvmInsStore(llvmIntegerType.getI32(),callResult,lvalResult);
        this.instructions.add(store);
    }
    private void getInsFromGetChar(LVal lval){//11:stmtGetChar: lval = getchar()
        //%3 = call i32 @getchar()
        //    store i32 %3, i32* %1
        llvmValue callResult = new llvmValue();
        llvmInsCall call = new llvmInsCall(llvmIntegerType.getI32(),2,callResult);
        this.instructions.add(call);
        //因为getchar返回I32，因此必须要有一次强转trunc
        llvmValue truncResult = new llvmValue(llvmIntegerType.getI8());
        llvmInsTrunc trunc = new llvmInsTrunc(callResult,truncResult);
        this.instructions.add(trunc);
        llvmValue lvalResult = getInsFromLval(lval);
        llvmInsStore store = new llvmInsStore(llvmIntegerType.getI8(),truncResult,lvalResult);
        lvalResult.setValueType(llvmIntegerType.getI8());
        this.instructions.add(store);
    }
    private llvmValue getInsFromConstExp(ConstExp constExp){
        AddExp addExp = constExp.getAddExp();
        llvmValue result =  getInsFromAddExp(addExp);
        return result;
    }
    private llvmValue getInsFromExp(Exp exp){//12: stmtExp;
        AddExp addExp = exp.getAddExp();
        llvmValue result =  getInsFromAddExp(addExp);
//        if(result!=null && (result.getValueType()==null||isIntegerI8(result.getValueType()))){//除开数组情况，其它都返回i32
//            if(result.getValueType()==null){
//                result.setValueType(llvmIntegerType.getI32());
//            }else if(isIntegerI8(result.getValueType())){
//                llvmValue newResult = new llvmValue();
//                llvmInsZext zext = new llvmInsZext(newResult,result);
//                this.instructions.add(zext);
//                return newResult;
//            }
//        }
        return result;
    }
    public llvmValue getInsFromAddExp(AddExp addExp){
        MulExp mulExpFirst = addExp.getMulExpFirst();
        ArrayList<MulExp> mulExps = addExp.getMulExpArrayList();
        ArrayList<Token> opTokens = addExp.getOpTokens();
        llvmValue result;
        if(mulExpFirst!=null){
            result = getInsFromMulExp(mulExpFirst);
            if(mulExps!=null){
                int len = mulExps.size();
                for(int i=0;i<len;i++){
                    llvmInstructionType type = getInsTypeFromBinary(opTokens.get(i));
                    MulExp second = mulExps.get(i);
                    llvmValue secondValue = getInsFromMulExp(second);
                    llvmValue firstValue = result;
                    result = new llvmValue(llvmIntegerType.getI32());
                    llvmInsBinary binaryAdd = new llvmInsBinary(firstValue,secondValue,result,type);
                    this.instructions.add(binaryAdd);
                }
                return result;
            }else{
                return result;
            }
        }
        return  null;//不可能到这
    }
    private llvmValue getInsFromMulExp(MulExp mulExp){
        UnaryExp unaryExpFirst = mulExp.getUnaryExpFirst();
        ArrayList<UnaryExp> unaryExps = mulExp.getUnaryExpArrayList();
        ArrayList<Token> opTokens = mulExp.getOpTokens();
        llvmValue result;
        if(unaryExpFirst!=null){
            result = getInsFromUnaryExp(unaryExpFirst);
            if(unaryExps!=null){
                int len = unaryExps.size();
                for(int i=0;i<len;i++){
                    llvmInstructionType type = getInsTypeFromBinary(opTokens.get(i));
                    UnaryExp second = unaryExps.get(i);
                    llvmValue secondValue = getInsFromUnaryExp(second);
                    llvmValue firstValue = result;
                    result = new llvmValue(llvmIntegerType.getI32());
                    llvmInsBinary binaryMul = new llvmInsBinary(firstValue,secondValue,result,type);
                    this.instructions.add(binaryMul);
                }
                return result;
            }else{
                return result;
            }
        }
        return  null;//不可能到这
    }
    private llvmValue getInsFromUnaryExp(UnaryExp unaryExp){
        PrimaryExp primaryExp = unaryExp.getPrimaryExp();
        //
        UnaryOp unaryOp = unaryExp.getUnaryOp();
        UnaryExp unaryExp1 = unaryExp.getUnaryExp();
        //
        Ident ident = unaryExp.getIdent();
        FuncRParams funcRParams = unaryExp.getFuncRparams();
        //
        if(primaryExp!=null){
            return getInsFromPrimaryExp(primaryExp);
        }else if(unaryOp!=null){
            if(unaryOp.getToken().getTokenTypeName().equals("PLUS")){
                return getInsFromUnaryExp(unaryExp1);
            }else if(unaryOp.getToken().getTokenTypeName().equals("MINU")){
                llvmValue result = getInsFromUnaryExp(unaryExp1);
                llvmValue zero = new llvmValue(llvmIntegerType.getI32(),0);
                zero.setConst(true);
                llvmValue retValue = new llvmValue(llvmIntegerType.getI32());
                llvmInsBinary binaryMinu = new llvmInsBinary(zero,result,retValue,llvmInstructionType.sub);
                this.instructions.add(binaryMinu);
                return retValue;
            }else if(unaryOp.getToken().getTokenTypeName().equals("NOT")){//用于条件表达式，返回也是I32
                //可以看作和0比较是否相等  exp==0，
                llvmValue result = getInsFromUnaryExp(unaryExp1);
                llvmValue zero = new llvmValue(llvmIntegerType.getI32(),0);
                zero.setConst(true);
                llvmValue retValue = new llvmValue(llvmIntegerType.getI1());
                llvmInsBinary binaryEq = new llvmInsBinary(zero,result,retValue,llvmInstructionType.eq);
                this.instructions.add(binaryEq);
                if(isIntegerI1(retValue.getValueType())){
                    llvmValue tmp = new llvmValue(llvmIntegerType.getI32());
                    llvmInsZext zext = new llvmInsZext(tmp,retValue);
                    this.instructions.add(zext);
                    retValue = tmp;
                }
                return retValue;
            }
        }else if(ident!=null){//函数调用，记得结果采用zext
            //call ty func(param)
            Symbol symbol = SymbolTable.searchSymbol(ident.getToken().getTokenValue(),this.symbolTable);
            SymbolFunc symbolFunc = (SymbolFunc) symbol;
            ArrayList<llvmValue> values = new ArrayList<>();
            ArrayList<Symbol> paramSymbol = symbolFunc.getParamList();
            if(funcRParams!=null){
                int i = 0;
                for(Exp item: funcRParams.getExpArrayList()){
                    llvmValue value = getInsFromExp(item);
                    //传参的时候要转类型，如果是i8,需要从i32转到i8,因为默认都是int, i32
                    Symbol symbolTmp = paramSymbol.get(i);
                    if(symbolTmp.symbolType.toString().equals("Char")&& isIntegerI32(value.getValueType())){
                        llvmValue result = new llvmValue();
                        llvmInsTrunc trunc = new llvmInsTrunc(value,result);
                        this.instructions.add(trunc);
                        value = result;
                    }
                    values.add(value);
                    i++;
                }
            }
            if(symbolFunc.symbolType.toString().equals("VoidFunc")){
                //call void func(param)
                llvmInsCall call = new llvmInsCall(symbolFunc.getStringName(), values);
                this.instructions.add(call);
            }else if(symbolFunc.symbolType.toString().equals("IntFunc")||symbolFunc.symbolType.toString().equals("CharFunc")){
                //result = call ty func(param)
                llvmValue result = new llvmValue();
                llvmType retType = null;
                if(symbolFunc.symbolType.toString().equals("IntFunc")){
                    retType = llvmIntegerType.getI32();
                }else if(symbolFunc.symbolType.toString().equals("CharFunc")){
                    retType = llvmIntegerType.getI8();
                }
                llvmInsCall call = new llvmInsCall(retType,symbolFunc.getStringName(),result,values);
                this.instructions.add(call);
                if(isIntegerI8(result.getValueType())){
                    llvmValue tmp = new llvmValue(llvmIntegerType.getI32());
                    llvmInsZext zext = new llvmInsZext(tmp,result);
                    this.instructions.add(zext);
                    result = tmp;
                }
                return result;
            }
        }
        return null;
    }
    private llvmValue getInsFromPrimaryExp(PrimaryExp primaryExp){
        PrimaryExpExtend primaryExpExtend = primaryExp.getPrimaryExpExtend();
        if(primaryExpExtend instanceof ExpLR){
            ExpLR expLR = (ExpLR) primaryExpExtend;
            Exp exp = expLR.getExp();
            if(exp!=null){
                return getInsFromExp(exp);
            }
            return null;
        }else if(primaryExpExtend instanceof LVal){
            LVal lVal = (LVal) primaryExpExtend;
            llvmValue result =  getInsFromLvalValue(lVal);//这里使用新的getLval，这里是直接获取值，而不是地址，而且是扩展以后的值
            return result;
        }else if(primaryExpExtend instanceof ConstNumber){
            ConstNumber constNumber = (ConstNumber) primaryExpExtend;
            //直接返回值就行
            llvmValue result = new llvmValue(llvmIntegerType.getI32(),constNumber.getValue());//直接作为具体值返回
            return result;
        }else if(primaryExpExtend instanceof  ConstChar){
            ConstChar constChar = (ConstChar) primaryExpExtend;
            //这里肯定需要扩展，但是由于语义保证值不会越界，那么他也可以看作是I32
            llvmValue result = new llvmValue(llvmIntegerType.getI32(),constChar.getValue());//直接作为具体值返回
            return result;
        }
        return  null;
    }
    private llvmValue getInsFromExpToChar(Exp exp){
        llvmValue result = getInsFromExp(exp);
        //在此之上截断
        llvmValue dst = new llvmValue();
        llvmInsTrunc trunc = new llvmInsTrunc(result,dst);
        this.instructions.add(trunc);
        return dst;
    }
    private llvmValue getInsFromLvalValue(LVal lVal){//exp里面的计算值，
        Ident ident = lVal.getIdent();
        Exp exp = lVal.getExp();
        llvmValue result = null;//返回的结果
        if(ident!=null){
            Symbol symbol = SymbolTable.llvmSearchSymbol(ident.getToken().getTokenValue(),this.symbolTable);
            if(symbol instanceof SymbolConst && exp==null){//单值常量直接查表
                SymbolConst symbolConst = (SymbolConst) symbol;
                result = symbolConst.value;//返回一个固定常量的value，不在乎设置的类型，显示的时候是具体数值常量就行
            }else{//变量单值，变量数组，常量数组，都可以使用getInsFromLval来获取地址
                //这里来拿值的有很多种情况，变量单值，数组（相当于返回它的地址），数组的元素
                //变量单值和数组元素可以用getInsFromLval来拿到地址
                //下面是数组的自身情况，不是数组元素，我们不做类型考虑
                if(exp==null && (symbol.symbolType.toString().equals("IntArray")||symbol.symbolType.toString().equals("CharArray"))){//不可能为常量数组，因为这里使用一定是作为参数
                    //%8 = getelementptr inbounds [6 x i32], [6 x i32]* @a, i32 0, i32 0,把i32* %8作为参数传进去，
                    //如果是局部的，直接拿symbol.value，因为数组的value我们处理过，为ty* 型，可以直接从site拿，而不是[length x ty]*型
                    if(symbol.tableId!=1){//局部的
                        return symbol.value;
                    }else{//全局的
                        llvmValue value = symbol.value;//这里的value是@glo
                        int len = 0;
                        if(symbol instanceof SymbolVar){
                            SymbolVar symbolVar = (SymbolVar) symbol;
                            len = symbolVar.getLength();
                        }
                        llvmValue getResult  = new llvmValue();
                        llvmInsGetElement getElement = new llvmInsGetElement(value.getValueType(),getResult,value,len);
                        this.instructions.add(getElement);
                        return getResult;
                    }
                }else{//单值变量，数组元素情况,
                    llvmValue address = getInsFromLval(lVal);//拿到值的地址，地址的valueType是元素类型，比如i32，i8
                    result = new llvmValue();//其类型会在下面的load指令里面赋值
                    llvmInsLoad load = new llvmInsLoad(address.getValueType(),result,address);
                    this.instructions.add(load);
                    //这里由于是exp的计算，需要转型到I32
                    if(isIntegerI8(result.getValueType())){
                        llvmValue tmp = new llvmValue();
                        llvmInsZext zext = new llvmInsZext(tmp,result);
                        this.instructions.add(zext);
                        result = tmp;
                    }
//                    if(address.getValueType() instanceof llvmIntegerType){
//                        llvmIntegerType item = (llvmIntegerType) address.getValueType();
//                        if(item.getName().equals("i8")){//需要扩展
//                            llvmValue preResult = result;
//                            preResult.setValueType(llvmIntegerType.getI8());
//                            result = new llvmValue();
//                            llvmInsZext zext = new llvmInsZext(result,preResult);
//                            this.instructions.add(zext);
//                        }
//                    }
                }

            }
        }
        return result;
    }
    private void getInsFromPrint(StringConst stringConst, ArrayList<Exp> printExpList){//12: stmtExp;
        String str = stringConst.getString().substring(1,stringConst.getString().length()-1);
        char[]chars = str.toCharArray();
        int len = chars.length;
        int cnt = 0;//记录迭代的exp位置
        ArrayList<llvmValue> values = new ArrayList<>();
        for(Exp exp:printExpList){
            values.add(getInsFromExp(exp));
        }
        for(int i=0;i<len;i++){
            char c = chars[i];
            llvmInsCall call;
            if(c=='%'&&i+1<len&&chars[i+1]=='c'){
                llvmValue result = values.get(cnt);
                cnt++;
                call = new llvmInsCall(4,result);
                i++;
            }else if(c=='%'&&i+1<len&&chars[i+1]=='d'){
                llvmValue result = values.get(cnt);
                cnt++;
                call = new llvmInsCall(3,result);
                i++;
            }else if(c=='\\'&&i+1<len&&chars[i+1]=='n'){
                llvmValue line = new llvmValue(llvmIntegerType.getI32(),'\n');
                call = new llvmInsCall(4,line);
                i++;
            }else{
                llvmValue line = new llvmValue(llvmIntegerType.getI32(),c);
                call = new llvmInsCall(4,line);
            }
            this.instructions.add(call);
        }
    }
    private llvmValue getInsFromLval(LVal lVal){//这个是用于lval = balabala的情况，返回的都是地址，比如单个值的地址，或者arr[site]的地址
        int isArr = lVal.getArrayOrVar();
        if(isArr==1){//单个值，局部全局，单个变量
            Ident ident = lVal.getIdent();
            Symbol symbol = SymbolTable.llvmSearchSymbol(ident.getToken().getTokenValue(),this.symbolTable);
            return symbol.value;//直接返回内存分配指令的结果
        }else if(isArr==2){//数组的某个元素，这里得区分局部还是全局数组
            //局部情况，符号表里面已经存了数组的ty*形式，因此可以使用get ty, ty* address, i32 site
            //全局情况，由于符号表里面存的是@arr，一样可以使用这种方式拿到a[site]的地址
            //全局情况，%1 = get [len x ty], [len x ty]* @glo, i32 0
            //%2 = get ty, ty* %1, i32 %site.
            Ident ident = lVal.getIdent();
            Symbol symbol = SymbolTable.llvmSearchSymbol(ident.getToken().getTokenValue(),this.symbolTable);
            //这里的symbol.value是数组的值，需要返回具体的元素,
            llvmValue target = null;
            llvmType elementType = null;
            if(symbol.symbolType.toString().equals("IntArray")||symbol.symbolType.toString().equals("ConstIntArray")){
                elementType = llvmIntegerType.getI32();
            }else if(symbol.symbolType.toString().equals("CharArray")||symbol.symbolType.toString().equals("ConstCharArray")){
                elementType = llvmIntegerType.getI8();
            }
            if(symbol.tableId==1){//全局数组
                llvmValue globalArrValue = new llvmValue();
                int len = 0;
                if(symbol instanceof SymbolVar){
                    SymbolVar symbolVar = (SymbolVar) symbol;
                    len = symbolVar.getLength();
                }else if(symbol instanceof  SymbolConst){
                    SymbolConst symbolConst = (SymbolConst) symbol;
                    len = symbolConst.getLength();
                }
                llvmInsGetElement getGlo = new llvmInsGetElement(new llvmArrayType(elementType),globalArrValue,symbol.value,len);
                this.instructions.add(getGlo);
                target = globalArrValue;
            }else{//局部数组
                target = symbol.value;//直接就是数组第一层的结果，然后再get具体位置的地址
            }
            llvmValue GEPresult = new llvmValue();
//            int site = lVal.getExp().getValue();
            llvmValue siteValue = getInsFromExp(lVal.getExp());
            llvmInsGetElement getElement = new llvmInsGetElement(elementType,GEPresult,target,siteValue);
            this.instructions.add(getElement);
            return GEPresult;
        }
        return null;
    }
    private void addALlInstructions(ArrayList<llvmInstruction> llvmInstructions){
        for(llvmInstruction item:llvmInstructions){
            this.instructions.add(item);
        }
    }
    private int checkBlockItemType(BlockItem item) {//这里复制BasicBlockBuilder的函数，解析这个blockitem
        if (item.getDecl() != null) {
            Decl decl = item.getDecl();
            if (decl.getConstDecl() != null) {
                return 4;
            } else if (decl.getVarDecl() != null) {
                return 5;
            }
        } else if (item.getStmt() != null) {
            return item.getStmt().parseStmtType();
        }
        return 0;
    }
    private llvmInstructionType getInsTypeFromBinary(Token token){
        //+-*/%
        String str = token.getTokenTypeName();
        if(str.equals("PLUS")){
            return llvmInstructionType.add;
        }else if(str.equals("MINU")){
            return llvmInstructionType.sub;
        }else if(str.equals("MULT")){
            return llvmInstructionType.mul;
        }else if(str.equals("DIV")){
            return llvmInstructionType.sdiv;
        }else if(str.equals("MOD")){
            return llvmInstructionType.srem;
        }
        return  null;
    }
    private boolean isIntegerI8(llvmType type){
        if(type instanceof  llvmIntegerType){
            llvmIntegerType integerType = (llvmIntegerType) type;
            if(integerType.getName().equals("i8")){
                return true;
            }
        }
        return false;
    }
    private boolean isIntegerI32(llvmType type){
        if(type instanceof  llvmIntegerType){
            llvmIntegerType integerType = (llvmIntegerType) type;
            if(integerType.getName().equals("i32")){
                return true;
            }
        }
        return false;
    }
    private boolean isIntegerI1(llvmType type){
        if(type instanceof  llvmIntegerType){
            llvmIntegerType integerType = (llvmIntegerType) type;
            if(integerType.getName().equals("i1")){
                return true;
            }
        }
        return false;
    }
    public void addAInstruction(llvmInstruction ins){
        this.instructions.add(ins);
    }

}
