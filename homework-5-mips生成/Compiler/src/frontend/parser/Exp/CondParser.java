package frontend.parser.Exp;

import middle.symbol.SymbolTable;

public class CondParser {
    private LOrExp lOrExp;

    public SymbolTable symbolTable;
    public CondParser(SymbolTable symbolTable){
        this.symbolTable = symbolTable;
    }

    public Cond CondParser(){
        LOrExpParser lOrExpParser = new LOrExpParser(this.symbolTable);
        this.lOrExp = lOrExpParser.LOrExpParser();
        if(this.lOrExp==null)return null;
        Cond cond = new Cond(this.lOrExp);
        return cond;
    }
}
