package frontend.parser.Stmt;

import frontend.lexer.Token;
import frontend.parser.Exp.Exp;
import frontend.parser.Exp.LVal;
import frontend.parser.parserOutput;

public class ForStmt implements parserOutput {
    private LVal lval;
    private Exp exp;
    private Token ASSIGN;
    public ForStmt(LVal lval, Exp exp,Token ASSIGN){
        this.lval = lval;
        this.exp = exp;
        this.ASSIGN = ASSIGN;
    }
    @Override
    public String output() {
        StringBuilder stringBuilder = new StringBuilder();
        if(lval!=null){
            stringBuilder.append(lval.output());
            stringBuilder.append(ASSIGN.output());
            stringBuilder.append(exp.output());
        }
        stringBuilder.append("<ForStmt>\n");
        return stringBuilder.toString();
    }
}
