package frontend.lexer;

public enum TokenType {
    //保留关键字
    MAINTK(false, "main"),
    CONSTTK(false, "const"),
    INTTK(false, "int"),
    CHARTK(false, "char"),
    BREAKTK(false, "break"),
    CONTINUETK(false, "continue"),
    IFTK(false, "if"),
    ELSETK(false, "else"),
    FORTK(false, "for"),
    GETINTTK(false, "getint"),
    GETCHARTK(false, "getchar"),
    PRINTFTK(false, "printf"),
    RETURNTK(false, "return"),
    VOIDTK(false, "void"),


    //
    NEQ(false, "!="),//先于!匹配
    NOT(false, "!"),
    AND(false, "&&"),
    OR(false, "||"),
    //
    PLUS(false, "+"),
    MINU(false, "-"),
    MULT(false, "*"),
    DIV(false, "/"),
    MOD(false, "%"),
    //
    LEQ(false, "<="),
    LSS(false, "<"),
    GEQ(false, ">="),
    GRE(false, ">"),
    EQL(false, "=="),
    ASSIGN(false, "="),
    //
    SEMICN(false, ";"),
    COMMA(false, ","),
    LPARENT(false, "("),
    RPARENT(false, ")"),
    LBRACK(false, "["),
    RBRACK(false, "]"),
    LBRACE(false, "{"),
    RBRACE(false, "}"),
    //Ident, IntConst, StringConst, CharConst
    IDENFR(true, "[_A-Za-z][_A-Za-z0-9]*"),
    INTCON(true, "(0|[1-9][0-9]*)"),
    STRCON(false, "stringConst"),
    CHRCON(false, "charConst");
    private boolean judge;//reserve

    private String patternString;
    TokenType(boolean judge,String patternString){
        this.judge = judge;
        this.patternString = patternString;
    }

    public boolean getJudge(){//judge决定是字符串比较还是正则匹配
        return this.judge;
    }
    public String getPatternString(){
        return this.patternString;
    }
    public String toString(){
        return this.name().toString();
    }
}
