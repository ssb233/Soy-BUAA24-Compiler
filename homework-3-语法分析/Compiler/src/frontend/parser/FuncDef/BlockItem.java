package frontend.parser.FuncDef;

import frontend.parser.Decl.Decl;
import frontend.parser.Stmt.Stmt;
import frontend.parser.parserOutput;

public class BlockItem implements parserOutput {
    private Decl decl;
    private Stmt stmt;

    public BlockItem(Decl decl, Stmt stmt){
        this.decl = decl;
        this.stmt = stmt;
    }
    @Override
    public String output() {
        StringBuilder stringBuilder = new StringBuilder();
        if(decl!=null){
            stringBuilder.append(decl.output());
        }else if(stmt!=null){
            stringBuilder.append(stmt.output());
        }
        return stringBuilder.toString();
    }
}
