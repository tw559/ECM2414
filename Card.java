public class Card {
    // Card class is used to represent Card objects, which have a value and are stored in decks
    public final int value;
    public Card(int value) {

        this.value = value;
    }

    public int getValue() {

        return value;
    }

    @Override public String toString() {

        return String.valueOf(value);
    }
}