package middle.llvm.constant;

import middle.llvm.llvmOutput;
import middle.llvm.llvmUser;
import middle.llvm.type.llvmType;

import java.util.ArrayList;

public class llvmConstant extends llvmUser implements llvmOutput {
    public llvmConstant(llvmType type){
        super(type);
    }

    public String llvmOutput(){
        return null;
    }
}
