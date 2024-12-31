package frontend.parser.Stmt;

import frontend.lexer.Token;
import frontend.parser.Exp.Cond;
import frontend.parser.Exp.Exp;
import frontend.parser.Exp.LVal;
import frontend.parser.FuncDef.Block;
import frontend.parser.Terminal.StringConst;
import frontend.parser.parserOutput;

import java.util.ArrayList;

public class Stmt implements parserOutput {
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

    public Stmt(Token BC, Token SEMICN, Token RETURN, Exp returnExp, Exp rightExp, ArrayList<Exp> printExpList, ArrayList<Token> opTokens, Exp exp, Token get_INT_CHAR, Token ASSIGN, Token PRINTF
            , Token LPARENT, Token RPARENT, StringConst stringConst, Token IFTK, Token FORTK, Cond cond, Token ELSE, Stmt stmt1, Stmt stmt2, Block block, Token for_semicn1, Token for_semicn2,
                ForStmt for_stmt1, ForStmt for_stmt2, LVal lVal){
        this.BC = BC;
        this.SEMICN = SEMICN;
        this.RETURN = RETURN;
        this.returnExp = returnExp;
        this.rightExp = rightExp;
        this.printExpList = printExpList;
        this.opTokens = opTokens;
        this.exp = exp;
        this.get_INT_CHAR = get_INT_CHAR;
        this.ASSIGN =ASSIGN;
        this.PRINTF = PRINTF;
        this.LPARENT = LPARENT;
        this.RPARENT = RPARENT;
        this.stringConst = stringConst;
        this.IFTK = IFTK;
        this.FORTK = FORTK;
        this.cond = cond;
        this.ELSE = ELSE;
        this.stmt1 = stmt1;
        this.stmt2 = stmt2;
        this.block = block;
        this.for_semicn1 = for_semicn1;
        this.for_semicn2 = for_semicn2;
        this.for_stmt1 = for_stmt1;
        this.for_stmt2 = for_stmt2;

        this.lVal = lVal;

    }
    @Override
    public String output() {
        StringBuilder stringBuilder = new StringBuilder();
        if(IFTK!=null){
            stringBuilder.append(IFTK.output());
            stringBuilder.append(LPARENT.output());
            stringBuilder.append(cond.output());
            stringBuilder.append(RPARENT.output());
            stringBuilder.append(stmt1.output());
            if(ELSE!=null){
                stringBuilder.append(ELSE.output());
                stringBuilder.append(stmt2.output());
            }
        }else if(FORTK!=null){
            stringBuilder.append(FORTK.output());
            stringBuilder.append(LPARENT.output());
            if(for_stmt1!=null)stringBuilder.append(for_stmt1.output());
            if(for_semicn1!=null)stringBuilder.append(for_semicn1.output());
            if(cond!=null)stringBuilder.append(cond.output());
            if(for_semicn2!=null)stringBuilder.append(for_semicn2.output());
            if(for_stmt2!=null)stringBuilder.append(for_stmt2.output());
            stringBuilder.append(RPARENT.output());
            stringBuilder.append(stmt1.output());
        }else if(BC!=null){
            stringBuilder.append(BC.output());
            stringBuilder.append(SEMICN.output());
        }else if(RETURN!=null){
            stringBuilder.append(RETURN.output());
            if(returnExp!=null)stringBuilder.append(returnExp.output());
            if(SEMICN!=null)stringBuilder.append(SEMICN.output());
        }else if(PRINTF!=null){
            stringBuilder.append(PRINTF.output());
            stringBuilder.append(LPARENT.output());
            stringBuilder.append(stringConst.output());
            if(opTokens!=null){
                int len = opTokens.size();
                for(int i=0;i<len;i++){
                    stringBuilder.append(opTokens.get(i).output());
                    stringBuilder.append(printExpList.get(i).output());
                }
            }
            stringBuilder.append(RPARENT.output());
            stringBuilder.append(SEMICN.output());
        }else if(block!=null){
            stringBuilder.append(block.output());
        }else if(ASSIGN!=null){//lval情况
            stringBuilder.append(lVal.output());
            stringBuilder.append(ASSIGN.output());
            if(rightExp!=null)stringBuilder.append(rightExp.output());
            else if(get_INT_CHAR!=null){
                stringBuilder.append(get_INT_CHAR.output());
                stringBuilder.append(LPARENT.output());
                stringBuilder.append(RPARENT.output());
            }
            if(SEMICN!=null)stringBuilder.append(SEMICN.output());
        }
        else if(exp!=null){
            stringBuilder.append(exp.output());
            if(SEMICN!=null)stringBuilder.append(SEMICN.output());
        } else if(SEMICN!=null){
            stringBuilder.append(SEMICN.output());
        }

        stringBuilder.append("<Stmt>\n");
        return stringBuilder.toString();
    }

    public Boolean checkReturn(){
        if(this.RETURN!=null){
            return true;
        }
        return false;
    }

    public boolean isIfCase(){
        if(this.IFTK!=null){
            return true;
        }return false;
    }

    public boolean isForCase(){
        if(this.FORTK!=null){
            return true;
        }return false;
    }

    public Block getBlock(){
        return this.block;
    }
    public LVal getlVal(){
        return this.lVal;
    }
    public Exp getRightExp(){
        return this.rightExp;
    }
    public Exp getReturnExp(){
        return this.returnExp;
    }
    public Exp getExp(){
        return this.exp;
    }
    public StringConst getStringConst(){
        return this.stringConst;
    }
    public ArrayList<Exp> getPrintExpList(){
        ArrayList<Exp> list = new ArrayList<>();
        if(this.printExpList!=null){
            for(Exp exp:this.printExpList){
                list.add(exp);
            }
        }
        return list;
    }

    public int parseStmtType(){
        if(IFTK!=null){
           return 1;
        }else if(FORTK!=null){
            return 2;
        }else if(BC!=null){//b7 c8
            if(BC.getTokenTypeName().equals("BREAKTK")){
                return 7;
            }else if(BC.getTokenTypeName().equals("CONTINUETK")){
                return 8;
            }
        }else if(RETURN!=null){
            return 9;
        }else if(PRINTF!=null){
            return 13;
        }else if(block!=null){
            return 3;
        }else if(ASSIGN!=null){//lval情况
            if(rightExp!=null){//assign
                return 6;
            }
            else if(get_INT_CHAR!=null){//getint/getchar
                if(get_INT_CHAR.getTokenTypeName().equals("GETINTTK")){
                    return 10;
                }else if(get_INT_CHAR.getTokenTypeName().equals("GETCHARTK")){
                    return 11;
                }
            }
        }
        else if(exp!=null){//exp情况
            return 12;
        } else if(SEMICN!=null){//纯分号情况
           return 14;
        }
        return 0;
    }
    public Cond getCond(){
        return this.cond;
    }
    public Stmt getIfStmt1(){
        return this.stmt1;
    }
    public Token getElse(){
        return this.ELSE;
    }
    public Stmt getIfStmt2(){
        return this.stmt2;
    }
    public ForStmt getFor_stmt1(){
        return this.for_stmt1;
    }
    public ForStmt getFor_stmt2(){
        return this.for_stmt2;
    }
}
