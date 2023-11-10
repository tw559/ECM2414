import java.util.*;

public class CardGame {
    public static void main(String[] args) {
        int n = 4; // Placeholder for number of players
        int totalCards = 8 * n;

        List<Deck> decks = new ArrayList<>();
        List<Player> players = new ArrayList<>();
        List<Card> remainingCards = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            decks.add(new Deck(i+1));
        }

        for (int i = 0; i < n; i++) {
            players.add(new Player(i, i));
        }

        for (int i = 0; i < 8; i++) {
            for (int j = 1; j <= n; j++) {
                remainingCards.add(new Card(j));
            }
        }
        Collections.shuffle(remainingCards);

        for (int i = 0; i < n; i++) {
            for (Player player : players) {
                Card card = remainingCards.remove(0);
                player.addCardToHand(card);
            }
        }

        int deckIndex = 0;
        for (Card card : remainingCards) {
            decks.get(deckIndex).addCardToDeck(card);
            deckIndex = (deckIndex + 1) % n;
        }



    }
}
