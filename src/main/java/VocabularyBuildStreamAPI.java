import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class VocabularyBuildStreamAPI implements VocabularyBuilder {

    private final NGramStrategy nGramStrategy;

    VocabularyBuildStreamAPI(NGramStrategy nGramStrategy) {
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



        return classifiableTexts.stream().map(x->nGramStrategy.getNGram(x.getText()))
                .flatMap(Collection::stream)
                .collect(Collectors.groupingBy(i -> i, Collectors.counting()))
                .entrySet().stream().filter(x->x.getValue()>freqCount).map(x->x.getKey())
                .sorted()
                .collect(Collectors.toList());
    }
}
