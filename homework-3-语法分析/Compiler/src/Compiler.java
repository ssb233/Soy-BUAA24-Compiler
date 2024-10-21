import frontend.GlobalParm;
import frontend.lexer.*;
import frontend.parser.CompUnit;
import frontend.parser.CompUnitParser;

import java.io.*;
import java.util.*;

public class Compiler {
    public static void main(String[] args) throws FileNotFoundException {
        String inputPath = "testfile.txt";
        List<Token> list = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(inputPath))) {
            TokenLexer tokenLexer = new TokenLexer(reader);
            tokenLexer.lexer();

            GlobalParm.setTokenList(tokenLexer.getTokenList());
            GlobalParm.setErrorList(tokenLexer.getErrorList());
            GlobalParm.setParserErrorList();

            CompUnitParser compUnitParser = new CompUnitParser();
            CompUnit compUnit = compUnitParser.CompUnitParser();//语法分析

            ArrayList<Token> parserError = GlobalParm.getParserErrorList();
            if (parserError.size() > 0) {
                GlobalParm.isParserWrong = true;//说明有语法分析错误
            }


            list = tokenLexer.getErrorList();


            //把语法分析的错误也加入到list里面
            for (Token item : parserError) {
                list.add(item);
            }

            //错误token排序
            Collections.sort(list, new Comparator<Token>() {
                @Override
                public int compare(Token o1, Token o2) {
                    return o1.getLineNum() - o2.getLineNum();
                }
            });

            //output
            String correctPath = "lexer.txt";
            String errorPath = "error.txt";
            String parserPath = "parser.txt";
//            if (tokenLexer.getIsCorrect()) {//词法分析输出
//                try (BufferedWriter writer = new BufferedWriter(new FileWriter(correctPath))) {
//                    for (Token token : list) {
//                        writer.write(token.getTokenTypeName() + ' ' + token.getTokenValue());
//                        writer.newLine();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            } else {
//                try (BufferedWriter writer = new BufferedWriter(new FileWriter(errorPath))) {
//                    for (Token token : list) {
//                        writer.write(String.valueOf(token.getLineNum()));
//                        writer.write(' ' + token.getTokenValue());
//                        writer.newLine();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }

            if (tokenLexer.getIsCorrect() && !GlobalParm.isParserWrong) {//语法分析输出
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(parserPath))) {
                    writer.write(compUnit.output());
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
