package middle.llvm.value.instructions.other;

import middle.llvm.llvmValue;
import middle.llvm.type.llvmIntegerType;
import middle.llvm.type.llvmType;
import middle.llvm.type.llvmVoidType;
import middle.llvm.value.function.llvmFunc;
import middle.llvm.value.function.llvmFuncCnt;
import middle.llvm.value.instructions.llvmInstruction;
import middle.llvm.value.instructions.llvmInstructionType;
import middle.symbol.Symbol;
import middle.symbol.SymbolFunc;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class llvmInsCall extends llvmInstruction {
    //<result> = call  <ty> <name>(<...args>), 无返回值的直接call，不用给结果
//    declare i32 @getint()   1
//    declare i32 @getchar()  2
//    declare void @putint(i32)  3
//    declare void @putch(i32) 4
//    declare void @putstr(i8*)  5
    private llvmType funcRetType;
    private String  funcName;
    private ArrayList<llvmValue> params;
    private llvmValue result;
    private llvmValue putParam;
    private int libFunc;
    public llvmInsCall(String funcName,ArrayList<llvmValue> params){//void
        super(llvmVoidType.getVoidType(), llvmInstructionType.call,1);
        this.funcRetType = llvmVoidType.getVoidType();
        this.funcName = funcName;
        this.params = params;
        this.result = null;
    }
    public llvmInsCall(llvmType funcRetType,String funcName, llvmValue result, ArrayList<llvmValue> params){
        super(llvmVoidType.getVoidType(), llvmInstructionType.call,1);
        this.funcRetType = funcRetType;
        this.funcName = funcName;
        this.result = result;
        this.result.setValueType(funcRetType);
        this.params = params;
    }
    public llvmInsCall(llvmType funcRetType,int libFunc, llvmValue result){//库函数输入
        super(llvmVoidType.getVoidType(), llvmInstructionType.call,1);
        this.funcRetType = funcRetType;
        this.libFunc = libFunc;
        this.result = result;
    }
    public llvmInsCall(int libFunc,llvmValue value){//库函数输出，put
        super(llvmVoidType.getVoidType(), llvmInstructionType.call,1);
        this.funcRetType = llvmVoidType.getVoidType();
        this.funcName = null;
        this.result = null;
        this.libFunc = libFunc;
        this.putParam = value;//输出的参数值
    }

    public void setAllName(llvmFuncCnt funcCnt, String funcName){
        if(this.result!=null){
            this.result.setName("%"+String.valueOf(funcCnt.getCnt()));
        }
    }
    public String llvmOutput(){
        StringBuilder sb = new StringBuilder();
        if(result!=null){
            sb.append(result.getName()+" = ");
        }
        sb.append("call ");
        if(this.funcRetType!=null){
            sb.append(funcRetType.llvmOutput()+" ");
        }else{
            sb.append("void ");
        }
        if(this.funcName!=null){//自定义函数输出
            sb.append("@"+this.funcName+"(");
            for(llvmValue value:params){
                if(value!=null)
                sb.append(value.getValueType().llvmOutput()+" "+value.outputConstOrName()+" ,");
            }
            if(params.size()!=0){
                sb.deleteCharAt(sb.length()-1);
            }
            sb.append(")");
        }else{//库函数输出
            switch (this.libFunc){
                case 1:sb.append("@getint()");
                    break;
                case 2:sb.append("@getchar()");
                    break;
                case 3:sb.append("@putint("+"i32"+" ");
                if(putParam.isConst){
                    sb.append(String.valueOf(putParam.getConstNum()));
                }else{
                    sb.append(putParam.getName());
                }
                sb.append(")");
                    break;
                case 4:sb.append("@putch("+"i32"+" ");
                    if(putParam.isConst){
                        sb.append(String.valueOf(putParam.getConstNum()));
                    }else{
                        sb.append(putParam.getName());
                    }
                    sb.append(")");
                    break;
                case 5:sb.append("fuckman");//没有使用到
                    break;
            }
        }
        sb.append("\n");
        return sb.toString();
    }

    public int getLibFunc(){
        return this.libFunc;
    }
    public llvmValue getPutParam(){
        return this.putParam;
    }
    public String getFuncName(){
        if(this.libFunc!=0){
            switch (this.libFunc){
                case 1:return "@getint";
                case 2:return "@getchar";
                case 3:return "@putint";
                case 4:return "@putchar";
                case 5:return "@putstr";
            }
        }else{
            return this.funcName;
        }
        return  null;
    }
    public llvmValue getResult(){
        return this.result;
    }
    public ArrayList<llvmValue> getParams(){
        ArrayList<llvmValue> list = new ArrayList<>();
        if(this.params!=null){
            for(llvmValue item:this.params){
                list.add(item);
            }
        }
        return list;
    }
}
