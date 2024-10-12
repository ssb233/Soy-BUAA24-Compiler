package frontend.parser.FuncDef;

import frontend.GlobalParm;
import frontend.lexer.Token;

import java.util.ArrayList;

public class BlockParser {
    private Token LBRACE;
    private Token RBRACE;
    private ArrayList<BlockItem> blockItemArrayList;
    public Block BlockParser(){
        Token token = GlobalParm.getAToken();
        if(token.getTokenTypeName().equals("LBRACE")){
            LBRACE = token;
            BlockItemParser blockItemParser = new BlockItemParser();
            token = GlobalParm.getAToken();
            if(!token.getTokenTypeName().equals("RBRACE")){
                this.blockItemArrayList = new ArrayList<BlockItem>();
            }
            while(!token.getTokenTypeName().equals("RBRACE")){
                GlobalParm.backAToken();
                blockItemArrayList.add(new BlockItemParser().BlockItemParser());
                token = GlobalParm.getAToken();
            }//这里跳出的token一定是右括号
            this.RBRACE = token;
        }else{
            GlobalParm.backAToken();
            return null;//第一个括号都不匹配
        }
        Block block = new Block(this.LBRACE, this.RBRACE, this.blockItemArrayList);
        return block;
    }
}
