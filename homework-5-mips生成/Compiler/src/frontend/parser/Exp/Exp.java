package frontend.parser.Exp;

import frontend.lexer.Token;
import frontend.parser.parserOutput;

public class Exp implements parserOutput {
    private AddExp addExp;
    public Exp(AddExp addExp){
        this.addExp = addExp;
    }

    @Override
    public String output() {
        StringBuilder stringBuilder = new StringBuilder();
        if(addExp!=null){
            stringBuilder.append(addExp.output());
        }
        stringBuilder.append("<Exp>\n");
        return stringBuilder.toString();
    }

    public int getType(){
        if(this.addExp!=null){
            return this.addExp.getType();
        }else return 0;
    }

    public int getValue(){
        if(this.addExp!=null){
            return this.addExp.getValue();
        }
        return 0;
    }
    public AddExp getAddExp(){
        return this.addExp;
    }
}
