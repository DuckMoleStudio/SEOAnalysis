import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class testVector {

    @Test
    public void testVectorSimple() {
        NGramStrategy nGramStrategy = new FilteredUnigram();
        VocabularyBuilder vocabularyBuilder = new VocabularyBuildStreamAPI(nGramStrategy); //StringAPI method
        List<ClassifiableText> requestList = new ArrayList<>();

        requestList.add(new ClassifiableText
                ("В вагоне №6 не важно, что валяется... Важно валить из вагона!"
                        , "brand", 0.5));
        requestList.add(new ClassifiableText
                ("В важные вагоны FORD валят, что попало"
                        , "brand", 0.5));
        requestList.add(new ClassifiableText
                ("Ford всегда отмечал важность вагонов"
                        , "brand", 0.5));
        requestList.add(new ClassifiableText
                ("ford или не ford, а вагоны всегда были важны"
                        , "brand", 0.5));
        requestList.add(new ClassifiableText
                ("Важнее вагонов ford никогда ничего не было и не будет!"
                        , "brand", 0.5));

        FileManager.saveVocabulary("C:\\Users\\User\\Documents\\voc001.txt"
                , vocabularyBuilder.createVocabulary(requestList,1));
        //[ford, был, в, вагон, важн, вал, всегд, не, что]

        List<String> vocabulary = new ArrayList<>();
        try {vocabulary = FileManager.loadVocabulary("C:\\Users\\User\\Documents\\voc001.txt");}
        catch(IOException ex){
            System.out.println(ex.getMessage());
        } // test save & load vocabulary by the way

        double[] controlVector = {1,1,0,1,1,0,0,1,0};

        assertEquals(true, Arrays.equals(controlVector
        ,Vector.getTextAsVector(new ClassifiableText
                        ("Важнее вагонов ford никогда ничего не было и не будет!"
                                , "brand", 0.5)
                        ,vocabulary,nGramStrategy)));
    }
}
