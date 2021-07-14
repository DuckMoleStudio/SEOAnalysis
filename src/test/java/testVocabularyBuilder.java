import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class testVocabularyBuilder {

    @Test
    public void testVocabularyBuilderSimple(){
        NGramStrategy nGramStrategy = new FilteredUnigram();
        VocabularyBuilder vocabularyBuilder = new VocabularyBuildStreamAPI(nGramStrategy); //StringAPI method
        List<ClassifiableText> requestList = new ArrayList<>();

        requestList.add(new ClassifiableText
                ("В вагоне №6 не важно, что валяется... Важно валить из вагона!"
                        ,"brand",0.5));
        requestList.add(new ClassifiableText
                ("В важные вагоны FORD валят, что попало"
                        ,"brand",0.5));
        requestList.add(new ClassifiableText
                ("Ford всегда отмечал важность вагонов"
                        ,"brand",0.5));
        requestList.add(new ClassifiableText
                ("ford или не ford, а вагоны всегда были важны"
                        ,"brand",0.5));
        requestList.add(new ClassifiableText
                ("Важнее вагонов ford никогда ничего не было и не будет!"
                        ,"brand",0.5));

        assertEquals("[ford, был, в, вагон, важн, вал, всегд, не, что]",
               vocabularyBuilder.createVocabulary(requestList,1)
                        .toString());

        assertEquals("[ford, вагон, важн, не]",
                vocabularyBuilder.createVocabulary(requestList,2)
                        .toString());

        vocabularyBuilder = new VocabularyBuildTraditional(nGramStrategy); // Traditional method

        assertEquals("[ford, был, в, вагон, важн, вал, всегд, не, что]",
                vocabularyBuilder.createVocabulary(requestList,1)
                        .toString());

        assertEquals("[ford, вагон, важн, не]",
                vocabularyBuilder.createVocabulary(requestList,2)
                        .toString());
    }

    @Test
    public void testVocabularyBuilderXLSStringAPI(){
        NGramStrategy nGramStrategy = new FilteredUnigram();
        VocabularyBuilder vocabularyBuilder = new VocabularyBuildStreamAPI(nGramStrategy); //StringAPI method

        List<ClassifiableText> requestList = new ArrayList<>();

        try {requestList = FileManager
                .loadRequestXLS("C:\\Users\\User\\Documents\\icom0521-1.xls");}
        catch(IOException ex){
            System.out.println(ex.getMessage());
        }
        assertEquals(344,
                vocabularyBuilder.createVocabulary(requestList,0)
                        .size());

        assertEquals(144,
                vocabularyBuilder.createVocabulary(requestList,1)
                        .size());

        assertEquals(89,
                vocabularyBuilder.createVocabulary(requestList,2)
                        .size());

        assertEquals(62,
                vocabularyBuilder.createVocabulary(requestList,3)
                        .size());

        assertEquals(55,
                vocabularyBuilder.createVocabulary(requestList,4)
                        .size());

        assertEquals(48,
                vocabularyBuilder.createVocabulary(requestList,5)
                        .size());

        //FileManager.saveVocabulary("C:\\Users\\User\\Documents\\voc01.txt", vocabularyBuilder.createVocabulary(requestList,5));

    }

    @Test
    public void testVocabularyBuilderXLSTraditional(){
        NGramStrategy nGramStrategy = new FilteredUnigram();
        VocabularyBuilder vocabularyBuilder = new VocabularyBuildTraditional(nGramStrategy); //Traditional method

        List<ClassifiableText> requestList = new ArrayList<>();

        try {requestList = FileManager
                .loadRequestXLS("C:\\Users\\User\\Documents\\icom0521-1.xls");}
        catch(IOException ex){
            System.out.println(ex.getMessage());
        }
        assertEquals(344,
                vocabularyBuilder.createVocabulary(requestList,0)
                        .size());

        assertEquals(144,
                vocabularyBuilder.createVocabulary(requestList,1)
                        .size());

        assertEquals(89,
                vocabularyBuilder.createVocabulary(requestList,2)
                        .size());

        assertEquals(62,
                vocabularyBuilder.createVocabulary(requestList,3)
                        .size());

        assertEquals(55,
                vocabularyBuilder.createVocabulary(requestList,4)
                        .size());

        assertEquals(48,
                vocabularyBuilder.createVocabulary(requestList,5)
                        .size());

    }

    @Test
    public void testVocabularyBuilderPerformanceStringAPI(){
        NGramStrategy nGramStrategy = new FilteredUnigram();
        VocabularyBuilder vocabularyBuilder = new VocabularyBuildStreamAPI(nGramStrategy); //StringAPI method

        List<ClassifiableText> requestList = new ArrayList<>();

        try {requestList = FileManager
                .loadRequestXLS("C:\\Users\\User\\Documents\\icom0521-1.xls");}
        catch(IOException ex){
            System.out.println(ex.getMessage());
        }

        long timeStart = System.currentTimeMillis();
        for(int ii=0;ii<1000;ii++){
            vocabularyBuilder.createVocabulary(requestList,1);
        }
        System.out.println("Elapsed time: " + (System.currentTimeMillis()-timeStart));
    }

    @Test
    public void testVocabularyBuilderPerformanceTraditional(){
        NGramStrategy nGramStrategy = new FilteredUnigram();
        VocabularyBuilder vocabularyBuilder = new VocabularyBuildTraditional(nGramStrategy); //Traditional method

        List<ClassifiableText> requestList = new ArrayList<>();

        try {requestList = FileManager
                .loadRequestXLS("C:\\Users\\User\\Documents\\icom0521-1.xls");}
        catch(IOException ex){
            System.out.println(ex.getMessage());
        }

        long timeStart = System.currentTimeMillis();
        for(int ii=0;ii<1000;ii++){
            vocabularyBuilder.createVocabulary(requestList,1);
        }
        System.out.println("Elapsed time: " + (System.currentTimeMillis()-timeStart));
    }
}
