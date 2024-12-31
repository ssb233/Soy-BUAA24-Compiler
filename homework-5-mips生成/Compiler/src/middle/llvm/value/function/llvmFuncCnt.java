package middle.llvm.value.function;

public class llvmFuncCnt {
    private int cnt = 0;
    public llvmFuncCnt(){
        this.cnt = 0;
    }
    public int getCnt(){
        int num = this.cnt;
        this.cnt++;
        return num;
    }
}
