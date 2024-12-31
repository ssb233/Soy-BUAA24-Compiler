package middle.llvm.value.instructions;

public enum llvmInstructionType {
    //binary二元指令
    add,//<result> = add <ty> <op1>, <op2> +
    sub,//<result> = sub <ty> <op1>, <op2> -
    mul,//<result> = mul <ty> <op1>, <op2> *,  <result> = shl <ty> <op1>, <op2>左移
    sdiv,//<result> = sdiv <ty> <op1>, <op2>有符号除法, div /
    srem,//<result> = srem <ty> <op1>, <op2>, mod %

    and,//<result> = and <ty> <op1>, <op2>按位与
    or, //<result> = or <ty> <op1>, <op2>按位或

    eq,//<result> = icmp eq <ty> <op1> , <op2>等于
    ne,//<result> = icmp ne <ty> <op1> , <op2>不等于
    sgt,//signed greater than  >
    sge,//signed greater or equal >=
    slt,//signed less than <
    sle,//signed less or equal <=


    call,//函数调用 call ty func,,,%4 = call i32 @getchar()  ; 注意 getchar 的返回值类型
    trunc,//,%5 = trunc i32 %4 to i8   ; 不可避免地进行一次类型转换
    zext,//<result> = zext <ty> <value> to <ty2>, 扩充

    //Terminator终止指令
    ret,//ret <type> <value>       ; Return a value from a non-void function
        //ret void                 ; Return from void function
    br, //br i1 <cond>, label <iftrue>, label <iffalse>
        //br label <dest>          ; Unconditional branch
    Goto,//goto <label>

    //mem op内存操作指令
    alloca,//alloca i32, alloca [3 * i32], %1 = alloca i32
    load,//<result> = load <ty>, ptr <pointer>
    store,//store <ty> <value> , ptr <pointer>, store i32 2, i32* %3
    GLE,//getElement, %3 = getelementptr i32, i32* @a, i32 3

    Label;
    public String toString(){
        return this.name().toString();
    }
}
