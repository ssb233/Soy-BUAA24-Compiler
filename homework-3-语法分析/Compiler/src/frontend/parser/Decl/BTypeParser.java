package frontend.parser.Decl;

import frontend.GlobalParm;
import frontend.lexer.Token;

public class BTypeParser {
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
