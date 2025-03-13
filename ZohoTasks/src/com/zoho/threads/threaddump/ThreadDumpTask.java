package com.zoho.threads.threaddump;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

public class ThreadDumpTask implements Runnable {
    int numDumps ;
    int interval ;
	public ThreadDumpTask(){
		
	}
	public ThreadDumpTask(int numDumps , int interval){
		this.numDumps = numDumps;
		this.interval = interval;
	
		
	}
    @Override
    public void run() {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

        for (int i = 0; i < numDumps; i++) {
            System.out.println("Thread dump " + (i + 1) + ":");
            long[] threadIds = threadMXBean.getAllThreadIds();
            ThreadInfo[] threadInfos = threadMXBean.getThreadInfo(threadIds, Integer.MAX_VALUE);

            for (ThreadInfo threadInfo : threadInfos) {
                System.out.println(threadInfo.toString());
            }
            System.out.println("-------------------------------------");
            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}