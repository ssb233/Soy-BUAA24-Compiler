package middle.llvm.value.instructions.memOp;

import middle.llvm.llvmValue;
import middle.llvm.type.llvmArrayType;
import middle.llvm.type.llvmIntegerType;
import middle.llvm.type.llvmPointerType;
import middle.llvm.type.llvmType;
import middle.llvm.value.function.llvmFuncCnt;
import middle.llvm.value.instructions.llvmInstruction;
import middle.llvm.value.instructions.llvmInstructionType;

public class llvmInsGetElement extends llvmInstruction {
    private llvmType elementType;//给出元素的类型就行
    private llvmValue target;//访问的数组
    private llvmValue index;//访问元素的位置
    private llvmValue value;//操作的左值，result
    private int site;//访问元素的位置

    //统一采用 %x = getelement inbound ty, ty* @arr, I32 index的形式
    //%3 = getelementptr ty, ty* %x, i32 0, i32 index
    //除开形参的数组
    //采用%3 = get  elementType, elementTYpe* %address, I32 index.
    //形参的数组,%6 = load i32*, i32** %4, 在符号表里面存load的值
    //    %7 = getelementptr inbounds i32, i32* %6, i32 2
    //value 就是左侧的结果
    //下面这个构造函数，%result = getelement i32, i32* %target, %index;
    public llvmInsGetElement(llvmType elementType, llvmValue value,llvmValue target, llvmValue index) {
        super(new llvmPointerType(elementType),llvmInstructionType.GLE,2);
        this.elementType = elementType;
        this.target = target;
        this.index = index;
        this.value = value;
        this.value.setValueType(elementType);
    }
    //%address = get [length x i8/i32], [length x i8/i32]* %result, i32 0 , i32 0;
    //%ad = get ty, ty* %target, i32 site两种情况都走下面
    public llvmInsGetElement(llvmType elementType, llvmValue value,llvmValue target, int site) {
        super(new llvmPointerType(elementType),llvmInstructionType.GLE,2);
        this.elementType = elementType;
        this.target = target;
        this.site = site;
        this.value = value;
        this.index = null;
        if(elementType instanceof llvmArrayType){//[length x ty] -> ty*
            llvmArrayType arrayType = (llvmArrayType)elementType;
            this.value.setValueType(new llvmPointerType(arrayType.getElementType()));
        }else{
            this.value.setValueType(elementType);
        }
    }
    public void setAllName(llvmFuncCnt funcCnt, String funcName){
        this.value.setName("%"+String.valueOf(funcCnt.getCnt()));
    }


    public String llvmOutput(){
        StringBuilder sb = new StringBuilder();
        sb.append(this.value.getName());
        sb.append(" = getelementptr ");
        if(this.elementType instanceof llvmIntegerType){
            sb.append(this.elementType.llvmOutput());
        }else if(this.elementType instanceof llvmArrayType){//site 存length
            llvmArrayType arrayType = (llvmArrayType) this.elementType;
            sb.append("["+String.valueOf(this.site)+" x "+arrayType.getElementType().llvmOutput()+"]");
        }else{//指针类型
            sb.append(this.elementType.llvmOutput());
        }
        sb.append(", ");
        if(this.elementType instanceof llvmIntegerType){
            sb.append(this.elementType.llvmOutput());
        }else if(this.elementType instanceof llvmArrayType){//site 存length
            llvmArrayType arrayType = (llvmArrayType) this.elementType;
            sb.append("["+String.valueOf(this.site)+" x "+arrayType.getElementType().llvmOutput()+"]");
        }else{//指针类型
            sb.append(this.elementType.llvmOutput());
        }
        sb.append("* ");
        sb.append(target.getName());
        sb.append(", i32 ");
        if(this.index!=null){//正常情况
//            if(index.isConst){
//                sb.append(index.getConstNum());
//            }else{
//                sb.append(index.getName());
//            }
            sb.append(index.outputConstOrName());

        }else if(this.elementType instanceof llvmArrayType){//[length x type]情况, i32 0, i32 0
            sb.append("0, i32 0");
        }else {//i32 site情况
            sb.append(String.valueOf(site));
        }
        sb.append("\n");
        return sb.toString();
    }
    public llvmType getElementType(){
        return this.elementType;
    }
    public llvmValue getValue(){
        return this.value;
    }
    public llvmValue getTarget(){
        return this.target;
    }
    public llvmValue getIndex(){
        return this.index;
    }
    public int getSite(){
        return this.site;
    }
}
