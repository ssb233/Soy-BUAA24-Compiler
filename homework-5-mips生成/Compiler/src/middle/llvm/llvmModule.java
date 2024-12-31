package middle.llvm;

import middle.llvm.value.function.llvmFunc;
import middle.llvm.value.globalVariable.llvmGlobalVariable;

import java.util.ArrayList;
//在 Module 中，可以定义多个函数(Function)，
// 每个函数都有自己的类型签名、参数列表、局部变量列表、基本块列表和属性列表等。
public class llvmModule implements llvmOutput{
    public ArrayList<llvmGlobalVariable> globalVariableArrayList;
    public ArrayList<llvmFunc> funcArrayList;

    public llvmModule(){
        this.globalVariableArrayList = new ArrayList<llvmGlobalVariable>();
        this.funcArrayList = new ArrayList<llvmFunc>();
    }

    public void addllvmGlobalVariable(llvmGlobalVariable globalVariable){
        this.globalVariableArrayList.add(globalVariable);
    }

    public void addllvmFunc(llvmFunc func){
        this.funcArrayList.add(func);
    }
    @Override
    public String llvmOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(
            "declare i32 @getint()\n" +
                    "declare i32 @getchar()\n" +
                    "declare void @putint(i32)\n" +
                    "declare void @putch(i32)\n" +
                    "declare void @putstr(i8*)\n");
        for(llvmGlobalVariable item:globalVariableArrayList){
            sb.append(item.llvmOutput());
        }
        for(llvmFunc item:funcArrayList){
            sb.append(item.llvmOutput());
        }
        return sb.toString();
    }
    public void setName(){
        if(this.funcArrayList!=null){
            for(llvmFunc func:this.funcArrayList){
                func.setAllName();
            }
        }
    }

    public ArrayList<llvmGlobalVariable> getGlobalVariableArrayList(){
        return this.globalVariableArrayList;
    }
}
