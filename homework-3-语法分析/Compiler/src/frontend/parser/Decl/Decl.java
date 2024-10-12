package frontend.parser.Decl;

import frontend.parser.parserOutput;

public class Decl implements parserOutput {
    private ConstDecl constDecl;
    private VarDecl varDecl;
    public Decl(ConstDecl constDecl, VarDecl varDecl){
        this.constDecl = constDecl;
        this.varDecl = varDecl;
    }
    @Override
    public String output() {
        StringBuilder stringBuilder = new StringBuilder();
        if(constDecl!=null){
            stringBuilder.append(constDecl.output());
        }else if(varDecl!=null){
            stringBuilder.append(varDecl.output());
        }
        return stringBuilder.toString();
    }
}
