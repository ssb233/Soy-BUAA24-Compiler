package middle.llvm.type;

import java.util.ArrayList;

public class llvmIntegerType extends llvmType{
    private int bitWidth;//位宽， 8或者32
    private String name;
    private static llvmIntegerType I32 = new llvmIntegerType(32);
    private static llvmIntegerType I1 = new llvmIntegerType(1);
    private static llvmIntegerType I8 = new llvmIntegerType(8);
    private llvmIntegerType(int bitWidth){
        this.bitWidth = bitWidth;
        this.name = "i"+String.valueOf(bitWidth);
    }

    public static llvmIntegerType getI32(){return I32;}
    public static llvmIntegerType getI8(){return I8;}
    public static llvmIntegerType getI1(){return I1;}
    public String getName(){
        return this.name;
    }

    public String llvmOutput(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("i"+String.valueOf(this.bitWidth));
        return stringBuilder.toString();
    }
}
