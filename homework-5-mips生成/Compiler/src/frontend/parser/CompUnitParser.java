package frontend.parser;

import frontend.GlobalParm;
import frontend.lexer.Token;
import frontend.parser.Decl.Decl;
import frontend.parser.Decl.DeclParser;
import frontend.parser.FuncDef.FuncDef;
import frontend.parser.FuncDef.FuncDefParser;
import middle.symbol.SymbolTable;

import java.util.ArrayList;

public class CompUnitParser {
    private ArrayList<Decl> decls;
    private ArrayList<FuncDef> funcDefs;
    private MainFuncDef mainFuncDef;
    private SymbolTable symbolTable;//自己表

    public CompUnit CompUnitParser(){
        this.symbolTable = new SymbolTable(GlobalParm.getTableId(),0);
        GlobalParm.symbolTables.add(this.symbolTable);

        Token token1 = GlobalParm.getAToken();
        Token token2 = GlobalParm.getAToken();
        Token token3 = GlobalParm.getAToken();


        if(!token3.getTokenTypeName().equals("LPARENT")){
            this.decls = new ArrayList<Decl>();
        }
        while(!token3.getTokenTypeName().equals("LPARENT")){
            GlobalParm.backAToken();
            GlobalParm.backAToken();
            GlobalParm.backAToken();
            decls.add(new DeclParser(this.symbolTable).DeclParser());
             token1 = GlobalParm.getAToken();
             token2 = GlobalParm.getAToken();
             token3 = GlobalParm.getAToken();
        }
        GlobalParm.backAToken();//只留下两个token,区别在于第二个token的命名，不能是main就行
        if(!token2.getTokenTypeName().equals("MAINTK")){
            this.funcDefs = new ArrayList<FuncDef>();
        }
        while(!token2.getTokenTypeName().equals("MAINTK")){
            GlobalParm.backAToken();
            GlobalParm.backAToken();
            funcDefs.add(new FuncDefParser(this.symbolTable).FuncDefParser());
            token1 = GlobalParm.getAToken();
            token2 = GlobalParm.getAToken();
        }
        GlobalParm.backAToken();
        GlobalParm.backAToken();//MAINTK
        this.mainFuncDef = new MainFuncDefParser(this.symbolTable).MainFuncDefParser();
        if(this.mainFuncDef == null)return null;
        CompUnit compUnit = new CompUnit(this.decls,this.funcDefs,this.mainFuncDef,this.symbolTable);
        return compUnit;
    }
}
