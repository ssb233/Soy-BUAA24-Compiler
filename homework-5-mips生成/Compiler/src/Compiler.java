import backend.mipsModule;
import backend.mipsModuleBuilder;
import backend2.MipsGenModule;
import frontend.GlobalParm;
import frontend.lexer.*;
import frontend.parser.CompUnit;
import frontend.parser.CompUnitParser;
import middle.error.Error;
import middle.llvm.llvmBuilder;
import middle.llvm.llvmModule;
import middle.symbol.Symbol;
import middle.symbol.SymbolTable;

import java.io.*;
import java.util.*;

public class Compiler {
    public static void main(String[] args) throws FileNotFoundException {
        String inputPath = "testfile.txt";
        List<Token> list = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(inputPath))) {
            TokenLexer2 tokenLexer = new TokenLexer2(reader);
            tokenLexer.lexer();

            GlobalParm.setTokenList(tokenLexer.getTokenList());

            CompUnitParser compUnitParser = new CompUnitParser();
            CompUnit compUnit = compUnitParser.CompUnitParser();//语法分析


            ArrayList<Error> ErrorList = GlobalParm.getErrorList();
            if (ErrorList.size() > 0) {
                GlobalParm.isWrong = true;//说明有分析错误
            }

            ArrayList<SymbolTable> tables = GlobalParm.symbolTables;


            //错误token排序
            Collections.sort(ErrorList, new Comparator<Error>() {
                @Override
                public int compare(Error o1, Error o2) {
                    return o1.getLineNum() - o2.getLineNum();
                }
            });


            //output
            String correctPath = "lexer.txt";
            String errorPath = "error.txt";
            String parserPath = "parser.txt";
            String symbolPath = "symbol.txt";
            String llvmPath = "llvm_ir.txt";
            String mipsPath = "mips.txt";
            if (!GlobalParm.isWrong) {//正确情况
                //词法分析
//                try (BufferedWriter writer = new BufferedWriter(new FileWriter(correctPath))) {
//                    list = tokenLexer.getTokenList();
//                    for (Token token : list) {
//                        writer.write(token.getTokenTypeName() + ' ' + token.getTokenValue());
//                        writer.newLine();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

                //语法分析
//                try (BufferedWriter writer = new BufferedWriter(new FileWriter(parserPath))) {
//                    writer.write(compUnit.output());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

                //语义分析输出
//                try (BufferedWriter writer = new BufferedWriter(new FileWriter(symbolPath))) {
//                    for (SymbolTable table:tables) {
//                        for(Symbol item: table.directory){
//                            writer.write(String.valueOf(table.id));
//                            writer.write(' '+item.getStringName());
//                            writer.write(' '+item.symbolType.toString());
//                            writer.newLine();
//                        }
//
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }


                //llvm生成
                llvmBuilder builder = new llvmBuilder(compUnit);
                llvmModule module = builder.buildLlvmModule();
                //setName
                module.setName();

                //llvm输出
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(llvmPath))) {
                    writer.write(module.llvmOutput());
                } catch (IOException e) {
                    e.printStackTrace();
                }


                //mips生成
                MipsGenModule mipsGenModule = new MipsGenModule(module);


                //mips输出
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(mipsPath))) {
                    writer.write(mipsGenModule.genMips());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {//错误情况
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(errorPath))) {
                    for (Error error : ErrorList) {
                        writer.write(String.valueOf(error.getLineNum()));
                        writer.write(' ' + error.getErrorTypeString());
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
