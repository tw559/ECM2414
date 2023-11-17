import java.util.*;
import java.io.*;

public class CardGame {
    //executable class for running the game
    public static void main(String[] args) throws Exception{

        Scanner input = new Scanner(System.in);
        int n;
        //gets a numerical input from user for number of players
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

        //creating players and decks
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
            } else {//need to assign the first deck to the last player to maintain ring topology
                Deck right = decks.get(0);
                player.setDecks(left, right);
            }
        }

        //loops until user gives a valid pack file
        boolean packRead = false;
        while (!packRead) {
            System.out.println("Please enter the location of the pack to load (pack should be a plain text file\nand contain *exactly* 8n entries where n is the number of players): ");
            String file = input.nextLine();
            try {
                remainingCards = attemptToReadPackFile(file, totalCards); //tries to read the file
                packRead = true;
            } catch (Exception e) {
                //if file is invalid, attemptToReadPackFile should throw an exception
                System.out.println("Can't find a valid pack file with that name");
            }
        }
        input.close();

        Collections.shuffle(remainingCards);

        for (int i = 0; i < 4; i++) { //always four cards in a hand
            for (Player player : players) {
                Card card = remainingCards.remove(0);
                player.addCardToHand(card);
            }
        }

        //adds the remaining cards to the decks
        int deckIndex = 0;
        for (Card card : remainingCards) {
            decks.get(deckIndex).addCardToDeck(card);
            deckIndex = (deckIndex + 1) % n;
        }

        //starts the player threads
        for (Player player : players) {
            Thread playerThread = new Thread(player);
            playerThread.start();
        }
        


        

    }

    //method to take a file and a number of cards and attempt to create a list of cards from that file
    public static ArrayList<Card> attemptToReadPackFile(String file, int cardNum) throws Exception {
        ArrayList<Card> listOfCardsFromFile = new ArrayList<>();
        int denomination;
        BufferedReader fileIn = new BufferedReader(new FileReader(file));
        for (int i = 0; i < cardNum; i++) {
            denomination = Integer.parseInt(fileIn.readLine());
            listOfCardsFromFile.add(new Card(denomination));//creating card objects from lines in file
        }
        if (fileIn.readLine() != null) { //if there are more entries, pack file was too large
            fileIn.close();
            throw new Exception();
        }
        fileIn.close();
        return listOfCardsFromFile;
    }


}
