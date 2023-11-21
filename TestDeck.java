import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.*;

public class TestDeck {

    private static Deck deck;

    public TestDeck () {}

    @BeforeClass
    public static void setUpClass () throws Exception {}

    @AfterClass
    public static void tearDownClass () throws Exception {}

    @Test
    public void testEmptyDeck () {
        deck = new Deck(7);
        assertEquals(deck.drawCard(),null);
    }

    @Test
    public void testAddingToDeck() {
        deck = new Deck(7);
        deck.addCardToDeck(new Card(7));
        assertEquals(deck.getCardsInDeck().get(0).getValue(),7);
    }

    @Test
    public void testRemovingFromDeck() {
        deck = new Deck(7);
        int deckSize = deck.getCardsInDeck().size();
        deck.addCardToDeck(new Card(7));
        assertNotEquals(deckSize,deck.getCardsInDeck().size());
        Card drawnCard = deck.drawCard();
        assertEquals(deckSize,deck.getCardsInDeck().size());
        assertEquals(7,drawnCard.getValue());
    }

    @Test
    public void testFile() {
        deck = new Deck(7);
        try{
            deck.addCardToDeck(new Card(19));
            deck.endGame();
            int deckNumber = deck.getDeckNumber();
            String fileStr = ("deck" + deckNumber + "_output.txt");
            BufferedReader reader = new BufferedReader(new FileReader(fileStr));
            String line = reader.readLine();
            reader.close();
            String expectedLine = ("Deck " + deckNumber + " contents at end of game: " + deck.getCardsInDeck());
            assertEquals(expectedLine,line);
        } catch (IOException ex) {
            assertFalse(true);
        }
    }

}