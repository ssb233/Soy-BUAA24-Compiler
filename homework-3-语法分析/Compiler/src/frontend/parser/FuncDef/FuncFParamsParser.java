package frontend.parser.FuncDef;

import frontend.GlobalParm;
import frontend.lexer.Token;

import java.util.ArrayList;

public class FuncFParamsParser {
    private FuncFParam funcFParamFirst;
    private ArrayList<Token> opTokens;
    private ArrayList<FuncFParam> funcFParamArrayList;

    public FuncFParams FuncFParamsParser(){
        FuncFParamParser funcFParamParser = new FuncFParamParser();
        this.funcFParamFirst = funcFParamParser.FuncParamParser();
        if(this.funcFParamFirst==null)return null;

        Token token = GlobalParm.getAToken();
        if(token.getTokenTypeName().equals("COMMA")){
            this.opTokens = new ArrayList<Token>();
            this.funcFParamArrayList = new ArrayList<FuncFParam>();
        }
        while(token.getTokenTypeName().equals("COMMA")){
            this.opTokens.add(token);
            this.funcFParamArrayList.add(new FuncFParamParser().FuncParamParser());
            token = GlobalParm.getAToken();
        }GlobalParm.backAToken();

        FuncFParams funcFParams = new FuncFParams(this.funcFParamFirst, this.opTokens, this.funcFParamArrayList);
        return funcFParams;
    }
}
