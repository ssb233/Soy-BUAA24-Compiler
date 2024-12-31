package frontend.parser.Decl;

import frontend.GlobalParm;
import frontend.lexer.Token;
import frontend.parser.Exp.ConstExp;
import frontend.parser.Exp.ConstExpParser;
import frontend.parser.Terminal.Ident;
import frontend.parser.Terminal.IdentParser;
import middle.error.Error;
import middle.error.ErrorType;
import middle.symbol.Symbol;
import middle.symbol.SymbolTable;
import middle.symbol.SymbolType;
import middle.symbol.SymbolVar;

public class VarDefParser {
    private Ident ident;
    private ConstExp constExp;
    private InitVal initVal;
    private Token LBRACK;
    private Token RBRACK;
    private Token ASSIGN;

    private BType bType;
    private SymbolTable symbolTable;

    public VarDefParser(BType bType, SymbolTable symbolTable){
        this.bType = bType;
        this.symbolTable = symbolTable;
    }

    public VarDef VarDefParser(){
        IdentParser identParser = new IdentParser();
        this.ident = identParser.IdentParser();
        if(this.ident ==null)return null;//第一个不匹配

        Token token = GlobalParm.getAToken();
        if(token.getTokenTypeName().equals("LBRACK")){
            this.LBRACK = token;
            ConstExpParser constExpParser = new ConstExpParser(this.symbolTable);
            this.constExp = constExpParser.ConstExpParser();
            token = GlobalParm.getAToken();
            if(token.getTokenTypeName().equals("RBRACK")){
                this.RBRACK = token;
                //有右括号，正确
            }else {
                GlobalParm.backAToken();
                //error, k型，无右侧中括号
                Token fToken = GlobalParm.getCurrentToken();
                Token eToken = new Token(null, "k",fToken.getLineNum(),0);
//                GlobalParm.addParserError(eToken);
                GlobalParm.addError(new Error(ErrorType.K_error,eToken));
                //flag
            }
        }else GlobalParm.backAToken();//无左括号
        token = GlobalParm.getAToken();
        if(token.getTokenTypeName().equals("ASSIGN")){//有等于号
            this.ASSIGN = token;
            InitValParser initValParser = new InitValParser(this.symbolTable);
            this.initVal = initValParser.InitValParser();
        }else GlobalParm.backAToken();

        //加入符号表
        int arrayFlag = 0;
        if(this.LBRACK!=null){
            arrayFlag = 1;
        }
        SymbolType symbolType = SymbolType.getSymbolType(0,this.bType,arrayFlag);
        Symbol symbol = new SymbolVar(symbolType, this.symbolTable.id, this.ident.getToken(),this.constExp,this.initVal);
        this.symbolTable.addASymbol(symbol);

        VarDef varDef = new VarDef(this.ident, this.constExp, this.initVal,this.LBRACK,this.RBRACK,this.ASSIGN);
        return varDef;
    }
}
