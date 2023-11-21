import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestCard {

    public TestCard () {}

    @BeforeClass
    public static void setUpClass () throws Exception {}

    @AfterClass
    public static void tearDownClass () throws Exception {}

    @Test
    public void testCard () {
        Card card = new Card(7);
        assertEquals(card.getValue(),7);
        assertEquals(card.toString(),"7");
    }

}