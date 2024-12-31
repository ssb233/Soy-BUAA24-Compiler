package frontend.parser.Exp;

import frontend.lexer.Token;
import frontend.parser.parserOutput;

import java.util.ArrayList;

public class PrimaryExp implements parserOutput {
    private PrimaryExpExtend primaryExpExtend;

    public PrimaryExp(PrimaryExpExtend primaryExpExtend){
        this.primaryExpExtend = primaryExpExtend;
    }
    @Override
    public String output() {
        StringBuilder stringBuilder = new StringBuilder();
        if(primaryExpExtend!=null){
            stringBuilder.append(primaryExpExtend.output());
        }
        stringBuilder.append("<PrimaryExp>\n");
        return stringBuilder.toString();
    }

    public int getType(){
        if(primaryExpExtend!=null){
            if(primaryExpExtend instanceof ExpLR){
                ExpLR expLR = (ExpLR) primaryExpExtend;
                return expLR.getType();
            }else if(primaryExpExtend instanceof  LVal){
                LVal lVal = (LVal) primaryExpExtend;
                return lVal.getType();
            }else if(primaryExpExtend instanceof ConstNumber){
                ConstNumber  constNumber = (ConstNumber) primaryExpExtend;
                return constNumber.getType();
            }else if(primaryExpExtend instanceof ConstChar){
                ConstChar constChar = (ConstChar) primaryExpExtend;
                return constChar.getType();
            }
        }
        return 0;
    }

    public int getValue(){
        if(this.primaryExpExtend!=null){
            return this.primaryExpExtend.getValue();
        }
        return 0;
    }
    public PrimaryExpExtend getPrimaryExpExtend(){
        return this.primaryExpExtend;
    }
}
