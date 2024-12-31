package frontend;

import frontend.lexer.Token;
import middle.symbol.SymbolTable;
import middle.error.Error;

import java.util.ArrayList;
import java.util.List;

public class GlobalParm {
    public static ArrayList<Token> tokenList;
    public static ArrayList<String> sourceCode;

//    public static ArrayList<Token> lexerErrorList;
//    public static ArrayList<Token> parserErrorList;
    public static ArrayList<Error> ErrorList = new ArrayList<>();


    public static int tokenIter = -1;
    public static boolean isWrong = false;
    public static int tableId = 0;
    public static ArrayList<SymbolTable> symbolTables = new ArrayList<SymbolTable>();
    public GlobalParm(){

    }
    public static void setTokenList(ArrayList<Token> tokenList){
        GlobalParm.tokenList = tokenList;
    }
    public static void setSourceCode(ArrayList<String> sourceCode){
        GlobalParm.sourceCode = sourceCode;
    }
//    public static void setErrorList(ArrayList<Token> errorList){
//        GlobalParm.lexerErrorList = errorList;
//    }
//    public static void setParserErrorList(){GlobalParm.parserErrorList = new ArrayList<Token>();
//    }
    public static void initErrorList(){
        GlobalParm.ErrorList = new ArrayList<Error>();
    }
    public static void addError(Error error){
        GlobalParm.ErrorList.add(error);
    }
    public static void getRidError(){
        GlobalParm.ErrorList.remove(GlobalParm.ErrorList.size()-1);
    }
    public static ArrayList<Error> getErrorList(){
        return GlobalParm.ErrorList;
    }


    public static Token getAToken(){
        if(!isEndOfToken()) return tokenList.get(++tokenIter);
        else return null;
//        return tokenList.get(++tokenIter);
    }
    public static void backAToken(){
        tokenIter--;
    }
    public static boolean isEndOfToken(){
        return tokenIter==tokenList.size()-1;
    }
//    public static void addParserError(Token token){
//        GlobalParm.parserErrorList.add(token);
//    }
//    public static void getRidParserError(){
//        GlobalParm.parserErrorList.remove(GlobalParm.parserErrorList.size()-1);
//    }
    public static Token getCurrentToken(){
        return tokenList.get(tokenIter);
    }
//    public static ArrayList<Token> getParserErrorList(){
//        return GlobalParm.parserErrorList;
//    }


    public static void initSymbolArray(){
        GlobalParm.symbolTables = new ArrayList<SymbolTable>();
    }
    public static void addSymbolTable(SymbolTable symbolTable){
        GlobalParm.symbolTables.add(symbolTable);
    }
    public static int getTableId(){
        return ++GlobalParm.tableId;
    }
    public static SymbolTable getSymbolTable(int id){
        if(id<=GlobalParm.tableId&& GlobalParm.symbolTables!=null){
            for(SymbolTable item : GlobalParm.symbolTables){
                if(item.id == id){
                    return item;
                }
            }
            return null;
        }else return null;
    }

}
