package middle.llvm;

public class llvmUse {
    private llvmValue value;//当前use的值
    private llvmUser user;//描述自己的user，用于追溯某个value的user
    private int oprandRank;//当前use所在的位置

    public llvmUse(llvmValue value, llvmUser user, int oprandRank){
        this.value = value;
        this.user = user;
        this.oprandRank = oprandRank;
        //在各自中维护这条双向边
        this.value.addUse(this);
        this.user.addUse(this);
    }
    public int getOprandRank(){
        return this.oprandRank;
    }
    public llvmValue getValue(){
        return this.value;
    }
    public void setValue(llvmValue value){
        this.value = value;
    }
}
