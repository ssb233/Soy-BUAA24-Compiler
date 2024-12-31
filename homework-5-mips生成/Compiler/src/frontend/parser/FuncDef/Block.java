package frontend.parser.FuncDef;

import frontend.lexer.Token;
import frontend.parser.parserOutput;
import middle.symbol.SymbolTable;

import java.util.ArrayList;

public class Block implements parserOutput {
    private Token LBRACE;
    private Token RBRACE;
    private ArrayList<BlockItem> blockItemArrayList;

    public int forLayerNum;//循环层的个数？，反正就是有循环就不为0,用来判断break/continue

    private SymbolTable currentTable;
    private int extra;
    public Block(Token LBRACE, Token RBRACE, ArrayList<BlockItem> blockItemArrayList, SymbolTable symbolTable, int extra, int forLayerNum){
        this.LBRACE = LBRACE;
        this.RBRACE = RBRACE;
        this.blockItemArrayList = blockItemArrayList;
        this.currentTable = symbolTable;
        this.extra = extra;
        this.forLayerNum = forLayerNum;
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

    public Token checkReturn(){//如果没有return就返回大括号的行号
        if(this.blockItemArrayList!=null){
            BlockItem blockItem = this.blockItemArrayList.get(this.blockItemArrayList.size()-1);//拿到最后一个
            if(blockItem.getStmt()!=null&&blockItem.getStmt().checkReturn()){//最后一条是语句而且是return语句,这里可能判断的有问题，插个眼flag
                return null;
            }else{//缺少return语句
                return this.RBRACE;//返回右侧大括号的行号
            }
        }else return this.RBRACE;//不可能有return，也没有语句
    }

    public ArrayList<BlockItem> getBlockItemArrayList(){
        ArrayList<BlockItem> list = new ArrayList<>();
        if(this.blockItemArrayList!=null){
            for(BlockItem item:this.blockItemArrayList){
                list.add(item);
            }
        }
        return list;
    }
}
