package com.sd.absa.aspect.entityextraction;

import com.sd.absa.engine.core.ToolKitExpression;
import com.sd.absa.textanalytics.CoreNLPController;
import com.sd.absa.textanalytics.PosTags;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.CoreMap;

import java.util.ArrayList;
import java.util.List;

public class EntityExtraction {

	private static Annotation document;

	public static void main(String[] args) throws Exception {

		ToolKitExpression.init();

		String doc = "Meets marketing and sales financial objectives by forecasting requirements; preparing an annual budget; scheduling expenditures; analyzing variances; initiating corrective actions. Determines annual and gross-profit plans by forecasting and developing annual sales quotas for regions; projecting expected sales volume and profit for existing and new products; analyzing trends and results; establishing pricing strategies; recommending selling prices; monitoring costs, competition, supply, and demand.";
		// System.out.println(extractEntityList(doc));
		annotateDocument(doc);
		List<CoreMap> sentencesList = document.get(SentencesAnnotation.class);
		for (CoreMap sentence : sentencesList) {
			System.out.println(extractEntityList(sentence));
		}
	}

//	public static List<String> extractEntityList(String text) throws
//			Exception {
//
//		List<String> entityList = new ArrayList<String>();
//		List<String> posList = CoreNLPController.getPOS(text);
//		String entity = "";
//		for (String wordPOS : posList) {
//			String word = wordPOS.split("/")[0];
//			String pos = wordPOS.split("/")[1];
//			if (pos.equals("NN") || pos.equals("NNS")) {
//				entity += " " + word;
//			} else {
//				if (!entity.equals("")) {
//					entityList.add(entity.trim());
//				}
//				entity = "";
//			}
//		}
//		return entityList;
//	}

	private static void annotateDocument(String review) {
		document = new Annotation(review);
		CoreNLPController.getPipeline().annotate(document);
	}

	public static List<String> extractEntityList(CoreMap sentence) throws Exception {
		List<String> entityList = new ArrayList<String>();
		String entity = "";
		for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
			String word = token.toString().toLowerCase();
			String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
			if (PosTags.isCommonNoun(pos)) {
				entity += " " + word;
			} else {
				if (!entity.equals("")) {
					entityList.add(entity.trim());
				}
				entity = "";
			}
		}
		return entityList;
	}
}
