package frontend.parser.Terminal;

import frontend.lexer.Token;
import frontend.parser.parserOutput;
import middle.symbol.Symbol;
import middle.symbol.SymbolConst;
import middle.symbol.SymbolTable;
import middle.symbol.SymbolVar;

public class Ident implements parserOutput {
    private Token token;
    public Ident(Token token){
        this.token = token;
    }
    public Token getToken(){
        return this.token;
    }
    @Override
    public String output() {
        StringBuilder stringBuilder = new StringBuilder();
        if(token !=null){
            stringBuilder.append(token.output());
        }
        return stringBuilder.toString();
    }

    public int searchIdentValue(SymbolTable symbolTable){
        Symbol symbol = SymbolTable.searchSymbol(this.token.getTokenValue(),symbolTable);
        if(symbol!=null){
            if(symbol instanceof SymbolVar){
                SymbolVar symbolVar = (SymbolVar) symbol;
                return symbolVar.getInitVal();
            }else if(symbol instanceof SymbolConst){
                SymbolConst symbolConst = (SymbolConst) symbol;
                return symbolConst.getInitVal();
            }
        }
        return 0;
    }

    public int searchArrayIndexValue(SymbolTable symbolTable, int index){
        Symbol symbol = SymbolTable.searchSymbol(this.token.getTokenValue(),symbolTable);
        if(symbol!=null){
            if(symbol instanceof SymbolVar){
                SymbolVar symbolVar = (SymbolVar) symbol;
                return symbolVar.getIndexValue(index);
            }else if(symbol instanceof SymbolConst){
                SymbolConst symbolConst = (SymbolConst) symbol;
                return symbolConst.getIndexValue(index);
            }
        }
        return 0;
    }
}
