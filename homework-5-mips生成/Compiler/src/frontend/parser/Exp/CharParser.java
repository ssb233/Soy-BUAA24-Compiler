package frontend.parser.Exp;

import frontend.GlobalParm;
import frontend.lexer.Token;
import frontend.parser.Terminal.CharConst;
import middle.symbol.SymbolTable;

public class CharParser {
    private CharConst charConst;
    private SymbolTable symbolTable;//来自父元素表，不用自己建表

    public CharParser(SymbolTable symbolTable){
        this.symbolTable = symbolTable;
    }
    public CharParser(){

    }
    public ConstChar CharParser(){
        Token token = GlobalParm.getAToken();
        if(token.getTokenTypeName().equals("CHRCON")){
            this.charConst = new CharConst(token);
        }else{
            GlobalParm.backAToken();
            return null;
        }
        ConstChar constChar = new ConstChar(this.charConst);
        return constChar;
    }
}
