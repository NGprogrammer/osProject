
import java.util.ArrayList;
import java.util.TreeMap;

public class os 
{ 
	public static ArrayList<pcb> jobtable;
	public static memorymanager memory;
	
//	public static void siodisk(int jobnum)
//	{
//		
//	}
	public static void siodrum(int jobnum, int jobsize, int coreaddress, int direction)
	{
		
	}
	public static void startup()
	{
	     jobtable = new ArrayList<>(999999);
	     memory = new memorymanager();
	}
	/**
	 * 
	 * @param a
	 * @param p
	 */
	public static void Crint(int []a, int []p)
	{
            bookKeep(p[5]);
            jobtable.add(new pcb(p));
            CPUScheduler(a, p);
	}
	public static void Dskint(int []a, int []p){
		
	}
	public static void Drmint(int []a, int []p){
		
	}
	public static void Tro(int[] a, int []p){
		
	}
	public static void Svc(int[] a, int []p){
			
	}
	
    public static int bookKeep(int currTime){
            int runningJobPos = findRunningJob();
            if(runningJobPos != -1 && jobtable.get(runningJobPos).RUNNING)
            {
                pcb runningJob = jobtable.get(runningJobPos);
                runningJob.RUNNING = false;
                runningJob.timeInCPU = currTime - runningJob.timeEnterCPU;
                runningJob.timeLeft = runningJob.timeLeft - runningJob.timeInCPU;
//                runningJob.maxCpcuTime-=runningJob.timeInCPU;
                return runningJobPos; 
            }
            return runningJobPos; //-1 if no running job
        }
    
        public static int findRunningJob(){
            int jobtablePos = -1;
            for(int i = 0; i < jobtable.size(); i++){
                if(jobtable.get(i).RUNNING)
                    jobtablePos = i;
            }
            return jobtablePos;
        }
        
        public static void CPUScheduler(int[] a, int[] p){
            if(jobtable.isEmpty())
                a[0] = 1;
            else{
                a[0] = 2;
            }
        }
        
        public static void Swapper(pcb Job, int direction){
        	for(int i = 0; i < jobtable.size(); i++){
        		if(!jobtable.get(i).INMEMORY){
        			siodrum()
        		}
//        	if(direction == 0 && !Job.INMEMORY){
//        		siodrum(Job.jobnum, Job.jobsize, startingPos, direction);
//        	} else if(direction == 1 && Job.INMEMORY){
//        		siodrum(Job.jobnum, Job.jobsize, startingPos, direction);
        	}
        }
}

