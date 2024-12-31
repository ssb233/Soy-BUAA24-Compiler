package middle.llvm.constant;

import middle.llvm.llvmOutput;
import middle.llvm.type.llvmType;

import java.util.ArrayList;

public class llvmConstantInt extends llvmConstant implements llvmOutput {

    private int initVal;//值
    public llvmConstantInt(llvmType type) {
        super(type);
    }
    public llvmConstantInt(llvmType type, int val){
        super(type);
        this.initVal = val;
    }

    public String llvmOutput(){
        StringBuilder sb = new StringBuilder();
        sb.append(this.getValueType().llvmOutput()+" "+String.valueOf(this.initVal));
        return sb.toString();
    }
    public int getInitVal(){
        return this.initVal;
    }

}
