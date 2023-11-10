import java.util.*;
import java.io.*;

public class CardGame {

    public static void playGame(List<Player> players, List<Deck> decks) {
        boolean gameWon = false; // Used to check when a player has won the game

        while (!gameWon) {
            for (Player player : players) {
                /*
                Runs a check to see if any player has won at the very start of the game. If they have, returns
                "Player # wins" and finishes the game
                */
                if (player.checkWinningHand()) {
                    System.out.println("Player " + player.getPlayerNumber() + " wins");
                    gameWon = true;
                    break;
                }

                /*
                Draw a card from the left deck and discard to the right deck as a single atomic action
                Decks are synchronized to stop multiple players accessing the same deck at the same time
                Makes it thread safe (I think - Theo)
                */
                synchronized (player.getLeftDeck()) {
                    synchronized (player.getRightDeck()) {
                        Deck leftDeck = player.getLeftDeck();
                        Deck rightDeck = player.getRightDeck();

                        // Checks if the leftDeck is empty (never should be, but who knows)
                        if (!leftDeck.getCardsInDeck().isEmpty()) {
                            // Removes the 'top' card of the deck
                            Card drawnCard = leftDeck.getCardsInDeck().remove(0);
                            // Announces what the card 'drawn' is
                            System.out.println("player " + player.getPlayerNumber() + " draws a " +
                                    drawnCard.getValue() + " from deck " + leftDeck.getDeckNumber());
                            // Then adds that card to hand
                            player.addCardToHand(drawnCard);

                            // Gets the players preferred card value (which is their player number)
                            int preferredCardValue = player.getPlayerNumber();
                            /*
                            Then iterates through their hand. When it finds a card that doesn't match their preferred
                            value, it discards it to the deck to the right, and announces what card is discarded to
                            what deck
                             */
                            for (Card card : player.getHand()) {
                                if (card.getValue() != preferredCardValue) {
                                    int discardIndex = player.getHand().indexOf(card);
                                    Card discardedCard = player.getHand().remove(discardIndex);
                                    rightDeck.addCardToDeck(discardedCard);
                                    System.out.println("player " + player.getPlayerNumber() + " discards a " +
                                            discardedCard.getValue() + " to deck " + rightDeck.getDeckNumber());
                                    break;
                                }
                            }
                            // Prints the current hand
                            System.out.print("player " + player.getPlayerNumber() + " current hand is ");
                            for (Card card : player.getHand()) {
                                System.out.print(card.getValue() + " ");
                            }
                            System.out.println();

                            // Checks if the player has won after the draw and discard
                            if (player.checkWinningHand()) {
                                System.out.println("Player " + player.getPlayerNumber() + " wins");
                                gameWon = true;
                                break;
                            }
                        }
                    }
                }
            }
        }

        // Print the final hands and exit messages for each player
        for (Player player : players) {
            System.out.println("player " + player.getPlayerNumber() + " final hand: " + player.getHand());
            System.out.println("player " + player.getPlayerNumber() + " exits");
        }

        // Print the contents of each deck at the end of the game
        for (Deck deck : decks) {
            System.out.println("deck" + deck.getDeckNumber() + " contents: " + deck.getCardsInDeck());
        }
        /*
        As a note, the decks will end with one of the decks having an additional card, since the game ends after a
        draw and discard, so the next player will not have drawn a card from the deck. For example, in a 3 player game
        the end deck sizes will be 3, 4 and 5
        */
    }
    public static void main(String[] args) throws Exception{
        Scanner in = new Scanner(System.in);
        System.out.println("Please enter number of players");
        int n = Integer.parseInt(in.nextLine());
        int totalCards = 8 * n;

        List<Deck> decks = new ArrayList<>();
        List<Player> players = new ArrayList<>();
        List<Card> remainingCards = new ArrayList<>();


        for (int i = 1; i <= n; i++) {
            decks.add(new Deck(i));
        }

        for (int i = 1; i <= n; i++) {
            players.add(new Player(i));
        }

        //now assign decks to players
        for(Player player : players){
            int playerNumber = player.getPlayerNumber();
            Deck left = decks.get(playerNumber - 1);
            if (playerNumber < n) {
                Deck right = decks.get(playerNumber);
                player.setDecks(left, right);
            } else {
                Deck right = decks.get(0);
                player.setDecks(left, right);
            }
        }

        /*testing that assigning decks works
        for (Player p : players) {
            System.out.println("player: " + p.getPlayerNumber());
            System.out.println("left deck: " + p.getLeftDeck().getDeckNumber());
            System.out.println("right deck: " + p.getRightDeck().getDeckNumber());
        }*/

        System.out.println("Please enter location of pack to load");
        String file = in.nextLine();    
        in.close();    
        try {
            remainingCards = attemptToReadPackFile(file,totalCards);
        } catch(Exception e) {
            System.out.println("Invalid pack file");
        }

        //testing getting cards from file works
        /*for (Card c : remainingCards) {
            System.out.println(c.getValue());
        }*/

        Collections.shuffle(remainingCards);

        for (int i = 0; i < 4; i++) { //always four cards in a hand
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
    

        //testing players have been allocated hands
        for (Player p : players) {
            for (Card c : p.getHand()) {
                System.out.println("player"+p.getPlayerNumber()+ "'s hand: " + c.getValue());
            }
        }

        //testing deck allocation
        for (Deck deck : decks) {
            for (Card card : deck.getCardsInDeck()) {
                System.out.println("deck"+deck.getDeckNumber()+ ": " + card.getValue());
            }
        }

        //now to actually start the player threads
        for (Player player : players) {
            Thread playerThread = new Thread(player);
            playerThread.start();
        }

        // Runs the game
        playGame(players, decks);

        /*
        for (Player player : players) {
            System.out.println(player.getHand());
            System.out.println(player.checkWinningHand());
        }
        */
    }

    public static ArrayList<Card> attemptToReadPackFile(String file, int cardNum) throws Exception {
        ArrayList<Card> listOfCardsFromFile = new ArrayList<>();
        int denomination;
        BufferedReader fileIn = new BufferedReader(new FileReader(file));
        for (int i = 0; i < cardNum; i++) {
            denomination = Integer.parseInt(fileIn.readLine());
            listOfCardsFromFile.add(new Card(denomination));
        }
        fileIn.close();
        return listOfCardsFromFile;
    }


}
