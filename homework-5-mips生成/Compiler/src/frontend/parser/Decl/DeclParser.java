package frontend.parser.Decl;

import frontend.GlobalParm;
import frontend.lexer.Token;
import middle.symbol.SymbolTable;

public class DeclParser {
    private ConstDecl constDecl;
    private VarDecl varDecl;
    private SymbolTable symbolTable;//父亲表，不是自己表
    public DeclParser(SymbolTable symbolTable){
        this.symbolTable = symbolTable;
    }

    public Decl DeclParser(){
        Token token = GlobalParm.getAToken();
        if(token.getTokenTypeName().equals("CONSTTK")){//constDecl
            GlobalParm.backAToken();
            ConstDeclParser constDeclParser = new ConstDeclParser(this.symbolTable);
            this.constDecl = constDeclParser.ConstDeclParser();
            if(this.constDecl == null)return null;
        }else{//varDecl
            GlobalParm.backAToken();
            VarDeclParser varDeclParser = new VarDeclParser(this.symbolTable);
            this.varDecl = varDeclParser.VarDeclParser();
            if(this.varDecl == null)return null;
        }
        Decl decl = new Decl(this.constDecl, this.varDecl);
        return decl;
    }
}
