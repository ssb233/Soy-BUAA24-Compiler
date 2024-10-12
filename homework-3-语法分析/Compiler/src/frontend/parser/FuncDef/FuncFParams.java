package frontend.parser.FuncDef;

import frontend.lexer.Token;
import frontend.parser.parserOutput;

import java.util.ArrayList;

public class FuncFParams implements parserOutput {
    private FuncFParam funcFParamFirst;
    private ArrayList<Token> opTokens;
    private ArrayList<FuncFParam> funcFParamArrayList;

    public FuncFParams( FuncFParam funcFParamFirst,  ArrayList<Token> opTokens, ArrayList<FuncFParam> funcFParamArrayList){
        this.funcFParamFirst = funcFParamFirst;
        this.opTokens = opTokens;
        this.funcFParamArrayList = funcFParamArrayList;
    }

    @Override
    public String output() {
        StringBuilder stringBuilder = new StringBuilder();
        if(funcFParamFirst!=null){
            stringBuilder.append(funcFParamFirst.output());
            if(opTokens!=null){
                int len = opTokens.size();
                for(int i=0;i<len;i++){
                    stringBuilder.append(opTokens.get(i).output());
                    stringBuilder.append(funcFParamArrayList.get(i).output());
                }
            }
        }
        stringBuilder.append("<FuncFParams>\n");
        return stringBuilder.toString();
    }
}
