public class Card {
    //card class that represents a card object with a denomination/value
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