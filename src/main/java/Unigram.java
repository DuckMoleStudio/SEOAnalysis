import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class Unigram implements NGramStrategy {
    @Override
    public Set<String> getNGram(String text) {
        if (text == null) {
            text = "";
        }

        // get all words and digits
        String[] words = text.toLowerCase().split("[ \\pP\n\t\r$+<>№=]");

        Set<String> uniqueValues = new LinkedHashSet<>(Arrays.asList(words));
        uniqueValues.removeIf(s -> s.equals(""));

        return uniqueValues;
    }
}