package frontend.parser.FuncDef;

import frontend.lexer.Token;
import frontend.parser.parserOutput;

import java.util.ArrayList;

public class Block implements parserOutput {
    private Token LBRACE;
    private Token RBRACE;
    private ArrayList<BlockItem> blockItemArrayList;
    public Block(Token LBRACE, Token RBRACE, ArrayList<BlockItem> blockItemArrayList){
        this.LBRACE = LBRACE;
        this.RBRACE = RBRACE;
        this.blockItemArrayList = blockItemArrayList;
    }

    @Override
    public String output() {
        StringBuilder stringBuilder = new StringBuilder();
        if(LBRACE!=null){
            stringBuilder.append(LBRACE.output());
            if(blockItemArrayList!=null){
                for(BlockItem item:blockItemArrayList){
                    stringBuilder.append(item.output());
                }
            }
            stringBuilder.append(RBRACE.output());
        }
        stringBuilder.append("<Block>\n");
        return stringBuilder.toString();
    }
}
