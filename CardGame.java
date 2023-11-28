import java.util.*;
import java.io.*;

public class CardGame {
    // Executable class for running the game
    public static void main(String[] args) throws Exception{

        Scanner input = new Scanner(System.in);
        int n;
        // Gets a numerical input from user for number of players
        do {
            System.out.println("Please enter number of players (number should be a positive integer): ");
            while (!input.hasNextInt()) {
                System.out.println("That's not a positive integer");
                System.out.println("Please enter number of players (number should be a positive integer): ");
                input.next();
            }
            n = input.nextInt();
            input.nextLine();
        } while (n <= 0);
        int totalCards = 8 * n;

        List<Deck> decks = new ArrayList<>();
        List<Player> players = new ArrayList<>();
        List<Card> remainingCards = new ArrayList<>();

        // Creates n decks and players
        for (int i = 1; i <= n; i++) {
            decks.add(new Deck(i));
        }

        for (int i = 1; i <= n; i++) {
            players.add(new Player(i));
        }

        // Assigns decks to players
        for(Player player : players){
            int playerNumber = player.getPlayerNumber();
            Deck left = decks.get(playerNumber - 1);
            if (playerNumber < n) {
                Deck right = decks.get(playerNumber);
                player.setDecks(left, right);
            } else { // Assigns the first deck to the last player to maintain ring topology
                Deck right = decks.get(0);
                player.setDecks(left, right);
            }
        }

        // Loops until the user gives a valid pack file (one containing 8n entries that are positive integers)
        boolean packRead = false;
        while (!packRead) {
            System.out.println("Please enter the location of the pack to load (pack should be a plain text file\n" +
                    "and contain exactly 8n entries where n is the number of players and each entry is a positive " +
                    "integer): ");
            String file = input.nextLine();
            try {
                remainingCards = attemptToReadPackFile(file, totalCards); // Tries to read the file
                packRead = true;
            } catch (Exception e) {
                // If file is invalid, attemptToReadPackFile should throw an exception
                System.out.println("Can't find a valid pack file with that name");
            }
        }
        input.close();

        Collections.shuffle(remainingCards);

        for (int i = 0; i < 4; i++) { // So there are always four cards in a hand
            for (Player player : players) {
                Card card = remainingCards.remove(0);
                player.addCardToHand(card);
            }
        }

        // Adds the remaining cards to the decks in a round-robin fashion
        int deckIndex = 0;
        for (Card card : remainingCards) {
            decks.get(deckIndex).addCardToDeck(card);
            deckIndex = (deckIndex + 1) % n;
        }

        // Starts the player threads
        for (Player player : players) {
            Thread playerThread = new Thread(player);
            playerThread.start();
        }
        


        

    }

    // Method to take a file and a number of cards and attempt to create a list of cards from that file
    public static ArrayList<Card> attemptToReadPackFile(String file, int cardNum) throws Exception {
        ArrayList<Card> listOfCardsFromFile = new ArrayList<>();
        int denomination;
        BufferedReader fileIn = new BufferedReader(new FileReader(file));
        for (int i = 0; i < cardNum; i++) {
            denomination = Integer.parseInt(fileIn.readLine());
            listOfCardsFromFile.add(new Card(denomination)); // Creates card objects from lines in a file
        }
        if (fileIn.readLine() != null) { // If there are more entries, pack file was too large
            fileIn.close();
            throw new Exception();
        }
        fileIn.close();
        return listOfCardsFromFile;
    }


}
