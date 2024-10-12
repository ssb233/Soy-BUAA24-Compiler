package frontend.parser.Decl;

import frontend.lexer.Token;
import frontend.parser.parserOutput;

import java.util.ArrayList;

public class ConstDecl implements parserOutput {
    private Token CONSTTK;
    private Token SEMICN;
    private BType bType;
    private ConstDef constDefFirst;
    private ArrayList<Token> opTokens;
    private ArrayList<ConstDef> constDefArrayList;

    public ConstDecl(BType bType, ConstDef constDefFirst, ArrayList<Token> opTokens, ArrayList<ConstDef> constDefArrayList, Token CONSTTK, Token SEMICN){
        this.bType = bType;
        this.constDefFirst = constDefFirst;
        this.opTokens = opTokens;
        this.constDefArrayList = constDefArrayList;
        this.CONSTTK = CONSTTK;
        this.SEMICN = SEMICN;
    }
    @Override
    public String output() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.CONSTTK.output());
        stringBuilder.append(this.bType.output());
        stringBuilder.append(this.constDefFirst.output());
        if(opTokens!=null){
            int len = opTokens.size();
            for(int i=0;i<len;i++){
                stringBuilder.append(opTokens.get(i).output());
                stringBuilder.append(constDefArrayList.get(i).output());
            }
        }
        stringBuilder.append(SEMICN.output());
        stringBuilder.append("<ConstDecl>\n");
        return stringBuilder.toString();
    }
}
