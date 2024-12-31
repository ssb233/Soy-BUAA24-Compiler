package frontend.parser.Exp;

import frontend.lexer.Token;
import middle.symbol.SymbolTable;

public class ExpParser {
    private AddExp addExp;

    public SymbolTable symbolTable;
    public ExpParser(SymbolTable symbolTable){
        this.symbolTable = symbolTable;
    }

    public ExpParser(){
    }
    public Exp ExpParser(){
        AddExpParser addExpParser = new AddExpParser(this.symbolTable);
        this.addExp = addExpParser.AddExpParser();
        if(this.addExp==null)return null;
        Exp exp = new Exp(addExp);
        return exp;
    }
}
