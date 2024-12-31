package backend.mipsInstruction.compare;

public class mipsInsBeq extends mipsInsCompare{
    public mipsInsBeq(int first, int second, String label){
        super(first,second,label,"beq");
    }
}
