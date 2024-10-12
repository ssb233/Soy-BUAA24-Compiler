package frontend.parser.Decl;

import frontend.GlobalParm;
import frontend.lexer.Token;
import frontend.parser.Exp.Exp;
import frontend.parser.Exp.ExpParser;
import frontend.parser.Terminal.StringConst;

import java.util.ArrayList;

public class InitValParser {
    private Exp exp;
    private Exp expFirst;
    private ArrayList<Token> opTokens;
    private ArrayList<Exp> expArrayList;
    private StringConst stringConst;
    private Token LBRACE;
    private Token RBRACE;

    public InitVal InitValParser(){
        Token token = GlobalParm.getAToken();
        if(token.getTokenTypeName().equals("STRCON")){
            this.stringConst = new StringConst(token);
        }else if(token.getTokenTypeName().equals("LBRACE")){
            this.LBRACE = token;
            token = GlobalParm.getAToken();
            if(token.getTokenTypeName().equals("RBRACE")){
                //直接结束了有右侧括号,这个不会出错，要么在这出现要么后面出现
                this.RBRACE = token;
            }else{//没出现右侧大括号就一定有exp
                GlobalParm.backAToken();//没结束，一定有一个exp
                ExpParser expParser = new ExpParser();
                this.expFirst = expParser.ExpParser();

                token = GlobalParm.getAToken();
                if(token.getTokenTypeName().equals("COMMA")){//有后续多个
                    this.opTokens = new ArrayList<Token>();
                    this.expArrayList = new ArrayList<Exp>();
                }
                while(token.getTokenTypeName().equals("COMMA")){
                    opTokens.add(token);
                    expArrayList.add(new ExpParser().ExpParser());
                    token = GlobalParm.getAToken();
                }GlobalParm.backAToken();
                //到这还要读取一个右侧大括号
                token = GlobalParm.getAToken();
                if(token.getTokenTypeName().equals("RBRACE")){
                    this.RBRACE = token;
                }else GlobalParm.backAToken();
            }
        }else {//剩余情况一定是exp
            GlobalParm.backAToken();
            ExpParser expParser = new ExpParser();
            this.exp = expParser.ExpParser();
            if(this.exp==null)return null;//exp里面如果有不匹配
        }
        InitVal initVal = new InitVal(this.exp, this.expFirst, this.opTokens,this.expArrayList, this.stringConst,this.LBRACE,this.RBRACE);
        return initVal;
    }
}
