package frontend.parser.FuncDef;

import frontend.GlobalParm;
import frontend.lexer.Token;
import frontend.parser.Decl.BType;
import frontend.parser.Decl.BTypeParser;
import frontend.parser.Terminal.Ident;
import frontend.parser.Terminal.IdentParser;
import middle.error.Error;
import middle.error.ErrorType;
import middle.symbol.*;

public class FuncFParamParser {
    private BType btype;
    private Ident ident;
    private Token LBRACK;
    private Token RBRACK;

    private SymbolTable symbolTable;//来自父元素
    private SymbolFunc symbolFunc;

    public FuncFParamParser(SymbolTable symbolTable, SymbolFunc symbolFunc){
        this.symbolTable = symbolTable;
        this.symbolFunc = symbolFunc;
    }

    public FuncFParam FuncParamParser(){
        BTypeParser bTypeParser = new BTypeParser(this.symbolTable);
        this.btype = bTypeParser.BTypeParser();
        if(this.btype==null)return null;

        IdentParser identParser = new IdentParser();
        this.ident = identParser.IdentParser();

        Token token = GlobalParm.getAToken();
        if(token.getTokenTypeName().equals("LBRACK")){//有左侧中括号
            this.LBRACK = token;
            token = GlobalParm.getAToken();
            if(!token.getTokenTypeName().equals("RBRACK")){//无右侧中括号
                GlobalParm.backAToken();
                //error, k型
                Token fToken = GlobalParm.getCurrentToken();
                Token eToken = new Token(null, "k", fToken.getLineNum(),0);
//                GlobalParm.addParserError(eToken);
                GlobalParm.addError(new Error(ErrorType.K_error,eToken));
                //flag
            }else {
                this.RBRACK = token;
            }
        }else GlobalParm.backAToken();//无左侧中括号


        int arrayFlag = 0;
        if(this.LBRACK!=null){
            arrayFlag = 1;
        }
        SymbolType symbolType = SymbolType.getSymbolType(0,this.btype,arrayFlag);
        Symbol symbol = new SymbolVar(symbolType, this.symbolTable.id,this.ident.getToken());
        this.symbolTable.addASymbol(symbol);
        this.symbolFunc.addParam(symbol);//加到函数名的参数列里面,这里是父类，但是应该得执行子类的方法

        FuncFParam funcFParam = new FuncFParam(this.btype, this.ident,this.LBRACK,this.RBRACK);
        return funcFParam;
    }
}
