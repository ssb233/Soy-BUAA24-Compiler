package frontend.parser.FuncDef;

import frontend.GlobalParm;
import frontend.lexer.Token;
import frontend.parser.Terminal.Ident;
import frontend.parser.Terminal.IdentParser;

public class FuncDefParser {
    private FuncType funcType;
    private Ident ident;
    private FuncFParams funcFParams;
    private Block block;
    private Token LPARENT;
    private Token RPARENT;
    public FuncDef FuncDefParser(){
        FuncTypeParser funcTypeParser = new FuncTypeParser();
        this.funcType = funcTypeParser.FuncTypeParser();
        if(this.funcType==null)return null;

        IdentParser identParser = new IdentParser();
        this.ident = identParser.IdentParser();

        Token token = GlobalParm.getAToken();
        if(token.getTokenTypeName().equals("LPARENT")){
            this.LPARENT = token;
        }else GlobalParm.backAToken();

        token = GlobalParm.getAToken();
        if(token.getTokenTypeName().equals("LPARENT")){
            this.LPARENT = token;
        }
        //这里有三种情况，1.读到参数，也就是BType开头，2读到右侧小括号， 3.读到Block的左大括号
        if(token.getTokenTypeName().equals("LBRACE")){//读到左大括号，说明这里无参数，并且右侧小括号缺失
            GlobalParm.backAToken();
            //error, j型
            Token fToken = GlobalParm.getCurrentToken();
            Token eToken = new Token(null, "j", fToken.getLineNum(),0);
            GlobalParm.addParserError(eToken);
        }else if(token.getTokenTypeName().equals("RPARENT")){//读到右侧括号，说明无参
            //正确无任何异议
            this.RPARENT = token;
        }else{//排除上面两种，这里一定是有参数的
            GlobalParm.backAToken();
            FuncFParamsParser funcFParamsParser = new FuncFParamsParser();
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
                GlobalParm.addParserError(eToken);
            }
        }
        //处理block
        BlockParser blockParser = new BlockParser();
        this.block = blockParser.BlockParser();

        FuncDef funcDef = new FuncDef(this.funcType, this.ident, this.funcFParams, this.block,this.LPARENT,this.RPARENT);
        return funcDef;
    }
}
