/**
 * 
 */
package com.sd.absa.sdgraphtraversal;

import ch.qos.logback.classic.Logger;
import com.sd.absa.engine.core.ToolKitLogger;
import com.sd.absa.textanalytics.PosTags;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.GrammaticalRelation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author Koustuv Saha
 * 10-Apr-2014 11:50:28 am
 * XpressoV2.0.1 StanfordDependencyGraph
 */
public class StanfordDependencyGraph {

	protected static final Class<?> c = StanfordDependencyGraph.class;
	protected static final Logger logger = ToolKitLogger.getXpLogger();
	private static final Pattern SPECIAL_CHARS_PATTERN = Pattern.compile("[^\\w\\s]", Pattern.UNICODE_CHARACTER_CLASS);

	protected SemanticGraph dependencyGraph;
	protected List<SemanticGraphEdge> sortedEdges;
	protected Set<SemanticGraphEdge> checkedEdge;

//	protected String text;
//	protected String subject;
//	protected String domainName;
//	protected Map<String, String> wordPOSmap;

//	protected XpFeatureVector featureSet;

//	protected Map<String, String> tagSentimentMap;
//	protected Map<String, String[]> tagAspectMap;
	protected Map<IndexedWord, List<String>> entityOpinionMap;
//	protected Map<String, List<String>> equivalentEntityMap;
//	protected Map<String, String> negatedOpinionMap;
//	protected Map<String, String> modifiedOpinionMap;
	protected Map<IndexedWord, String> multiWordEntityMap;
//	protected Map<String, String> nerMap;

//	protected XpTextObject xto;

	//	protected Map<IndexedWord, SemanticGraphEdge> tempEntityMap;

//	protected Languages language;

	//	public Map<String, String> getTagSentimentMap() {
	//		return this.tagSentimentMap;
	//	}

	//	public Map<String, String[]> getTagAspectMap() {
	//		return this.tagAspectMap;
	//	}

//	public String getEdgesAsStr() {
//		if (sortedEdges != null) {
//			return sortedEdges.toString();
//		} else {
//			return "[]";
//		}
//	}

	public StanfordDependencyGraph(SemanticGraph dependency_graph) {
		checkedEdge = new HashSet<SemanticGraphEdge>();

		if (dependency_graph != null) {
			this.dependencyGraph = dependency_graph;
			this.setSortedEdges();

//			this.text = text;
//			this.subject = subject;
//			this.domainName = domainName;
//
//			this.tagSentimentMap = xto.getTagSentimentMap();
//			this.featureSet = featureSet;
//			this.wordPOSmap = xto.getWordPOSmap();
//			this.tagAspectMap = xto.getTagAspectMap();
//			this.nerMap = xto.getNerMap();
//			this.language = language;
//
//			this.xto = xto;

			this.entityOpinionMap = new HashMap<IndexedWord, List<String>>();
//			this.equivalentEntityMap = new HashMap<String, List<String>>();
//			this.negatedOpinionMap = new HashMap<String, String>();
//			this.modifiedOpinionMap = new HashMap<String, String>();
			this.multiWordEntityMap = new HashMap<IndexedWord, String>();

			this.fireDependencyRules();
			
			logger.info("entityOpinionMap: "+entityOpinionMap);
			logger.info("multiWordEntityMap: "+multiWordEntityMap);
		}
	}

	public void fireDependencyRules() {
		// double startTime = System.nanoTime();
		// System.out.println("Time to get EdgesListSorted: " +
		// (System.nanoTime() - startTime) + " ns");
		//		List<SemanticGraphEdge> sortedEdgesList = this.dependencyGraph.edgeListSorted();
		//		Collections.sort(sortedEdgesList, new DependencyRelationSorter());
		//		System.out.println(sortedEdgesList.toString());

		//		for (SemanticGraphEdge edge : sortedEdgesList) {

		for (SemanticGraphEdge edge : sortedEdges) {

			IndexedWord gov = edge.getGovernor();
			IndexedWord dep = edge.getDependent();

			//			IndexedWord nsubjDep = this.dependencyGraph.getChildWithReln(gov, UniversalEnglishGrammaticalRelations.NOMINAL_SUBJECT);
			//			System.out.println("nsubjDep: " + nsubjDep);

			// System.out.println("Gov: " + gov + "\tGovWord: " + gov.value() +
			// gov.tag() + "\tDep: " + dep + "\tDepWord: " + dep.value());
			//			System.out.println("CheckedEdges: " + this.checkedEdge);

			if (this.checkedEdge.contains(edge)) {
				continue;
			}
			GrammaticalRelation rel = edge.getRelation();

			//			if (rel.equals(UniversalEnglishGrammaticalRelations.CLAUSAL_COMPLEMENT) || rel.equals(UniversalEnglishGrammaticalRelations.CLAUSAL_COMPLEMENT) || rel.equals(UniversalEnglishGrammaticalRelations.DIRECT_OBJECT) || rel.equals(UniversalEnglishGrammaticalRelations.XCLAUSAL_COMPLEMENT)) {
			//				System.out.println("entered Firest time");
			//			} else if (rel.equals(UniversalEnglishGrammaticalRelations.XCLAUSAL_COMPLEMENT)) {
			//				System.out.println("entered secn time");
			//			} else if (rel.equals(UniversalEnglishGrammaticalRelations.NOMINAL_SUBJECT)) {
			//				System.out.println("entered the time");
			//			}

			this.checkedEdge.add(edge);
			try {
				String relationStr = rel.toString();
				String methodName = getMethodName(relationStr);
				System.out.println("Calling Method: " + methodName);
				logger.info("Method Call:\t" + relationStr + "\t" + methodName);
				
				if(relationStr.equals("nsubj")
						|| relationStr.equals("compound")
						|| relationStr.equals("nn")
						|| relationStr.equals("amod")) {
					Method method = c.getMethod(methodName, IndexedWord.class, IndexedWord.class, IndexedWord.class, GrammaticalRelation.class);
					method.invoke(this, gov, null, dep, rel);
				} else {
					continue;
				}
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}

		}

		//		logger.info("TagAspectMap " + this.tagAspectMap.toString());

		// for (DefaultEdge ed : sortedEdgesList) {
		// if (checkedEdge.contains(ed)) {
		// continue;
		// }
		// String edgeRelation = ed.toString();
		// checkedEdge.add(ed);
		// // String sourceV = this.dependency_graph.getEdgeSource(ed);
		// String gov = this.dependency_graph.getEdgeSource(ed);
		// // String currTargetV = this.dependency_graph.getEdgeTarget(ed);
		// String dep = this.dependency_graph.getEdgeTarget(ed);
		// try {
		// String methodName = getMethodName(edgeRelation);
		// Method method = c.getMethod(methodName, String.class, String.class,
		// String.class, String.class);
		// method.invoke(this, gov, null, dep, edgeRelation);
		// } catch (IllegalAccessException | IllegalArgumentException |
		// InvocationTargetException | NoSuchMethodException | SecurityException
		// e) {
		// e.printStackTrace();
		// }
		// }

	}

	/**
	 * @param sortedEdges the sortedEdges to set
	 */
	public void setSortedEdges() {
		this.sortedEdges = this.dependencyGraph.edgeListSorted();
		Collections.sort(this.sortedEdges, new DependencyRelationSorter());
	}

	public String getMethodName(String relation) {

		//		System.out.print("Relation: " + relation + "\t");

		if (relation.contains(":")) {
			relation = relation.split(":")[0];
		} else if (relation.contains("_")) {
			relation = relation.split("_")[0];
		}
		// return "fireRule_" + relation;
		//		System.out.println(relation);
		return relation + "_fireRule";
	}

	public void nsubj_fireRule(IndexedWord gov, IndexedWord v2, IndexedWord dep, GrammaticalRelation edgeRelation) {
		logger.info("NSUBJ: " + dep + " nsubj " + gov);
		String depWord = dep.word();
		String govWord = gov.word();

		if (!PosTags.isProperNoun(gov.tag())) {
			this.addToOpinionMap(dep, govWord);
		}
	}

	public void nn_fireRule(IndexedWord gov, IndexedWord v2, IndexedWord dep, GrammaticalRelation edgeRelation) {
		//		String govWord = gov.value().toLowerCase();
		//		String entity = gov.value().toLowerCase();
		//		String entity = gov.value();
		//		System.out.println("EdgeRelation: " + edgeRelation);

		//		IndexedWord prefix = this.dependencyGraph.getChildWithReln(gov, edgeRelation);
		//		if (prefix != null) {
		//			String prefixWord = prefix.word();
		//			System.out.println("Prefix: " + prefixWord);
		//			entity = prefixWord + " " + entity;
		//		}
		//		IndexedWord prefix2 = this.dependencyGraph.getChildWithReln(gov, UniversalEnglishGrammaticalRelations.NOUN_COMPOUND_MODIFIER);
		//		if (prefix2 != null) {
		//			System.out.println("Prefix2: " + prefix2);
		//			entity = prefix2.word() + " " + entity;
		//		}

		if (!this.multiWordEntityMap.containsKey(gov)) {
			String govWord = gov.value();
			StringBuilder netEntity = new StringBuilder();
			//		System.out.println("Gov: " + gov);
			Set<IndexedWord> prefixList = this.dependencyGraph.getChildrenWithReln(gov, edgeRelation);
			if (prefixList != null) {
				//			int i = 0;
				for (IndexedWord prefix : prefixList) {
					netEntity.append(prefix.word()).append(" ");
				}
			}
			String entity = netEntity.append(govWord).toString();
			logger.info("NN: Full Entity of " + govWord + " is " + entity);
			this.addToMultiWordEntityMap(gov, entity);
		}
	}

	public void compound_fireRule(IndexedWord gov, IndexedWord v2, IndexedWord dep, GrammaticalRelation edgeRelation) {
		String relationName = edgeRelation.toString();
		if (relationName.contains(":")) {
//			if (relationName.split(":")[1].equals("prt")) {
//				prt_fireRule(gov, v2, dep, edgeRelation);
//			}
		} else {
			nn_fireRule(gov, v2, dep, edgeRelation);
		}
	}

	public void amod_fireRule(IndexedWord gov, IndexedWord v2, IndexedWord dep, GrammaticalRelation edgeRelation) {
		// System.out.println("Amod " + gov + " is qualified by " + dep);

		logger.info("AMOD: " + gov + " is qualified by " + dep + " and v2= " + v2);

		// addToOpinionMap(gov, dep);
		String govWord = gov.word().toLowerCase();
		String depWord = dep.word().toLowerCase();
		
		this.addToOpinionMap(gov, depWord);
		
		if (v2 != null) {
			addToOpinionMapModified(v2, depWord, govWord);
		}
	}
	
	protected void addToOpinionMap(IndexedWord entity, String currOpinion) {
		List<String> opinionSet = this.entityOpinionMap.get(entity);
		currOpinion = currOpinion.toLowerCase();
		if (opinionSet == null) {
			opinionSet = new ArrayList<String>();
			//			System.out.println("1.1: Opinion Adding\t" + entity.word() + "\t" + currOpinion);
			opinionSet.add(currOpinion);
		} else {
			// for (String str : tempSet) {
			int length = opinionSet.size();
			boolean addFlag = true;
			for (int i = 0; i < length; i++) {
				String existingOpinion = opinionSet.get(i);

				//				System.out.println("EntityOpinionMap: \t" + entity.word() + ":\t" + existingOpinion + " && " + currOpinion);

				if (existingOpinion.contains(currOpinion)) {
					addFlag = false;
					break;
				} else if (currOpinion.contains(existingOpinion)) {
					opinionSet.remove(existingOpinion);
					opinionSet.add(currOpinion);
					//					System.out.println("1.2: Opinion Adding " + entity.word() + "\t" + currOpinion);

					addFlag = false;
					break;
				}
			}
			if (addFlag) {
				//				System.out.println("1.3: Opinion Adding " + entity.word() + "\t" + currOpinion);
				opinionSet.add(currOpinion);
			}
		}
//		logger.info("AddToEntityOpinionMap (1) :\t" + entity + "\t" + currOpinion);
		this.entityOpinionMap.put(entity, opinionSet);
	}

	private void addToMultiWordEntityMap(IndexedWord entity, String multiWordEntity) {
//		logger.info("Adding MultiWordEntity " + entity + "\t" + multiWordEntity);

		//		if (this.multiWordEntityMap.containsKey(entity)) {
		//			String existingMultiWordEntity = this.multiWordEntityMap.get(entity);
		//			String prefix = 
		//		}

		this.multiWordEntityMap.put(entity, multiWordEntity);
	}

	private void addToOpinionMapModified(IndexedWord entity, String dep, String gov) {
		List<String> tempSet = this.entityOpinionMap.get(entity);
		String opinion = (dep + " " + gov).toLowerCase();
		if (tempSet == null) {
			tempSet = new ArrayList<String>();
			tempSet.add(opinion);
			//			System.out.println("1.1: OpinionModified Adding\t" + entity.word() + "\t" + opinion);
		} else {
			int length = tempSet.size();
			boolean addFlag = true;
			for (int i = 0; i < length; i++) {
				String str = tempSet.get(i);
				if (str.contains(gov)) {
					//					str = str.replaceAll(gov, dep + " " + gov);
					tempSet.remove(str);
					str = str.replaceAll(gov, opinion);
					tempSet.add(str);
					//					System.out.println("1.2: OpinionModified Adding\t" + entity.word() + "\t" + str);
					addFlag = false;
					break;
				}
			}
			if (addFlag) {
				//				System.out.println("1.3: OpinionModified Adding\t" + entity.word() + "\t" + opinion);
				tempSet.add(opinion);
			}
		}
//		logger.info("AddToEntityOpinionMap (2) :\t" + entity + "\t" + opinion);
		this.entityOpinionMap.put(entity, tempSet);
	}
	
	public Map<String, Set<String>> getEntityOpinionMap() {
		
		logger.info("Initial EntityOpinionMap:\t" + this.entityOpinionMap + "\n");

		if (this.entityOpinionMap == null) {
			return null;
		}

		logger.info("MultiWordEntity Map:\t" + this.multiWordEntityMap);


		Map<String, Set<String>> entityOpinionMap_mod = new LinkedHashMap<String, Set<String>>();
		
//		Set<String> s1 = new HashSet<String>(Arrays.asList("aaa", "bbb"));
//		entityOpinionMap_mod.put("xxx",s1);
//
//		Set<String> s2 = new HashSet<String>(Arrays.asList("aaa", "bbb"));
//		entityOpinionMap_mod.put("yyy",s2);
		
		for (Map.Entry<IndexedWord, List<String>> entry : this.entityOpinionMap.entrySet()) {

			IndexedWord keyIdxWord = entry.getKey();
			//			String entity = keyIdxWord.word();
			String entity = keyIdxWord.word().toLowerCase();
			
			List<String> opinionList = entry.getValue();
			Set<String> opinionSet = new HashSet<String>(opinionList);
			
			String entity_multiword = null;
			if(this.multiWordEntityMap.containsKey(keyIdxWord)) {
				entity_multiword = this.multiWordEntityMap.get(keyIdxWord);
			}
			
			if(entity_multiword != null) {
				entityOpinionMap_mod.put(entity_multiword, opinionSet);
			} else {
				entityOpinionMap_mod.put(entity, opinionSet);
			}
			
		}

		return entityOpinionMap_mod;
	}

}
