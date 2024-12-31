package frontend.parser.FuncDef;

import frontend.lexer.Token;
import frontend.parser.Terminal.Ident;
import frontend.parser.parserOutput;
import middle.symbol.SymbolTable;

public class FuncDef implements parserOutput {
    private FuncType funcType;
    private Ident ident;
    private FuncFParams funcFParams;
    private Block block;
    private Token LPARENT;
    private Token RPARENT;

    private SymbolTable currentSymbolTable;

    public FuncDef(FuncType funcType, Ident ident, FuncFParams funcFParams, Block block,Token LPARENT,Token RPARENT, SymbolTable symbolTable){
        this.funcType = funcType;
        this.ident = ident;
        this.funcFParams = funcFParams;
        this.block = block;
        this.LPARENT = LPARENT;
        this.RPARENT = RPARENT;
        this.currentSymbolTable = symbolTable;
    }
    @Override
    public String output() {
        StringBuilder stringBuilder = new StringBuilder();
        if(funcType!=null){
            stringBuilder.append(funcType.output());
            stringBuilder.append(ident.output());
            stringBuilder.append(LPARENT.output());
            if(funcFParams!=null){
                stringBuilder.append(funcFParams.output());
            }
            stringBuilder.append(RPARENT.output());
            stringBuilder.append(block.output());
        }
        stringBuilder.append("<FuncDef>\n");
        return stringBuilder.toString();
    }

    public SymbolTable getCurrentSymbolTable(){
        return this.currentSymbolTable;
    }

    public FuncType getFuncType(){
        return  this.funcType;
    }

    public FuncFParams getFuncFParams(){
        return this.funcFParams;
    }

    public String getName(){
        return this.ident.getToken().getTokenValue();
    }
    public Block getBlock(){
        return this.block;
    }

}
