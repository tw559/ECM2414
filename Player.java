import java.util.*;

public class Player implements Runnable{
    private int playerNumber; //I don't think playernumber and preferredcardalue ought to be separate attributes
    public ArrayList<Card> hand;
    Deck leftDeck;
    Deck rightDeck; //having these be attributes of the player makes them easier to access


    public Player(int playerNumber) {
        this.playerNumber = playerNumber;
        this.hand = new ArrayList<>();
    }

    public void addCardToHand(Card card) {
        hand.add(card);
    }

    public void playCard() {
        /**I do not entirely understand what this method is doing
         * it seems to only discard a card but not take one
         * but the spec says the combination of draw and discard has to be a single atomic action
         */

        /**
         * Yeah, it's just a bit of code I copied over. It's largely redundant now, so it can be ignored.
         */
        int handIndex = 0;
        Card discardedCard = hand.get(handIndex);
        if (discardedCard.getValue() == playerNumber) {
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

    public void setDecks(Deck left, Deck right) {
        leftDeck = left;
        rightDeck = right;
    }

    public Deck getLeftDeck() {
        return leftDeck;
    }

    public Deck getRightDeck() {
        return rightDeck;
    }

    public int getPlayerNumber() {
        return (playerNumber);
    }

    public ArrayList<Card> getHand() {
        return hand;
    }

    /*
    So fixed this. It basically just iterates through the hand, checking every card against the first one, since for a
    list to contain all the same elements, everything must be equal to the first element. If it ever finds something
    that isn't the same, it returns false
     */
    public boolean checkWinningHand() {
        for (Card card : hand) {
            int cardValue = card.getValue();
            if (cardValue != hand.get(0).getValue())
                return false;
        }
        return true;
    }




    public void run() {
        if (checkWinningHand()) {
            System.out.println("Player " + playerNumber + " wins.");
        }
        //TODO: need to implement rest of run-time functionality for players i.e. drawing and discarding cards,
        // writing to files, triggering game end status when winning

    }
}
