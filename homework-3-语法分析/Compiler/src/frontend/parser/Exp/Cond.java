package frontend.parser.Exp;

import frontend.parser.parserOutput;

public class Cond implements parserOutput {
    private LOrExp lOrExp;

    public Cond(LOrExp lOrExp){
        this.lOrExp = lOrExp;
    }
    @Override
    public String output() {
        StringBuilder stringBuilder = new StringBuilder();
        if(lOrExp!=null){
            stringBuilder.append(lOrExp.output());
        }
        stringBuilder.append("<Cond>\n");
        return stringBuilder.toString();
    }
}
