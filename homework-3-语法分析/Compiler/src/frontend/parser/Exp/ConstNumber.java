package frontend.parser.Exp;

import frontend.parser.Terminal.IntConst;
import frontend.parser.parserOutput;

public class ConstNumber extends PrimaryExpExtend implements parserOutput {
    private IntConst intConst;
    public ConstNumber(IntConst intConst){
        this.intConst = intConst;
    }

    @Override
    public String output() {
        StringBuilder stringBuilder = new StringBuilder();
        if(intConst!=null){
            stringBuilder.append(intConst.output());
        }
        stringBuilder.append("<Number>\n");
        return stringBuilder.toString();
    }
}
