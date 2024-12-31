package backend.mipsInstruction;

public class Asciiz extends mipsInstruction{
    public String name;
    public String value;
    public int count;//第几个字符串常量
    public Asciiz(String value, int count){
        super(".asciiz");
        this.value = value;
        this.name = "str_"+count;
        this.count = count;
    }

    public String mipsOutput(){
        StringBuilder sb = new StringBuilder();
        this.value = this.value.replaceAll("\n","\\\\n");
        sb.append(this.name+": .asciiz \""+this.value+"\"\n");
        return sb.toString();
    }

}
