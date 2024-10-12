package frontend.parser.Exp;

import frontend.GlobalParm;
import frontend.lexer.Token;
import frontend.parser.Terminal.CharConst;

public class CharParser {
    private CharConst charConst;
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
