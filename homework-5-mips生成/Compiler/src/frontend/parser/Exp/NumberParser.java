package frontend.parser.Exp;

import frontend.GlobalParm;
import frontend.lexer.Token;
import frontend.parser.Terminal.IntConst;
import middle.symbol.SymbolTable;

public class NumberParser {
    private IntConst intConst;
    private SymbolTable symbolTable;//来自父元素表，不用自己建表

    public NumberParser(SymbolTable symbolTable){
        this.symbolTable = symbolTable;
    }
    public NumberParser(){

    }
    public ConstNumber NumberParser(){
        Token token = GlobalParm.getAToken();
        if(token.getTokenTypeName().equals("INTCON")){
            this.intConst = new IntConst(token);
        }else{
            GlobalParm.backAToken();
            return null;
        }
        ConstNumber constNumber = new ConstNumber(this.intConst);
        return constNumber;
    }
}
