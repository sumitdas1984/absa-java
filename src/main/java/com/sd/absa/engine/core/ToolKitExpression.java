package com.sd.absa.engine.core;

import ch.qos.logback.classic.Logger;
import com.sd.absa.sdgraphtraversal.StanfordDependencyGraph;
import com.sd.absa.textanalytics.CoreNLPController;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.util.CoreMap;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ToolKitExpression extends ToolKitText {

	private static Logger logger;
	private static List<String> stopWordList;

	public ToolKitExpression(String reviewStr) {
		super(reviewStr);
	}

	public Map<String, String> getSentiment() {
		Map<String, String> review_entitySentimentMap = this.processPipeline();

		Map<String, String> review_entitySentimentMap_refined = new LinkedHashMap<String, String>();

		for (Map.Entry<String, String> entry : review_entitySentimentMap.entrySet()) {
			String entity = entry.getKey();
			String sentiment = entry.getValue();
			if(stopWordList.contains(entity)){
				logger.info("stopword found: "+entity);
				continue;
			} else {
				review_entitySentimentMap_refined.put(entity, sentiment);
			}
//			entitySentimentMap.put(entity, sentiment);
		}

		return review_entitySentimentMap_refined;
	}

	public Map<String, String> processPipeline() {
		List<CoreMap> sentencesList = getSentences();
//		System.out.println("@@sentencesList: "+sentencesList);
		System.out.println("@@logger: "+logger);
		logger.info("sentencesList: "+sentencesList);

		Map<String, String> review_entitySentimentMap = new LinkedHashMap<String, String>();

		for(CoreMap sentence : sentencesList) {
			logger.info("sentence: "+sentence);
			String stanfordSentiment = this.getStanfordSentiment(sentence);
//			System.out.println("@@sentence: "+sentence+"\t"+stanfordSentiment);
			logger.info("stanfordSentiment: "+stanfordSentiment);

			SemanticGraph semanticDepGraph = this.getDependencyGraph(sentence);
//			System.out.println("@@semanticDepGraph: "+sentence+"\t"+semanticDepGraph);

			StanfordDependencyGraph sdg = new StanfordDependencyGraph(semanticDepGraph);

			Map<String, Set<String>> entityOpinionMap = null;
			if (sdg != null) {
				entityOpinionMap = sdg.getEntityOpinionMap();
				logger.info("entityOpinionMap: "+entityOpinionMap);
			}

			Map<String, String> entitySentimentMap = new LinkedHashMap<String, String>();
			for (Map.Entry<String, Set<String>> entry : entityOpinionMap.entrySet()) {
				String entity = entry.getKey();
				String sentiment = stanfordSentiment;
				entitySentimentMap.put(entity, sentiment);
			}
			logger.info("entitySentimentMap: "+entitySentimentMap);
			review_entitySentimentMap.putAll(entitySentimentMap);
		}
		logger.info("review_entitySentimentMap: "+review_entitySentimentMap);

		return review_entitySentimentMap;
	}

	public static void loadResources() {
		try {
			stopWordList = FileUtils.readLines(new File("resources/stopwords_refined.txt"), "utf-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void init() {
		CoreNLPController.init();
        ToolKitLogger.init();
        logger = ToolKitLogger.getXpLogger();
        loadResources();
	}
	
	public static void main(String [] args) {
        String text = "this phone has good wifi";
//        String text = "The screen size of the mobile is huge. But the battery life is low.";
//		String text = "The new handset has a huge screen. But the battery life is low.";
//		String text = "Margot Robbi is THE most exquisite human creature on the face of the planet, in this moment in time.";
//		String text = "The restaurant was good. But the cashier was slow.";
		init();
        ToolKitExpression review = new ToolKitExpression(text);
        Map<String, String> review_entitySentimentMap = review.getSentiment();
        
        logger.info("review: "+text);
        logger.info("final entity sentiment map: "+review_entitySentimentMap);
	}

}
