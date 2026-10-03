import java.util.Objects;
public record Card(int rank, char suit) {
    public Card {
        if (rank < 2 || rank > 14 || "CDHS".indexOf(suit) < 0)
            throw new IllegalArgumentException("Invalid card");
    }
    public static Card parse(String token) {
        Objects.requireNonNull(token);
        if (token.length() != 2) throw new IllegalArgumentException("Use cards such as AS or TH");
        return new Card("23456789TJQKA".indexOf(token.charAt(0)) + 2, token.charAt(1));
    }
    @Override public String toString() {
        return "" + "23456789TJQKA".charAt(rank - 2) + suit;
    }
}
