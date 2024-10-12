package frontend.parser.Exp;

public class CondParser {
    private LOrExp lOrExp;

    public Cond CondParser(){
        LOrExpParser lOrExpParser = new LOrExpParser();
        this.lOrExp = lOrExpParser.LOrExpParser();
        if(this.lOrExp==null)return null;
        Cond cond = new Cond(this.lOrExp);
        return cond;
    }
}
