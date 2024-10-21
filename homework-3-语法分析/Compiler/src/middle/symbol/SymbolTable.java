package middle.symbol;


import java.util.ArrayList;
import java.util.HashMap;

public class SymbolTable {
    public int id;
    public int fatherId;
    public HashMap<String, Symbol> directory;
    public ArrayList<SymbolTable> subTables;
    public SymbolTable(int id, int fatherId){
        this.id = id;
        this.fatherId = fatherId;
        this.directory = new HashMap<>();
        this.subTables = new ArrayList<>();
    }
}
