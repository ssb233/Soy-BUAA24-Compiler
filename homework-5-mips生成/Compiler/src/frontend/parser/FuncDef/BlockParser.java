package frontend.parser.FuncDef;

import frontend.GlobalParm;
import frontend.lexer.Token;
import middle.symbol.SymbolTable;

import java.util.ArrayList;

public class BlockParser {
    private Token LBRACE;
    private Token RBRACE;
    private ArrayList<BlockItem> blockItemArrayList;

    public int forLayerNum;//记录for
    public boolean funcTag;//记录是否是void，void就给false

    private SymbolTable symbolTable;//来自父元素
    private SymbolTable currentTable;//自身的表
    private int extra = 0;//做一个标记
    public BlockParser(SymbolTable symbolTable, int forLayerNum, boolean funcTag){//自己的表继承自父亲的表，funcDef情况
        this.currentTable = symbolTable;
        this.extra = 0;
        this.forLayerNum = forLayerNum;
        this.funcTag = funcTag;
    }
    public BlockParser(SymbolTable symbolTable, int extra, int forLayerNum, boolean funcTag){//建立自己的新表，stmt的情况
        this.symbolTable = symbolTable;
        this.currentTable = new SymbolTable(GlobalParm.getTableId(), this.symbolTable.id);
        GlobalParm.symbolTables.add(this.currentTable);
        this.symbolTable.addASymbolTable(this.currentTable);
        this.extra = extra;
        this.forLayerNum = forLayerNum;
        this.funcTag = funcTag;
    }

    public Block BlockParser(){
        Token token = GlobalParm.getAToken();
        if(token.getTokenTypeName().equals("LBRACE")){
            LBRACE = token;
            BlockItemParser blockItemParser = new BlockItemParser(this.currentTable, this.forLayerNum, this.funcTag);
            token = GlobalParm.getAToken();
            if(!token.getTokenTypeName().equals("RBRACE")){
                this.blockItemArrayList = new ArrayList<BlockItem>();
            }
            while(!token.getTokenTypeName().equals("RBRACE")){
                GlobalParm.backAToken();
                blockItemArrayList.add(new BlockItemParser(this.currentTable, this.forLayerNum, this.funcTag).BlockItemParser());
                token = GlobalParm.getAToken();
            }//这里跳出的token一定是右括号
            this.RBRACE = token;
        }else{
            GlobalParm.backAToken();
            return null;//第一个括号都不匹配
        }
        Block block = new Block(this.LBRACE, this.RBRACE, this.blockItemArrayList,this.currentTable, this.extra, this.forLayerNum);
        return block;
    }
}
