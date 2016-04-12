import java.util.ArrayList;

public class os 
{ 
	public static ArrayList<pcb> jobtable;
	
	public static void siodisk(int jobnum)
	{
		
	}
	public static void siodrum(int jobnum, int jobsize, int coreaddress, int direction)
	{
		
	}
	public static void ontrace()
	{
		
	}
	public static void offtrace()
	{
		
	}
	public static void startup()
	{
	     jobtable = new ArrayList<pcb>(999999);
	}
	/**
	 * 
	 * @param a
	 * @param p
	 */
	public static void Crint(int []a, int []p)
	{
		 pcb newjob = new pcb(p);
		 jobtable.add(newjob);
		 
	}
	public static void Dskint(int []a, int []p){
		
	}
	public static void Drmint(int []a, int []p){
		
	}
	public static void Tro(int[] a, int []p){
		
	}
	public static void Svc(int[] a, int []p){
			
	}
}

