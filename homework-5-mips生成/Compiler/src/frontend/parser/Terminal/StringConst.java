package frontend.parser.Terminal;
import frontend.lexer.Token;
import frontend.parser.parserOutput;
public class StringConst implements parserOutput{
    private Token token;
    public StringConst(Token token){
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

    public int getMatchNum(){
        int num = 0;
        String string = this.token.getTokenValue();
        int len = string.length();
        for(int i=0;i<len;i++){
            if(string.charAt(i)=='%'&&i!=len-1){
                if(string.charAt(i+1)=='c'||string.charAt(i+1)=='d'){
                    num++;
                    i++;
                }
            }
        }
        return num;
    }

    public String getString(){
        if(this.token!=null){
            return this.token.getTokenValue();
        }
        return null;
    }
}
