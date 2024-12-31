package frontend.parser.FuncDef;

import frontend.GlobalParm;
import frontend.lexer.Token;
import frontend.parser.Exp.Exp;
import frontend.parser.Exp.ExpParser;
import frontend.parser.Terminal.Ident;
import frontend.parser.Terminal.IdentParser;
import middle.error.Error;
import middle.error.ErrorType;
import middle.symbol.Symbol;
import middle.symbol.SymbolFunc;
import middle.symbol.SymbolTable;

import java.util.ArrayList;

public class FuncRParamsParser {
    private Exp expFirst=null;
    private ArrayList<Token> opTokens=null;
    private ArrayList<Exp> expArrayList=null;

    public SymbolTable symbolTable;
    public Ident ident;//函数名

    public FuncRParamsParser(SymbolTable symbolTable, Ident ident){
        this.symbolTable = symbolTable;
        this.ident = ident;
    }

    public FuncRParams FuncRParamsParser(){
        ExpParser expParser = new ExpParser(this.symbolTable);
        this.expFirst = expParser.ExpParser();

        if(expFirst == null){
            if(getFparams(ident, symbolTable)!=null && getFparams(ident, symbolTable).size()!=0){
                GlobalParm.addError(new Error(ErrorType.D_error, this.ident.getToken()));
            }
            return null;//特殊情况，在unaryExp中
        }

        Token token = GlobalParm.getAToken();
        if(token.getTokenTypeName().equals("COMMA")){
            this.opTokens = new ArrayList<Token>();
            this.expArrayList = new ArrayList<Exp>();
        }

        while(token.getTokenTypeName().equals("COMMA")){
            opTokens.add(token);
            expArrayList.add(new ExpParser(this.symbolTable).ExpParser());
            token = GlobalParm.getAToken();
        }GlobalParm.backAToken();

        //这里来匹配形参和实参
        matchFpRp(getFparams(ident,symbolTable), this.expFirst, this.expArrayList);

        FuncRParams funcRParams = new FuncRParams(expFirst, opTokens, expArrayList);
        return funcRParams;
    }

    public  void matchFpRp(ArrayList<Symbol> fParams, Exp expFirst, ArrayList<Exp> expList){
        Symbol symbol;

        if(expFirst!=null){
            if(expList!=null){
                if(fParams==null){
                    GlobalParm.addError(new Error(ErrorType.D_error, this.ident.getToken()));
                    return;
                }else if(fParams!=null && fParams.size()!=1+expList.size()){
                    GlobalParm.addError(new Error(ErrorType.D_error, this.ident.getToken()));
                    return;
                }

            }else if(expList==null){
                if(fParams==null){
                    GlobalParm.addError(new Error(ErrorType.D_error, this.ident.getToken()));
                    return;
                }else if(fParams!=null && fParams.size()!=1){
                    GlobalParm.addError(new Error(ErrorType.D_error, this.ident.getToken()));
                    return;
                }
            }
            //如果运行到这里，那么fParam一定不为空，而且数量对的上
            //接下来考虑类型不匹配
            ArrayList<Exp> list = new ArrayList<>();
            list.add(expFirst);
            if(expList!=null){
                for(Exp item:expList){
                    list.add(item);
                }
            }
            int i=0;
            while(i<fParams.size()){
                int type1 = fParams.get(i).getType();
                int type2 = list.get(i).getType();
                if(type1!=type2){
                    GlobalParm.addError(new Error(ErrorType.E_error, this.ident.getToken()));
                    break;
                }
                i++;
            }
        }
    }
    public static ArrayList<Symbol> getFparams(Ident ident, SymbolTable symbolTable){
        Token token = ident.getToken();
        String name = token.getTokenValue();
        Symbol symbol = SymbolTable.searchSymbol(name, symbolTable);//递归向上查找符号表
        if(symbol!=null){
            if(symbol instanceof SymbolFunc){
                SymbolFunc symbolFunc = (SymbolFunc) symbol;
                return symbolFunc.getParamList();
            }
        }
        return null;
    }
}
