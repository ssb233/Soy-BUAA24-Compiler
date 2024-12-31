package frontend.parser.Decl;

import frontend.lexer.Token;
import frontend.parser.parserOutput;

import java.util.ArrayList;

public class VarDecl implements parserOutput {
    private BType bType;
    private VarDef varDefFirst;
    private ArrayList<Token> opTokens;
    private ArrayList<VarDef> varDefArrayList;
    private Token SEMICN;
    public VarDecl(BType bType, VarDef varDefFirst, ArrayList<Token> opTokens, ArrayList<VarDef> varDefArrayList, Token SEMICN){
        this.bType = bType;
        this.varDefFirst = varDefFirst;
        this.opTokens = opTokens;
        this.varDefArrayList = varDefArrayList;
        this.SEMICN = SEMICN;
    }
    @Override
    public String output() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(bType.output());
        stringBuilder.append(varDefFirst.output());
        if(opTokens!=null){
            int len = opTokens.size();
            for(int i=0;i<len;i++){
                stringBuilder.append(opTokens.get(i).output());
                stringBuilder.append(varDefArrayList.get(i).output());
            }
        }
        stringBuilder.append(SEMICN.output());
        stringBuilder.append("<VarDecl>\n");
        return stringBuilder.toString();
    }

    public BType getbType(){
        return this.bType;
    }
    public ArrayList<VarDef> getVarDefList(){
        ArrayList<VarDef> varDefs = new ArrayList<>();
        if(this.varDefFirst!=null){
            varDefs.add(this.varDefFirst);
            if(this.varDefArrayList!=null){
                for(VarDef item:this.varDefArrayList){
                    varDefs.add(item);
                }
            }
        }
        return varDefs;
    }
}
