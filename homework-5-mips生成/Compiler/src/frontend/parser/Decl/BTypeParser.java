package frontend.parser.Decl;

import frontend.GlobalParm;
import frontend.lexer.Token;
import middle.symbol.SymbolTable;

public class BTypeParser {
    private SymbolTable symbolTable;//来自父元素表，不用自己建表

    public BTypeParser(SymbolTable symbolTable){
        this.symbolTable = symbolTable;
    }
    public BType BTypeParser(){
        Token token = GlobalParm.getAToken();
        BType bType;
        if(token.getTokenTypeName().equals("INTTK")||token.getTokenTypeName().equals("CHARTK")){
            bType = new BType(token);
        }else{
            GlobalParm.backAToken();
            bType = null;//不匹配
        }

        return bType;
    }
}
