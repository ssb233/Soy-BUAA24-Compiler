package frontend.lexer;

import frontend.GlobalParm;
import middle.error.ErrorType;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import middle.error.Error;

public class TokenLexer2 {
    private BufferedReader bufferedReader;
    private int lineNum = 1;
    private int site = 0;
    private ArrayList<Token> tokenList;
    private ArrayList<Token> errorList;
    private boolean isCorrect = true;//默认语法解析正确
    private ArrayList<Character> characters;

    public TokenLexer2(BufferedReader bufferedReader) throws IOException {
        this.bufferedReader = bufferedReader;
        this.tokenList = new ArrayList<Token>();
        this.errorList = new ArrayList<Token>();
        this.characters = new ArrayList<>();
        this.readAll();
    }
    public void readAll() throws IOException {
        String tmp = null;
        while ((tmp = this.bufferedReader.readLine()) != null) {
            for (Character ch : tmp.toCharArray()) {
                characters.add(ch);
            }
            characters.add('\n');
        }
    }

    private char getChar() {
        if(site<this.characters.size()) {
            int tmp = site;
            site++;
//            while(this.characters.get(tmp) == '\n'&&site<this.characters.size()) {
//                this.lineNum++;
//                tmp = site++;
//            }
            if(this.characters.get(tmp)=='\n')this.lineNum++;
            return this.characters.get(tmp);
        }
        return 0;
    }

    private boolean isNonDigit(char ch) {
        if (Character.isAlphabetic(ch) || ch == '_') return true;
        else return false;
    }

    private boolean isSpecialCh(char ch) {
        char[] list = {'+', '-', '*', '%', ';', ',', '(', ')', '[', ']', '{', '}'};//把'/'排除了，对于注释情况需要考虑
        for (char item : list) {
            if (ch == item) return true;
        }
        return false;
    }

    private boolean isGreedyCh(char ch) {
        char[] list = {'!', '&', '|', '<', '>', '='};
        for (char item : list) {
            if (ch == item) return true;
        }
        return false;
    }

    private TokenType patternMatch(String patternString) {
        Pattern pattern;
        for (TokenType type : TokenType.values()) {
            if (type.getJudge()) {//true代表正则匹配
                pattern = Pattern.compile(type.getPatternString());
                Matcher matcher = pattern.matcher(patternString);
                if (matcher.find()) return type;
            } else {//字符串比较
                if (patternString.equals(type.getPatternString())) return type;
            }
        }
        return null;//无匹配
    }
    private char getCurrentCh() {
        return this.characters.get(site);
    }
    public void lexer() {
        char ch;
        while (site<this.characters.size()) {
            ch = getChar();
                if (isNonDigit(ch)) {//标识符或保留关键字
                    String tokenString = new String();
                    tokenString += ch;
                    while (site<this.characters.size() && (isNonDigit(getCurrentCh()) || Character.isDigit(getCurrentCh()))) {
                        tokenString += getChar();
                    }
                    TokenType tokenType = patternMatch(tokenString);
                    if (tokenType != null) {
                        Token token = new Token(tokenType, tokenString, lineNum, 0);
                        tokenList.add(token);
                    } else System.out.println(tokenString + "没匹配到任何符号");
                } else if (Character.isDigit(ch)) {//数字开头
                    String tokenString = new String();
                    tokenString += ch;
                    while (site<this.characters.size() && Character.isDigit(getCurrentCh())) {
                        tokenString += getChar();
                    }
                    long numValue = Long.valueOf(tokenString);
                    Token token = new Token(TokenType.INTCON, tokenString, lineNum, numValue);
                    tokenList.add(token);
                } else if (ch == '\'') {//单引号，单个字符形式，特殊：单引号，双引号，反斜杠，要转义
                    if (site + 1 < this.characters.size()) {
                        int value=0;
                        char chMid, chR;
                        chMid = (char) getChar();
                        chR = (char) getChar();
                        String tokenString = new String();
                        tokenString+='\'';//正单引号
                        tokenString+=chMid;
                        tokenString+=chR;//chR有两种情况，一种是真正的字符，一种是反单引号
                        if(chMid =='\\'){//遇到了转义符号，必须再读取一位，此时chR一定是真正字符，因此要加上反引号并且多读一位
                            if(site<this.characters.size())getChar();//读取的一定是反单引号
                            tokenString+='\'';//反单引号
                            switch (chR){
                                case 'a':value = 7;
                                    break;
                                case 'b':value = 8;
                                    break;
                                case 't':value = 9;
                                    break;
                                case 'n':value = 10;
                                    break;
                                case 'v':value = 11;
                                    break;
                                case 'f':value = 12;
                                    break;
                                case '\"':value = 34;
                                    break;
                                case '\'':value = 39;
                                    break;
                                case '\\':value = 92;
                                    break;
                                case '0':value = 0;
                                    break;
                            }
                        }else value = chMid;
                        Token token = new Token(TokenType.CHRCON, tokenString, lineNum, value);
                        tokenList.add(token);
                    }
                    //这里不知道要不要继续处理
                } else if (ch == '"') {//字符串
                    String tokenString = new String();
                    tokenString+='"';
                    while (site<this.characters.size() && getCurrentCh() != '"') {
                        char tmp = (char) getChar();
                        if(tmp=='\\'){//读到转义不管怎么样都再读一个
                            tokenString+=tmp;
                            tokenString+=getChar();
                        }
                        else{
                            tokenString+=tmp;
                        }
                    }//这里应该默认得以双引号退出循环
                    tokenString+=getChar();
                    Token token = new Token(TokenType.STRCON, tokenString, lineNum, 0);
                    tokenList.add(token);
                } else if (isSpecialCh(ch)) {//特殊字符，非贪婪
                    String tokenString = new String();
                    tokenString += ch;
                    TokenType tokenType = patternMatch(tokenString);
                    Token token = new Token(tokenType, tokenString, lineNum, 0);
                    tokenList.add(token);
                } else if (isGreedyCh(ch)) {//贪婪匹配
                    char ch2 = 0;
                    String tokenString = new String();
                    if (site<this.characters.size()) {
                        ch2 = (char) getCurrentCh();
                    }
                    switch (ch) {
                        case '!':
                            tokenString += "!";
                            if (ch2 == '=') {
                                getChar();
                                tokenString += "=";
                            }
                            break;
                        case '&':
                            tokenString += "&";
                            if (ch2 == '&') {
                                getChar();
                                tokenString += "&";
                            } else {
                                Token eToken = new Token(null, "a", lineNum, -1);
//                                errorList.add(eToken);
                                Error error = new Error(ErrorType.A_error,eToken);
                                GlobalParm.addError(error);
                                //为了语法分析跳过此法分析错误，这里要加一个正确的token
                                tokenString+="&";//这个在末尾会被加进去
                                //
                                isCorrect = false;
                            }
                            break;
                        case '|':
                            tokenString += "|";
                            if (ch2 == '|') {
                                getChar();
                                tokenString += "|";
                            } else {
                                Token eToken = new Token(null, "a", lineNum, -1);
//                                errorList.add(eToken);
                                Error error = new Error(ErrorType.A_error,eToken);
                                GlobalParm.addError(error);
                                //为了语法分析跳过此法分析错误，这里要加一个正确的token
                                tokenString+="|";
                                //
                                isCorrect = false;
                            }
                            break;
                        case '>':
                            tokenString += ">";
                            if (ch2 == '=') {
                                getChar();
                                tokenString += "=";
                            }
                            break;
                        case '<':
                            tokenString += "<";
                            if (ch2 == '=') {
                                getChar();
                                tokenString += "=";
                            }
                            break;
                        case '=':
                            tokenString += "=";
                            if (ch2 == '=') {
                                getChar();
                                tokenString += "=";
                            }
                            break;
                    }
                    TokenType tokenType = patternMatch(tokenString);
                    Token token = new Token(tokenType, tokenString, lineNum, 0);
                    tokenList.add(token);
                } else if (ch == '/') {//注释情况，除法情况
                    String tokenString = new String();
                    tokenString += "/";
                    if (site<this.characters.size() && getCurrentCh() == '/') {//单行注释情况
                        tokenString += getChar();
                        while (site<this.characters.size()&&getCurrentCh()!='\n') {
                            tokenString += getChar();
                        }
                        //这里应该不用再管这个换行符了，它会在下一轮读取被处理
                    } else if (site<this.characters.size() && getCurrentCh() == '*') {//多行注释的起始
                        tokenString += getChar();//*
//                        if(isEndOfLine())readALine();//确保特殊情况下能进入下面的循环
//                        while (!isEndOfLine()) {//可能读完/*就完了，就需要下一行
//                            while (!isEndOfLine() && getCurrentCh() != '*') {
//                                char tmp = getChar();
//                                tokenString += tmp;
//                                if (tmp == '\n'|| isEndOfLine()) readALine();//到下一行,这里可能读到换行符，也可能莫名其妙没读到换行符，需要我手动判断是否到达末尾
//                            }
//                            //到这里说明读到*了
//                            while (!isEndOfLine() && getCurrentCh() == '*') {
//                                tokenString += getChar();
//                            }
//                            if (!isEndOfLine() && getCurrentCh() == '/') {
//                                tokenString += getChar();//注释结束
//                            }
//                        }
                        int lineTmp;
                        while(true){
                            while(getChar()!='*');//到这读到*
                            //记录此时行号
                            lineTmp = this.lineNum;
                            ch = (char) getCurrentCh();
                            if(ch=='/'){
                                getChar();
                                if(lineTmp==this.lineNum)break;
                            }else if(ch=='*'){

                            }
                        }
                    } else {//是除号
                        Token token = new Token(TokenType.DIV, tokenString, lineNum, 0);
                        tokenList.add(token);
                    }
                }
                //再后面就是不需要处理的字符了
            }
        }

    public boolean getIsCorrect(){
        return isCorrect;
    }
    public ArrayList<Token> getTokenList(){return tokenList;}
    public ArrayList<Token> getErrorList(){return errorList;}
    public void outputTokenList(){
        for(Token token:tokenList){
//            if(token.getTokenTypeName().equals("INTCON")){
//                System.out.println("INTCON"+' '+token.getNumValue());//数值其实本质和字符串一样
//            }else{
//                System.out.println(token.getTokenTypeName()+' '+token.getTokenValue());
//            }
            System.out.println(token.getTokenTypeName()+' '+token.getTokenValue());
        }
    }
    public void outputErrorList(){
        for(Token token:errorList){
            System.out.print(token.getLineNum());
            System.out.println(' '+token.getTokenValue());
//            System.out.println(token.getLineNum()+' '+token.getTokenValue());
        }
    }

}