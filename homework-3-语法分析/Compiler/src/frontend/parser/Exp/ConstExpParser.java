package frontend.parser.Exp;

import frontend.GlobalParm;
import frontend.lexer.Token;

public class ConstExpParser {
    private AddExp addExp=null;
    
    public ConstExp ConstExpParser(){
        AddExpParser addExpParser = new AddExpParser();
        this.addExp = addExpParser.AddExpParser();
        if(this.addExp==null)return null;
        ConstExp constExp = new ConstExp(addExp);
        return constExp;
    }
}
