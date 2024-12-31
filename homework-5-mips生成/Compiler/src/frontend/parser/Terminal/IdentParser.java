package frontend.parser.Terminal;

import frontend.GlobalParm;
import frontend.lexer.Token;

public class IdentParser {
    private Token token;
    public Ident IdentParser(){
        Token token = GlobalParm.getAToken();
        if(token.getTokenTypeName().equals("IDENFR")){
            this.token = token;
            return new Ident(this.token);
        }else{
            GlobalParm.backAToken();
            return null;//不是ident
        }
    }
}
