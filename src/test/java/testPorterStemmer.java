import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class testPorterStemmer {
    @Test
     public void testPorterStemmer(){
        assertEquals("важн", PorterStemmer.stem("важная"));
        assertEquals("важн", PorterStemmer.stem("важнее"));
        assertEquals("важн", PorterStemmer.stem("важнейшие"));
        assertEquals("важн", PorterStemmer.stem("важнейшими"));
        assertEquals("важнича", PorterStemmer.stem("важничает"));
        assertEquals("важнича", PorterStemmer.stem("важничал"));
        assertEquals("важнича", PorterStemmer.stem("важничала"));
        assertEquals("важнича", PorterStemmer.stem("важничать"));
        assertEquals("важн", PorterStemmer.stem("важно"));
        assertEquals("важн", PorterStemmer.stem("важное"));
        assertEquals("важн", PorterStemmer.stem("важной"));
        assertEquals("важн", PorterStemmer.stem("важном"));
        assertEquals("важн", PorterStemmer.stem("важному"));
        assertEquals("важност", PorterStemmer.stem("важности"));
        assertEquals("важност", PorterStemmer.stem("важностию"));
        assertEquals("важност", PorterStemmer.stem("важность"));
        assertEquals("важност", PorterStemmer.stem("важностью"));
        assertEquals("важн", PorterStemmer.stem("важную"));
        assertEquals("важн", PorterStemmer.stem("важны"));
        assertEquals("важн", PorterStemmer.stem("важные"));
        assertEquals("важн", PorterStemmer.stem("важный"));
        assertEquals("важн", PorterStemmer.stem("важным"));
        assertEquals("важн", PorterStemmer.stem("важных"));
       assertEquals("ford", PorterStemmer.stem("ford"));
    }


}
