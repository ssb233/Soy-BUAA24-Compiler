package frontend.parser.Stmt;

import frontend.GlobalParm;
import frontend.lexer.Token;
import frontend.parser.Exp.*;
import frontend.parser.FuncDef.Block;
import frontend.parser.FuncDef.BlockParser;
import frontend.parser.Terminal.StringConst;

import java.util.ArrayList;

public class StmtParser {
    private Token BC;
    private Token SEMICN;
    private Token RETURN;
    private Exp returnExp;
    private Exp rightExp;
    private ArrayList<Exp> printExpList;
    private ArrayList<Token> opTokens;
    private Exp exp;//作为单独的exp或者LVal
    private Token get_INT_CHAR;
    private Token ASSIGN;
    private Token PRINTF;
    private Token LPARENT;
    private Token RPARENT;
    private StringConst stringConst;
    private Token IFTK;
    private Token FORTK;
    private Cond cond;
    private Token ELSE;
    private Stmt stmt1;
    private Stmt stmt2;
    private Block block;
    private Token for_semicn1;
    private Token for_semicn2;
    private ForStmt for_stmt1;
    private ForStmt for_stmt2;

    private LVal lVal;

    public Stmt StmtParser() {
        Token token = GlobalParm.getAToken();
        if (token.getTokenTypeName().equals("BREAKTK") || token.getTokenTypeName().equals("CONTINUETK")) {
            this.BC = token;
            token = GlobalParm.getAToken();
            if (token.getTokenTypeName().equals("SEMICN")) {
                this.SEMICN = token;
            } else {
                GlobalParm.backAToken();
                Token fToken = GlobalParm.getCurrentToken();
                Token eToken = new Token(null, "i", fToken.getLineNum(), 0);
                GlobalParm.addParserError(eToken);
            }
        } else if (token.getTokenTypeName().equals("RETURNTK")) {//return
            this.RETURN = token;
            ExpParser expParser = new ExpParser();
            this.returnExp = expParser.ExpParser();
            token = GlobalParm.getAToken();
            if (token.getTokenTypeName().equals("SEMICN")) {
                this.SEMICN = token;
            } else {
                GlobalParm.backAToken();
                Token fToken = GlobalParm.getCurrentToken();
                Token eToken = new Token(null, "i", fToken.getLineNum(), 0);
                GlobalParm.addParserError(eToken);
            }
        } else if (token.getTokenTypeName().equals("PRINTFTK")) {
            this.PRINTF = token;
            token = GlobalParm.getAToken();
            if (token.getTokenTypeName().equals("LPARENT")) {
                this.LPARENT = token;
                token = GlobalParm.getAToken();
                if (token.getTokenTypeName().equals("STRCON")) {
                    this.stringConst = new StringConst(token);
                    token = GlobalParm.getAToken();
                    if (token.getTokenTypeName().equals("COMMA")) {
                        this.opTokens = new ArrayList<Token>();
                        this.printExpList = new ArrayList<Exp>();
                    }
                    while (token.getTokenTypeName().equals("COMMA")) {
                        opTokens.add(token);
                        printExpList.add(new ExpParser().ExpParser());
                        token = GlobalParm.getAToken();
                    }
                    if (token.getTokenTypeName().equals("RPARENT")) {
                        this.RPARENT = token;
                        token = GlobalParm.getAToken();
                        if (token.getTokenTypeName().equals("SEMICN")) {
                            this.SEMICN = token;
                        } else {
                            GlobalParm.backAToken();
                            Token fToken = GlobalParm.getCurrentToken();
                            Token eToken = new Token(null, "i", fToken.getLineNum(), 0);
                            GlobalParm.addParserError(eToken);
                        }
                    } else if (token.getTokenTypeName().equals("SEMICN")) {
                        this.SEMICN = token;
                        GlobalParm.backAToken();//当前指向的是分号，需要向前走一位，得到正确的行号
                        Token fToken = GlobalParm.getCurrentToken();
                        GlobalParm.getAToken();
                        Token eToken = new Token(null, "j", fToken.getLineNum(), 0);
                        GlobalParm.addParserError(eToken);
                    } else {//ij都有
                        GlobalParm.backAToken();
                        Token fToken = GlobalParm.getCurrentToken();
                        Token eToken = new Token(null, "j", fToken.getLineNum(), 0);
                        GlobalParm.addParserError(eToken);
//                        eToken = new Token(null, "i", fToken.getLineNum(), 0);
//                        GlobalParm.addParserError(eToken);
                    }
                }
            }
        } else if (token.getTokenTypeName().equals("IFTK")) {
            this.IFTK = token;
            token = GlobalParm.getAToken();
            if (token.getTokenTypeName().equals("LPARENT")) {
                this.LPARENT = token;
                CondParser condParser = new CondParser();
                this.cond = condParser.CondParser();
                token = GlobalParm.getAToken();
                if (token.getTokenTypeName().equals("RPARENT")) {
                    this.RPARENT = token;
                } else {
                    GlobalParm.backAToken();
                    Token fToken = GlobalParm.getCurrentToken();
                    Token eToken = new Token(null, "j", fToken.getLineNum(), 0);
                    GlobalParm.addParserError(eToken);
                }
                StmtParser stmtParser = new StmtParser();
                this.stmt1 = stmtParser.StmtParser();
                token = GlobalParm.getAToken();
                if (token.getTokenTypeName().equals("ELSETK")) {
                    this.ELSE = token;
                    this.stmt2 = new StmtParser().StmtParser();
                } else GlobalParm.backAToken();
            }
        } else if (token.getTokenTypeName().equals("FORTK")) {
            this.FORTK = token;
            token = GlobalParm.getAToken();
            if (token.getTokenTypeName().equals("LPARENT")) {
                this.LPARENT = token;
                token = GlobalParm.getAToken();
                if (!token.getTokenTypeName().equals("SEMICN")) {
                    GlobalParm.backAToken();
                    ForStmtParser forStmtParser = new ForStmtParser();
                    this.for_stmt1 = forStmtParser.ForStmtParser();
                    this.for_semicn1 = GlobalParm.getAToken();
                } else {
                    this.for_semicn1 = token;
                }

                token = GlobalParm.getAToken();
                if (!token.getTokenTypeName().equals("SEMICN")) {
                    GlobalParm.backAToken();
                    CondParser condParser = new CondParser();
                    this.cond = condParser.CondParser();
                    this.for_semicn2 = GlobalParm.getAToken();
                } else {
                    this.for_semicn2 = token;
                }

                token = GlobalParm.getAToken();
                if (!token.getTokenTypeName().equals("RPARENT")) {
                    GlobalParm.backAToken();
                    ForStmtParser forStmtParser = new ForStmtParser();
                    this.for_stmt2 = forStmtParser.ForStmtParser();
                    this.RPARENT = GlobalParm.getAToken();
                } else {
                    this.RPARENT = token;
                }

                StmtParser stmtParser = new StmtParser();
                this.stmt1 = stmtParser.StmtParser();
            }
        } else if (token.getTokenTypeName().equals("LBRACE")) {//Block
            GlobalParm.backAToken();
            BlockParser blockParser = new BlockParser();
            this.block = blockParser.BlockParser();
        } else if (token.getTokenTypeName().equals("SEMICN")) {//无exp，直接;
            this.SEMICN = token;
        } else if(token.getTokenTypeName().equals("LPARENT")||token.getTokenTypeName().equals("INTCON")||token.getTokenTypeName().equals("CHRCON")){
            //exp情况
            GlobalParm.backAToken();
            ExpParser expParser = new ExpParser();
            this.exp = expParser.ExpParser();
            token = GlobalParm.getAToken();
            if(token.getTokenTypeName().equals("SEMICN")){
                this.SEMICN = token;
            }else{
                GlobalParm.backAToken();
                Token fToken = GlobalParm.getCurrentToken();
                Token eToken = new Token(null, "i", fToken.getLineNum(), 0);
                GlobalParm.addParserError(eToken);
            }
        }
        else {//都是以ident开头，需要区分exp，LVal
            GlobalParm.backAToken();
            token = GlobalParm.getCurrentToken();//为当前的token做一个标记，确保后续能够回退到当前标记
            long flag = 114514;
            String flagTokenType = token.getTokenTypeName();
            token.setNumValue(flag);//特殊标记作为回退位置
            int len = GlobalParm.getParserErrorList().size();//还要处理在回溯过程中，可能有错误，需要把错误也回溯了，避免重复错误

            boolean isAssign = false;
            this.lVal = new LValParser().LValParser();//不论是LVal还是exp，这里一定都是一个LVal，先解析，然后看他后面是不是ASSIGN
            token = GlobalParm.getAToken();
            if(token.getTokenTypeName().equals("ASSIGN")){
                isAssign = true;
                GlobalParm.backAToken();
            }
            //这里之前都是为了弄清楚到底是exp还是lval
            //所以弄清楚后，不论怎么样，都要回退掉这个lval
            while(GlobalParm.getCurrentToken().getNumValue()!=flag||!GlobalParm.getCurrentToken().getTokenTypeName().equals(flagTokenType)){
                GlobalParm.backAToken();
            }//到这完成lval的回退任务
            //还要把lval设置为null
            this.lVal = null;
            //还要回退错误
            while(len<GlobalParm.getParserErrorList().size()){
                GlobalParm.getRidParserError();
            }//完成对错误的回退

            if(isAssign){//LVal部分,这次一定是LVAL了
                LValParser lValParser = new LValParser();
                this.lVal = lValParser.LValParser();
                token = GlobalParm.getAToken();
                if(token.getTokenTypeName().equals("ASSIGN")){
                    this.ASSIGN = token;
                }
                token = GlobalParm.getAToken();
                if(token.getTokenTypeName().equals("GETINTTK")||token.getTokenTypeName().equals("GETCHARTK")){
                    this.get_INT_CHAR = token;
                    token = GlobalParm.getAToken();
                    if(token.getTokenTypeName().equals("LPARENT")){
                        this.LPARENT = token;
                    }
                    token = GlobalParm.getAToken();
                    if(token.getTokenTypeName().equals("RPARENT")){
                        this.RPARENT = token;

                        token = GlobalParm.getAToken();
                        if(token.getTokenTypeName().equals("SEMICN")){
                            this.SEMICN = token;
                        }else{
                            GlobalParm.backAToken();
                            Token fToken = GlobalParm.getCurrentToken();
                            Token eToken = new Token(null, "i", fToken.getLineNum(), 0);
                            GlobalParm.addParserError(eToken);
                        }
                    }else{//缺少右侧括号，j型
                        GlobalParm.backAToken();
                        Token fToken = GlobalParm.getCurrentToken();
                        Token eToken = new Token(null, "j", fToken.getLineNum(), 0);
                        GlobalParm.addParserError(eToken);
                    }
                }else{//rightExp
                    GlobalParm.backAToken();
                    this.rightExp = new ExpParser().ExpParser();

                    token = GlobalParm.getAToken();
                    if(token.getTokenTypeName().equals("SEMICN")){
                        this.SEMICN = token;
                    }else{
                        GlobalParm.backAToken();
                        Token fToken = GlobalParm.getCurrentToken();
                        Token eToken = new Token(null, "i", fToken.getLineNum(), 0);
                        GlobalParm.addParserError(eToken);
                    }
                }

            }else{//exp情况
                this.exp = new ExpParser().ExpParser();

                token = GlobalParm.getAToken();
                if(token.getTokenTypeName().equals("SEMICN")){
                    this.SEMICN = token;
                }else{
                    GlobalParm.backAToken();
                    Token fToken = GlobalParm.getCurrentToken();
                    Token eToken = new Token(null, "i", fToken.getLineNum(), 0);
                    GlobalParm.addParserError(eToken);
                }
            }

//            //判断两者共同的分号
//            token = GlobalParm.getAToken();
//            if(token.getTokenTypeName().equals("SEMICN")){
//                this.SEMICN = token;
//            }else{
//                GlobalParm.backAToken();
//                Token fToken = GlobalParm.getCurrentToken();
//                Token eToken = new Token(null, "i", fToken.getLineNum(), 0);
//                GlobalParm.addParserError(eToken);
//            }

        }
        Stmt stmt = new Stmt(this.BC,
                this.SEMICN,
                this.RETURN,
                this.returnExp,
                this.rightExp,
                this.printExpList,
                this.opTokens,
                this.exp,
                this.get_INT_CHAR,
                this.ASSIGN,
                this.PRINTF,
                this.LPARENT,
                this.RPARENT,
                this.stringConst,
                this.IFTK,
                this.FORTK,
                this.cond,
                this.ELSE,
                this.stmt1,
                this.stmt2,
                this.block,
                this.for_semicn1,
                this.for_semicn2,
                this.for_stmt1,
                this.for_stmt2,
                this.lVal);
        return stmt;
    }
}
