package frontend.parser.Stmt;

import frontend.GlobalParm;
import frontend.lexer.Token;
import frontend.parser.Exp.Exp;
import frontend.parser.Exp.ExpParser;
import frontend.parser.Exp.LVal;
import frontend.parser.Exp.LValParser;
import middle.symbol.SymbolTable;

public class ForStmtParser {
    private LVal lval;
    private Exp exp;
    private Token ASSIGN;

    private SymbolTable symbolTable;//来自父元素

    public ForStmtParser(SymbolTable symbolTable){
        this.symbolTable = symbolTable;
    }
    public ForStmt ForStmtParser(){
        LValParser lValParser = new LValParser(this.symbolTable);
        this.lval = lValParser.LValParser();
        if(lval==null)return null;
        this.lval.checkIdentConst(this.symbolTable);

        Token token = GlobalParm.getAToken();//肯定得是=
        if(token.getTokenTypeName().equals("ASSIGN")){
            this.ASSIGN = token;
        }else GlobalParm.backAToken();

        ExpParser expParser = new ExpParser(this.symbolTable);
        this.exp = expParser.ExpParser();
        ForStmt forStmt = new ForStmt(this.lval,this.exp,this.ASSIGN);
        return forStmt;
    }
}
