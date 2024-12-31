package frontend.parser;

import frontend.parser.Decl.Decl;
import frontend.parser.FuncDef.FuncDef;
import middle.symbol.SymbolTable;

import java.util.ArrayList;

public class CompUnit implements parserOutput{
    private ArrayList<Decl> decls;
    private ArrayList<FuncDef> funcDefs;
    private MainFuncDef mainFuncDef;
    private SymbolTable symbolTable;

    public CompUnit(ArrayList<Decl> decls, ArrayList<FuncDef> funcDefs, MainFuncDef mainFuncDef,SymbolTable symbolTable){
        this.decls = decls;
        this.funcDefs = funcDefs;
        this.mainFuncDef = mainFuncDef;
        this.symbolTable = symbolTable;
    }

    public ArrayList<Decl> getDecls(){
        return this.decls;
    }
    public ArrayList<FuncDef> getFuncDefs(){
        return this.funcDefs;
    }
    public MainFuncDef getMainFuncDef(){
        return this.mainFuncDef;
    }


    @Override
    public String output() {
        StringBuilder stringBuilder = new StringBuilder();
        if(this.decls!=null){
            for(Decl item:decls){
                stringBuilder.append(item.output());
            }
        }
        if(this.funcDefs!=null){
            for(FuncDef item:funcDefs){
                stringBuilder.append(item.output());
            }
        }
        stringBuilder.append(mainFuncDef.output());
        stringBuilder.append("<CompUnit>");
        return stringBuilder.toString();
    }
}
