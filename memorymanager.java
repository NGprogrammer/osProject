/**
 * The memory manager class is a representation of Free Space Tables
 */

import java.util.Collections;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class memorymanager {
	public static final int MAX_SIZE = 100;
	private ArrayList<Integer> memory;
	private TreeMap<Integer, Integer> fsTable;

	/**
	 * memorymanager()
	 * Initialize memory with 0's representing free space
	 */
	public memorymanager() {
		/* Initialize memory with 0's representing free space */
		memory = new ArrayList<Integer>(Collections.nCopies(MAX_SIZE, 0));
		fsTable = new TreeMap<>();
	}

	/**
	 * fillFreeSpaceTable()
	 * Locates where in memory there is free space and puts the free space size
	 * as the key and the address of the free space as the value.
	 */
	private void fillFreeSpaceTable() {
		fsTable.clear(); // Clears the contents of the FST
		int FSbegin = -1;
		int FSend = -1;
		/* Loop to find where the free space begins and the size of it */
		for (int i = 0; i < MAX_SIZE; i++) {
			if (memory.get(i) == 0) {
				FSbegin = i;
				FSend = i;
				for (int j = i; j < MAX_SIZE; j++) {
					if (memory.get(j) == 0)
						FSend = j;
					else if (memory.get(j) == 1) { // End of free space
						break; // Break out because it found the end of free space
					}
				}
				i = FSend; // Set i to the end of free space so it can look for the next one
			}
			/* Place the free space onto the table */
			if (FSbegin != -1 && FSend != -1) {
				fsTable.put(FSend-FSbegin+1, FSbegin);
 			}
		}
	}

	/**
	 * findFreeSpace()
	 * Determines if there is enough space for the current job size
	 * If so, return the starting address else return -1 (not enough space)
	 */
	public int findFreeSpace(int jobSize) {
		for (Map.Entry<Integer, Integer> entry : fsTable.entrySet()) {
			if (entry.getKey() >= jobSize)
				return entry.getValue();
		}
		return -1;
	}

	/**
	 * Process of allocating memory for the current jobSize
	 */
	public int allocateMemory(int jobSize) {
		// Before allocating memory we call this function determine where there is free space
		fillFreeSpaceTable();
		int freeSpacePos = findFreeSpace(jobSize);
		if (freeSpacePos != -1) {
			for (int i = freeSpacePos; i < freeSpacePos+jobSize; i++) {
				memory.set(i, 1);
			}
			return freeSpacePos;
		}
		return -1;
	}

	/**
	 * If a job is terminated then this function is called to reset the memory
	 * so it can be used by other jobs
	 */
	public void removeFromMemory(int addressInMemory, int jobSize) {
		for (int i = addressInMemory; i < addressInMemory+jobSize; i++) {
			memory.set(i, 0);
		}
	}
}
