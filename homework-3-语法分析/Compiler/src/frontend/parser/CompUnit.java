package frontend.parser;

import frontend.parser.Decl.Decl;
import frontend.parser.FuncDef.FuncDef;

import java.util.ArrayList;

public class CompUnit implements parserOutput{
    private ArrayList<Decl> decls;
    private ArrayList<FuncDef> funcDefs;
    private MainFuncDef mainFuncDef;

    public CompUnit(ArrayList<Decl> decls, ArrayList<FuncDef> funcDefs, MainFuncDef mainFuncDef){
        this.decls = decls;
        this.funcDefs = funcDefs;
        this.mainFuncDef = mainFuncDef;
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
