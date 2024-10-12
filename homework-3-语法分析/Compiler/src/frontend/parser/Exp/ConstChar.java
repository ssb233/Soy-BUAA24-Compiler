package frontend.parser.Exp;

import frontend.parser.Terminal.CharConst;
import frontend.parser.parserOutput;

public class ConstChar extends PrimaryExpExtend implements parserOutput {
    private CharConst charConst;
    public ConstChar(CharConst charConst){
        this.charConst = charConst;
    }

    @Override
    public String output() {
        StringBuilder stringBuilder = new StringBuilder();
        if(charConst!=null){
            stringBuilder.append(charConst.output());
        }
        stringBuilder.append("<Character>\n");
        return stringBuilder.toString();
    }
}
