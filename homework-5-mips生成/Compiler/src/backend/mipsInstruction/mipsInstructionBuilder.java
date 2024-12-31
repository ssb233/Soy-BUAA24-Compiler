package backend.mipsInstruction;

import backend.mipsBasicBlock.mipsBasicBlock;
import backend.mipsInstruction.binaryCal.*;
import backend.mipsInstruction.compare.mipsInsBne;
import backend.mipsInstruction.jump.mipsInsJ;
import backend.mipsInstruction.jump.mipsInsJal;
import backend.mipsInstruction.jump.mipsInsJr;
import backend.mipsInstruction.memOp.mipsInsLw;
import backend.mipsInstruction.memOp.mipsInsMfhi;
import backend.mipsInstruction.memOp.mipsInsSw;
import backend.mipsRegister;
import backend.symbol.mipsSymbol;
import backend.symbol.mipsSymbolTable;
import middle.llvm.llvmValue;
import middle.llvm.type.llvmArrayType;
import middle.llvm.type.llvmPointerType;
import middle.llvm.type.llvmType;
import middle.llvm.value.instructions.binary.llvmInsBinary;
import middle.llvm.value.instructions.llvmInstruction;
import middle.llvm.value.instructions.llvmLabel;
import middle.llvm.value.instructions.memOp.llvmInsAlloca;
import middle.llvm.value.instructions.memOp.llvmInsGetElement;
import middle.llvm.value.instructions.memOp.llvmInsLoad;
import middle.llvm.value.instructions.memOp.llvmInsStore;
import middle.llvm.value.instructions.other.llvmInsBr;
import middle.llvm.value.instructions.other.llvmInsCall;
import middle.llvm.value.instructions.other.llvmInsTrunc;
import middle.llvm.value.instructions.other.llvmInsZext;
import middle.llvm.value.instructions.terminator.llvmInsRet;

import java.util.ArrayList;

public class mipsInstructionBuilder {
    public mipsBasicBlock basicBlock;//当前指令集所属基本块
    public llvmInstruction irIns;//需要解析的llvm指令
    public mipsSymbolTable symbolTable;//当前符号表，也就是基本块的符号表，也就是函数的符号表
    public mipsRegister register;//寄存器表，也就是基本块的寄存器表，也就是函数的寄存器表，当然基本块用不到寄存器表和符号表

    public mipsInstructionBuilder(mipsBasicBlock basicBlock,llvmInstruction irIns){
        this.basicBlock = basicBlock;
        this.irIns = irIns;
        this.symbolTable = basicBlock.function.table;
        this.register = this.symbolTable.register;
    }
    public ArrayList<mipsInstruction> genMipsInstruction(){
        //根据指令类型去生成mips指令
        //我们考虑所有类型为i32，不考虑i1和i8，统一当作i32处理
        if(irIns instanceof llvmInsBinary){
            return genMipsInsFromBinary();
        }else if(irIns instanceof llvmInsAlloca){
            return genMipsInsFromAlloca();
        }else if(irIns instanceof llvmInsLoad){
            return genMipsInsFromLoad();
        }else if(irIns instanceof llvmInsStore){
            return genMipsInsFromStore();
        }else if(irIns instanceof llvmInsBr){
            return genMipsInsFromBr();
        }else if(irIns instanceof llvmInsCall){
            return genMipsInsFromCall();
        }else if(irIns instanceof llvmInsRet){
            return genMipsInsFromRet();
        }else if(irIns instanceof llvmInsGetElement){
            return genMipsInsFromGetElement();
        }else if(irIns instanceof llvmLabel){
            return genMipsInsFromLabel();
        }else if(irIns instanceof llvmInsZext || irIns instanceof llvmInsTrunc){
            return genMipsInsFromZextTrunc();
        }else{
            System.out.println("error in builde ins!");
            return null;
        }
    }
    public ArrayList<mipsInstruction> genMipsInsFromBinary(){
        ArrayList<mipsInstruction> retList = new ArrayList<>();
        llvmInsBinary binary = (llvmInsBinary) irIns;

        //处理左操作数
        llvmValue firstValue = binary.getFirst();
        String firstName = firstValue.getName();
        mipsSymbol firstSymbol = null;
        int firstReg = -1;
        if(firstValue.isConst){
            firstSymbol = new mipsSymbol("temp",30,false,-1,false,-1,true,false);
            //找到一个临时寄存器，将常数装进去
            firstReg = this.register.getReg(true,firstSymbol,this.basicBlock);
            mipsInsLi li = new mipsInsLi(firstReg,firstValue.getConstNum());
            retList.add(li);
        }else{//变量
            firstReg = this.symbolTable.getRegIndex(firstName,this.basicBlock, false);
            firstSymbol = this.symbolTable.getSymbol(firstName);
        }

        //处理右操作数
        llvmValue secondValue = binary.getSecond();
        String secondName = secondValue.getName();
        mipsSymbol secondSymbol = null;
        int secondReg = -1;
        if(secondValue.isConst){
            secondSymbol = new mipsSymbol("temp",30,false,-1,false,-1,true,false);
            //找到一个临时寄存器，将常数装进去
            secondReg = this.register.getReg(true,secondSymbol,this.basicBlock);
            mipsInsLi li = new mipsInsLi(secondReg,secondValue.getConstNum());
            retList.add(li);
        }else{
            secondReg = this.symbolTable.getRegIndex(secondName,this.basicBlock,false);
            secondSymbol = this.symbolTable.getSymbol(secondName);
        }

        //处理结果
        String resultName = binary.getResult().getName();
        mipsSymbol resultSymbol = new mipsSymbol(resultName,30,false,-1,false,-1,true,false);
        this.symbolTable.addSymbol(resultName,resultSymbol);
        int resultReg = this.symbolTable.getRegIndex(resultName,this.basicBlock,false);

        if(binary.getInstructionType().toString().equals("add")){
            mipsInsAdd add = new mipsInsAdd(resultReg,firstReg,secondReg);
            retList.add(add);
        }else if(binary.getInstructionType().toString().equals("sub")){
            mipsInsSub sub = new mipsInsSub(resultReg,firstReg,secondReg);
            retList.add(sub);
        }else if(binary.getInstructionType().toString().equals("mul")){
            mipsInsMul mul = new mipsInsMul(resultReg,firstReg,secondReg);
            retList.add(mul);
        }else if(binary.getInstructionType().toString().equals("sdiv")){
            mipsInsDiv div = new mipsInsDiv(resultReg,firstReg,secondReg);
            retList.add(div);
        }else if(binary.getInstructionType().toString().equals("srem")){//%,mod
            mipsInsDiv div = new mipsInsDiv(-1,firstReg,secondReg);
            mipsInsMfhi mfhi = new mipsInsMfhi(resultReg);
            retList.add(div);
            retList.add(mfhi);
        }else if(binary.getInstructionType().toString().equals("eq")){//==
            mipsInsSeq seq = new mipsInsSeq(resultReg,firstReg,secondReg);
            retList.add(seq);
        }else if(binary.getInstructionType().toString().equals("ne")){//!=
            mipsInsSne sne = new mipsInsSne(resultReg,firstReg,secondReg);
            retList.add(sne);
        }else if(binary.getInstructionType().toString().equals("sgt")){//>
            mipsInsSgt sgt =new mipsInsSgt(resultReg,firstReg,secondReg);
            retList.add(sgt);
        }else if(binary.getInstructionType().toString().equals("sge")){//>=
            mipsInsSge sge = new mipsInsSge(resultReg,firstReg,secondReg);
            retList.add(sge);
        }else if(binary.getInstructionType().toString().equals("slt")){//<
            mipsInsSlt slt = new mipsInsSlt(resultReg,firstReg,secondReg);
            retList.add(slt);
        }else if(binary.getInstructionType().toString().equals("sle")){//<=
            mipsInsSle sle = new mipsInsSle(resultReg,firstReg,secondReg);
            retList.add(sle);
        }

        /* 将左右操作数标记为已使用，方便释放寄存器 */
        firstSymbol.isUsed = true;
        secondSymbol.isUsed = true;
        return retList;
    }
    public ArrayList<mipsInstruction> genMipsInsFromAlloca(){
        /* alloca是LLVM IR中的变量声明语句，其本意是申请内存空间
         * 在这里，我们为了提高性能，在alloca时仅将其加入符号表，暂时不为其分配寄存器和内存 */
        llvmInsAlloca alloca = (llvmInsAlloca) irIns;
        String name = alloca.getResult().getName();
        llvmType type = alloca.getSelfAllocaValueType();
        mipsSymbol symbol = null;
        boolean isArr = false;
        int len = 0;
        if(type instanceof llvmArrayType){
            isArr = true;
        }
        if(isArr==false){//单值
            symbol = new mipsSymbol(name,30);

        }else{
            symbol = new mipsSymbol(name,30);
            symbol.arrLength = alloca.getLength();
            symbol.isArr = true;
            //数组起始位置的偏移
            symbol.offset = this.symbolTable.Offset;
            symbol.haveRam = true;//标记已分配内存
            this.symbolTable.Offset = (this.symbolTable.Offset+4*symbol.arrLength);
        }
        this.symbolTable.addSymbol(name,symbol);
        return null;
    }
    public ArrayList<mipsInstruction> genMipsInsFromLoad(){
        /* IrLoad左侧的变量都是新临时变量，用于取用全局变量或局部变量 */
        /* 全局变量直接从lw从内存中加载 */
        /* 局部变量若位于寄存器中，则move */
        /* 局部变量若位于内存中，则lw */
        llvmInsLoad load = (llvmInsLoad) irIns;

        //处理左侧值
        String resultName = load.getResult().getName();
        mipsSymbol resultSymbol = new mipsSymbol(resultName,30,false,-1,false,-1,true,false);
        this.symbolTable.addSymbol(resultName,resultSymbol);

        //处理右侧值
        llvmValue address = load.getAddress();
        String addressName = address.getName();
        mipsSymbol addressSymbol = this.symbolTable.getSymbol(addressName);
        boolean isArr = addressSymbol.isArr;
        int length = addressSymbol.arrLength;
        int addressReg = -1;

        ArrayList<mipsInstruction> retList = new ArrayList<>();
        int resultReg = this.register.getReg(true,resultSymbol,this.basicBlock);
        resultSymbol.isInReg = true;
        resultSymbol.regIndex = resultReg;
        //获取右侧的值的寄存器
        addressReg = this.symbolTable.getRegIndex(addressName,this.basicBlock,true);
        //左边寄存器等于右侧寄存器，move指令
        mipsInsMove move = new mipsInsMove(resultReg,addressReg);
        retList.add(move);

        if(addressName.contains("@")){
            /* 将全局变量标记为不在寄存器中 */
            addressSymbol.isInReg = false;
        }

        if(retList!=null&&retList.size()>0){
            this.basicBlock.addInstructions(retList);
            retList = new ArrayList<>();
        }
        return retList;
    }
    public ArrayList<mipsInstruction> genMipsInsFromStore(){
        llvmInsStore store = (llvmInsStore) irIns;
        //store value to pointer
        llvmValue targetValue = store.getValue();
        llvmValue pointer = store.getPointer();
        if(targetValue == null){
            targetValue = new llvmValue("temp");
            targetValue.setConst(true);
            targetValue.setConstNum(store.getNum());
        }
        String targetName= targetValue.getName();
        String pointerName = pointer.getName();
        int targetReg = -1;
        int pointerReg = -1;
        ArrayList<mipsInstruction> retList = new ArrayList<>();
        //先处理左侧的值
        mipsSymbol targetSymbol = null;
        if(targetValue.isConst){
            //常数
            targetSymbol = new mipsSymbol("name",30,false,-1,false,-1,true,false);
            targetReg = this.register.getReg(true,targetSymbol,this.basicBlock);
            mipsInsLi li = new mipsInsLi(targetReg,targetValue.getConstNum());
            retList.add(li);
        }else{
            //变量,先取出它的寄存器
            targetReg = this.symbolTable.getRegIndex(targetName,this.basicBlock,true);
        }
        //再处理右侧的地址，或者说指针
        mipsSymbol addressSymbol = this.symbolTable.getSymbol(pointerName);
        //右侧的地址一定已经在符号表的
        pointerReg = this.symbolTable.getRegIndex(pointerName,this.basicBlock,true);
        mipsInsMove move = new mipsInsMove(pointerReg,targetReg);
        retList.add(move);

        //右侧写回内存
        if(!addressSymbol.haveRam){
            addressSymbol.offset = symbolTable.Offset;
            this.symbolTable.Offset+=4;
            addressSymbol.haveRam = true;
        }
        mipsInstruction tmp = this.register.writeBackPublic(addressSymbol);
        addressSymbol.isUsed = true;
        addressSymbol.isInReg = false;
        retList.add(tmp);

        if(targetSymbol!=null){
            //这个值被用过了
            targetSymbol.isUsed = true;
        }
        return retList;
    }
    public ArrayList<mipsInstruction> genMipsInsFromBr(){
        llvmInsBr br = (llvmInsBr) irIns;
        ArrayList<mipsInstruction> retList = new ArrayList<>();

        llvmLabel dst = br.getDst();
        if(dst!=null){//直接跳转
            mipsInsJ j = new mipsInsJ(dst.getName());
            retList.add(j);
        }else{
            //br value  label1 label2
            llvmLabel label1 = br.getLabel1();
            llvmLabel label2 = br.getLabel2();
            //生成一个beq，一个j指令
            llvmValue value = br.getValue();
            String name = value.getName();
            mipsSymbol symbol = null;
            int reg = -1;
            if(value.isConst){
                symbol = new mipsSymbol("temp",30,false,-1,false,-1,true,false);
                reg = this.register.getReg(true,symbol,this.basicBlock);
                mipsInsLi li = new mipsInsLi(reg,value.getConstNum());
                retList.add(li);
            }else{
                //变量
                reg = this.symbolTable.getRegIndex(name,this.basicBlock,true);
                symbol = this.symbolTable.getSymbol(name);
            }

            //用bne $r, $0  label1，不等于0那就跳到label1
            //否则向下顺序执行，直接J label2
            symbol.isUsed = true;//标记已经使用，释放寄存器
            //写回内存
            ArrayList<mipsInstruction> sws = this.register.writeBackAllRegs();
            if(sws!=null && sws.size()>0){
                for(mipsInstruction item:sws){
                    retList.add(item);
                }
            }
            mipsInsBne bne = new mipsInsBne(reg,0,label1.getName());
            mipsInsJ j = new mipsInsJ(label2.getName());
            retList.add(bne);
            retList.add(j);
        }
        return retList;
    }
    public ArrayList<mipsInstruction> genMipsInsFromCall(){
        llvmInsCall call = (llvmInsCall) irIns;
        String funcName = call.getFuncName();
        ArrayList<mipsInstruction> retList = new ArrayList<>();
        if(funcName.equals("@putint")||funcName.equals("@putchar")){
            mipsInsMove move = new mipsInsMove(3, 4);
            int imm = 0;
            if(funcName.equals("@putint")){
                imm = 1;
            }else if(funcName.equals("@putchar")){
                imm = 11;
            }
            mipsInsLi li = new mipsInsLi(2, imm);
            retList.add(move);
            retList.add(li);
            String paramName = call.getPutParam().getName();
            llvmValue value = call.getPutParam();
            mipsSymbol symbol = null;
            if(!value.isConst){
                symbol = this.symbolTable.getSymbol(paramName);
                int reg = this.symbolTable.getRegIndex(paramName,this.basicBlock,true);
                move = new mipsInsMove(4, reg);
                retList.add(move);
            }else{
                //立即数
                li = new mipsInsLi(4, call.getPutParam().getConstNum());
                retList.add(li);
            }
            mipsInsSyscall syscall = new mipsInsSyscall();
            move = new mipsInsMove(4, 3);
            retList.add(syscall);
            retList.add(move);
            if(symbol!=null){
                symbol.isUsed = true;
            }
        }else if(funcName.equals("@getint")||funcName.equals("@getchar")){
            retList = genMipsInsFromGetIntChar();
        }else{
            //普通函数调用
            retList = genMipsInsFromSelfDefFunc();
        }
        return retList;
    }
    public ArrayList<mipsInstruction> genMipsInsFromRet(){
        llvmInsRet ret = (llvmInsRet) irIns;
        ArrayList<mipsInstruction> ans = new ArrayList<>();
        if(!ret.isVoidType()){
            //一律返回int, 返回值为int的函数需要将返回值存入$v0即$2
            llvmValue retValue = ret.getRetValue();
            String name = retValue.getName();
            int reg;
            mipsSymbol tmp = null;
            if(retValue.isConst){
                //常数需要从寄存器表获取一个$t并使用li将该立即数加载进去
                //                // 然后使用move进行赋值
                //                // 这里的Symbol不应当被加入符号表
                tmp = new mipsSymbol("temp",30);
                reg = this.register.getReg(true,tmp,this.basicBlock);
                mipsInsLi li = new mipsInsLi(reg,retValue.getConstNum());
                ans.add(li);
                tmp.isUsed = true;
            }else{
                //变量
                reg = this.symbolTable.getRegIndex(name,this.basicBlock,true);
            }
            mipsInsMove move = new mipsInsMove(2, reg);
            if(tmp!=null){
                tmp.isUsed = true;
            }
            ans.add(move);
            if(!this.basicBlock.function.isMainFunc){
                mipsInsJr jr = new mipsInsJr(31);
                ans.add(jr);
            }else{//main函数
                mipsInsLi li = new mipsInsLi(2, 0xa);
                ans.add(li);
                mipsInsSyscall syscall = new mipsInsSyscall();
                ans.add(syscall);
            }
        }else{
            mipsInsJr jr = new mipsInsJr(31);
            ans.add(jr);
        }
        return ans;
    }
    public ArrayList<mipsInstruction> genMipsInsFromGetElement(){
        ArrayList<mipsInstruction> ret = new ArrayList<>();
        llvmInsGetElement getElement = (llvmInsGetElement) irIns;
        llvmType eleType = getElement.getElementType();
        if(eleType instanceof  llvmArrayType){//getelemtn [length x type],
            llvmValue left = getElement.getValue();
            String leftName = left.getName();
            String rightName = getElement.getTarget().getName();
            mipsSymbol symbol = this.symbolTable.getSymbol(rightName);
            this.symbolTable.symbolHashMap.put(leftName,symbol);//映射到同一个符号上，就是这个数组的符号
        }else{//插到符号表就行，访存的指令自然在store会有
            llvmValue left = getElement.getValue();
            String leftName = left.getName();
            String rightName = getElement.getTarget().getName();
            int site = -1;
            if(getElement.getIndex()!=null){
                if(getElement.getIndex().isConst){
                    site = getElement.getIndex().getConstNum();
                }
            }else{
                site = getElement.getSite();
            }//位置
            mipsSymbol symbolSrc = this.symbolTable.getSymbol(rightName);
            mipsSymbol dstSymbol = null;
            if(site!=-1){//正常的site，具体的数值
                dstSymbol = new mipsSymbol(leftName,symbolSrc.base,false,-1,true,symbolSrc.offset+4*site,true,false);

            }else{//site在寄存器里面，不是具体值
                //需要$base+offset+$site
                llvmValue index = getElement.getIndex();
                int siteReg = this.symbolTable.getRegIndex(index.getName(),this.basicBlock,true);
                mipsSymbol symbol = new mipsSymbol("temp",30,false,-1,false,-1,true,false);
                int tmpReg = this.register.getReg(true,symbol,this.basicBlock);
                mipsInsLi li = new mipsInsLi(tmpReg,4);
                mipsInsMul mul = new mipsInsMul(tmpReg,tmpReg,siteReg);// 4*site
                ret.add(li);
                ret.add(mul);
                if(symbolSrc.offset!=0){//offset + 4*site
                    mipsInsAddi addi = new mipsInsAddi(tmpReg,tmpReg,symbolSrc.offset);
                    ret.add(addi);
                }
                //base + (offset + 4*site)
                mipsInsAdd add = new mipsInsAdd(tmpReg,symbolSrc.base,tmpReg);
                dstSymbol = new mipsSymbol(leftName,tmpReg,false,-1,true,0,true,false);
                ret.add(add);
            }
            this.symbolTable.addSymbol(leftName,dstSymbol);
        }
        return ret;
    }
    public ArrayList<mipsInstruction> genMipsInsFromLabel(){
        /* 写回内存 */
        ArrayList<mipsInstruction> retList = new ArrayList<>();
        ArrayList<mipsInstruction> allSw  = this.register.writeBackAllRegs();
        if(allSw!=null && allSw.size()>0){
            for(mipsInstruction item:allSw){
                retList.add(item);
            }
        }
        llvmLabel label = (llvmLabel) irIns;
        //去掉原来的%,作为新label名字
        mipsInsLabel mipsLabel = new mipsInsLabel(label.getName(),this.basicBlock.function.name);
        retList.add(mipsLabel);
        return retList;
    }
    //处理getint()和getchar()输入，
    public ArrayList<mipsInstruction> genMipsInsFromGetIntChar(){
        llvmInsCall call = (llvmInsCall) irIns;

        String funcName = call.getFuncName();
        int v1Num = 0;//根据getint,getchar确定v的值
        if(funcName.equals("@getchar")){
            v1Num = 12;
        }else if(funcName.equals("@getint")){
            v1Num = 5;
        }

        ArrayList<mipsInstruction> retList = new ArrayList<>();
        /* 将$v0移入$v1做保护 */
        mipsInsMove move = new mipsInsMove(3,2);
        /* 将立即数v1num装入$v0 */
        mipsInsLi li = new mipsInsLi(2,v1Num);
        mipsInsSyscall syscall = new mipsInsSyscall();
        retList.add(move);
        retList.add(li);
        retList.add(syscall);
        String resultName = call.getResult().getName();
        mipsSymbol symbol = new mipsSymbol(resultName,30,false,-1,false,-1,true,false);
        this.symbolTable.addSymbol(symbol.name,symbol);
        int reg = this.symbolTable.getRegIndex(symbol.name,this.basicBlock,false);
        move = new mipsInsMove(reg,2);
        retList.add(move);
        /* 将原$v0的值移回 */
        move = new mipsInsMove(2,3);
        retList.add(move);
        return retList;
    }
    //处理正常的自定义函数调用
    public ArrayList<mipsInstruction> genMipsInsFromSelfDefFunc(){
        ArrayList<mipsInstruction> ret = new ArrayList<>();
        ArrayList<mipsInstruction> tmp = this.register.writeBackAllRegs();
        if(tmp!=null&&tmp.size()>0){
           ret.addAll(tmp);
        }
        if(ret.size()>0){
            this.basicBlock.addInstructions(ret);
            ret = new ArrayList<>();
        }
        /* 1. 保存现场到$sp */
        int spOffset = 0;
        for (int i = 2; i < 32; i++) {
            if (26 <= i && i <= 30) {
                continue;
            }
            if (this.register.isInReg(i) ||  i == 31) {
                mipsInsSw sw = new mipsInsSw(i,29,spOffset);
                ret.add(sw);
                spOffset -= 4;
            }
        }

        /* 实参存入寄存器与内存（如果有）$fp */
        /* 将子函数fp装入v1 */
        mipsInsAddi addi = new mipsInsAddi(3,30,this.symbolTable.Offset+32*4);
        ret.add(addi);
        this.basicBlock.addInstructions(ret);
        ret = new ArrayList<>();
        /* 应当建立新表 */
        /* 深拷贝 */
        mipsRegister newRegister = new mipsRegister();
        mipsSymbolTable newTable = new mipsSymbolTable(newRegister);
        newTable.symbolHashMap=this.symbolTable.cloneSymbols();
        newTable.Offset = this.symbolTable.Offset;
        newRegister.symbolTable = newTable;
        newRegister.hasValues = this.register.cloneHasValues();
        newRegister.regNum = this.register.regNum;
        newRegister.regs = this.register.cloneRegs();
        newRegister.SregUse = this.register.cloneSregUse();

        llvmInsCall call = (llvmInsCall) irIns;
        ArrayList<llvmValue> params = call.getParams();
        int length = 0;
        if(params!=null){
             length = params.size();
        }

        int newOffset = 0;
        for(int i=0;i<length;i++){
            llvmValue value = params.get(i);
            String name = value.getName();
            if(newTable.symbolHashMap.containsKey(name)){
                int reg = newTable.getRegIndex(name,this.basicBlock,true);
                if(i<4){
                    //存入$a
                    mipsInsMove move = new mipsInsMove(4+i,reg);
                    newTable.getSymbol(name).isUsed = true;
                    ret.add(move);
                    this.basicBlock.addInstructions(ret);
                    ret = new ArrayList<>();
                }else{
                    //存入内存
                    mipsInsSw sw = new mipsInsSw(reg,3,newOffset);
                    ret.add(sw);
                    this.basicBlock.addInstructions(ret);
                    ret = new ArrayList<>();
                }
            }else{
                //常数
                mipsInsLi li = new mipsInsLi(4+i,value.getConstNum());
                ret.add(li);
                this.basicBlock.addInstructions(ret);
                ret = new ArrayList<>();
            }
            if(i>=4){
                newOffset+=4;
            }
        }

        /* 3. 修改$fp, $sp */
        /* fp */
        mipsInsMove move = new mipsInsMove(30,3);
        ret.add(move);
        //sp
        mipsInsAddi addi1 = new mipsInsAddi(29,29,spOffset);
        ret.add(addi1);

        //jal,跳转到函数那里去执行，并保存返回结果到ra寄存器
        mipsInsJal jal = new mipsInsJal(call.getFuncName());
        ret.add(jal);
        this.basicBlock.addInstructions(ret);
        ret = new ArrayList<>();

        /* 5. 恢复$fp现场，本质上是通过MipsSymbolTable的fpOffset自减 */
        mipsInsAddi addi2 = new mipsInsAddi(30,30,-(this.symbolTable.Offset+32*4));
        ret.add(addi2);
        /* 6. 恢复$sp现场，本质上是通过讲$sp自增至原值，将$ra和其他保存寄存器的值恢复 */
        addi2 = new mipsInsAddi(29,29,-spOffset);
        ret.add(addi2);
        this.basicBlock.addInstructions(ret);
        ret = new ArrayList<>();
        for (int i = 31; i >= 2; i--) {
            if (26 <= i && i <= 30) {
                continue;
            }
            if (this.register.isInReg(i) || i == 31) {
                spOffset += 4;
                mipsInsLw lw = new mipsInsLw(i,29,spOffset);
                ret.add(lw);
            }
        }
        this.basicBlock.addInstructions(ret);
        ret = new ArrayList<>();
        /* 7. 可能会有一个左值赋值 */
        if(call.getResult()!=null){
            mipsSymbol resultSymbol = new mipsSymbol(call.getResult().getName(),30,false,-1,false,0,true,false);
            this.symbolTable.addSymbol(resultSymbol.name,resultSymbol);
            int reg = this.symbolTable.getRegIndex(resultSymbol.name,this.basicBlock,false);
            mipsInsMove move1 = new mipsInsMove(reg,2);
            ret.add(move1);
            this.basicBlock.addInstructions(ret);
            ret = new ArrayList<>();
        }
        return ret;
    }
    //处理zext 和 trunc语句
    public ArrayList<mipsInstruction> genMipsInsFromZextTrunc(){
        //映射到同一个符号上
        if(irIns instanceof llvmInsZext){
            llvmInsZext ins = (llvmInsZext) irIns;
            llvmValue result = ins.getResult();
            llvmValue src  = ins.getSource();
            if(!src.isConst){//只有右侧不是常量的情况下，才能进行符号映射
                mipsSymbol srcSymbol = this.symbolTable.getSymbol(src.getName());
                this.symbolTable.addSymbol(result.getName(),srcSymbol);
            }

        }else if(irIns instanceof llvmInsTrunc){
            llvmInsTrunc ins = (llvmInsTrunc) irIns;
            llvmValue result = ins.getDstValue();
            llvmValue src  = ins.getSourceValue();
            if(!src.isConst){
                mipsSymbol srcSymbol = this.symbolTable.getSymbol(src.getName());
                this.symbolTable.addSymbol(result.getName(),srcSymbol);
            }
        }
        return new ArrayList<>();
    }
}
