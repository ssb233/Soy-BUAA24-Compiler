package frontend.parser.Exp;

import frontend.lexer.Token;

public class ExpParser {
    private AddExp addExp;
    public ExpParser(){
    }
    public Exp ExpParser(){
        AddExpParser addExpParser = new AddExpParser();
        this.addExp = addExpParser.AddExpParser();
        if(this.addExp==null)return null;
        Exp exp = new Exp(addExp);
        return exp;
    }
}
