import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestPlayer {

    static Player player;

    public TestPlayer () {}

    @BeforeClass
    public static void setUpClass () throws Exception {
        player = new Player(19);
    }

    @AfterClass
    public static void tearDownClass () throws Exception {}

    @Test
    public void testPlayerCreation () {
        assertEquals(player.getPlayerNumber(),19);
    }

    @Test
    public void testDecks() {
        Deck leftDeck = new Deck(1);
        Deck rightDeck = new Deck(2);
        player.setDecks(leftDeck, rightDeck);
        assertEquals(player.getLeftDeck(),leftDeck);
        assertEquals(player.getRightDeck(), rightDeck);
    }

    @Test 
    public void testHand() {
        for (int i = 0;i<4;i++) {
            player.addCardToHand(new Card(1));
        }
        assertTrue(player.checkWinningHand());
        assertEquals(player.getHandSize(),4);
    }

}