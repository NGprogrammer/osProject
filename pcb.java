
public class pcb {
	public int jobnum;
	public int priority; 
	public int jobsize;
	public int maxCpuTime;
	public int currentTime;
	
	public pcb(int [] arr){
		jobnum = arr[1];
		priority = arr[2];
		jobsize = arr[3];
		maxCpuTime = arr[4];
		currentTime = arr[5];
	}

}
