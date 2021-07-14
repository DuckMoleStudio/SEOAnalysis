import java.util.List;

public interface VocabularyBuilder {
    List<String> createVocabulary(List<ClassifiableText> classifiableTexts, int freqCount);
}
