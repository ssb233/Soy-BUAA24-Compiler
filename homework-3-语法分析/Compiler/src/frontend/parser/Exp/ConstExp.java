package frontend.parser.Exp;

import frontend.parser.parserOutput;

public class ConstExp implements parserOutput {
    private AddExp addExp=null;

    public ConstExp(AddExp addExp){
        this.addExp = addExp;
    }
    @Override
    public String output() {
        StringBuilder stringBuilder = new StringBuilder();
        if(addExp!=null){
            stringBuilder.append(addExp.output());
        }
        stringBuilder.append("<ConstExp>\n");
        return stringBuilder.toString();
    }
}
