package backend.symbol;

//mips符号表的符号
public class mipsSymbol {
    public String name;
    public int regIndex;//当inreg时，标记变量所在寄存器的位置
    public boolean isInReg;//变量是否在寄存器当中
    public boolean isDirty;//标记当需要释放该符号所对应的寄存器时，是否需要回写内存
    public boolean haveRam;//标记当前符号是否拥有合法的offset
    public int base;// gp=28, fp=30 标记当前符号在内存中的基地址所在的寄存器，具体地，全局变量是gp，局部变量是fp
    public int offset;// 标记当前符号在内存中相对于$base的偏移
    public boolean isTmp;// 标记当前符号是否是临时变量
    public boolean isUsed;// 若本符号为临时变量，标记是否被使用过。由于LLVM IR是SSA，因此一旦被使用就可以free，且不用写回内存
    public int arrLength = 0;
    public boolean isArr;//是否为数组
    public boolean isParam = false;// 标记是否为函数参数，对于数组形参和数组全局/局部变量的处理逻辑不同

    public mipsSymbol(String name,int base){
        this.name = name;
        this.isInReg = false;
        this.regIndex = -1;
        this.base = base;
        this.haveRam = false;
        this.offset = 0;
        this.isTmp = false;
        this.isUsed = false;
    }
    //全局变量处，单值
    public mipsSymbol(String name,int base,int offset){
        this(name,base);
        this.offset = offset;
        this.isDirty = true;
        this.haveRam = true;
    }

    //binary指令构建的符号
    public mipsSymbol(String name,int base, boolean isInReg, int regIndex,boolean haveRam,int offset,boolean isTmp,boolean isUsed){
        this.name = name;
        this.base = base;
        this.isInReg = isInReg;
        this.regIndex = regIndex;
        this.haveRam = haveRam;
        this.offset = offset;
        this.isTmp = isTmp;
        this.isUsed = isUsed;
    }

    //全局变量处数组构造
    public mipsSymbol(String name,int base, boolean isInReg, int regIndex,boolean haveRam,int offset,boolean isTmp,boolean isUsed,int arrLength, boolean isArr){
        this.name = name;
        this.base = base;
        this.isInReg = isInReg;
        this.regIndex = regIndex;
        this.haveRam = haveRam;
        this.offset = offset;
        this.isTmp = isTmp;
        this.isUsed = isUsed;
        this.arrLength = arrLength;
        this.isArr = isArr;
    }

    //函数前四个参数
    public mipsSymbol(String name,int base,boolean isInReg, int regIndex, boolean isTmp){
        this.name = name;
        this.base = base;
        this.isInReg = isInReg;
        this.regIndex = regIndex;
        this.isTmp = isTmp;
    }
    //函数参数多余的参数，有的可能为数组
    public mipsSymbol(String name,int base, boolean isInReg, boolean haveRam,int offset,boolean isTmp, boolean isArr){
        this.name = name;
        this.base = base;
        this.isInReg = isInReg;
        this.haveRam = haveRam;
        this.offset = offset;
        this.isTmp = isTmp;
        this.isArr = isArr;
    }

    public mipsSymbol cloneMipsSymbol(){
        mipsSymbol symbol = new mipsSymbol(this.name,this.base,this.isInReg,this.regIndex,this.haveRam,this.offset,this.isTmp,this.isUsed);
        return symbol;
    }
}
