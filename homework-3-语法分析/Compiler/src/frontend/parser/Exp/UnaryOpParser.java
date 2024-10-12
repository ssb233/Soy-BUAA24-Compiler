package frontend.parser.Exp;

import frontend.GlobalParm;
import frontend.lexer.Token;

public class UnaryOpParser {
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
