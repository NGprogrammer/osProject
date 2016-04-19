
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class memorymanager {
    public static final int MAX_SIZE = 100;
    private ArrayList<Integer> memory;
    private TreeMap<Integer, Integer> fsTable;
    /*Using FirstFit so key is address of FS and value is size of FS*/
    
    public memorymanager(){
        memory = new ArrayList<>(MAX_SIZE);
        fsTable = new TreeMap<>();
        for(int i = 0; i < MAX_SIZE; i++){
            memory.add(0);
        }
    }
    
    private void fillFreeSpaceTable(){
        fsTable.clear(); //makes sure last table's contents are gone
        int FSbegin = -1;
        int FSend = -1;
        for(int i = 0; i < MAX_SIZE-1;){
            if(memory.get(i) == 0){
                FSbegin = i;
                while(memory.get(i) != 1 && i < MAX_SIZE-1){
                    i++;
                }
                FSend = i-1; //i-1 because i's value is 1 past the end of the free space
                if(FSbegin != -1 && FSend != -1)
                    fsTable.put(FSbegin, (FSend-FSbegin)+1);
            }
            
        }
    }
    
    public int findFreeSpace(int jobSize){
        for(Map.Entry<Integer, Integer> entry : fsTable.entrySet()){
            if(entry.getValue() >= jobSize)
                return entry.getKey();
        }
        return -1;
    }
    
    public int allocateMemory(int jobSize){
        fillFreeSpaceTable();
        int freeSpacePos = findFreeSpace(jobSize);
        if(freeSpacePos != -1){
            for(int i = freeSpacePos; i < freeSpacePos+jobSize; i++){
                memory.set(i, 1);
            }
//            for(int k = 0; k < memory.size()-1; k++){
//                System.out.print(memory.get(k) + " " );
//            }
            return freeSpacePos;
        }
        return -1;
    }
    
    public void removeFromMemory(int addressInMemory, int jobSize){
        for(int i = addressInMemory; i < addressInMemory+jobSize; i++){
            memory.set(i, 0);
        }
    }
}
