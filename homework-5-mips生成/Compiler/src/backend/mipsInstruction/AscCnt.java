package backend.mipsInstruction;

public class AscCnt {
    private static int count = 0;
    public static int getcnt(){
        int save = count;
        count++;
        return save;
    }
    public static String buildAsciizName(int cnt){
        String name = "str_";
        return name+cnt;
    }
}
