package frontend.parser.Terminal;
import frontend.lexer.Token;
import frontend.parser.parserOutput;
public class CharConst implements parserOutput{

    private Token token;
    public CharConst(Token token){
        this.token = token;
    }

    @Override
    public String output() {
        StringBuilder stringBuilder = new StringBuilder();
        if(token !=null){
            stringBuilder.append(token.output());
        }
        return stringBuilder.toString();
    }

    public int getValue(){
        if(this.token!=null){
            return (int) this.token.getNumValue();
        }
        return 0;
    }
}
