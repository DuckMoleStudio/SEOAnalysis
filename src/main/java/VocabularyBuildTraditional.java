
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class VocabularyBuildTraditional implements VocabularyBuilder {

    private final NGramStrategy nGramStrategy;

    VocabularyBuildTraditional(NGramStrategy nGramStrategy) {
        if (nGramStrategy == null) {
            throw new IllegalArgumentException();
        }

        this.nGramStrategy = nGramStrategy;
    }

    @Override
    public List<String> createVocabulary(List<ClassifiableText> classifiableTexts, int freqCount) {
        if (classifiableTexts == null ||
                classifiableTexts.size() == 0) {
            throw new IllegalArgumentException();
        }

        Map<String, Integer> uniqueValues = new HashMap<>();
        List<String> vocabulary = new ArrayList<>();

        // count frequency of use each word (converted to n-gram) from all Classifiable Texts
        //

        for (ClassifiableText classifiableText : classifiableTexts) {
            for (String word : nGramStrategy.getNGram(classifiableText.getText())) {
                if (uniqueValues.containsKey(word)) {
                    // increase counter
                    uniqueValues.put(word, uniqueValues.get(word) + 1);
                } else {
                    // add new word
                    uniqueValues.put(word, 1);
                }
            }
        }

        // convert uniqueValues to Vocabulary, excluding infrequent
        //

        for (Map.Entry<String, Integer> entry : uniqueValues.entrySet()) {
            if (entry.getValue() > freqCount) {
                vocabulary.add(entry.getKey());
            }
        }

        vocabulary = vocabulary.stream().sorted().collect(Collectors.toList()); // sorting added
        return vocabulary;


    }
}
