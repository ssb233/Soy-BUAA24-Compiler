package frontend.parser.Decl;

import frontend.lexer.Token;
import frontend.parser.Exp.ConstExp;
import frontend.parser.Terminal.StringConst;
import frontend.parser.parserOutput;

import java.util.ArrayList;

public class ConstInitVal implements parserOutput {
    private ConstExp constExp;
    private ConstExp constExpFirst;
    private ArrayList<Token> opTokens;
    private ArrayList<ConstExp> constExpArrayList;
    private StringConst stringConst;
    private Token LBRACE;
    private Token RBRACE;
    public ConstInitVal(ConstExp constExp, ConstExp constExpFirst, ArrayList<Token> opTokens, ArrayList<ConstExp> constExpArrayList, StringConst stringConst,Token LBRACE,Token RBRACE){
        this.constExp = constExp;
        this.constExpFirst = constExpFirst;
        this.opTokens = opTokens;
        this.constExpArrayList = constExpArrayList;
        this.stringConst = stringConst;
        this.LBRACE = LBRACE;
        this.RBRACE = RBRACE;
    }
    @Override
    public String output() {
        StringBuilder stringBuilder = new StringBuilder();
        if(constExp!=null){
            stringBuilder.append(constExp.output());
        }else if(stringConst!=null){
            stringBuilder.append(stringConst.output());
        }else if(LBRACE!=null){
            stringBuilder.append(LBRACE.output());
            if(constExpFirst!=null){
                stringBuilder.append(constExpFirst.output());
                if(opTokens!=null){
                    int len = opTokens.size();
                    for(int i=0;i<len;i++){
                        stringBuilder.append(opTokens.get(i).output());
                        stringBuilder.append(constExpArrayList.get(i).output());
                    }
                }
            }

            stringBuilder.append(RBRACE.output());
        }
        stringBuilder.append("<ConstInitVal>\n");
        return stringBuilder.toString();
    }
}
