package frontend.parser.FuncDef;

import frontend.GlobalParm;
import frontend.lexer.Token;
import middle.symbol.Symbol;
import middle.symbol.SymbolFunc;
import middle.symbol.SymbolTable;

import java.util.ArrayList;

public class FuncFParamsParser {
    private FuncFParam funcFParamFirst;
    private ArrayList<Token> opTokens;
    private ArrayList<FuncFParam> funcFParamArrayList;

    private SymbolTable symbolTable;//父元素
    private SymbolFunc symbolFunc;

    public FuncFParamsParser(SymbolTable symbolTable, SymbolFunc symbolFunc){
        this.symbolTable = symbolTable;
        this.symbolFunc = symbolFunc;
    }

    public FuncFParams FuncFParamsParser(){
        FuncFParamParser funcFParamParser = new FuncFParamParser(this.symbolTable, this.symbolFunc);
        this.funcFParamFirst = funcFParamParser.FuncParamParser();
        if(this.funcFParamFirst==null)return null;

        Token token = GlobalParm.getAToken();
        if(token.getTokenTypeName().equals("COMMA")){
            this.opTokens = new ArrayList<Token>();
            this.funcFParamArrayList = new ArrayList<FuncFParam>();
        }
        while(token.getTokenTypeName().equals("COMMA")){
            this.opTokens.add(token);
            this.funcFParamArrayList.add(new FuncFParamParser(this.symbolTable, this.symbolFunc).FuncParamParser());
            token = GlobalParm.getAToken();
        }GlobalParm.backAToken();

        FuncFParams funcFParams = new FuncFParams(this.funcFParamFirst, this.opTokens, this.funcFParamArrayList);
        return funcFParams;
    }
}
