package middle.llvm.type;

import java.util.ArrayList;

public class llvmVoidType extends llvmType{
    private String name = "voidType";
    private static llvmVoidType voidType = new llvmVoidType();
    //void返回类型
    private llvmVoidType(){
        this.name = "voidType";
    }
    public static llvmVoidType getVoidType(){
        return voidType;
    }

    public String llvmOutput(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("void");
        return stringBuilder.toString();
    }
}
