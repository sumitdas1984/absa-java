package com.sd.absa.sdgraphtraversal;

import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.UniversalEnglishGrammaticalRelations;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * 
 */

/**
 * @author Koustuv Saha
 * 23-Apr-2014 3:15:57 pm
 * XpressoV2.0.1  DependencyRelationSorter
 */
public class DependencyRelationSorter implements Comparator<SemanticGraphEdge> {

	//	private static final List<String> ORDERED_ENTRIES = Arrays.asList(DependencyRelationsLexicon.NEG, DependencyRelationsLexicon.DEP, DependencyRelationsLexicon.NSUBJ, DependencyRelationsLexicon.NSUBJPASS, DependencyRelationsLexicon.NN);
	//	private static final List<String> ORDERED_ENTRIES = Arrays.asList(EnglishGrammaticalRelations.NEGATION_MODIFIER.toString(), EnglishGrammaticalRelations.SEMANTIC_DEPENDENT.toString(), EnglishGrammaticalRelations.NOMINAL_SUBJECT.toString(), EnglishGrammaticalRelations.NOMINAL_PASSIVE_SUBJECT.toString(), EnglishGrammaticalRelations.NOUN_COMPOUND_MODIFIER.toString(), EnglishGrammaticalRelations.ADJECTIVAL_COMPLEMENT.toString(), EnglishGrammaticalRelations.CLAUSAL_COMPLEMENT.toString(), EnglishGrammaticalRelations.XCLAUSAL_COMPLEMENT.toString(), EnglishGrammaticalRelations.NUMERIC_MODIFIER.toString());
	private static final List<String> ORDERED_ENTRIES = Arrays.asList(UniversalEnglishGrammaticalRelations.NEGATION_MODIFIER.toString(), UniversalEnglishGrammaticalRelations.SEMANTIC_DEPENDENT.toString(), UniversalEnglishGrammaticalRelations.COMPOUND_MODIFIER.toString(), UniversalEnglishGrammaticalRelations.NOMINAL_SUBJECT.toString(), UniversalEnglishGrammaticalRelations.NOMINAL_PASSIVE_SUBJECT.toString(), UniversalEnglishGrammaticalRelations.CLAUSAL_COMPLEMENT.toString(), UniversalEnglishGrammaticalRelations.XCLAUSAL_COMPLEMENT.toString(), UniversalEnglishGrammaticalRelations.NUMERIC_MODIFIER.toString());

	@Override
	public int compare(SemanticGraphEdge o1, SemanticGraphEdge o2) {
		//		String o1_str = o1.toString();
		//		String o2_str = o2.toString();

		String o1_str = o1.getRelation().toString();
		String o2_str = o2.getRelation().toString();

		//		System.out.println(o1_str + "\t" + o2_str);

		if (ORDERED_ENTRIES.contains(o1_str) || ORDERED_ENTRIES.contains(o2_str)) {
			if (ORDERED_ENTRIES.contains(o1_str) && ORDERED_ENTRIES.contains(o2_str)) {
				/*Both objects are in our ordered list. Compare them by their position in the list*/
				return ORDERED_ENTRIES.indexOf(o1_str) - ORDERED_ENTRIES.indexOf(o2_str);
			}

			if (ORDERED_ENTRIES.contains(o1_str)) {
				/* o1 is in the ordered list, but o2 isn't. o1 is smaller (i.e. first)*/
				return -1;
			}

			if (ORDERED_ENTRIES.contains(o2_str)) {
				/* o2 is in the ordered list, but o1 isn't. o2 is smaller (i.e. first)*/
				return 1;
			}
		}
		if (o1_str.endsWith("mod") || o2_str.endsWith("mod")) {
			if (o1_str.endsWith("mod") && o2_str.endsWith("mod")) {
				return o1_str.compareTo(o2_str);
			}
			if (o1_str.endsWith("mod")) {
				return -1;
			}
			if (o2_str.endsWith("mod")) {
				return 1;
			}
		}

		return o1_str.compareTo(o2_str);
	}
}
