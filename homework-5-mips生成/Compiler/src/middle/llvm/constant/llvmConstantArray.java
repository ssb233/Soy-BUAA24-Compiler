package middle.llvm.constant;

import middle.llvm.llvmOutput;
import middle.llvm.type.llvmArrayType;
import middle.llvm.type.llvmType;

import java.util.ArrayList;

public class llvmConstantArray extends llvmConstant implements llvmOutput {
    private ArrayList<Integer> integers;
    public llvmConstantArray(llvmType type) {
        super(type);
    }

    public void setIntegers(ArrayList<Integer> list){
        this.integers = list;
    }

    public String llvmOutput(){
        StringBuilder sb = new StringBuilder();
        sb.append("["+String.valueOf(this.integers.size())+" x ");
        llvmType elementType = null;
        if(this.getValueType() instanceof llvmArrayType){
            llvmArrayType arrayType = (llvmArrayType) this.getValueType();
            elementType = arrayType.getElementType();
            sb.append(arrayType.getElementType().llvmOutput());
        }
        sb.append("] ");

        //是否要全部为0
        int flag = 0;
        for(Integer item:integers){
            if(item!=0){
                flag=1;break;
            }
        }
        if(flag==0){
            sb.append("zeroinitializer");
        }else{
            sb.append("[");
            int len = integers.size();
            sb.append(elementType.llvmOutput()+" "+String.valueOf(integers.get(0)));
            for(int i=1;i<len;i++){
                sb.append(", "+elementType.llvmOutput()+" "+String.valueOf(integers.get(i)));
            }
            sb.append("]");
        }
        return sb.toString();
    }
    public ArrayList<Integer> getIntegers(){
        return this.integers;
    }
}
