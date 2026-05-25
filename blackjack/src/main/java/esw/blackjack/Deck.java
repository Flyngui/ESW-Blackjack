package esw.blackjack;

import java.util.ArrayList;
import java.util.Random;

public class Deck {
    private ArrayList<Card> cards;
    private Random random;

    public Deck() {
        this.cards = new ArrayList<Card>();
        this.random = new Random();
        buildDeck();
        shuffleDeck();
    }

    
    private void buildDeck() {
        String[] values = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
        String[] types = {"C", "D", "H", "S"};

        for (String type : types) {
            for (String value : values) {
                cards.add(new Card(value, type));
            }
        }
        System.out.println("BUILD DECK:");
        System.out.println(cards);
    }

    public void shuffleDeck() {
        for (int i = 0; i < cards.size(); i++) {
            int j = random.nextInt(cards.size());
            Card currCard = cards.get(i);
            Card randomCard = cards.get(j);
            cards.set(i, randomCard);
            cards.set(j, currCard);
        }
        System.out.println("AFTER SHUFFLE");
        System.out.println(cards);
    }

    public Card drawCard() {
        if (cards.isEmpty()) {
            return null; 
        }
        return cards.remove(cards.size() - 1);
    }

    public int getSize() {
        return cards.size();
    }
}