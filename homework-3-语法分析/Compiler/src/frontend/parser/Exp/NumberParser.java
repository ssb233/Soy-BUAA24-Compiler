package frontend.parser.Exp;

import frontend.GlobalParm;
import frontend.lexer.Token;
import frontend.parser.Terminal.IntConst;

public class NumberParser {
    private IntConst intConst;
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
