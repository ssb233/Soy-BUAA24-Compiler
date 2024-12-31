package middle.llvm.value.basicblock;

//用来对应符号表
public class llvmSymbolTableCnt {
    private int cnt = 0;
    public llvmSymbolTableCnt(){
        this.cnt = 0;
    }
    public int getCnt(){
        int num = this.cnt;
        this.cnt++;
        return num;
    }
}
