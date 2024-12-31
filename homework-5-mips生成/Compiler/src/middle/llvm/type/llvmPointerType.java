package middle.llvm.type;

import java.util.ArrayList;

public class llvmPointerType extends llvmType{
    private llvmType pointerType;//指针类型,这里是指向的类型
    public llvmPointerType(llvmType pointerType){
        this.pointerType = pointerType;
    }

    public String llvmOutput(){
        StringBuilder sb = new StringBuilder();
        sb.append(this.pointerType.llvmOutput()+"*");
        return sb.toString();
    }
}
