/*	The memory manager class is a representation of Free Space Tables */

import java.util.Collections;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class memorymanager {
	public static final int MAX_SIZE = 100;
	private ArrayList<Integer> memory;
	private TreeMap<Integer, Integer> fsTable;
	/* Using FirstFit so key is address of FS and value is size of FS */

	public memorymanager() {
		/* Initialize memory with 0's representing free space */
		memory = new ArrayList<Integer>(Collections.nCopies(MAX_SIZE, 0));
		fsTable = new TreeMap<>();
	}

	/* Locates where in memory there is free space and puts the free space size
	as the key and the address of the free space as the value.
	 */
	private void fillFreeSpaceTable() {
		System.out.println("in fst");
		fsTable.clear(); // Clears the contents of the FST
		int FSbegin = -1;
		int FSend = -1;
		for (int i = 0; i < MAX_SIZE; i++) {
			if (memory.get(i) == 0) {
				FSbegin = i;
				FSend = i;
				for (int j = i; j < MAX_SIZE; j++) {
					if (memory.get(j) == 0)
						FSend = j;
					else if(memory.get(j) == 1) { //End of free space
						break; //Break out because it found the end of free space
					}
				}
				i = FSend; //To move i to the end of free space so it can look for the next one
			}
			if (FSbegin != -1 && FSend != -1) {
				fsTable.put(FSend-FSbegin+1, FSbegin); //The second value is not the end of free space but the size of it
				//break; Got rid of this because if there were more free spaces then it
				//would not find them if you break after you find the first one
				//example 0000110000 if you break after finding first 4 then you won't get to the last 4
 			}
		}
		// Prints out the contents of FST
		for (Map.Entry<Integer, Integer> entry : fsTable.entrySet()) {
			System.out.println("Free space at starting: " + entry.getKey() + " free space left: " + entry.getValue());
		}
	}

	/* Determines if there is enough space for the current job size
	 * If so, return the starting address else return -1 (not enough space)
	 */
	public int findFreeSpace(int jobSize) {
		for (Map.Entry<Integer, Integer> entry : fsTable.entrySet()) {
			if (entry.getKey() >= jobSize)
				return entry.getValue();
		}
		return -1;
	}

	/* Process of allocating memory for the current jobSize */
	public int allocateMemory(int jobSize) {
		System.out.println("I'm in allocateMemory");
		fillFreeSpaceTable();
		int freeSpacePos = findFreeSpace(jobSize);
			System.out.println("****Error in allocateMemory****");
			System.out.println("freeSpacePos = " + freeSpacePos + " jobSize = " + jobSize);
			System.out.println("****Not enough room for this job****");
		if (freeSpacePos != -1) {
			for (int i = freeSpacePos; i < freeSpacePos+jobSize; i++) {
				memory.set(i, 1);
			}
			// for(int k = 0; k < memory.size(); k++){
			//     System.out.print(memory.get(k) + " " );
			// }
			return freeSpacePos;
		}
		return -1;
	}

	/* If a job is terminated then this function is called to reset the memory
	 * so it can be used by other jobs
	 */
	public void removeFromMemory(int addressInMemory, int jobSize) {
		for (int i = addressInMemory; i < addressInMemory+jobSize; i++) {
			memory.set(i, 0);
		}
	}
}
