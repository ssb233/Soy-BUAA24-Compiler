package frontend.parser.Exp;

import frontend.GlobalParm;
import frontend.lexer.Token;
import frontend.parser.FuncDef.FuncRParams;
import frontend.parser.FuncDef.FuncRParamsParser;
import frontend.parser.Terminal.Ident;
import middle.error.Error;
import middle.error.ErrorType;
import middle.symbol.Symbol;
import middle.symbol.SymbolTable;

import java.util.ArrayList;

public class UnaryExpParser {
    private PrimaryExp primaryExp=null;
    private Ident ident=null;
    private FuncRParams funcRparams=null;
    private UnaryOp unaryOp=null;
    private UnaryExp unaryExp=null;
    private Token LPARENT;
    private Token RPARENT;

    public SymbolTable symbolTable;
    public UnaryExpParser(SymbolTable symbolTable){
        this.symbolTable = symbolTable;
    }

    public UnaryExp UnaryExpParser(){
        Token token = GlobalParm.getAToken();
        if(token.getTokenTypeName().equals("LPARENT")||token.getTokenTypeName().equals("INTCON")||token.getTokenTypeName().equals("CHRCON")){//PrimaryExp->'('Exp')',Number, Char
            GlobalParm.backAToken();
            PrimaryExpParser parser = new PrimaryExpParser(this.symbolTable);
            this.primaryExp = parser.PrimaryExpParser();
        }else if(token.getTokenTypeName().equals("IDENFR")){//Ident,两种情况,[.(
            Token token2 = GlobalParm.getAToken();
            if(token2.getTokenTypeName().equals("LPARENT")){//Iden([参数])
                this.LPARENT = token2;
                this.ident = new Ident(token);

                UnaryExpParser.checkIdentExist(this.symbolTable, this.ident);
                //flag

                //这里不回退了，在输出的时候处理一下,左括号
                FuncRParamsParser funcRParamsParser = new FuncRParamsParser(this.symbolTable,this.ident);
                this.funcRparams = funcRParamsParser.FuncRParamsParser();
                //这里解析的实参可以为空，也就是null
                token2 = GlobalParm.getAToken();
                if(token2.getTokenTypeName().equals("RPARENT")){
                    //有右侧括号，
                    this.RPARENT = token2;
                }else{
                    GlobalParm.backAToken();
                    //error , j型
                    Token fToken = GlobalParm.getCurrentToken();
                    Token eToken = new Token(null, "j",fToken.getLineNum(),0);
//                    GlobalParm.addParserError(eToken);
                    GlobalParm.addError(new Error(ErrorType.J_error, eToken));
                    //flag
                }
            }else{//一定走PrimaryExp，需要回退再处理
                GlobalParm.backAToken();
                GlobalParm.backAToken();//前面读了两个token，要退回两次
                PrimaryExpParser parser = new PrimaryExpParser(this.symbolTable);
                this.primaryExp = parser.PrimaryExpParser();
            }
        }else if(token.getTokenTypeName().equals("NOT")||token.getTokenTypeName().equals("PLUS")||token.getTokenTypeName().equals("MINU")){//!+-
            GlobalParm.backAToken();
            UnaryOpParser unaryOpParser = new UnaryOpParser(this.symbolTable);
            this.unaryOp = unaryOpParser.UnaryOpParser();
            UnaryExpParser unaryExpParser = new UnaryExpParser(this.symbolTable);
            this.unaryExp = unaryExpParser.UnaryExpParser();
        }else{
            GlobalParm.backAToken();
            return null;//超出三种情况
        }
        UnaryExp unaryExp1 = new UnaryExp(primaryExp,ident,funcRparams,unaryOp,unaryExp, this.LPARENT,this.RPARENT, this.symbolTable);
        return unaryExp1;
    }

    public static void checkIdentExist(SymbolTable symbolTable, Ident ident){
        Token token = ident.getToken();
        String name = token.getTokenValue();
        Symbol symbol = SymbolTable.searchSymbol(name, symbolTable);//递归向上查找符号表
        if(symbol == null){
            GlobalParm.addError(new Error(ErrorType.C_error, token));
        }
    }
}
