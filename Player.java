import java.io.*;
import java.util.*;

public class Player implements Runnable{
    //runnable player class that takes part in the game
    private int playerNumber;
    public ArrayList<Card> hand;
    Deck leftDeck;
    Deck rightDeck; 
    static boolean gameWon = false; 
    static int winningPlayerNumber = 0;


    public Player(int playerNumber) {
        this.playerNumber = playerNumber;
        this.hand = new ArrayList<>();
    }

    public void addCardToHand(Card card) {
        hand.add(card);
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
    Iterates through the hand, checking every card against the first one, since for a
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
        try {
            playGame();
        } catch (IOException anIOException) {
            anIOException.printStackTrace();
        }
    }

    private void playGame() throws IOException {
        //method which handles drawing and discarding cards as well as ending the game when hand is winning 
        String playerStr = ("player"+playerNumber+"_output.txt");
        BufferedWriter writer = new BufferedWriter(new FileWriter(playerStr));
        writer.write("player "+playerNumber +" initial hand: "+hand+"\n");
        /**Checks if the game has been won by another player
         * As it only does this between a combined draw and discard action this action is atomic
         * and will not be interrupted when another player wins
         */
        while (!gameWon) {
            
                /*
                Runs a check to see if any player has won at the very start of the game. If they have, returns
                "Player # wins" and finishes the game
                */
                if (checkWinningHand()) {
                    System.out.println("Player " + playerNumber + " wins");
                    writer.write("Player " + playerNumber + " wins\n");
                    winningPlayerNumber = playerNumber;//so other players know which player has won
                    gameWon = true;
                    break;
                }

                //to prevent attempts to access an empty deck
                if (leftDeck.deckSize() == 0) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    //calls the synchronized drawAndDiscard method
                    drawAndDiscard(writer);
                }
            
                
        }
        

        //if player hasn't won but loop has ended game must be over
        if (!checkWinningHand()) {
            writer.write("Player " + winningPlayerNumber + " has informed Player " + playerNumber + " that Player " +winningPlayerNumber+" has won.\n");
        }
        writer.write("Player " + playerNumber + " exits\n");
        writer.write("Player " + playerNumber + " final hand: " + hand + "\n");
        
        
        endGame(writer);
        

    }


   
    private synchronized void drawAndDiscard(BufferedWriter writer) throws IOException {
        // Removes the 'top' card of the deck
        Card drawnCard = leftDeck.getCardsInDeck().remove(0);
        // Announces what the card 'drawn' is
        //System.out.println("player " + playerNumber + " draws a " + drawnCard.getValue() + " from deck " + leftDeck.getDeckNumber());
        writer.write("Player " + playerNumber + " draws a " + drawnCard.getValue() + " from deck " + leftDeck.getDeckNumber() + "\n");
        // Then adds that card to hand
        addCardToHand(drawnCard);

        // Gets the players preferred card value (which is their player number)
        int preferredCardValue = playerNumber;
        /*
        Then iterates through their hand. When it finds a card that doesn't match their preferred
        value, it discards it to the deck to the right, and announces what card is discarded to
        what deck
        */
        boolean hasDiscarded = false;
        for (Card card : hand) {
            if (card.getValue() != preferredCardValue) {
                int discardIndex = hand.indexOf(card);
                Card discardedCard = hand.remove(discardIndex);
                rightDeck.addCardToDeck(discardedCard);
                hasDiscarded = true;
                writer.write("Player " + playerNumber + " discards a " + discardedCard.getValue() + " to deck " + rightDeck.getDeckNumber()+"\n");
                break;
            }
        }
        //to prevent a player ending up with 5 cards of their preferred denomination
        if (!hasDiscarded) {
            int discardIndex = 0;
            Card discardedCard = hand.remove(discardIndex);
            rightDeck.addCardToDeck(discardedCard); 
        }

        
        writer.write("player " + playerNumber + " current hand is " + hand + "\n");
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void endGame(BufferedWriter writer) {
        try {
            leftDeck.endGame();//if each player tells their left deck to end this will account for all decks
            writer.close(); //finally closes the writer
        } catch(IOException anIOException) {
            anIOException.printStackTrace();//should never happen
        }
        
    }
}
