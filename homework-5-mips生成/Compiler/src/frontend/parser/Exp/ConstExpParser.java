package frontend.parser.Exp;

import frontend.GlobalParm;
import frontend.lexer.Token;
import middle.symbol.SymbolTable;

public class ConstExpParser {
    private AddExp addExp=null;

    public SymbolTable symbolTable;
    public ConstExpParser(SymbolTable symbolTable){
        this.symbolTable = symbolTable;
    }
    
    public ConstExp ConstExpParser(){
        AddExpParser addExpParser = new AddExpParser(this.symbolTable);
        this.addExp = addExpParser.AddExpParser();
        if(this.addExp==null)return null;
        ConstExp constExp = new ConstExp(addExp);
        return constExp;
    }
}
