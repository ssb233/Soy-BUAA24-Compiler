package frontend.parser.Decl;

import frontend.GlobalParm;
import frontend.lexer.Token;

public class DeclParser {
    private ConstDecl constDecl;
    private VarDecl varDecl;

    public Decl DeclParser(){
        Token token = GlobalParm.getAToken();
        if(token.getTokenTypeName().equals("CONSTTK")){//constDecl
            GlobalParm.backAToken();
            ConstDeclParser constDeclParser = new ConstDeclParser();
            this.constDecl = constDeclParser.ConstDeclParser();
            if(this.constDecl == null)return null;
        }else{//varDecl
            GlobalParm.backAToken();
            VarDeclParser varDeclParser = new VarDeclParser();
            this.varDecl = varDeclParser.VarDeclParser();
            if(this.varDecl == null)return null;
        }
        Decl decl = new Decl(this.constDecl, this.varDecl);
        return decl;
    }
}
