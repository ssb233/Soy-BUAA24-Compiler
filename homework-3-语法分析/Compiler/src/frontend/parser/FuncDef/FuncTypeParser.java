package frontend.parser.FuncDef;

import frontend.GlobalParm;
import frontend.lexer.Token;

public class FuncTypeParser {

    public FuncType FuncTypeParser(){
        Token token = GlobalParm.getAToken();
        FuncType funcType;
        if(token.getTokenTypeName().equals("VOIDTK")||token.getTokenTypeName().equals("INTTK")||token.getTokenTypeName().equals("CHARTK")) funcType = new FuncType(token);
        else{
            GlobalParm.backAToken();
            funcType = null;
        }
        return funcType;
    }
}
