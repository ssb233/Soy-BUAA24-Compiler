package middle.symbol;

import frontend.lexer.Token;

import java.util.ArrayList;

public class SymbolFunc extends Symbol{
    public ArrayList<Symbol> paramList;//这个函数名的参数

    public SymbolFunc(SymbolType symbolType, int tableId, Token token){
        super(symbolType,tableId,token);
        paramList = new ArrayList<Symbol>();
    }
    public void addParam(Symbol symbol){
        this.paramList.add(symbol);
    }
    public ArrayList<Symbol> getParamList(){
        return this.paramList;
    }
}
