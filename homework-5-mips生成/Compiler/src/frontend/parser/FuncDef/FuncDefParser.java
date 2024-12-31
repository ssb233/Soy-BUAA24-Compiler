package frontend.parser.FuncDef;

import frontend.GlobalParm;
import frontend.lexer.Token;
import frontend.parser.Terminal.Ident;
import frontend.parser.Terminal.IdentParser;
import middle.error.Error;
import middle.error.ErrorType;
import middle.symbol.Symbol;
import middle.symbol.SymbolFunc;
import middle.symbol.SymbolTable;
import middle.symbol.SymbolType;

public class FuncDefParser {
    private FuncType funcType;
    private Ident ident;
    private FuncFParams funcFParams;
    private Block block;
    private Token LPARENT;
    private Token RPARENT;

    private SymbolTable symbolTable;//来自父元素
    private SymbolTable currentSymbolTable;//函数自身的表,函数的参数属于其自身的作用域

    public FuncDefParser(SymbolTable symbolTable){
        this.symbolTable = symbolTable;//拿到父亲的表
    }
    public FuncDef FuncDefParser(){
        FuncTypeParser funcTypeParser = new FuncTypeParser();
        this.funcType = funcTypeParser.FuncTypeParser();
        if(this.funcType==null)return null;

        IdentParser identParser = new IdentParser();
        this.ident = identParser.IdentParser();


        //创建属于这个函数的符号表
        this.currentSymbolTable = new SymbolTable(GlobalParm.getTableId(),this.symbolTable.id);
        GlobalParm.symbolTables.add(this.currentSymbolTable);
        this.symbolTable.addASymbolTable(this.currentSymbolTable);


        //函数名称加入符号表,
        SymbolType symbolType = SymbolType.getFuncSymbolType(this.funcType);
        SymbolFunc symbolFunc = new SymbolFunc(symbolType, this.symbolTable.id, this.ident.getToken());
        this.symbolTable.addASymbol(symbolFunc);


        Token token = GlobalParm.getAToken();
        if(token.getTokenTypeName().equals("LPARENT")){
            this.LPARENT = token;
        }else GlobalParm.backAToken();

        token = GlobalParm.getAToken();
        //下面这个if貌似没什么用
        if(token.getTokenTypeName().equals("LPARENT")){
            this.LPARENT = token;
        }
        //这里有三种情况，1.读到参数，也就是BType开头，2读到右侧小括号， 3.读到Block的左大括号
        if(token.getTokenTypeName().equals("LBRACE")){//读到左大括号，说明这里无参数，并且右侧小括号缺失
            GlobalParm.backAToken();
            //error, j型
            Token fToken = GlobalParm.getCurrentToken();
            Token eToken = new Token(null, "j", fToken.getLineNum(),0);
//            GlobalParm.addParserError(eToken);
            GlobalParm.addError(new Error(ErrorType.J_error, eToken));
            //flag
        }else if(token.getTokenTypeName().equals("RPARENT")){//读到右侧括号，说明无参
            //正确无任何异议
            this.RPARENT = token;
        }else{//排除上面两种，这里一定是有参数的
            GlobalParm.backAToken();
            FuncFParamsParser funcFParamsParser = new FuncFParamsParser(this.currentSymbolTable,symbolFunc);
            this.funcFParams = funcFParamsParser.FuncFParamsParser();
            token = GlobalParm.getAToken();
            if(token.getTokenTypeName().equals("RPARENT")){
                //正确
                this.RPARENT = token;
            }else{
                GlobalParm.backAToken();
                //error, j型
                Token fToken = GlobalParm.getCurrentToken();
                Token eToken = new Token(null, "j", fToken.getLineNum(),0);
//                GlobalParm.addParserError(eToken);
                GlobalParm.addError(new Error(ErrorType.J_error,eToken));
                //flag
            }
        }

        //处理block
        if(this.funcType.getToken().getTokenTypeName().equals("VOIDTK")){
            BlockParser blockParser = new BlockParser(this.currentSymbolTable, 0, false);//funcdef 1
            this.block = blockParser.BlockParser();
        }else{
            BlockParser blockParser = new BlockParser(this.currentSymbolTable, 0, true);//funcdef 1
            this.block = blockParser.BlockParser();
        }


        //检查block的最后一条语句
        Token eToken = this.block.checkReturn();
        if(eToken!=null && !this.funcType.getToken().getTokenTypeName().equals("VOIDTK")){//要把void情况排除掉,这里找的是没有return的情况
            GlobalParm.addError(new Error(ErrorType.G_error,eToken));
        }
        //flag



        FuncDef funcDef = new FuncDef(this.funcType, this.ident, this.funcFParams, this.block,this.LPARENT,this.RPARENT,this.currentSymbolTable);
        return funcDef;
    }
}
