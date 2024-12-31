package frontend.parser.Exp;

import frontend.GlobalParm;
import frontend.lexer.Token;
import middle.symbol.SymbolTable;

import java.util.ArrayList;

public class EqExpParser {
    private RelExp relExpFirst=null;
    private ArrayList<Token> opTokens=null;
    private ArrayList<RelExp> relExpArrayList=null;

    public SymbolTable symbolTable;
    public EqExpParser(SymbolTable symbolTable){
        this.symbolTable = symbolTable;
    }

    public EqExp EqExpParser(){
        RelExpParser relExpParser = new RelExpParser(this.symbolTable);
        this.relExpFirst = relExpParser.RelExpParser();
        if(this.relExpFirst ==null)return null;

        Token token = GlobalParm.getAToken();
        if(token.getTokenTypeName().equals("EQL")||token.getTokenTypeName().equals("NEQ")){
            opTokens = new ArrayList<Token>();
            relExpArrayList = new ArrayList<RelExp>();
        }
        while(token.getTokenTypeName().equals("EQL")||token.getTokenTypeName().equals("NEQ")){
            opTokens.add(token);
            relExpArrayList.add(new RelExpParser(this.symbolTable).RelExpParser());
            token = GlobalParm.getAToken();
        }GlobalParm.backAToken();
        EqExp eqExp = new EqExp(relExpFirst, opTokens, relExpArrayList);
        return eqExp;
    }
}
