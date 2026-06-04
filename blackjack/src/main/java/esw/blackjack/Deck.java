package esw.blackjack;

import java.util.ArrayList;
import java.util.Random;

public class Deck {
    private ArrayList<Card> cards;
    private Random random;
    // ao usar o factory method o Deck nao está preso à classe Carta
    // seria possivel criar um deck com cartas de outro tipo
    private CardFactory cardFactory;

    public Deck() {
        this(new StandardCardFactory());
    }

    public Deck(CardFactory cardFactory) {
        this.cardFactory = cardFactory;
        this.cards = new ArrayList<Card>();
        this.random = new Random();
        buildDeck();
        shuffleDeck();
    }

    private void buildDeck() {
        String[] values = { "A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K" };
        String[] types = { "Clubs", "Diamonds", "Hearts", "Spades" };

        for (String type : types) {
            for (String value : values) {
                cards.add(cardFactory.createCard(value, type));
            }
        }
    }

    public void shuffleDeck() {
        for (int i = 0; i < cards.size(); i++) {
            int j = random.nextInt(cards.size());
            Card currCard = cards.get(i);
            Card randomCard = cards.get(j);
            cards.set(i, randomCard);
            cards.set(j, currCard);
        }
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