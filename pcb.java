/**
 * This is the PCB class, progress control block.
 */
public class pcb {

	public int jobnum;
	public int priority;
	public int jobsize;
	public int maxCpuTime;
	public int timeOnCreate;
	public int timeLeft;
	public int timeInCPU;
	public int timeEnterCPU;
	public int posInMemory;
	public boolean RUNNING;
	public boolean INMEMORY;
	public boolean DOINGIO;
	public boolean SWAPPING;
	public boolean BLOCKED;
	public boolean KILL;
	public boolean REQUESTIO;
	/**
	 * This function initialize the variables from the ??? i forgot
	 * @param arr
	 */
	public pcb(int [] arr) {
		jobnum = arr[1];
		priority = arr[2];
		jobsize = arr[3];
		maxCpuTime = timeLeft = arr[4];
		timeOnCreate = arr[5];
		timeInCPU = -1;
		timeEnterCPU = -1;
		posInMemory = -1;
		RUNNING = false;
		INMEMORY = false;
		DOINGIO = false;
		SWAPPING = false;
		KILL = false;
		REQUESTIO = false;
	}
}
