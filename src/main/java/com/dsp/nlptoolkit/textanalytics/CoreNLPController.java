package com.dsp.nlptoolkit.textanalytics;


import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;


import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.codehaus.jettison.json.JSONObject;

/**
 * Created by sumitd on 1/18/16.
 */
public class CoreNLPController {

    public static StanfordCoreNLP pipeline = null;

    public static void init() {
        String language = "english";
        pipeline = initPipeline();
    }

    private static StanfordCoreNLP initPipeline() {
        Properties props = new Properties();
        props.put("tokenize.options", "ptb3Escaping=false");
        props.put("parse.maxlen", "10000");
        props.put("pos.model", "edu/stanford/nlp/models/pos-tagger/english-caseless-left3words-distsim.tagger");
        props.put("annotators", "tokenize,ssplit,pos,lemma,ner,parse,sentiment");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        return pipeline;
    }

    public static StanfordCoreNLP getPipeline() {
        return pipeline;
    }

    private static List<CoreMap> getAnnotationCoreMap(String documentText){
        Annotation document = new Annotation(documentText);
        // run all Annotators on this text
        getPipeline().annotate(document);
        // Iterate over all of the sentences found
        List<CoreMap> annotationCoreMap = document.get(CoreAnnotations.SentencesAnnotation.class);
        return annotationCoreMap;
    }

    public static List<String> getPOS(String documentText) throws Exception {
        List<String> posList = new LinkedList<String>();

        List<CoreMap> sentences =  getAnnotationCoreMap(documentText);

        for (CoreMap sentence : sentences) {
            // traversing the words in the current sentence
            // a CoreLabel is a CoreMap with additional token-specific methods
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                // this is the text of the token
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                // this is the POS tag of the token
                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                String word_pos = word + "/" + pos;
                posList.add(word_pos);
            }
        }
        return posList;
    }

    public static List<String> splitSentences(String documentText) {
        List<String> sentencesList = new ArrayList<String>();

        List<CoreMap> sentences =  getAnnotationCoreMap(documentText);

        for (CoreMap sentence : sentences) {
            sentencesList.add(sentence.toString());
        }
        return sentencesList;
    }

    public static List<String> getLemmas(String documentText) {
        List<String> lemmas = new LinkedList<String>();

        List<CoreMap> sentences =  getAnnotationCoreMap(documentText);

        for (CoreMap sentence : sentences) {
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                lemmas.add(token.get(CoreAnnotations.LemmaAnnotation.class));
            }
        }
        return lemmas;
    }

    public static Map<String,List<String>> getPhrases(String documentText) {
        List<CoreMap> sentences = getAnnotationCoreMap(documentText);
        List<String> nounPhrases = new ArrayList<String>();
        List<String> verbPhrases = new ArrayList<String>();
        List<String> prepositionalPhrases = new ArrayList<String>();
        Pattern r;
        Matcher m;

        Map<String, List<String>> phrases = new HashMap<String, List<String>>();

        for (CoreMap sentence : sentences) {
            Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);

            //System.out.println("@@lexicalized paser tree: "+tree.toString());
            //r = Pattern.compile("(MD|NN|DT|PRP|JJ|RB)[A-Z]{0,3}");
            r = Pattern.compile("(MD|NN|DT|PRP|JJ|RB)[A-Z]{0,3}");
            //Extracting Noun Phrases
            TregexPattern pattern = TregexPattern.compile("@NP");
            TregexMatcher matcher = pattern.matcher(tree);
            while (matcher.find()) {
                Tree match = matcher.getMatch();
                List<Tree> leaves1 = match.getChildrenAsList();
                String ss = "";

                for (Tree tree1 : leaves1) {
                    //System.out.println(tree1);
                    String val = tree1.label().value();
                    m = r.matcher(val);
                    if (m.find()) {
                        Tree nn[] = tree1.children();
                        ss = ss + Sentence.listToString(nn[0].yield())+"/"+val + " ";
                    }
                }

                if (!ss.isEmpty() && ss.matches("/NN[A-Z]{0,3}")) {
                    nounPhrases.add(ss.trim().replaceAll("/[A-Z]{0,5}",""));
                }
            }
            phrases.put("@NP",nounPhrases);

            //Extracting Verb Phrases
            r = Pattern.compile("(VB|MD)[A-Z]{0,3}");
            pattern = TregexPattern.compile("@VP");
            matcher = pattern.matcher(tree);
            while (matcher.find()) {
                Tree match = matcher.getMatch();
                List<Tree> leaves1 = match.getChildrenAsList();
                String ss = "";

                for (Tree tree1 : leaves1) {
                    String val = tree1.label().value();
                    //System.out.println(tree1);

                    m = r.matcher(val);
                    if (m.find()) {
                        Tree nn[] = tree1.children();
                        ss = ss + Sentence.listToString(nn[0].yield()) + " ";
                    }
                }

                if (!ss.isEmpty()) {
                    verbPhrases.add(ss.trim());
                }
            }
            phrases.put("@VP",verbPhrases);

            //Extracting Verb Phrases
            pattern = TregexPattern.compile("@PP");
            matcher = pattern.matcher(tree);
            while (matcher.find()) {
                Tree match = matcher.getMatch();
                List<Tree> leaves1 = match.getChildrenAsList();
                String ss = "";

                for (Tree tree1 : leaves1) {
                    //String val = tree1.label().value();
                    for(Tree nn : tree1.children()) {
                        ss = ss + Sentence.listToString(nn.yield()) + " ";
                    }
                }

                if (!ss.isEmpty()) {
                    verbPhrases.add(ss.trim());
                }
            }
            phrases.put("@PP",verbPhrases);

        }
        return phrases;
    }
    
	/**
	 * The method processes a sentence(in stanford custom data structure) and returns the map of named entities 
	 * extracted from the sentence.
	 * @param sentence text(in stanford custom data structure) from where named entities are extracted.
	 * @return map of named entities extracted from the sentence
	 */
	private static Map<String, String> getNERsentence(CoreMap sentence) {
		//		Map<String, String> entitiesNERmap = new LinkedHashMap<String, String>();
		Map<String, String> entitiesNERmap = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
		List<CoreLabel> coreLabelList = sentence.get(TokensAnnotation.class);
		String previousNER = null;
		String previousEntity = null;
		for (CoreLabel coreLabel : coreLabelList) {

			String currNER = coreLabel.ner();

			if (currNER.equals(previousNER)) {
				previousEntity += " " + coreLabel.word();
			} else {
				if (previousEntity != null) {
					if (!previousNER.equals("O")) {
						entitiesNERmap.put(previousEntity, previousNER);
					}
				}
				previousEntity = coreLabel.word();
				previousNER = currNER;
			}
		}
		if (previousEntity != null) {
			if (!previousNER.equals("O")) {
				entitiesNERmap.put(previousEntity, previousNER);
			}
		}
		return entitiesNERmap;
	}

	/**
	 * The method computes and returns a map of named entities from a list of sentences(in stanford custom data structure)
	 * @return string containing the map of the named entities extracted from the list of the sentences
	 */
	public static JSONObject getNER(String documentText) {

		return new JSONObject(getNERMap(documentText));
	}

//	private static Map<String, Map<String, String>> getNerMap(String documentText) {
//		List<CoreMap> sentencesList = getAnnotationCoreMap(documentText);
//		Map<String, Map<String, String>> sentenceNERmap = new LinkedHashMap<String, Map<String, String>>();
//		// StringBuilder sb = new StringBuilder();
//		for (CoreMap sentence : sentencesList) {
//			Map<String, String> entitiesNERmap = getNERsentence(sentence);
//			sentenceNERmap.put(sentence.toString(), entitiesNERmap);
//		}
//		return sentenceNERmap;
//	}

	private static Map<String, String> getNERMap(String documentText) {
		List<CoreMap> sentencesList = getAnnotationCoreMap(documentText);
		Map<String, String> documentNERmap = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
		for (CoreMap sentence : sentencesList) {
			Map<String, String> sentenceEntityNERmap = getNERsentence(sentence);
			documentNERmap.putAll(sentenceEntityNERmap);
		}
		return documentNERmap;
	}

	public SemanticGraph getDependencyGraph(CoreMap sentence) {

		// SemanticGraph dependency_graph =
		// sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class);
		SemanticGraph dependency_graph = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
		return dependency_graph;

	}

    public static void main(String [] args) throws Exception {
        init();
        System.out.println("Starting Stanford CoreNLP");

//        String text[] = {"All the kids were sleeping.",
//                "The boy in the blue jeans says he'll do it.",
//                "He bought her a beautiful red dress.",
//                "Mom baked tasty chocolate cookies.",
//                "Julia was thinking about her friends back home.",
//                "Will you talk with these rude people?",
//                "You are a true hero.",
//                "My dog is my best friend",
//                "Many buildings were destroyed in the earthquake.",
//                "Few of her colleagues are married.",
//                "Did you know his elder brother had emigrated to New Zealand?",
//                "Elephants are the largest land animals.",
//                "In fact, some of my best friends are journalists.",
//                "For years the house had no electricity."};
//
//        String text1[] = {"She was walking quickly to the mall.",
//                "He should wait before going swimming.",
//                "Those girls are not trying very hard.",
//                "Ted might eat the cake.",
//                "You must go right now.",
//                "You can’t eat that!",
//                "My mother is fixing us some dinner.",
//                "Words were spoken.",
//                "These cards may be worth hundreds of dollars!",
//                "The teacher is writing a report.",
//                "You have woken up everyone in the neighborhood.",
//                "Texting on his phone, the man swerved into a ditch.",
//                "As the cat watched, the two puppies fought over a bone.",
//                "The small dog was reluctant to learn new things.",
//                "When he arrives, we can try to build a fort.",
//                "Finally, we can afford to buy a new house.",
//                "Walking on the ice, she slipped and fell.",
//                "Open the door to let the fresh air in.",
//                "To make lemonade, you first need some lemons.",
//                "It takes two people to tango."};
//
//        String text2[] = {"You can use the broom behind you to sweep the floor.",
//                "The bracelet in the storefront window is the one I want.",
//                "We stayed at the cabin by the river.",
//                "The store at the corner sells sandwiches.",
//                "I adopted a black cat with white paws.",
//                "When you get to the sign, take a left.",
//                "We climbed up the hill to see the view.",
//                "It annoys me when people talk during movies.",
//                "Hannah looked under the bed to see if she could find her necklace.",
//                "The sun rose over the mountain.",
//                "I’ll meet you after school."};
//        for(String str : text) {
//            System.out.println("Phrases : " + getPhrases(str).get("@NP"));
//        }

        //String text = "I have an ipod. it is a great buy but I'm probably the only person that dislikes the itune software";
        //String text = "The house has a beautiful garden view.";
        //String text = "After slithering down the stairs and across the road to scare nearly to death Mrs. Philpot busy pruning her rose bushes";

        String text = "Jim bought 300 shares of Acme Corp. in 2006. Aravind went to Talentica.";
//        String text = "Humphrey Sheil, co-author of Sun Certified Enterprise Architect for Java EE Study Guide, 2nd Edition, demonstrates how an off the shelf Machine Learning package can be used to add significant value to vanilla Java code for language parsing, recognition and entity extraction.";
        System.out.println("POS : " + getPOS(text));
        System.out.println("Sentences : " + splitSentences(text));
        System.out.println("Lemmas : " + getLemmas(text));
//        System.out.println("Phrases : " + getPhrases("NP",text));
        System.out.println("NER: "+ getNER(text));
//        System.out.println("Dep graph: "+getDependencyGraph());

    }
}
