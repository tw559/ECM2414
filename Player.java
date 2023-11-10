import java.util.*;

public class Player {
    private int playerNumber;
    private List<Card> hand;
    private int preferredCardValue;


    public Player(int playerNumber, int preferredCardValue) {
        this.playerNumber = playerNumber;
        this.hand = new ArrayList<>();
        this.preferredCardValue = preferredCardValue;
    }

    public void addCardToHand(Card card) {
        hand.add(card);
    }

    public void playCard(Deck rightDeck) {
        int handIndex = 0;
        Card discardedCard = hand.get(handIndex);
        if (discardedCard.getValue() == preferredCardValue) {
            handIndex = (handIndex + 1) % 4;
            discardedCard = hand.get(handIndex);
        }
        else {
            rightDeck.addCardToDeck(hand.remove(handIndex));
        }
    }

    public synchronized int getHandSize() {
        return hand.size();
    }
}
