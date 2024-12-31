package middle.error;

import frontend.lexer.Token;

public enum ErrorType {
    A_error("a"),
    B_error("b"),//12, 60%，可能
    C_error("c"),//12,%40.可能
    D_error("d"),//13, %40,可能
    E_error("e"),//14,15和G互补
    F_error("f"),//13, 60%
    G_error("g"),//14,15, 20%, 60%?
    H_error("h"),//16, 100%
    I_error("i"),//18,19,22,26
    J_error("j"),//17,20,21,22
    K_error("k"),//23 100%
    L_error("l"),//24.100%
    M_error("m");//25,100%

    private String errorCh;
    ErrorType(String type){
        this.errorCh = type;
    }
    public String getErrorCh(){
        return this.errorCh;
    }
}
