import java.util.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.*;
import javax.swing.*;
import javax.imageio.ImageIO;

public final class Main {
    private static int checks;
    private static final StringBuilder LOG = new StringBuilder();
    private static void log(String text) { LOG.append(text).append('\n'); System.out.println(text); }
    private static void check(boolean condition, String name) {
        if (!condition) throw new AssertionError(name);
        checks++;
    }
    private static void rejects(Class<? extends Throwable> type, Runnable operation) {
        try { operation.run(); } catch (Throwable ex) {
            check(type.isInstance(ex), "Expected " + type + ", got " + ex); return;
        }
        throw new AssertionError("Expected " + type.getSimpleName());
    }
    private static int compare(String left, String right) {
        return Hand.byPokerStrength().compare(Hand.of(left), Hand.of(right));
    }
    public static void main(String[] args) throws Exception {
        log("EXERCISE 12 - CHAPTER 3 | FIVE-CARD POKER");
        log("Java " + System.getProperty("java.version") + " | " + System.getProperty("os.name"));
        log("Run time: " + java.time.ZonedDateTime.now(java.time.ZoneId.of("Asia/Ho_Chi_Minh")));
        log("");
        String[] examples = {"AS KD 9H 6C 3S", "AS AD 9H 6C 3S", "AS AD 9H 9C 3S",
            "AS AD AH 6C 3S", "2S 3D 4H 5C 6S", "AS JS 9S 6S 3S",
            "AS AD AH 6C 6S", "AS AD AH AC 3S", "TS JS QS KS AS"};
        for (int i = 0; i < examples.length; i++) {
            Hand hand = Hand.of(examples[i]);
            check(hand.pokerValue().category().ordinal() == i, "Category " + i);
            if (i > 0) check(compare(examples[i], examples[i - 1]) > 0, "Category order");
            log(String.format("%-17s -> %s", examples[i], hand.pokerValue()));
        }
        log("\nTIE BREAKS AND ACE RULES");
        String[][] wins = {
            {"2S 3D 4H 5C 6S", "AS 2D 3H 4C 5S"},
            {"TS JD QH KC AS", "9S TD JH QC KS"},
            {"AS AD KH 8C 3S", "AC AH QH 8D 3C"},
            {"KS KD 2H 2C 3S", "QS QD JH JC AS"},
            {"KS KD QH QC AS", "KC KH JD JC AH"},
            {"KS KD QH QC AS", "KC KH QD QS JH"},
            {"KS KD KH 2C 2S", "QS QD QH AC AS"},
            {"KS KD KH AC AS", "KC KD KS QC QS"},
            {"AS AD AH AC KS", "AS AD AH AC QS"},
            {"KS KD KH 8C 3S", "QS QD QH AC JS"},
            {"KS KD KH AC 3S", "KC KD KS QC JS"},
            {"AS JS 9S 6S 4S", "AH JH 9H 6H 3H"},
            {"AS JD 9H 6C 4S", "AH JC 9D 6H 3H"},
            {"3S 4S 5S 6S 7S", "2H 3H 4H 5H 6H"}
        };
        for (String[] pair : wins) {
            check(compare(pair[0], pair[1]) > 0, "Tie break " + Arrays.toString(pair));
            check(compare(pair[1], pair[0]) < 0, "Reverse comparison");
        }
        check(compare("AS KD 9H 6C 3S", "AH KC 9D 6S 3H") == 0, "Suit does not break ties");
        check(compare("AS KD 9H 6C 3S", "3S 6C 9H KD AS") == 0, "Order independence");
        check(Hand.of("QS KD AH 2C 3S").pokerValue().category() == PokerValue.Category.HIGH_CARD, "No wraparound");
        log("PASS: 14 winner pairs, reverse comparisons, suit ties, card order");
        log("PASS: wheel A2345 < 23456; TJQKA > 9TJQK; QKA23 is not straight");
        log("\nCONTRACTS AND ENCAPSULATION");
        for (int size = 0; size <= 7; size++) {
            if (size == 5) continue;
            Hand hand = new Hand(7);
            for (int i = 0; i < size; i++) hand.add(new Card(i + 2, 'S'));
            rejects(IllegalStateException.class, hand::pokerValue);
        }
        Hand hand = Hand.of("AS KD 9H 6C 3S");
        rejects(IllegalArgumentException.class, () -> hand.add(Card.parse("AS")));
        rejects(IllegalStateException.class, () -> hand.add(Card.parse("2S")));
        rejects(NullPointerException.class, () -> hand.add(null));
        rejects(IllegalArgumentException.class, () -> Card.parse("XS"));
        rejects(IllegalArgumentException.class, () -> new Hand(0));
        Iterator<Card> iterator = hand.iterator(); iterator.next();
        rejects(UnsupportedOperationException.class, iterator::remove);
        rejects(UnsupportedOperationException.class, () -> hand.pokerValue().tieBreak().clear());
        check(hand.isFull() && hand.size() == 5 && hand.contains(Card.parse("AS")), "Queries");
        check(hand.remove(Card.parse("AS")) && hand.size() == 4, "Remove");
        rejects(IllegalStateException.class, () -> Hand.byPokerStrength().compare(hand, Hand.of(examples[0])));
        log("PASS: reject sizes 0-4, 6, 7; duplicates, overflow, null, bad card");
        log("PASS: immutable iterator/value; add/remove/query; invalid comparison");
        log("\nRESULT: " + checks + " checks passed, 0 failed.");
        Files.createDirectories(Path.of("ảnh thực tế"));
        Files.writeString(Path.of("ảnh thực tế/run-log.txt"), LOG);
        if (Arrays.asList(args).contains("--gui")) {
            JFrame[] window = new JFrame[1];
            SwingUtilities.invokeAndWait(() -> {
                JFrame frame = new JFrame("Exercise 12 | Actual Java execution results");
                JTextArea area = new JTextArea(LOG.toString());
                area.setEditable(false); area.setFont(new Font("Consolas", Font.PLAIN, 17));
                area.setBackground(new Color(20, 27, 39)); area.setForeground(new Color(229, 237, 247));
                area.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));
                frame.add(new JScrollPane(area)); frame.setSize(1080, 820);
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.setLocationRelativeTo(null); frame.setVisible(true); window[0] = frame;
            });
            Thread.sleep(1800);
            Rectangle bounds = window[0].getBounds();
            BufferedImage shot = new Robot().createScreenCapture(bounds);
            ImageIO.write(shot, "png", Path.of("ảnh thực tế/screenshot.png").toFile());
            SwingUtilities.invokeAndWait(() -> window[0].dispose());
        }
    }
}
