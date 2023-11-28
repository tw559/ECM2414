import java.util.*;
import java.io.*; 

public class Deck {
    // Deck class is used to represent lists of cards, which forms decks for players to draw from

    private ArrayList<Card> cards;
    private int deckNumber;

    public synchronized void shuffle() {
        Collections.shuffle(cards);
    }

    public Deck(int deckNumber) {
        this.cards = new ArrayList<>();
        this.deckNumber = deckNumber;
    }

    public synchronized Card drawCard() {
        if (!cards.isEmpty()) {
            return cards.remove(0);
        }
        // Should never be called, but if a deck is empty a 'null' value with enter the decks or hand of a player
        return null;
    }

    public synchronized void addCardToDeck(Card card) {
        cards.add(card);
    }

    public synchronized int deckSize() {
        return cards.size();
    }

    public int getDeckNumber() {
        return deckNumber;
    }

    public ArrayList<Card> getCardsInDeck() {
        return cards;
    }
    // Never called except for in testing
    public void printDeck() {
        System.out.println(cards);
    }

    // Outputs a file at the end of the game for each deck detailing their contents
    public void endGame() throws IOException {
        String deckStr = ("deck" + deckNumber + "_output.txt");
        BufferedWriter writer = new BufferedWriter(new FileWriter(deckStr));
        writer.write("Deck " + deckNumber + " contents at end of game: " + cards);
        writer.close();

    }
}
