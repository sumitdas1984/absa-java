/**
 * 
 */
package com.sd.absa.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Koustuv Saha
 * 15-Aug-2014 12:27:44 pm
 * XpressoV2.0.1  ThreadUtils
 */
public class ThreadUtils {

	public static void shutdownAndAwaitTermination(ExecutorService pool) {
		pool.shutdown(); // Disable new tasks from being submitted
		try {
			/*Wait a while for existing tasks to terminate*/
			if (!pool.awaitTermination(30, TimeUnit.DAYS)) {
				pool.shutdownNow(); // Cancel currently executing tasks
				/*Wait a while for tasks to respond to being cancelled*/
				if (!pool.awaitTermination(30, TimeUnit.DAYS))
					System.err.println("Pool did not terminate");
			}
		} catch (InterruptedException ie) {
			/*(Re-)Cancel if current thread also interrupted*/
			pool.shutdownNow();
			/*Preserve interrupt status*/
			Thread.currentThread().interrupt();
		}
	}

}
