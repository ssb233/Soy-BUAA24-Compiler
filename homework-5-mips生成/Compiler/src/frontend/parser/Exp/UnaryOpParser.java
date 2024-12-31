package frontend.parser.Exp;

import frontend.GlobalParm;
import frontend.lexer.Token;
import middle.symbol.SymbolTable;

public class UnaryOpParser {
    private SymbolTable symbolTable;//来自父元素表，不用自己建表

    public UnaryOpParser(SymbolTable symbolTable){
        this.symbolTable = symbolTable;
    }
    public UnaryOp UnaryOpParser(){
        Token token = GlobalParm.getAToken();
        UnaryOp unaryOp;
        if(token.getTokenTypeName().equals("PLUS")||token.getTokenTypeName().equals("MINU")||token.getTokenTypeName().equals("NOT")) unaryOp = new UnaryOp(token);
        else{
            GlobalParm.backAToken();
            return null;
        }
        return unaryOp;
    }
}
