import java.util.List;
import java.util.Set;

public class Vector {
    static double[] getTextAsVector(ClassifiableText classifiableText
            , List<String> vocabulary, NGramStrategy nGramStrategy) {
        double[] vector = new double[vocabulary.size()];

        // convert text to nGram
        Set<String> uniqueValues = nGramStrategy.getNGram(classifiableText.getText());

        // create vector
        //

        for (String word : uniqueValues) {
            int vw = vocabulary.indexOf(word);

            if (vw != -1) { // word found in vocabulary
                vector[vw] = 1;
            }
        }

        return vector;
    }
}
