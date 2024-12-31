package frontend.parser.Exp;

import frontend.lexer.Token;
import frontend.parser.FuncDef.FuncRParams;
import frontend.parser.Terminal.Ident;
import frontend.parser.parserOutput;
import middle.symbol.Symbol;
import middle.symbol.SymbolTable;

public class UnaryExp implements parserOutput {
    private PrimaryExp primaryExp=null;
    private Ident ident=null;
    private FuncRParams funcRparams=null;
    private UnaryOp unaryOp=null;
    private UnaryExp unaryExp=null;
    private Token LPARENT;
    private Token RPARENT;

    public SymbolTable symbolTable;

    public UnaryExp(PrimaryExp primaryExp, Ident ident, FuncRParams funcRparams, UnaryOp unaryOp, UnaryExp unaryExp,Token LPARENT,Token RPARENT, SymbolTable symbolTable){
        this.primaryExp = primaryExp;
        this.ident = ident;
        this.funcRparams = funcRparams;
        this.unaryOp = unaryOp;
        this.unaryExp = unaryExp;
        this.LPARENT = LPARENT;
        this.RPARENT = RPARENT;
        this.symbolTable = symbolTable;
    }
    @Override
    public String output() {
        StringBuilder stringBuilder = new StringBuilder();
        if(primaryExp!=null){
            stringBuilder.append(primaryExp.output());
        }else if(unaryOp!=null){
            stringBuilder.append(unaryOp.output());
            stringBuilder.append(unaryExp.output());
        }else if(ident!=null){
            stringBuilder.append(ident.output());
            stringBuilder.append(LPARENT.output());
            if(funcRparams!=null)stringBuilder.append(funcRparams.output());
            stringBuilder.append(RPARENT.output());
        }
        stringBuilder.append("<UnaryExp>\n");
        return stringBuilder.toString();
    }

    public int getType(){
        if(this.primaryExp!=null){//primaryExp
            return primaryExp.getType();
        }else if(unaryOp !=null){//unaryExp
            return unaryExp.getType();
        }else if(ident!=null){//函数调用,找到返回值类型就行
            String name = ident.getToken().getTokenValue();
            Symbol symbol = SymbolTable.searchSymbol(name, this.symbolTable);
            if(symbol !=null)return symbol.getType();//获取函数名的类型，也就是函数的返回类型
            else{
                return -1;
            }
        }else{
            return -1;//这是不可能的情况
        }
    }

    public int getValue(){
        if(this.primaryExp!=null){
            return this.primaryExp.getValue();
        }else if(this.unaryOp!=null){
            String op = this.unaryOp.getToken().getTokenValue();
            if(op.equals("+")){
                return this.unaryExp.getValue();
            }else if(op.equals("-")){
                return 0-this.unaryExp.getValue();
            }else if(op.equals("!")){
                if(this.unaryExp.getValue()==0)return 1;
                else return 0;
            }
        }else if(this.ident!=null){
            System.out.println("maybe it should not be calculated!");
            return 0;
        }
        return 0;
    }
    public PrimaryExp getPrimaryExp(){
        return this.primaryExp;
    }
    public UnaryOp getUnaryOp(){
        return this.unaryOp;
    }
    public UnaryExp getUnaryExp(){
        return this.unaryExp;
    }
    public Ident getIdent(){
        return this.ident;
    }
    public FuncRParams getFuncRparams(){
        return this.funcRparams;
    }
}
