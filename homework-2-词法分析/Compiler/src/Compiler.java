import frontend.lexer.*;

import java.io.*;
import java.util.List;

public class Compiler {
    public static void main(String[] args) throws FileNotFoundException {
        String inputPath = "testfile.txt";
        List<Token> list;
        try (BufferedReader reader = new BufferedReader(new FileReader(inputPath))) {
            TokenLexer tokenLexer = new TokenLexer(reader);
            tokenLexer.lexer();
            if (tokenLexer.getIsCorrect()) {//程序正确
                list = tokenLexer.getTokenList();
            } else {//不正确，
                list = tokenLexer.getErrorList();
            }
            //output
            String correctPath = "lexer.txt";
            String errorPath = "error.txt";
            if (tokenLexer.getIsCorrect()) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(correctPath))) {
                    for (Token token : list) {
//                        if(token.getTokenTypeName().equals("INTCON")){
//                            writer.write("INTCON"+' '+token.getNumValue());//数值本质和字符串应该一样
//                        }else{
//                            writer.write(token.getTokenTypeName()+' '+token.getTokenValue());
//                        }
                        writer.write(token.getTokenTypeName() + ' ' + token.getTokenValue());
                        writer.newLine();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(errorPath))) {
                    for (Token token : list) {
                        writer.write(String.valueOf(token.getLineNum()));
                        writer.write(' ' + token.getTokenValue());
                        writer.newLine();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(System.getProperty("user.dir"));
        }
    }
}