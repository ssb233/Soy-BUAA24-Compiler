package middle.llvm.type;

import java.util.ArrayList;

public class llvmArrayType extends llvmType{
    private llvmType valueType;//数组元素类型
    public llvmArrayType(llvmType valueType){
        this.valueType = valueType;

    }
    public llvmType getElementType(){
        return this.valueType;
    }
    @Override
    public String  llvmOutput(){
        StringBuilder sb  = new StringBuilder();



        return sb.toString();
    }
}
