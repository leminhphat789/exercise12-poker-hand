import java.util.*;
public final class Hand implements Iterable<Card> {
    private final int capacity;
    private final List<Card> cards = new ArrayList<>();
    public Hand(int capacity) {
        if (capacity < 1) throw new IllegalArgumentException("Capacity must be positive");
        this.capacity = capacity;
    }
    public void add(Card card) {
        Objects.requireNonNull(card, "card");
        if (cards.contains(card)) throw new IllegalArgumentException("Duplicate card: " + card);
        if (isFull()) throw new IllegalStateException("Hand is full");
        cards.add(card);
    }
    public boolean remove(Card card) { return cards.remove(Objects.requireNonNull(card)); }
    public boolean contains(Card card) { return cards.contains(Objects.requireNonNull(card)); }
    public int size() { return cards.size(); }
    public boolean isEmpty() { return cards.isEmpty(); }
    public boolean isFull() { return size() == capacity; }
    @Override public Iterator<Card> iterator() { return List.copyOf(cards).iterator(); }
    public PokerValue pokerValue() { return PokerValue.evaluate(cards); }
    public static Comparator<Hand> byPokerStrength() {
        return Comparator.comparing(Hand::pokerValue);
    }
    public static Hand of(String text) {
        Hand hand = new Hand(5);
        if (!text.isBlank()) for (String token : text.trim().split("\\s+")) hand.add(Card.parse(token));
        return hand;
    }
    @Override public String toString() { return cards.toString(); }
}
