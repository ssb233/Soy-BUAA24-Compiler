package frontend;

import frontend.lexer.Token;

import java.util.ArrayList;
import java.util.List;

public class GlobalParm {
    public static ArrayList<Token> tokenList;
    public static ArrayList<String> sourceCode;
    public static ArrayList<Token> lexerErrorList;
    public static ArrayList<Token> parserErrorList;
    public static int tokenIter = -1;
    public static boolean isParserWrong = false;
    public GlobalParm(){

    }
    public static void setTokenList(ArrayList<Token> tokenList){
        GlobalParm.tokenList = tokenList;
    }
    public static void setSourceCode(ArrayList<String> sourceCode){
        GlobalParm.sourceCode = sourceCode;
    }
    public static void setErrorList(ArrayList<Token> errorList){
        GlobalParm.lexerErrorList = errorList;
    }
    public static void setParserErrorList(){GlobalParm.parserErrorList = new ArrayList<Token>();
    }
    public static Token getAToken(){
        if(!isEndOfToken()) return tokenList.get(++tokenIter);
        else return null;
    }
    public static void backAToken(){
        tokenIter--;
    }
    public static boolean isEndOfToken(){
        return tokenIter==tokenList.size()-1;
    }
    public static void addParserError(Token token){
        GlobalParm.parserErrorList.add(token);
    }
    public static Token getCurrentToken(){
        return tokenList.get(tokenIter);
    }
    public static ArrayList<Token> getParserErrorList(){
        return GlobalParm.parserErrorList;
    }
}
