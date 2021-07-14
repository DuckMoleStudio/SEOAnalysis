import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class testFilteredUnigram {
    @Test
    public void testFilteredUnigram(){
        NGramStrategy nGramStrategy = new FilteredUnigram();

        assertEquals("[в, важн, вагон, не, что, валя, вал, из]",
                nGramStrategy.getNGram("В важном вагоне №6 не важно, что валяется... Важно валить из вагона!")
                        .toString());
    }
}
