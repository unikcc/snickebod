package ch.ethz.nlp.headline.generators;

import java.io.IOException;
import java.util.Map.Entry;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;

import ch.ethz.nlp.headline.Dataset;
import ch.ethz.nlp.headline.DocumentId;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.BinaryHeapPriorityQueue;
import edu.stanford.nlp.util.PriorityQueue;

public abstract class TfIdfGenerator extends CoreNLPGenerator {

	/**
	 * For each term, stores the collection of the IDs of all documents in which
	 * it occurs.
	 */
	private final Multimap<String, DocumentId> docFreqs;

	public TfIdfGenerator(Dataset dataset, String... annotators)
			throws IOException {
		super(dataset, annotators);

		docFreqs = HashMultimap.create();

		for (Entry<DocumentId, Annotation> entry : annotations.entrySet()) {
			DocumentId documentId = entry.getKey();
			Annotation annotation = entry.getValue();
			for (CoreLabel label : annotation.get(TokensAnnotation.class)) {
				docFreqs.put(label.word(), documentId);
			}
		}
	}

	/**
	 * Compute the frequency of each term in the given document.
	 * 
	 * @param documentId
	 * @return
	 */
	protected Multiset<String> getTermFreqs(DocumentId documentId) {
		Multiset<String> termFreqs = HashMultiset.create();
		Annotation annotation = annotations.get(documentId);

		for (CoreLabel label : annotation.get(TokensAnnotation.class)) {
			String term = label.word();
			termFreqs.add(term);
		}

		return termFreqs;
	}

	/**
	 * Compute the tf-idf score for each term in the given document.
	 * 
	 * @param documentId
	 * @return
	 */
	protected PriorityQueue<String> getTfIdfMap(DocumentId documentId) {
		Multiset<String> termFreqs = getTermFreqs(documentId);
		PriorityQueue<String> tfIdfMap = new BinaryHeapPriorityQueue<>();
		double numDocs = annotations.size();

		for (String term : termFreqs.elementSet()) {
			double termFreq = termFreqs.count(term);
			double docFreq = docFreqs.get(term).size();
			double inverseDocFreq = Math.log(numDocs / docFreq);
			double tfIdf = termFreq * inverseDocFreq;
			tfIdfMap.add(term, tfIdf);
		}

		return tfIdfMap;
	}

}
