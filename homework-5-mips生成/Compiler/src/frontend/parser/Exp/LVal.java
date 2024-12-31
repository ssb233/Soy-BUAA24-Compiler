package frontend.parser.Exp;

import frontend.GlobalParm;
import frontend.lexer.Token;
import frontend.parser.Terminal.Ident;
import frontend.parser.parserOutput;
import middle.error.Error;
import middle.error.ErrorType;
import middle.symbol.Symbol;
import middle.symbol.SymbolTable;

public class LVal extends PrimaryExpExtend implements parserOutput {
    private Ident ident;
    private Exp exp;
    private Token LBRACK;
    private Token RBRACK;

    public SymbolTable symbolTable;
    public LVal(Ident ident, Exp exp,Token LBRACK,Token RBRACK, SymbolTable symbolTable){
        this.exp = exp;
        this.ident = ident;
        this.LBRACK = LBRACK;
        this.RBRACK = RBRACK;
        this.symbolTable = symbolTable;
    }
    public Token getRBRACK(){return this.RBRACK;}
    public Token getLBRACK(){return this.LBRACK;}
    public Ident getIdent(){
        return this.ident;
    }
    @Override
    public String output() {
       StringBuilder stringBuilder = new StringBuilder();
       if(ident!=null){
           stringBuilder.append(ident.output());
           if(LBRACK!=null){
               stringBuilder.append(LBRACK.output());
               stringBuilder.append(exp.output());
               stringBuilder.append(RBRACK.output());
           }
       }
       stringBuilder.append("<LVal>\n");
       return stringBuilder.toString();
    }

    public void checkIdentConst(SymbolTable symbolTable){
        Token token = this.ident.getToken();
        String name = token.getTokenValue();
        Symbol symbol = SymbolTable.searchSymbol(name, symbolTable);//递归向上查找符号表
        if(symbol!=null){//前提这个符号得有
            if(symbol.symbolType.toString().equals("ConstChar")||symbol.symbolType.toString().equals("ConstInt")||symbol.symbolType.toString().equals("ConstCharArray")||symbol.symbolType.toString().equals("ConstIntArray")){
                //说明是const，需要报错
                GlobalParm.addError(new Error(ErrorType.H_error, token));
            }
        }

    }
    public int getArrayOrVar(){//var 1, array 2
        if(this.LBRACK!=null){
            return 2;
        }else return 1;
    }

    public static void checkIdentExist(SymbolTable symbolTable, Ident ident){
        Token token = ident.getToken();
        String name = token.getTokenValue();
        Symbol symbol = SymbolTable.searchSymbol(name, symbolTable);//递归向上查找符号表
        if(symbol == null){
            GlobalParm.addError(new Error(ErrorType.C_error, token));
        }
    }

    public int getType(){
        Symbol symbol = SymbolTable.searchSymbol(this.ident.getToken().getTokenValue(), this.symbolTable);
        if(this.LBRACK!=null){//带括号的，一定是int/char变量吧？
            int type=0;
            if(symbol!=null){
                type = symbol.getType();
            }
            if(type == 2 || type==3){//intarr, charArr, 都有括号，也就是变成了变量，要返回1
                return 1;
            }else{//只可能是4了
                return 4;
            }
        }else{//只有ident，不确定什么类型
            if(symbol!=null){
                return symbol.getType();
            }else return 0;

        }
    }

    public int getValue(){
        if(this.exp==null){
            if(this.ident!=null){
                return this.ident.searchIdentValue(this.symbolTable);
            }
        }else{
            if(this.ident!=null){
                int index = this.exp.getValue();
                return this.ident.searchArrayIndexValue(this.symbolTable,index);
            }
        }
        return 0;
    }

    public Exp getExp(){
        return this.exp;
    }
}
