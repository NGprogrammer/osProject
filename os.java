import java.util.ArrayList;
import java.util.LinkedList;

public class os {
	public static ArrayList<pcb> jobtable;
	public static LinkedList<pcb> IOQueue;
	public static memorymanager memory;
	public static boolean swapIn, swapOut, doingIO;

	/**
	 *  Startup()
	 *  This is also the first function called by the SOS class
	 *  Initializing the system variables declared above
	 */
	public static void startup() {
		jobtable = new ArrayList<>(50);
		memory = new memorymanager();
		IOQueue = new LinkedList<>();
		swapIn = false;
		swapOut = false;
		sos.offtrace();
	}

	/**
	 * Crint()
	 * Indicates a new job has arrived on the drum
	 */
	public static void Crint(int []a, int []p) {
		bookKeep(p[5]);
		jobtable.add(new pcb(p));
		runOnCPU(a, p);
	}

	/**
	 * Disk interrupt,
	 * Finishes I/O and notifies that I/O is done
	 */
	public static void Dskint(int []a, int []p) {
		bookKeep(p[5]);
		doingIO = false;
		pcb IOJob = jobtable.get(findIOJob());
		IOJob.DOINGIO = false;
		IOJob.BLOCKED = false;
		IOJob.REQUESTIO = false;
		if (IOJob.KILL)
			terminateJob(findJobTablePos(IOJob.jobnum));
		runIO();
		runOnCPU(a, p);
	}

	/**
	 * Drum Interrupt,
	 * Transfer between drum and memory completed,
	 * a boolean value changed to indicate if there are any swaps happening
	 * to swap in and out another job from the drum to memory
	 */
	public static void Drmint(int []a, int []p) {
		bookKeep(p[5]);
		if (swapIn) {
			swapIn = false;
			for (int i = 0; i < jobtable.size(); i++) {
				if (jobtable.get(i).SWAPPING) {
					jobtable.get(i).SWAPPING = false;
					jobtable.get(i).INMEMORY = true;
					break;
				}
			}
		}
		if (swapOut) {
			swapOut = false;
			for (int i = 0; i < jobtable.size(); i++) {
				if (jobtable.get(i).SWAPPING) {
					jobtable.get(i).SWAPPING = false;
					jobtable.get(i).INMEMORY = false;
					break;
				}
			}
		}
		runOnCPU(a, p);
	}

	/**
	 * Times Run Out,
	 * Timer interrupt,
	 * Occurs when a job is to be terminated
	 */
	public static void Tro(int[] a, int []p) {
		int interruptedJobPos = bookKeep(p[5]);
		pcb interruptedJob = jobtable.get(interruptedJobPos);
		if (interruptedJob.timeLeft == 0) {
			if (interruptedJob.DOINGIO || interruptedJob.REQUESTIO)
				interruptedJob.KILL = true;
			else
				terminateJob(interruptedJobPos);
		}
		runOnCPU(a, p);
	}

	/**
	 * Occurs when sos issues a command to be done
	 * 5 means the job is requesting to be killed (can't be killed if doing I/O)
	 * 6 means the job is requesting to do I/O
	 * 7 means the job is requesting to be blocked
	 */


	public static void Svc(int[] a, int []p) {
		int posInterruptedJob = bookKeep(p[5]);
		pcb interruptedJob = jobtable.get(posInterruptedJob);
		bookKeep(p[5]);
		int status = a[0];
		switch (status) {
			case 5 :
				if (interruptedJob.REQUESTIO)
					interruptedJob.KILL = true;
				else
					terminateJob(posInterruptedJob);
				break;
			case 6 :
				interruptedJob.REQUESTIO = true;
				IOQueue.add(interruptedJob);
				if (!IOQueue.isEmpty())
					runIO();
				break;
			case 7 :
				if (interruptedJob.REQUESTIO)
					interruptedJob.BLOCKED = true;
				break;
		}
		runOnCPU(a, p);
	}

	/**
	 * bookKeep()
	 * This function calculates how much time an interrupted running job spend on CPU
	 * The time is subtracted from the maxCPUTime and also returns position of interrrupted running job
	 */
	public static int bookKeep(int currTime) {
		int runningJobPos = findRunningJob();
		if (runningJobPos != -1 && jobtable.get(runningJobPos).RUNNING) {
			pcb runningJob = jobtable.get(runningJobPos);
			runningJob.RUNNING = false;
			runningJob.timeInCPU = currTime - runningJob.timeEnterCPU;
			runningJob.timeLeft = runningJob.timeLeft - runningJob.timeInCPU;
			return runningJobPos; // pos of interrupted running job
		}
		return runningJobPos; // -1 if no running job
	}

	/**
	 *  runOnCPU()
	 *  Assigns the info from CPUScheduler so that the sos can run the job
	 *  the job is selected in the readyQueue that will be next to run on the CPU,
	 *  if there is  no current job to run on the CPU it will IDLE
	 */
	public static void runOnCPU(int[] a, int[] p) {
		Swapper();
		int jobTablePos = CPUScheduler();
		if (jobTablePos != -1 && !jobtable.get(jobTablePos).BLOCKED) {
			pcb jobToRun = jobtable.get(jobTablePos);
			a[0] = 2;
			p[2] = jobToRun.posInMemory;
			p[3] = jobToRun.jobsize;
			p[4] = jobToRun.timeLeft;
			jobToRun.RUNNING = true;
			jobToRun.timeEnterCPU = p[5];
		} else {
			a[0] = 1;
		}
	}

	/**
	 *  runIO()
	 *  function to allow the job to do IO
	 */
	public static void runIO() {
		if (!doingIO) {
			if (!IOQueue.isEmpty()) {
				for (pcb job : IOQueue) {
					if (job.INMEMORY) {
						sos.siodisk(job.jobnum);
						IOQueue.remove(job);
						jobtable.get(findJobTablePos(job.jobnum)).DOINGIO = true;
						jobtable.get(findJobTablePos(job.jobnum)).REQUESTIO = true;
						doingIO = true;
						break;
					}
				}
			}
		}
	}

	/**
	 * findRunningJob()
	 * If a job is currently running, return the address of that job
	 */
	public static int findRunningJob() {
		int jobtablePos = -1;
		for (int i = 0; i < jobtable.size(); i++) {
			if (jobtable.get(i).RUNNING)
				jobtablePos = i;
		}
		return jobtablePos;
	}

	/**
	 * findIOJob()
	 * Locate job that is doing io
	 */
	public static int findIOJob() {
		int jobtablePos = -1;
		for (int i = 0; i < jobtable.size(); i++) {
			if (jobtable.get(i).DOINGIO)
				jobtablePos = i;
		}
		return jobtablePos;
	}

	/**
	 * Locates the position of the job in the job table
	 */
	public static int findJobTablePos(int jobNum) {
		for (int i = 0; i < jobtable.size(); i++) {
			if (jobtable.get(i).jobnum == jobNum)
				return i;
		}
		return -1;
	}

	/**
	 * CPUScheduler()
	 * Will use Shortest Remaining Time for our CPUScheduling to
	 * determined which job to run on the CPU that is ready to run
 	 */
	public static int CPUScheduler() {
		int shortestRemainingTime = 999999;
		int shortestJobPos = -1;
		if (jobtable.isEmpty()) {
			return -1;
		} else {
			for (int i = 0; i < jobtable.size(); i++) {
				pcb job = jobtable.get(i);
				if (job.INMEMORY && !job.BLOCKED && !job.KILL) {
					if (job.timeLeft < shortestRemainingTime) {
						shortestRemainingTime = job.timeLeft;
						shortestJobPos = i;
					}
				}
			}
			return shortestJobPos;
		}
	}

	/**
	 * Swapper()
	 * Determines if job is not in memory to be placed in memory
	 * Also swap jobs out which are idle for another job
	 */
	public static void Swapper() {
		int startingAddress = -1;
		if (!swapIn && !swapOut) {
			for (int i = 0; i < jobtable.size(); i++) {
				pcb job = jobtable.get(i);
				if (!job.INMEMORY) {
					startingAddress = memory.allocateMemory(job.jobsize);
					if (startingAddress != -1) {
						sos.siodrum(job.jobnum, job.jobsize, startingAddress, 0);
						job.posInMemory = startingAddress;
						swapIn = true;
						job.SWAPPING = true;
						break;
					}
				}
			}
			// Occurs if the jobtable is full
			if (startingAddress == -1) {
				for (int i = 0; i < IOQueue.size(); i++) {
					pcb job = IOQueue.get(i);
					// Swaps out the job in the jobtable that isn't doing I/O and not blocked
					if (job.INMEMORY && !job.DOINGIO && !job.BLOCKED) {
						sos.siodrum(job.jobnum, job.jobsize, job.posInMemory, 1);
						memory.removeFromMemory(job.posInMemory, job.jobsize);
						swapOut = true;
						job.SWAPPING = true;
						job.INMEMORY = false;
						break;
					}
				}
			}
		}
	}

	/**
	 * TerminateJob()
	 * Occurs when a job is finished or request to be terminated
	 */
	public static void terminateJob(int jobTablePos) {
		pcb Job = jobtable.get(jobTablePos);
		memory.removeFromMemory(Job.posInMemory, Job.jobsize);
		jobtable.remove(jobTablePos);
	}
}
