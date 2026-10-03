import java.util.*;
public record PokerValue(Category category, List<Integer> tieBreak) implements Comparable<PokerValue> {
    public enum Category {
        HIGH_CARD, ONE_PAIR, TWO_PAIR, THREE_OF_A_KIND, STRAIGHT,
        FLUSH, FULL_HOUSE, FOUR_OF_A_KIND, STRAIGHT_FLUSH
    }
    public PokerValue {
        Objects.requireNonNull(category);
        tieBreak = List.copyOf(tieBreak);
    }
    static PokerValue evaluate(List<Card> cards) {
        if (cards.size() != 5)
            throw new IllegalStateException("Poker evaluation requires exactly 5 cards; got " + cards.size());
        int[] counts = new int[15];
        for (Card card : cards) counts[card.rank()]++;
        List<Integer> ranks = new ArrayList<>();
        for (int rank = 14; rank >= 2; rank--) if (counts[rank] > 0) ranks.add(rank);
        boolean flush = cards.stream().allMatch(c -> c.suit() == cards.get(0).suit());
        int straightHigh = 0;
        if (ranks.size() == 5) {
            if (ranks.get(0) - ranks.get(4) == 4) straightHigh = ranks.get(0);
            else if (ranks.equals(List.of(14, 5, 4, 3, 2))) straightHigh = 5;
        }
        List<Integer> groups = new ArrayList<>(ranks);
        groups.sort(Comparator.<Integer>comparingInt(r -> counts[r]).reversed()
                .thenComparing(Comparator.reverseOrder()));
        int first = counts[groups.get(0)], second = counts[groups.get(1)];
        if (flush && straightHigh > 0) return value(Category.STRAIGHT_FLUSH, straightHigh);
        if (first == 4) return new PokerValue(Category.FOUR_OF_A_KIND, groups);
        if (first == 3 && second == 2) return new PokerValue(Category.FULL_HOUSE, groups);
        if (flush) return new PokerValue(Category.FLUSH, ranks);
        if (straightHigh > 0) return value(Category.STRAIGHT, straightHigh);
        if (first == 3) return new PokerValue(Category.THREE_OF_A_KIND, groups);
        if (first == 2 && second == 2) return new PokerValue(Category.TWO_PAIR, groups);
        if (first == 2) return new PokerValue(Category.ONE_PAIR, groups);
        return new PokerValue(Category.HIGH_CARD, ranks);
    }
    private static PokerValue value(Category category, int rank) {
        return new PokerValue(category, List.of(rank));
    }
    @Override public int compareTo(PokerValue other) {
        int result = category.compareTo(other.category);
        if (result != 0) return result;
        for (int i = 0; i < Math.min(tieBreak.size(), other.tieBreak.size()); i++) {
            result = Integer.compare(tieBreak.get(i), other.tieBreak.get(i));
            if (result != 0) return result;
        }
        return Integer.compare(tieBreak.size(), other.tieBreak.size());
    }
    @Override public String toString() { return category + " " + tieBreak; }
}
