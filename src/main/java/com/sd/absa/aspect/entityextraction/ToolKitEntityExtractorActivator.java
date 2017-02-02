/**
 *
 */
package com.sd.absa.aspect.entityextraction;

import ch.qos.logback.classic.Logger;
import com.sd.absa.engine.config.ToolKitConfig;
import com.sd.absa.engine.core.ToolKitEngine;
import com.sd.absa.engine.core.ToolKitLogger;
import com.sd.absa.utils.FileIO;
import com.sd.absa.utils.ThreadUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Sumit Das
 * Jul 16, 2014 11:43:24 AM
 */
public class ToolKitEntityExtractorActivator {

	/**
	 * @param args
	 */
	protected static Logger logger;

	public static void init() throws Exception {
		logger = ToolKitLogger.getXpLogger();
	}

	public static void main(String[] args) throws Exception {
		long startTime;
		long endTime;
		long executionTime;
		startTime = System.currentTimeMillis();

		ToolKitEngine.init();
		init();

		endTime = System.currentTimeMillis();
		executionTime = endTime - startTime;


		startTime = System.currentTimeMillis();
		ToolKitEntityCommons tkEntity = new ToolKitEntityCommons(null);
		tkEntity.processedReviewCount++;
		String inputFile = ToolKitConfig.INPUT_REVIEW_FILENAME;
		ToolKitConfig.Languages language = ToolKitConfig.REVIEW_LANGUAGE;
		List<String> reviewList = new ArrayList<String>();
		FileIO.read_file(inputFile, reviewList);

		ExecutorService executor = Executors.newFixedThreadPool(16);

		int i = 0;
		for (String oneLine : reviewList) {
			System.out.println("\n$$review count: " + i);
			i++;
			//			String[] lineParts = oneLine.split("\\|");
			String[] lineParts = oneLine.split(ToolKitConfig.REVIEW_DELIMITER);
			if (lineParts.length < ToolKitConfig.REVIEW_FIELD + 1) {
				continue;
			}
			String review = lineParts[ToolKitConfig.REVIEW_FIELD];
			System.out.println("@@review: " + review);
			int maxReviewLength = ToolKitConfig.MAX_REVIEW_LENGTH;
			if (maxReviewLength != 0 && review.length() > maxReviewLength) {
				continue;
			}

			Runnable worker = new ToolKitEntityExtractorThread(language, review, tkEntity);
			executor.execute(worker);
		}
		ThreadUtils.shutdownAndAwaitTermination(executor);

		System.out.println("Finished all threads");

		tkEntity.writeMap();
		endTime = System.currentTimeMillis();
		executionTime = endTime - startTime;
		logger.info("Total time for entity extraction: " + executionTime);
		return;
	}
}
