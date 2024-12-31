package middle.llvm.value.basicblock;

import frontend.lexer.Token;
import frontend.parser.Decl.Decl;
import frontend.parser.Exp.*;
import frontend.parser.FuncDef.Block;
import frontend.parser.FuncDef.BlockItem;
import frontend.parser.FuncDef.FuncFParam;
import frontend.parser.FuncDef.FuncFParams;
import frontend.parser.Stmt.ForStmt;
import frontend.parser.Stmt.Stmt;
import middle.llvm.llvmValue;
import middle.llvm.type.llvmIntegerType;
import middle.llvm.type.llvmPointerType;
import middle.llvm.type.llvmType;
import middle.llvm.type.llvmVoidType;
import middle.llvm.value.function.llvmFunc;
import middle.llvm.value.function.llvmFuncCnt;
import middle.llvm.value.function.llvmParam;
import middle.llvm.value.instructions.binary.llvmInsBinary;
import middle.llvm.value.instructions.llvmInstruction;
import middle.llvm.value.instructions.llvmInstructionBuilder;
import middle.llvm.value.instructions.llvmInstructionType;
import middle.llvm.value.instructions.llvmLabel;
import middle.llvm.value.instructions.memOp.llvmInsAlloca;
import middle.llvm.value.instructions.memOp.llvmInsLoad;
import middle.llvm.value.instructions.memOp.llvmInsStore;
import middle.llvm.value.instructions.other.llvmInsBr;
import middle.llvm.value.instructions.other.llvmInsZext;
import middle.llvm.value.instructions.terminator.llvmInsRet;
import middle.symbol.Symbol;
import middle.symbol.SymbolTable;


import java.util.ArrayList;
import java.util.Base64;

public class llvmBasicBlockBuilder {
    private SymbolTable symbolTable;//对应的就是当前block的作用域,严格按照只有进入blcok才更新作用域
    private Block block;
    private llvmFuncCnt funcCnt;

    private Stmt stmt;
    private ArrayList<BlockItem> blockItems;
    private ArrayList<llvmBasicBlock> basicBlocks = new ArrayList<>();
    private llvmSymbolTableCnt symbolTableSite;//每个builder维护一个子表的顺序，用于拿表

    private ArrayList<Symbol>  params;
    //for
    private llvmLabel beginForLabel;
    private llvmLabel endForLabel;
    private llvmType retType;//来自外面的函数

    public llvmBasicBlockBuilder(SymbolTable symbolTable,llvmFuncCnt funcCnt,llvmLabel beginForLabel, llvmLabel endForLabel,llvmType retType){
        this.symbolTable = symbolTable;
        this.funcCnt = funcCnt;
        this.beginForLabel = beginForLabel;
        this.endForLabel = endForLabel;
        this.retType = retType;
    }
    //专为if的stmt和for要执行的stmt设计
    //由于if和for的stmt的类型有很多种，无法及时判断，所以使用通用处理，相当于伪造了一个假的block，里面只有一条语句stmt
    public llvmBasicBlockBuilder(SymbolTable symbolTable,llvmFuncCnt funcCnt,llvmLabel beginForLabel, llvmLabel endForLabel,llvmType retType,Stmt stmt,llvmSymbolTableCnt symbolTableSite){
        this(symbolTable,funcCnt,beginForLabel,endForLabel, retType);
        this.blockItems = new ArrayList<>();
        BlockItem blockItem = new BlockItem(null,stmt);
        blockItems.add(blockItem);
        this.symbolTableSite = symbolTableSite;
        //特意构造一个
    }
    //这是解析的初始入口,也是进入子表的情况
    public llvmBasicBlockBuilder(SymbolTable symbolTable,llvmFuncCnt funcCnt,Block block,llvmLabel beginForLabel, llvmLabel endForLabel, ArrayList<Symbol> params,llvmType retType){
        this(symbolTable,funcCnt,beginForLabel,endForLabel, retType);
        this.block = block;
        this.blockItems = this.block.getBlockItemArrayList();
        this.params = params;
        this.symbolTableSite=new llvmSymbolTableCnt();
    }
    public llvmBasicBlockBuilder(SymbolTable symbolTable,llvmFuncCnt funcCnt, Stmt stmt,llvmSymbolTableCnt symbolTableSite,llvmLabel beginForLabel, llvmLabel endForLabel,llvmType retType){//这是进入cond 和for的情况，需要继续维护子表顺序
        this(symbolTable,funcCnt,beginForLabel,endForLabel,retType);
        this.stmt = stmt;//这里包含了三种情况，block，if，for
        this.symbolTableSite = symbolTableSite;//继续维护site
    }
    //进入block内部，其可能有Block，条件表达式if，for循环和其它类型（其它要一直遍历到块结束）
    public ArrayList<llvmBasicBlock> getBasicBlocks(){//程序递归的入口
        if(this.blockItems!=null){//调整一下，不用block，用blockItems，确保从外部构建和特殊单条语句的构建都能走这里
            //传入block
            return getBasicBlockFromSubBlock();
        }else if(this.stmt.isIfCase()==true){
            //if情况
            return getBasicBlockFromCond();
        }else if(this.stmt.isForCase()==true){
            //for
            return getBasicBlockFromFor();
        }
        return null;
    }
    //子block内获得,
    private ArrayList<llvmBasicBlock> getBasicBlockFromSubBlock(){
        if(params!=null){//处理参数列表
            llvmBasicBlock basicBlock = new llvmBasicBlock();
            ArrayList<llvmInstruction> instructions = new ArrayList<>();
            for(Symbol param:params){//对于每一个参数符号，采取alloca 和store
                llvmType elementType = null;
                boolean isArr = false;
                if(param.symbolType.toString().equals("Int")){
                    elementType = llvmIntegerType.getI32();
                }else if(param.symbolType.toString().equals("Char")){
                    elementType = llvmIntegerType.getI8();
                }else if(param.symbolType.toString().equals("IntArray")){
                    elementType = new llvmPointerType(llvmIntegerType.getI32());
                    isArr = true;
                }else if(param.symbolType.toString().equals("CharArray")){
                    elementType = new llvmPointerType(llvmIntegerType.getI8());
                    isArr = true;
                }
                llvmValue allocaResult = new llvmValue();
                llvmInsAlloca alloca = new llvmInsAlloca(elementType,allocaResult);
                llvmInsStore store = new llvmInsStore(elementType,param.value,allocaResult);
                param.setLLVMValue(allocaResult);
                instructions.add(alloca);
                instructions.add(store);
                if(isArr){//数组这里我们需要补充load指令，同时吧load的结果作为其值,这样求值的时候可以直接使用getelemtptr
                    llvmValue loadResult = new llvmValue();
                    llvmInsLoad load = new llvmInsLoad(elementType,loadResult,allocaResult);
                    param.setLLVMValue(loadResult);
                    instructions.add(load);
                }
            }
            basicBlock.addAllInstructions(instructions);
            this.basicBlocks.add(basicBlock);
        }
        int len = this.blockItems.size();
        int site = 0;
        while(site<len){
            BlockItem item = this.blockItems.get(site);
            int typeCode = checkBlockItemType(item);//获取blockItem的类型码
            if(0<typeCode && typeCode <4){//if,for,block，需要进入新的
                Stmt stmtTmp = item.getStmt();
                llvmBasicBlockBuilder basicBlockBuilder = null;
                //if和for都使用当前表，一定只有blcok才会有新的子表使用
                if(typeCode==1||typeCode==2){//if和for，一样的，都走这条路
                    basicBlockBuilder = new llvmBasicBlockBuilder(this.symbolTable,this.funcCnt,stmtTmp,this.symbolTableSite,this.beginForLabel,this.endForLabel,this.retType);
                }else if(typeCode==3){//block情况
                    SymbolTable subTable = getNextSubSymbolTable();
                    if(subTable!=null){
                        basicBlockBuilder = new llvmBasicBlockBuilder(subTable,this.funcCnt,item.getStmt().getBlock(),this.beginForLabel,this.endForLabel,null,this.retType);
                    }else{
                        System.out.println("error, no tables!");
                    }
                }
                //builder完成，
                this.addAllIrBasicBlocks(basicBlockBuilder.getBasicBlocks());
                site++;//完成后+1*-
            }else if(3<typeCode&& typeCode<=13){//4~13
                //构建指令，遇到能构建指令的，需要一直往后迭代，直到遇到不能迭代的，然后全部打包为一个基本块
                llvmBasicBlock basicBlock = new llvmBasicBlock();
                while(site<len){
                    //根据上述，不停的解析指令，打包到一个基本块内，直到结束或者遇到非指令
                    BlockItem curItem = this.blockItems.get(site);
                    int curTypeCode = checkBlockItemType(curItem);
                    if(0<curTypeCode&&curTypeCode<=3){//遇到if for, block，跳出
                        break;
                    }else if(curTypeCode==14){//遇到分号，跳过
                        site++;
                        continue;
                    }else if(curTypeCode<=0||curTypeCode>14){
                        System.out.println("error, typeCode out of bound!");
                    }
                    llvmInstructionBuilder insBuilder = new llvmInstructionBuilder(this.symbolTable,this.funcCnt,basicBlock,curItem,this.beginForLabel,this.endForLabel,this.retType);
                    ArrayList<llvmInstruction> instructions = insBuilder.getInstructions();
                    basicBlock.addAllInstructions(instructions);
                    site++;
                }
                this.basicBlocks.add(basicBlock);
            }else if(typeCode==14){//纯分号
                site++;
            }else{
                System.out.println("error, out of the typeCode bound!");
            }
        }
        return this.basicBlocks;
    }
    //从if，if-else , if-else if -else
    private ArrayList<llvmBasicBlock> getBasicBlockFromCond(){
        //basicBlockBuilder = new llvmBasicBlockBuilder(this.symbolTable,this.funcCnt,stmtTmp,this.symbolTableSite,this.beginForLabel,this.endForLabel,this.retType);
        Cond cond = this.stmt.getCond();
        Stmt stmt1 = this.stmt.getIfStmt1();
        Stmt stmt2 = null;
        if(this.stmt.getElse()!=null){
            stmt2 = this.stmt.getIfStmt2();
        }

        //构造标签，if，else，if-else结束位置的块标签
        llvmLabel condLabel = new llvmLabel();
        llvmLabel ifLabel = new llvmLabel();
        llvmLabel elseLabel = null;
        if(stmt2!=null){
            elseLabel = new llvmLabel();
        }
        llvmLabel ifElseEndLabel = new llvmLabel();

        //先加入cond标签,先添加br，再添加标签
        llvmBasicBlock condBlock = new llvmBasicBlock();
        llvmInsBr brToCond = new llvmInsBr(condLabel);
        condBlock.addInstruction(brToCond);
        condBlock.addInstruction(condLabel);
        this.basicBlocks.add(condBlock);

        //解析cond表达式，根据cond的值来跳转两个label，一个真，一个假的，假的为下一个label
        //下面的cond解析完已经加入基本块
        if(stmt2!=null){
            genCond(cond,ifLabel,elseLabel);
        }else{
           genCond(cond,ifLabel,ifElseEndLabel);
        }

        //先将if标签加入基本块
        llvmBasicBlock ifBlock = new llvmBasicBlock();
        ifBlock.addInstruction(ifLabel);
        this.basicBlocks.add(ifBlock);

        //处理stmt1;
        llvmBasicBlockBuilder stmt1Builder = new llvmBasicBlockBuilder(this.symbolTable,this.funcCnt,beginForLabel,endForLabel,retType,stmt1,this.symbolTableSite);
        this.addAllIrBasicBlocks(stmt1Builder.getBasicBlocks());

        //这里要加一个跳转语句，不管怎么样都是跳到nextLabel,属于stmt1执行完毕的跳转
        llvmBasicBlock tmpBrBlock = new llvmBasicBlock();
        llvmInsBr stmtBr = new llvmInsBr(ifElseEndLabel);
        tmpBrBlock.addInstruction(stmtBr);
        this.basicBlocks.add(tmpBrBlock);

        //如果有else，有stmt2，
        if(elseLabel!=null){
            llvmBasicBlock elseBlock = new llvmBasicBlock();
            elseBlock.addInstruction(elseLabel);
            this.basicBlocks.add(elseBlock);
            //处理stmt2
            llvmBasicBlockBuilder stmt2Builder = new llvmBasicBlockBuilder(this.symbolTable,this.funcCnt,beginForLabel,endForLabel,retType,stmt2,this.symbolTableSite);
            this.addAllIrBasicBlocks(stmt2Builder.getBasicBlocks());
            //还要加一个跳到nextLabel
            this.basicBlocks.add(tmpBrBlock);
        }
        //if-else, if处理完，加入next块的标签
        llvmBasicBlock nextBlock = new llvmBasicBlock();
        nextBlock.addInstruction(ifElseEndLabel);
        this.basicBlocks.add(nextBlock);

        return this.basicBlocks;
    }

    private ArrayList<llvmBasicBlock> getBasicBlockFromFor(){
        //forStmt1, cond, forStmt2, stmt
        ForStmt for_stmt1 = stmt.getFor_stmt1();
        ForStmt for_stmt2 = stmt.getFor_stmt2();
        Cond cond = stmt.getCond();
        Stmt stmt1 = stmt.getIfStmt1();//这里就是for的循环体

        //先解析forStmt1，for的初始化
        if(for_stmt1!=null){
            llvmBasicBlock forStmt1Block = new llvmBasicBlock();
            llvmInstructionBuilder insBuilder1 = new llvmInstructionBuilder(this.symbolTable,this.funcCnt,forStmt1Block,this.beginForLabel,this.endForLabel,this.retType);
            insBuilder1.getInsFromStmtAssign(for_stmt1.getLval(),for_stmt1.getExp());
            forStmt1Block.addAllInstructions(insBuilder1.getDirectInstructions());
            this.basicBlocks.add(forStmt1Block);
        }


        //创建所有的标签并，
        llvmLabel condLabel = new llvmLabel();
        llvmLabel successLabel = new llvmLabel();
        llvmLabel forStmt2Label = new llvmLabel();
        llvmLabel nextBlockLabel = new llvmLabel();

        //添加cond标签
        llvmBasicBlock condBlock = new llvmBasicBlock();
        llvmInsBr brToCond = new llvmInsBr(condLabel);
        condBlock.addInstruction(brToCond);
        condBlock.addInstruction(condLabel);
        this.basicBlocks.add(condBlock);

        //解析cond,cond可能为空,空为真
        if(cond!=null){
            genCond(cond,successLabel,nextBlockLabel);
        }else{//真，需要跳转到successLabel，顺序执行的话，还是到下面的successLabel
            llvmBasicBlock noneCond = new llvmBasicBlock();
            llvmInsBr brToStmt = new llvmInsBr(successLabel);
            noneCond.addInstruction(brToStmt);
            this.basicBlocks.add(noneCond);
        }
        //加入成功跳转到stmt的标签,即去执行stmt
        llvmBasicBlock successBlock = new llvmBasicBlock();
        successBlock.addInstruction(successLabel);
        this.basicBlocks.add(successBlock);

        //建立for循环块的开始和结束标签
        this.beginForLabel = forStmt2Label;//continue跳转的标签
        this.endForLabel = nextBlockLabel;//break跳转的标签

        //解析stmt
        llvmBasicBlockBuilder stmtBuilder = new llvmBasicBlockBuilder(this.symbolTable,this.funcCnt,beginForLabel,endForLabel,retType,stmt1,this.symbolTableSite);
        this.addAllIrBasicBlocks(stmtBuilder.getBasicBlocks());

        //加入跳转到forstmt2，并解析forstmt2
        llvmBasicBlock forStmt2Block = new llvmBasicBlock();
        llvmInsBr brToForStmt2 = new llvmInsBr(forStmt2Label);
        forStmt2Block.addInstruction(brToForStmt2);
        forStmt2Block.addInstruction(forStmt2Label);

        //forstmt2的执行语句
        if(for_stmt2!=null){
            llvmInstructionBuilder insBuilder2 = new llvmInstructionBuilder(this.symbolTable,this.funcCnt,forStmt2Block,this.beginForLabel,this.endForLabel,this.retType);
            insBuilder2.getInsFromStmtAssign(for_stmt2.getLval(),for_stmt2.getExp());
            forStmt2Block.addAllInstructions(insBuilder2.getDirectInstructions());
        }

        //forstmt2结束末尾还要添加到cond的跳转
        forStmt2Block.addInstruction(brToCond);

        //加入endfor的标签，也就是下一个块
        forStmt2Block.addInstruction(nextBlockLabel);
        this.basicBlocks.add(forStmt2Block);

        return this.basicBlocks;
    }
    //判断blockItem的类型
    /*
    1:cond
    2:for
    3:block
    *****************************这些用于获取新的基本块
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
    14: stmtNull，分号
    * */
    private int checkBlockItemType(BlockItem item){
        if(item.getDecl()!=null){
            Decl decl = item.getDecl();
            if(decl.getConstDecl()!=null){
                return 4;
            }else if(decl.getVarDecl()!=null){
                return 5;
            }
        }else if(item.getStmt()!=null){
            return item.getStmt().parseStmtType();
        }
        return 0;
    }
    private SymbolTable getNextSubSymbolTable(){
        int site = this.symbolTableSite.getCnt();
        ArrayList<SymbolTable> tables = this.symbolTable.subTables;
        if(site<tables.size()){
            return tables.get(site);
        }
        return null;
    }

    private void addAllIrBasicBlocks(ArrayList<llvmBasicBlock> blocks){
        if(blocks!=null){
            for(llvmBasicBlock item:blocks){
                this.basicBlocks.add(item);
            }
        }

    }
    private void genCond(Cond cond,llvmLabel ifLabel,llvmLabel nextLabel){
//        llvmBasicBlock basicBlock = new llvmBasicBlock();
        LOrExp lOrExp = cond.getlOrExp();
        ArrayList<LAndExp> lAndExps = lOrExp.getlAndExpArrayList();
        int len = lAndExps.size();
//        llvmLabel cur = new llvmLabel("%"+String.valueOf(this.funcCnt.getCnt()));
//
//        llvmInsBr br1 = new llvmInsBr(cur);//直接跳转到第一个land表达式
//        //先加默认跳转再加第一个式子的标签
//        basicBlock.addInstruction(br1);
//        basicBlock.addInstruction(cur);
//        this.basicBlocks.add(basicBlock);
        for(int i=0;i<len;i++){
            llvmLabel nxt = null;
            if(i<len-1){//当前没到最后一个land式子
                nxt = new llvmLabel();
            }else{//到了最后一个land式子
                nxt = nextLabel;
            }
            //这里面要负责把指令按顺序加入基本块
            genllvmInstructionFromLandExp(lAndExps.get(i),ifLabel,nextLabel,nxt);
            if(i!=len-1){//最后一个别加进去
                llvmBasicBlock tmpblock = new llvmBasicBlock();
                tmpblock.addInstruction(nxt);
                this.basicBlocks.add(tmpblock);
            }
        }
    }
    private void genllvmInstructionFromLandExp(LAndExp lAndExp, llvmLabel ifLabel, llvmLabel nextLabel,llvmLabel specialLabel){//specialLabel在非最后一个land的情况下，指向下一个land的标签，
        ArrayList<EqExp> eqExps = lAndExp.getEqExpArrayList();
        int length = eqExps.size();
        llvmLabel retLabel = null;
        if(length ==1 ){//只有一个eqExp，只有一个eq，如果真，那么and就是真，那么最外层的lor就是真，那么就到iflabel，如果假的，到下一个landexp，如果没有下一个land，那么就是到next了
            genllvmInstructionFromEqExp(eqExps.get(0),ifLabel,specialLabel,null,true);
        }else{//多个eq
            //应该要为每一个eq都分配一个标签,但是每个land的第一个标签都分配了，只需要为后面的分配标签就行
            boolean flag= false;
            for(int i=0;i<length;i++){
                llvmLabel nxt = null;
                flag=false;//对于非最后一个eq，都是false情况，最后一个eq得走true情况，因为最后一个eq等价于只有一个eq的情况，因为到最后一个eq的时候，默认前面的eq都是对的
                if(i<length-1){//没到最后一个eq，那么都为下一个eq分配标签
                    nxt = new llvmLabel();
                }else if(i==length-1){//到达最后一个eq，那么这里应该为下一个land标签，如果没有下一个land，就是next标签，这里对应的就是specialLabel
                    nxt = specialLabel;
                    flag=true;
                }
                genllvmInstructionFromEqExp(eqExps.get(i),ifLabel,specialLabel,nxt,flag);//如果真，到下一个eq，如果假的，这个land就是假的，到下一个land，如果没有下一个land，就到next
                if(i!=length-1){//最后一个别加进去，这里肯定是nextLabel，它不属于cond的标签
                    llvmBasicBlock tmpBlock = new llvmBasicBlock();
                    tmpBlock.addInstruction(nxt);
                    this.basicBlocks.add(tmpBlock);
                }
            }
        }
    }
    private void genllvmInstructionFromEqExp(EqExp eqExp, llvmLabel ifLabel,llvmLabel label1,llvmLabel label2, boolean flag){//label1为下一个land的标签或者next的标签，label2为下一个eq的标签
        llvmBasicBlock basicBlock  = new llvmBasicBlock();
        //这里已经可以判断对错了，需要写跳转
        ArrayList<RelExp> relExps = eqExp.getRelExpArrayList();
        ArrayList<Token> opTokens = eqExp.getOpTokens();
        llvmValue first = genllvmInstructionFromRelExp(relExps.get(0),basicBlock);
        llvmValue second = null;
        llvmValue result = null;
        int length = relExps.size();
        for(int i=1;i<length;i++){
            if(first.getValueType().llvmOutput().equals("i1")){//需要扩展到i32计算
                llvmValue tmp = new llvmValue();
                llvmInsZext zext = new llvmInsZext(tmp,first);
                basicBlock.addInstruction(zext);
                first = tmp;
            }
            second = genllvmInstructionFromRelExp(relExps.get(i),basicBlock);
            if(second.getValueType().llvmOutput().equals("i1")){//需要扩展到i32计算
                llvmValue tmp = new llvmValue();
                llvmInsZext zext = new llvmInsZext(tmp,second);
                basicBlock.addInstruction(zext);
                second = tmp;
            }
            result = new llvmValue();
            llvmInstructionType binaryType = null;
            llvmInsBinary binary = null;
            if(opTokens.get(i-1).getTokenTypeName().equals("EQL")){
                binaryType = llvmInstructionType.eq;
            }else if(opTokens.get(i-1).getTokenTypeName().equals("NEQ")){
                binaryType = llvmInstructionType.ne;
            }
            binary = new llvmInsBinary(first,second,result,binaryType);
            //这里默认都使用I32，那么如果是(a>b)==(c<d)，都使用i32
            basicBlock.addInstruction(binary);
            first = result;
            first.setValueType(llvmIntegerType.getI1());
        }
        if(length==1){//只有一个值，那么这里应该加一个exp != 0的比较结果
            if(first.getValueType().llvmOutput().equals("i1")){//需要扩展到i32计算
                llvmValue tmp = new llvmValue();
                llvmInsZext zext = new llvmInsZext(tmp,first);
                basicBlock.addInstruction(zext);
                first = tmp;
            }
            llvmValue result1 = new llvmValue();
            llvmValue zero = new llvmValue(llvmIntegerType.getI32(),0);
            zero.setConst(true);
            llvmInsBinary binary1 = new llvmInsBinary(first,zero,result1,llvmInstructionType.ne);
            basicBlock.addInstruction(binary1);
            first = result1;
            first.setValueType(llvmIntegerType.getI1());
        }
        //到这里first就是最终的计算结果，需要
        first.setValueType(llvmIntegerType.getI1());
        //将结果写成br
        if(flag==true){//外层and只有一个eq，如果真，那么and就是真，那么最外层的lor就是真，那么就到iflabel，如果假的，到下一个landexp，如果没有下一个land，那么就是到next了
            llvmInsBr br = new llvmInsBr(first,ifLabel,label1);
            basicBlock.addInstruction(br);
        }else if(flag == false){//外层and有多个eq，如果真，到下一个eq，如果假的，这个land就是假的，到下一个land，如果没有下一个land，就到next
            llvmInsBr br = new llvmInsBr(first,label2,label1);
            basicBlock.addInstruction(br);
        }
        this.basicBlocks.add(basicBlock);
    }
    private llvmValue genllvmInstructionFromRelExp(RelExp relExp,llvmBasicBlock basicBlock){
        ArrayList<AddExp> addExps = relExp.getAddExpArrayList();
        ArrayList<Token> opTokens = relExp.getOpTokens();
        int length = addExps.size();
        llvmInstructionBuilder insBuilder = new llvmInstructionBuilder(this.symbolTable,this.funcCnt,basicBlock,this.beginForLabel,this.endForLabel,this.retType);
        llvmValue first = insBuilder.getInsFromAddExp(addExps.get(0));
        //确保了从getAddexp,getExp的结果，正常情况下都是I32
        for(int i=1;i<length;i++){
            if(first.getValueType().llvmOutput().equals("i1")){//需要扩展到i32计算
                llvmValue tmp = new llvmValue();
                llvmInsZext zext = new llvmInsZext(tmp,first);
                basicBlock.addInstruction(zext);
                first = tmp;
            }
            llvmValue second = insBuilder.getInsFromAddExp(addExps.get(i));
            llvmInstructionType insType = null;
            llvmValue result = new llvmValue();
            if(opTokens.get(i-1).getTokenTypeName().equals("LEQ")){//<=
                insType = llvmInstructionType.sle;
            }else if(opTokens.get(i-1).getTokenTypeName().equals("LSS")){//<
                insType = llvmInstructionType.slt;
            }else if(opTokens.get(i-1).getTokenTypeName().equals("GEQ")){//>=
                insType = llvmInstructionType.sge;
            }else if(opTokens.get(i-1).getTokenTypeName().equals("GRE")){//>
                insType = llvmInstructionType.sgt;
            }
            llvmInsBinary binary = new llvmInsBinary(first,second,result,insType);
            insBuilder.addAInstruction(binary);
            first = result;
            first.setValueType(llvmIntegerType.getI1());
        }
        //计算完的结果在first里面
        basicBlock.addAllInstructions(insBuilder.getDirectInstructions());
//        if(length>1){
//            first.setValueType(llvmIntegerType.getI1());
//        }
        return first;
    }
}
