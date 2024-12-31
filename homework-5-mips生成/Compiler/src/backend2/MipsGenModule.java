package backend2;

import backend.mipsInstruction.binaryCal.*;
import backend.mipsInstruction.memOp.mipsInsMfhi;
import com.sun.jdi.IntegerType;
import middle.llvm.llvmModule;
import middle.llvm.type.llvmArrayType;
import middle.llvm.type.llvmIntegerType;
import middle.llvm.type.llvmPointerType;
import middle.llvm.type.llvmType;
import middle.llvm.value.function.llvmFunc;
import middle.llvm.value.globalVariable.llvmGlobalVariable;
import middle.llvm.value.instructions.binary.llvmInsBinary;
import middle.llvm.value.instructions.llvmInstruction;
import middle.llvm.value.instructions.llvmInstructionType;
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

import java.util.*;

public class MipsGenModule {
    private llvmModule irModule;
    public StringBuilder sb;
    public boolean isMain;
    public HashMap<String, Boolean> isGep;
    private Map<String, Pair<String, Integer>> globalVariable = new HashMap<>();
    public MipsGenModule() {
    }
    public MipsGenModule(llvmModule module){
        this.irModule = module;
    }


    public void loadIR(llvmModule llvmModule) {
        irModule = llvmModule;
    }

    public String genMips() {
        this.sb = new StringBuilder();
        sb.append(".data\n");
//        for (GlobalVar globalVar : irModule.getGlobalVars()) {
//            if (globalVar.isString()) {
//                ConstString constString = (ConstString) globalVar.getValue();
//                IOUtils.mips("\n# " + globalVar + "\n\n");
//                IOUtils.mips(globalVar.getUniqueName() + ": .asciiz " + constString.getName() + "\n");
//            }
//        }

        sb.append("\n.text\n");
        mem = globalVariable;//先全部加到global里
        for(llvmGlobalVariable item:irModule.globalVariableArrayList){
            sb.append("\n#"+item+"\n\n");
            llvmType type = item.getValueType();
            int typeNum = this.parseGlobalVariableType(type);
            if(typeNum ==3 || typeNum == 4){
                String name = "@"+item.getName();
                getGp(name);
                int initVal = item.getSingleInitVal();
                load("$t2",String.valueOf(initVal));
                store("$t2",name);
            }else if(typeNum == 1 || typeNum == 2){
                String name = "@"+item.getName();
                ArrayList<Integer> initVal = item.getArrInitVal();
                int len = initVal.size();
                getGpArray(name,4*len);
                for(int i=0;i<len;i++){
                    sb.append("\n");
                    load("$t2",String.valueOf(initVal.get(i)));
                    load("$t0",name);
                    sb.append("addu $t0 $t0, "+(4*i)+"\n");
                    store("$t2","$t0",0);
                }
            }
        }
        System.out.println("parse global is finished!, begin main\n");
        sb.append("\njal main\n");

        int cnt = 0;
        isMain = false;
        for(llvmFunc func:irModule.funcArrayList){
            cnt++;
            if(cnt == irModule.funcArrayList.size()){
                isMain = true;
            }
            this.isGep =new HashMap<>();//每个函数一个对gep的映射
            this.mem = new HashMap<>();
            for(String name:this.globalVariable.keySet()){
                mem.put(name,this.globalVariable.get(name));
            }
            sb.append("\n"+func.getName()+":\n");
            System.out.println("begin parse func:"+func.getName()+"\n");
            rec = func.getParams().size();
            for(int i=0;rec>0;i++){
                rec--;
                load("$t0","$sp",4*rec);
                getSp(func.getParams().get(i).getName());
                store("$t0",func.getParams().get(i).getName());
            }
            rec=0;
            sb.append("\n");
            for(llvmInstruction ins:func.getInstrutions()){
                sb.append("\n#"+ins.llvmOutput().substring(0,ins.llvmOutput().length()-1)+":\n\n");
                //getsp，为每个左值
                translate(ins);
                System.out.println("parse ins "+ins.llvmOutput()+"is correct!");
            }
        }

        sb.append("li $v0, 10\n");
        sb.append("syscall\n\n");
        return this.sb.toString();
    }


    private Map<String, Pair<String, Integer>> mem = new HashMap<>();
    int gpOff = 0, spOff = 0, rec = 0;

    private void getGp(String name) {
        if (mem.containsKey(name)) {
            return;
        }
        mem.put(name, new Pair<>("$gp", gpOff));
        gpOff += 4;
    }

    private void getGpArray(String name, int offset) {
        if (mem.containsKey(name)) {
            return;
        }
        getGp(name);
        sb.append("addu $t0, $gp, " + gpOff + "\n");
        store("$t0", name);
        gpOff += offset;
    }

    private void getSp(String name) {
        if (mem.containsKey(name)) {
            return;
        }
        spOff -= 4;
        mem.put(name, new Pair<>("$sp", spOff));
    }

    private void getSpArray(String name, int offset) {
        if (mem.containsKey(name)) {
            return;
        }
        getSp(name);
        spOff -= offset;
        sb.append("addu $t0, $sp, " + spOff + "\n");
        store("$t0", name);
    }

    private void translate(llvmInstruction ir) {
        if (ir instanceof llvmInsBinary) parseBinary((llvmInsBinary) ir);
        else if (ir instanceof llvmInsCall) parseCall((llvmInsCall) ir);
        else if (ir instanceof llvmInsRet) parseRet((llvmInsRet) ir);
        else if (ir instanceof llvmInsAlloca) parseAlloca((llvmInsAlloca) ir);
        else if (ir instanceof llvmInsLoad) parseLoad((llvmInsLoad) ir);
        else if (ir instanceof llvmInsStore) parseStore((llvmInsStore) ir);
        else if (ir instanceof llvmInsGetElement) parseGEP((llvmInsGetElement) ir);
        else if (ir instanceof llvmInsBr) parseBr((llvmInsBr) ir);
        else if (ir instanceof llvmInsZext || ir instanceof llvmInsTrunc){
            parseConv(ir);
        }else if(ir instanceof llvmLabel){
            sb.append(ir.llvmOutput());
        }
    }

    private void parseBinary(llvmInsBinary b) {
        if (parseBinaryType(b)==1) calc(b, "addu");
        else if (parseBinaryType(b)==2) calc(b, "subu");
        else if (parseBinaryType(b)==3) calc(b, "mul");
        else if (parseBinaryType(b)==4) calc(b, "div");
        else if (parseBinaryType(b)==5) calc(b, "rem");
//        else if (b.isShl()) calc(b, "sll");
//        else if (b.isShr()) calc(b, "srl");
//        else if (b.isAnd()) calc(b, "and");
//        else if (b.isOr()) calc(b, "or");
        else if (parseBinaryType(b)==6) calc(b, "sle");
        else if (parseBinaryType(b)==7) calc(b, "slt");
        else if (parseBinaryType(b)==8) calc(b, "sge");
        else if (parseBinaryType(b)==9) calc(b, "sgt");
        else if (parseBinaryType(b)==10) calc(b, "seq");
        else if (parseBinaryType(b)==11) calc(b, "sne");
//        else if (b.isNot()) {
//            load("$t0", b.getOperand(0).getUniqueName());
//            IOUtils.mips("not $t1, $t0\n");
//            store("$t1", b.getUniqueName());
//        }
    }
    public int parseBinaryType(llvmInsBinary binary){
        if(binary.getInstructionType().toString().equals("add")){
            return 1;
        }else if(binary.getInstructionType().toString().equals("sub")){
            return 2;
        }else if(binary.getInstructionType().toString().equals("mul")){
            return 3;
        }else if(binary.getInstructionType().toString().equals("sdiv")){
            return 4;
        }else if(binary.getInstructionType().toString().equals("srem")){//%,mod
            return 5;
        }else if(binary.getInstructionType().toString().equals("eq")){//==
            return 10;
        }else if(binary.getInstructionType().toString().equals("ne")){//!=
            return 11;
        }else if(binary.getInstructionType().toString().equals("sgt")){//>
            return 9;
        }else if(binary.getInstructionType().toString().equals("sge")){//>=
            return 8;
        }else if(binary.getInstructionType().toString().equals("slt")){//<
            return 7;
        }else if(binary.getInstructionType().toString().equals("sle")){//<=
            return 6;
        }
        return 0;
    }

    private void calc(llvmInsBinary b, String op) {
        if (op.equals("srem")) {
            getSp(b.getResult().getName());
            load("$t0", b.getFirst().outputConstOrName());
            load("$t1", b.getSecond().outputConstOrName());
            sb.append("div $t0, $t1\n");
            sb.append("mfhi $t2\n");
            store("$t2", b.getResult().outputConstOrName());
        }else{
            getSp(b.getResult().getName());
            load("$t0", b.getFirst().outputConstOrName());
            load("$t1", b.getSecond().outputConstOrName());
            sb.append(op + " $t2, $t0, $t1\n");
            store("$t2", b.getResult().outputConstOrName());
        }

    }

    private void parseCall(llvmInsCall callInst) {
        if(callInst.getResult()!=null){
            getSp(callInst.getResult().getName());
        }
        int libFunc = callInst.getLibFunc();
        String funName = callInst.getFuncName();
        if (libFunc!=0) {
            if (Objects.equals(funName, "@getint")) {
                sb.append("li $v0, 5\n");
                sb.append("syscall\n");
                store("$v0", callInst.getResult().outputConstOrName());
            }else if (Objects.equals(funName, "@getchar")) {
                sb.append("li $v0, 12\n");
                sb.append("syscall\n");
                store("$v0", callInst.getResult().outputConstOrName());
            } else if (Objects.equals(funName, "@putint")) {
                load("$a0", callInst.getPutParam().outputConstOrName());
                sb.append("li $v0, 1\n");
                sb.append("syscall\n");
            } else if (Objects.equals(funName, "@putchar")) {
                if(callInst.getPutParam().isConst){
                    sb.append("li $a0, "+String.valueOf(callInst.getPutParam().getConstNum())+"\n");
                }else{
                    load("$a0", callInst.getPutParam().outputConstOrName());
                }
                sb.append("li $v0, 11\n");
                sb.append("syscall\n");
            } else if (Objects.equals(funName, "@putstr")) {
                sb.append("li $v0, 4\n");
                sb.append("syscall\n");
            }
        } else{
            store("$ra", "$sp", spOff - 4);
            rec = 1;
            int argSize = callInst.getParams().size();
            for (int i = 1; i <= argSize; i++) {
                rec++;
                load("$t0", callInst.getParams().get(i-1).outputConstOrName());
                store("$t0", "$sp", spOff - rec * 4);
            }
            sb.append("addu $sp, $sp, " + (spOff - rec * 4) + "\n");
            sb.append("jal " + callInst.getFuncName() + "\n");
            sb.append("addu $sp, $sp, " + (-spOff + rec * 4) + "\n");
            load("$ra", "$sp", spOff - 4);
            if (callInst.getResult()!=null) {
                store("$v0", callInst.getResult().outputConstOrName());
            }
        }
    }

    private void parseRet(llvmInsRet ret) {
        if (!ret.isVoidType()) {
            load("$v0", ret.getRetValue().outputConstOrName());
        }
        if(!isMain){
            sb.append("jr $ra\n");
        }

    }

    private void parseAlloca(llvmInsAlloca allocaInst) {
        if (allocaInst.getSelfAllocaValueType() instanceof llvmPointerType) {
            llvmPointerType pointerType = (llvmPointerType) allocaInst.getSelfAllocaValueType();
//            if (pointerType.getTargetType() instanceof IntegerType) {
//                getSp(allocaInst.getUniqueName());
//            }
            getSp(allocaInst.getResult().outputConstOrName());
        }else if(allocaInst.getSelfAllocaValueType() instanceof  llvmArrayType){
            llvmArrayType arrayType = (llvmArrayType) allocaInst.getSelfAllocaValueType();
            getSpArray(allocaInst.getResult().outputConstOrName(), 4*allocaInst.getLength());
        }
        else if (allocaInst.getSelfAllocaValueType() instanceof llvmIntegerType) {
            getSp(allocaInst.getResult().outputConstOrName());
        }
    }

    private void parseLoad(llvmInsLoad loadInst) {
        getSp(loadInst.getResult().getName());
//        if (loadInst.getOperand(0) instanceof GEPInst) {
//            load("$t0", loadInst.getOperand(0).getUniqueName());
//            load("$t1", "$t0", 0);
//            store("$t1", loadInst.getUniqueName());
//        } else {
//            load("$t0", loadInst.getOperand(0).getUniqueName());
//            store("$t0", loadInst.getUniqueName());
//        }
        String name = loadInst.getAddress().outputConstOrName();
        if(this.isGep.containsKey(name)){
            loadInst.isFromGep = true;
        }
        if(loadInst.isFromGep){
            load("$t0",loadInst.getAddress().outputConstOrName());
            load("$t1","$t0",0);
            store("$t1",loadInst.getResult().outputConstOrName());
        }else{
            load("$t0",loadInst.getAddress().outputConstOrName());
            store("$t0",loadInst.getResult().outputConstOrName());
        }


    }

    private void parseStore(llvmInsStore storeInst) {
//        if (storeInst.getOperand(1) instanceof GEPInst) {
//            load("$t0", storeInst.getOperand(0).getUniqueName());
//            load("$t1", storeInst.getOperand(1).getUniqueName());
//            store("$t0", "$t1", 0);
//        } else {
//            load("$t0", storeInst.getOperand(0).getUniqueName());
//            store("$t0", storeInst.getOperand(1).getUniqueName());
//        }
        String name = storeInst.getPointer().outputConstOrName();
        if(this.isGep.containsKey(name)){
            storeInst.isFromGep = true;
        }
        if(storeInst.isFromGep){
            if(storeInst.getValue() == null){
                load("$t0",String.valueOf(storeInst.getNum()));
            }else{
                load("$t0",storeInst.getValue().outputConstOrName());
            }
            load("$t1",storeInst.getPointer().outputConstOrName());
            sb.append("sw $t0, 0($t1)\n");
        }else{
            if(storeInst.getValue() == null){
                load("$t0",String.valueOf(storeInst.getNum()));
            }else{
                load("$t0",storeInst.getValue().outputConstOrName());
            }
            store("$t0",storeInst.getPointer().outputConstOrName());
        }

//        store("$t0",storeInst.getPointer().outputConstOrName());
    }

    private void parseGEP(llvmInsGetElement gepInst) {

//        PointerType pt = (PointerType) gepInst.getPointer().getType();
//        if (pt.isString()) {
//            IOUtils.mips("la $a0, " + gepInst.getPointer().getUniqueName() + "\n");
//            return;
//        }
//        int offsetNum;
//        List<Integer> dims;
//        if (pt.getTargetType() instanceof ArrayType) {
//            offsetNum = gepInst.getOperands().size() - 1;
//            dims = ((ArrayType) pt.getTargetType()).getDimensions();
//        } else {
//            offsetNum = 1;
//            dims = new ArrayList<>();
//        }
//        load("$t2", gepInst.getPointer().getUniqueName()); // arr
//        store("$t2", gepInst.getUniqueName());
//        int lastOff = 0;
//        for (int i = 1; i <= offsetNum; i++) {
//            int base = 4;
//            if (pt.getTargetType() instanceof ArrayType) {
//                for (int j = i - 1; j < dims.size(); j++) {
//                    base *= dims.get(j);
//                }
//            }
//            if (gepInst.getOperand(i).isNumber()) {
//                int dimOff = gepInst.getOperand(i).getNumber() * base;
//                lastOff += dimOff;
//                if (i == offsetNum) {
//                    if (lastOff == 0) {
//                        store("$t2", gepInst.getUniqueName());
//                    } else {
//                        IOUtils.mips("addu $t2, $t2, " + lastOff + "\n");
//                        store("$t2", gepInst.getUniqueName());
//                    }
//                }
//            } else {
//                if (lastOff != 0) {
//                    IOUtils.mips("addu $t2, $t2, " + lastOff + "\n");
//                }
//                load("$t1", gepInst.getOperand(i).getUniqueName()); // offset
//                IOUtils.mips("mul $t1, $t1, " + base + "\n");
//                IOUtils.mips("addu $t2, $t2, $t1\n");
//                store("$t2", gepInst.getUniqueName());
//            }
//            IOUtils.mips("\n");
//        }
//        getSp(gepInst.getValue().getName());
        if(gepInst.getElementType() instanceof llvmArrayType){
//            相当于直接复制地址的值
//            load("$t0",gepInst.getTarget().getName());
//            store("$t0",gepInst.getValue().getName());
            //不用新的指令，直接映射
            String name = gepInst.getValue().getName();
            Pair tmp = mem.get(gepInst.getTarget().getName());
            mem.put(name, tmp);
        }else{
            //此时内存里面这个位置的值就是数组首地址
            //直接相加偏移,偏移可能是常量可能是变量
            String name = gepInst.getValue().getName();
            getSp(name);
            this.isGep.put(name,true);
            load("$t0",gepInst.getTarget().getName());
            if(gepInst.getIndex()!=null){
                if(gepInst.getIndex().isConst){
                    int num = gepInst.getIndex().getConstNum();
                    sb.append("addu $t0, $t0, "+String.valueOf(num*4)+"\n");
                }else{
                    String indexName = gepInst.getIndex().getName();
                    load("$t1",indexName);
                    sb.append("li $t2, 4\n");
                    sb.append("mul $t1,$t1,$t2\n");
                    sb.append("addu $t0, $t1, $t0\n");
                }
            }else{//偏移是常量，site
                int num = gepInst.getSite();
                sb.append("addu $t0, $t0, "+String.valueOf(num*4)+"\n");
            }
            store("$t0",gepInst.getValue().getName());

        }

    }

    private void parseBr(llvmInsBr brInst) {
        if(brInst.getDst()==null){
            load("$t0",brInst.getValue().outputConstOrName());
            sb.append("beqz $t0, "+brInst.getLabel2().getName()+"\n");
            sb.append("j "+brInst.getLabel1().outputConstOrName()+"\n");
        }else{
            sb.append("j "+brInst.getDst().outputConstOrName()+"\n");
        }
    }

    private void parseConv(llvmInstruction convInst) {
        if(convInst instanceof llvmInsZext){
            llvmInsZext ins = (llvmInsZext) convInst;
            getSp(ins.getResult().getName());
            load("$t0",ins.getSource().outputConstOrName());
//            sb.append("sll $t0, $t0, 16\n");
//            sb.append("srl $t0, $t0, 16\n");
            store("$t0",ins.getResult().outputConstOrName());
        }else if(convInst instanceof  llvmInsTrunc){
            llvmInsTrunc ins = (llvmInsTrunc) convInst;
            getSp(ins.getDstValue().getName());
            load("$t0",ins.getSourceValue().outputConstOrName());
            store("$t0",ins.getDstValue().outputConstOrName());
        }
    }

    private void load(String reg, String name) {

        if (isNumber(name)) {
            sb.append("li " + reg + ", " + name + "\n");
        } else {
            sb.append("lw " + reg + ", " + mem.get(name).getSecond() + "(" + mem.get(name).getFirst() + ")\n");
        }
    }

    private void load(String reg, String name, int offset) {
        sb.append("lw " + reg + ", " + offset + "(" + name + ")\n");
    }

    private void store(String reg, String name) {
        sb.append("sw " + reg + ", " + mem.get(name).getSecond() + "(" + mem.get(name).getFirst() + ")\n");
    }

    private void store(String reg, String name, int offset) {
        sb.append("sw " + reg + ", " + offset + "(" + name + ")\n");
    }

    private boolean isNumber(String str) {
        return str.matches("-?[0-9]+");
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
