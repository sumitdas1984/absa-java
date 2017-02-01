package com.sd.absa.engine.core;

import com.sd.absa.textanalytics.CoreNLPController;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;

import java.util.List;

public class ToolKitText {

//	protected static final Logger logger = ToolKitLogger.getXpLogger();
	
	public String review_text;
	
	public Annotation document;
	
	public ToolKitText(String reviewStr) {
		this.review_text = reviewStr;
		this.initialProcess();
	}

	private void initialProcess() {
		this.preProcess();
		double tac = System.currentTimeMillis();
		// logger.info("Obtained normalized text in " + (tac - tic) + "ms\n" + this.normalized_text);
		this.annotateDocument();
		double tic = System.currentTimeMillis();
		System.out.println("Annotation completed in " + (tic - tac) + " ms");
	}

	protected void preProcess() {
		
		System.out.println("Original Text :\t" + this.review_text);
		
		/*should not hit for 9/10*/
		this.review_text = this.review_text.replaceAll("(\\w{2,})/(\\w{2,})", "$1 or $2");
		/* If "....." exists in sentence, then replace it with . and a space */
		this.review_text = this.review_text.replaceAll("\\.\\.+", ". ");
		/* If no space exists after a ".". Then add a space */
		this.review_text = this.review_text.replaceAll("\\.([A-Z][^\\.])", ". $1");
		/* omit the single letters at the end of a sentence*/
		this.review_text = this.review_text.replaceAll("\\s\\S\\.", ".");
		this.review_text = this.review_text.replaceAll("\\s\\S\\!", "!");
		this.review_text = this.review_text.replaceAll("\\s\\S\\Z", "");
		/* If a lowercaps letter after . and " " then replace with capitalized version */
		this.review_text = this.review_text.replaceAll("\\.([a-z][^\\.])", ". " + "$1".toUpperCase());
		/* Multiple occurences of same punctuation character is replaced by one. This leads to wrong parsing .!?\\-*/
		this.review_text = this.review_text.replaceAll("(\\?|\\*|\\$|\\!|\\-)+", "$1");
		this.review_text = this.review_text.replaceAll("\\.([a-z][^\\.])", ". " + "$1".toUpperCase());
		this.review_text = this.review_text.replaceAll("[^\\x00-\\x7F]", "");
		this.review_text = this.review_text.replaceAll("\\s+", " ");

		System.out.println("Normalized Text :\t" + this.review_text);

	}

	public void annotateDocument() {
		document = new Annotation(this.review_text);
		CoreNLPController.getPipeline().annotate(document);
	}

	public List<CoreMap> getSentences() {
		List<CoreMap> sentencesList = this.document.get(SentencesAnnotation.class);
		return sentencesList;
	}

	protected String getStanfordSentiment(CoreMap sentence) {
			return sentence.get(SentimentCoreAnnotations.SentimentClass.class);
	}

	public SemanticGraph getDependencyGraph(CoreMap sentence) {

		// SemanticGraph dependency_graph =
		// sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class);
		SemanticGraph dependency_graph = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
		return dependency_graph;

	}

//	public static void main(String [] args) {
//        String text = "Jim bought 300 shares of Acme Corp. in 2006. Aravind went to Talentica.";
//        CoreNLPController.init();
//        ToolKitText review = new ToolKitText(text);
//	}
}
