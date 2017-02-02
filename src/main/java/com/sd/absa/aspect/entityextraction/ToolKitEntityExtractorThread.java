package com.sd.absa.aspect.entityextraction;

import com.sd.absa.engine.config.ToolKitConfig;
import com.sd.absa.textanalytics.CoreNLPController;

import java.util.Map;
import java.util.Set;

/**
 * @author Sumit Das
 * Jul 16, 2014 11:45:10 AM 
 */
public class ToolKitEntityExtractorThread implements Runnable {

	private String review;
	private ToolKitConfig.Languages language;

	private ToolKitEntityCommons xpEntity;

	public ToolKitEntityExtractorThread(ToolKitConfig.Languages language, String review, ToolKitEntityCommons xpEntity) {
		this.review = review;
		this.language = language;
		this.xpEntity = xpEntity;
	}

	public void run() {
		try {
			Set<String> reviewEntitySet = null;
			int version = 1;
//			if (version == 1) {
//				EntityExtractionByPruning ep = new EntityExtractionByPruning();
//				reviewEntitySet = ep.extractReviewEntities(language, review);
//			} else {
//				XpExpression xpExp = new XpExpression(language, Annotations.SENTI, false, "0", "", "", review, true);
//				reviewEntitySet = xpExp.processPipelineForEntities(false);
//			}

			ToolKitEntityExtractor tee = new ToolKitEntityExtractor();
			reviewEntitySet = tee.extractReviewEntities(review);

			Map<String, Integer> allEntityMap = xpEntity.getAllEntityMap();
			for (String entity : reviewEntitySet) {
				entity = entity.toLowerCase();
//				if (ProcessString.isStopWord(language, entity) || entity.matches("\\W+")) {
//					continue;
//				}
//				String entityLemma = CoreNLPController.lemmatizeToString(language, entity);

				String entityLemma = entity;

				Integer count = allEntityMap.get(entityLemma);
				if (count == null) {
					count = 0;
				}
				count += 1;
				allEntityMap.put(entityLemma, count);
			}
			xpEntity.reviewBatchSize++;
			xpEntity.processedReviewCount++;
			if (xpEntity.reviewBatchSize % 100 == 0) {
				xpEntity.writeMap();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
