package frontend.parser.FuncDef;

import frontend.GlobalParm;
import frontend.lexer.Token;
import frontend.parser.Decl.Decl;
import frontend.parser.Decl.DeclParser;
import frontend.parser.Stmt.Stmt;
import frontend.parser.Stmt.StmtParser;

public class BlockItemParser {
    private Decl decl;
    private Stmt stmt;
    public BlockItem BlockItemParser(){
        Token token = GlobalParm.getAToken();
        if(token.getTokenTypeName().equals("CONSTTK")||token.getTokenTypeName().equals("INTTK")||token.getTokenTypeName().equals("CHARTK")){
            GlobalParm.backAToken();
            DeclParser declParser = new DeclParser();
            this.decl = declParser.DeclParser();
            if(this.decl==null)return null;
        }else{
            GlobalParm.backAToken();
            StmtParser stmtParser = new StmtParser();
            this.stmt = stmtParser.StmtParser();
            if(this.stmt==null)return null;
        }
        BlockItem blockItem = new BlockItem(this.decl, this.stmt);
        return blockItem;
    }
}
