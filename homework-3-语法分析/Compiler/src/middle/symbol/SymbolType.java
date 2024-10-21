package middle.symbol;

public enum SymbolType {
    ConstChar,
    ConstInt,
    ConstCharArray,
    ConstIntArray,
    Char,
    Int,
    CharArray,
    IntArray,
    VoidFunc,
    CharFunc,
    IntFunc;

    public String toString(){
        return this.name().toString();
    }
}
