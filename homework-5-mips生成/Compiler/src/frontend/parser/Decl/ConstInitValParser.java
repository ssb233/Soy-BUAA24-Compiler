package frontend.parser.Decl;

import frontend.GlobalParm;
import frontend.lexer.Token;
import frontend.parser.Exp.ConstExp;
import frontend.parser.Exp.ConstExpParser;
import frontend.parser.Terminal.StringConst;
import middle.symbol.SymbolTable;

import java.util.ArrayList;

public class ConstInitValParser {
    private ConstExp constExp;
    private ConstExp constExpFirst;
    private ArrayList<Token> opTokens;
    private ArrayList<ConstExp> constExpArrayList;
    private StringConst stringConst;
    private Token LBRACE;
    private Token RBRACE;

    public SymbolTable symbolTable;
    public ConstInitValParser(SymbolTable symbolTable){
        this.symbolTable = symbolTable;
    }

    public ConstInitVal ConstInitValParser(){
        Token token = GlobalParm.getAToken();
        if(token.getTokenTypeName().equals("STRCON")){
            this.stringConst = new StringConst(token);
        }else if(token.getTokenTypeName().equals("LBRACE")){
            this.LBRACE = token;
            token = GlobalParm.getAToken();
            if(token.getTokenTypeName().equals("RBRACE")){
                //直接结束了，合法
                this.RBRACE = token;
            }else{//有ident
                GlobalParm.backAToken();
                ConstExpParser constExpParser = new ConstExpParser(this.symbolTable);
                this.constExpFirst = constExpParser.ConstExpParser();

                token = GlobalParm.getAToken();
                if(token.getTokenTypeName().equals("COMMA")){
                    opTokens = new ArrayList<Token>();
                    constExpArrayList = new ArrayList<ConstExp>();
                }
                while(token.getTokenTypeName().equals("COMMA")){
                    opTokens.add(token);
                    constExpArrayList.add(new ConstExpParser(this.symbolTable).ConstExpParser());
                    token = GlobalParm.getAToken();
                }GlobalParm.backAToken();
//                GlobalParm.getAToken();//右侧大括号，一定有的
                token = GlobalParm.getAToken();
                if(token.getTokenTypeName().equals("RBRACE")){
                    this.RBRACE = token;
                }
            }
        }else{

            GlobalParm.backAToken();
            ConstExpParser constExpParser = new ConstExpParser(this.symbolTable);
            this.constExp = constExpParser.ConstExpParser();
        }
        ConstInitVal constInitVal = new ConstInitVal(this.constExp,this.constExpFirst, this.opTokens, this.constExpArrayList, this.stringConst,this.LBRACE,this.RBRACE);
        return constInitVal;
    }
}
