package middle.symbol;

import frontend.lexer.Token;

public class Symbol {
    public int lineNum;//符号行号
    public String name;//符号名字
    public SymbolType symbolType;//符号类型
    public SymbolTable symbolTable;//属于哪个符号表
    public Token token;
    public int tableId;

}
