package ch.ethz.nlp.headline.generators;

import java.io.IOException;
import java.util.List;
import ch.ethz.nlp.headline.Dataset;
import ch.ethz.nlp.headline.Document;
import edu.stanford.nlp.util.PriorityQueue;

public class TfIdfWordsGenerator extends TfIdfGenerator {

	/**
	 * The maximum number of characters in the generated headline. The generator
	 * will greedily build the headline from the terms with the highest TF-IDF
	 * score until the limit is reached.
	 */
	private static final int MAX_LENGTH = 100;

	public TfIdfWordsGenerator(Dataset dataset) throws IOException {
		super(dataset);
	}

	@Override
	public String getId() {
		return "TF-IDF-WORDS";
	}

	@Override
	public String generate(Document document) throws IOException {
		PriorityQueue<String> tfIdfMap = getTfIdfMap(document.getId());

		// Build the headline from the words with the highest tf-idf score
		List<String> sortedTerms = tfIdfMap.toSortedList();
		StringBuilder builder = new StringBuilder();
		for (String sortedTerm : sortedTerms) {
			if (builder.length() + sortedTerm.length() > MAX_LENGTH) {
				break;
			}
			builder.append(" " + sortedTerm);
		}

		String result = builder.toString().trim();
		return result;
	}

}
