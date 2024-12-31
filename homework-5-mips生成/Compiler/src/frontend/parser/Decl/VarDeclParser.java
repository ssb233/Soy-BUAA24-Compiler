package frontend.parser.Decl;

import frontend.GlobalParm;
import frontend.lexer.Token;
import middle.error.Error;
import middle.error.ErrorType;
import middle.symbol.SymbolTable;

import java.util.ArrayList;

public class VarDeclParser {
    private BType bType;
    private VarDef varDefFirst;
    private ArrayList<Token> opTokens;
    private ArrayList<VarDef> varDefArrayList;
    private Token SEMICN;

    private SymbolTable symbolTable;//来自父元素

    public VarDeclParser(SymbolTable symbolTable){
        this.symbolTable = symbolTable;
    }

    public VarDecl VarDeclParser(){
        BTypeParser bTypeParser = new BTypeParser(this.symbolTable);
        this.bType = bTypeParser.BTypeParser();

        if(bType == null)return null;//第一个不匹配

        VarDefParser varDefParser = new VarDefParser(this.bType,this.symbolTable);
        this.varDefFirst = varDefParser.VarDefParser();

        Token token = GlobalParm.getAToken();
        if(token.getTokenTypeName().equals("COMMA")){
            this.opTokens = new ArrayList<Token>();
            this.varDefArrayList = new ArrayList<VarDef>();
        }
        while(token.getTokenTypeName().equals("COMMA")){
            this.opTokens.add(token);
            this.varDefArrayList.add(new VarDefParser(this.bType,this.symbolTable).VarDefParser());
            token = GlobalParm.getAToken();
        }GlobalParm.backAToken();
        token = GlobalParm.getAToken();
        if(token.getTokenTypeName().equals("SEMICN")){
            this.SEMICN = token;
        }else{
            GlobalParm.backAToken();
            //error, i型，缺少分号
            Token fToken = GlobalParm.getCurrentToken();
            Token eToken = new Token(null,"i",fToken.getLineNum(),0);
//            GlobalParm.addParserError(eToken);
            GlobalParm.addError(new Error(ErrorType.I_error, eToken));
            //flag， 不知道要不要补上分号
        }
        VarDecl varDecl = new VarDecl(this.bType, this.varDefFirst, this.opTokens, this.varDefArrayList,this.SEMICN);
        return varDecl;
    }
}
