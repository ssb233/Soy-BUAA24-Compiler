package middle.llvm.constant;

import middle.llvm.llvmOutput;
import middle.llvm.type.llvmType;

import java.util.ArrayList;

public class llvmConstantChar extends llvmConstant implements llvmOutput {
    private int initVal;

    public llvmConstantChar(llvmType type) {
        super(type);
    }
    public llvmConstantChar(llvmType type, int initVal) {
        super(type);
        this.initVal = initVal;
    }
    public String llvmOutput(){
        StringBuilder sb = new StringBuilder();
        sb.append(this.getValueType().llvmOutput()+" "+String.valueOf(this.initVal));
        return sb.toString();
    }
}
