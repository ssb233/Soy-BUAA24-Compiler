package frontend.parser;

import frontend.GlobalParm;
import frontend.lexer.Token;
import frontend.parser.FuncDef.BlockParser;
import frontend.parser.FuncDef.Block;
import middle.error.Error;
import middle.error.ErrorType;
import middle.symbol.SymbolTable;

public class MainFuncDefParser {
    private Token INTTK;
    private Token MAINTK;
    private Token LPARENT;
    private Token RPARENT;
    private Block block;

    private SymbolTable symbolTable;//父元素的表格
    private SymbolTable currentTable;//自己的表格

    public MainFuncDefParser(SymbolTable symbolTable){
        this.symbolTable = symbolTable;
    }

    public MainFuncDef MainFuncDefParser(){
        Token token = GlobalParm.getAToken();
        if(token.getTokenTypeName().equals("INTTK")){
            this.INTTK = token;
        }else{
            GlobalParm.backAToken();
            return null;//第一个就不匹配，退出匹配
        }
        token = GlobalParm.getAToken();
        if(token.getTokenTypeName().equals("MAINTK")){
            this.MAINTK = token;
        }
        token = GlobalParm.getAToken();
        if(token.getTokenTypeName().equals("LPARENT")){
            this.LPARENT = token;
        }
        token = GlobalParm.getAToken();
        if(!token.getTokenTypeName().equals("RPARENT")){//不是右括号
            GlobalParm.backAToken();
            //error, j型
            Token fToken = GlobalParm.getCurrentToken();
            Token eToken = new Token(null, "j", fToken.getLineNum(),0);
            //GlobalParm.addParserError(eToken);
            GlobalParm.addError(new Error(ErrorType.J_error, eToken));
            //flag
        }else{
            this.RPARENT = token;
        }

        this.currentTable = new SymbolTable(GlobalParm.getTableId(),this.symbolTable.id);
        GlobalParm.symbolTables.add(this.currentTable);

        this.symbolTable.addASymbolTable(this.currentTable);

        BlockParser blockParser = new BlockParser(this.currentTable, 0, true);
        this.block = blockParser.BlockParser();

        //检查block的最后一条语句
        Token eToken = this.block.checkReturn();
        if(eToken!=null){
            GlobalParm.addError(new Error(ErrorType.G_error,eToken));
        }
        //flag

        MainFuncDef mainFuncDef = new MainFuncDef(this.block,this.INTTK,this.MAINTK,this.LPARENT,this.RPARENT,this.currentTable);
        return mainFuncDef;
    }
}
