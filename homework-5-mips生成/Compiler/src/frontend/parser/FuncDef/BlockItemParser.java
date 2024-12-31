package frontend.parser.FuncDef;

import frontend.GlobalParm;
import frontend.lexer.Token;
import frontend.parser.Decl.Decl;
import frontend.parser.Decl.DeclParser;
import frontend.parser.Stmt.Stmt;
import frontend.parser.Stmt.StmtParser;
import middle.symbol.SymbolTable;

public class BlockItemParser {
    private Decl decl;
    private Stmt stmt;

    public int forLayerNum;
    public boolean funcTag;
    private SymbolTable symbolTable;//来自父元素

    public BlockItemParser(SymbolTable symbolTable, int forLayerNum, boolean funcTag){
        this.symbolTable = symbolTable;
        this.forLayerNum = forLayerNum;
        this.funcTag = funcTag;
    }

    public BlockItem BlockItemParser(){
        Token token = GlobalParm.getAToken();
        if(token.getTokenTypeName().equals("CONSTTK")||token.getTokenTypeName().equals("INTTK")||token.getTokenTypeName().equals("CHARTK")){
            GlobalParm.backAToken();
            DeclParser declParser = new DeclParser(this.symbolTable);
            this.decl = declParser.DeclParser();
            if(this.decl==null)return null;
        }else{
            GlobalParm.backAToken();
            StmtParser stmtParser = new StmtParser(this.symbolTable, this.forLayerNum, this.funcTag);
            this.stmt = stmtParser.StmtParser();
            if(this.stmt==null)return null;
        }
        BlockItem blockItem = new BlockItem(this.decl, this.stmt);
        return blockItem;
    }
}
