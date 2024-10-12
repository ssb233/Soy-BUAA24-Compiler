package frontend.parser;

import frontend.GlobalParm;
import frontend.lexer.Token;
import frontend.parser.FuncDef.BlockParser;
import frontend.parser.FuncDef.Block;
public class MainFuncDefParser {
    private Token INTTK;
    private Token MAINTK;
    private Token LPARENT;
    private Token RPARENT;
    private Block block;
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
            GlobalParm.addParserError(eToken);
        }else{
            this.RPARENT = token;
        }
        BlockParser blockParser = new BlockParser();
        this.block = blockParser.BlockParser();

        MainFuncDef mainFuncDef = new MainFuncDef(this.block,this.INTTK,this.MAINTK,this.LPARENT,this.RPARENT);
        return mainFuncDef;
    }
}
