package frontend.parser;

import frontend.lexer.Token;
import frontend.parser.FuncDef.Block;
import frontend.parser.Terminal.Ident;
import middle.symbol.SymbolTable;

public class MainFuncDef implements parserOutput{
    private Token INTTK;
    private Token MAINTK;
    private Token LPARENT;
    private Token RPARENT;
    private Block block;

    private SymbolTable symbolTable;
    public MainFuncDef(Block block,Token INTTK, Token MAINTK, Token LPARENT, Token RPARENT, SymbolTable symbolTable){
        this.block = block;
        this.INTTK = INTTK;
        this.MAINTK = MAINTK;
        this. LPARENT = LPARENT;
        this.RPARENT = RPARENT;
        this.symbolTable = symbolTable;
    }
    @Override
    public String output() {
        StringBuilder stringBuilder = new StringBuilder();
        if(INTTK!=null){
            stringBuilder.append(INTTK.output());
            stringBuilder.append(MAINTK.output());
            stringBuilder.append(LPARENT.output());
            stringBuilder.append(RPARENT.output());
            stringBuilder.append(block.output());
        }
        stringBuilder.append("<MainFuncDef>\n");
        return stringBuilder.toString();
    }

    public SymbolTable getSymbolTable(){
        return this.symbolTable;
    }
    public Block getBlock(){
        return this.block;
    }
}
