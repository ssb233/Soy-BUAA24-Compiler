package frontend.parser.Exp;

import frontend.GlobalParm;
import frontend.lexer.Token;
import middle.symbol.SymbolTable;

import java.util.ArrayList;

public class PrimaryExpParser {
    private PrimaryExpExtend primaryExpExtend;

    public SymbolTable symbolTable;
    public PrimaryExpParser(SymbolTable symbolTable){
        this.symbolTable = symbolTable;
    }

    public PrimaryExp PrimaryExpParser(){
        Token token = GlobalParm.getAToken();
        if(token.getTokenTypeName().equals("LPARENT")){
            GlobalParm.backAToken();
            ExpLRParser expLRParser = new ExpLRParser(this.symbolTable);
            this.primaryExpExtend = expLRParser.ExpLRParser();
        }else if(token.getTokenTypeName().equals("IDENFR")){
            GlobalParm.backAToken();
            LValParser lValParser = new LValParser(this.symbolTable);
            this.primaryExpExtend = lValParser.LValParser();
        }else if(token.getTokenTypeName().equals("INTCON")){
            GlobalParm.backAToken();
            NumberParser numberParser = new NumberParser(this.symbolTable);
            this.primaryExpExtend = numberParser.NumberParser();
        }else if(token.getTokenTypeName().equals("CHRCON")){
            GlobalParm.backAToken();
            CharParser charParser = new CharParser(this.symbolTable);
            this.primaryExpExtend = charParser.CharParser();
        }else{
            GlobalParm.backAToken();
            return null;//超出所有情况
        }
        PrimaryExp primaryExp = new PrimaryExp(this.primaryExpExtend);
        return primaryExp;
    }
}
