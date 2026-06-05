package esw.blackjack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

class DeckTest {

	@Test
	void freshDeckHas52Cards() {
		assertEquals(52, new Deck().getSize());
	}

	@Test
	void drawingReducesTheSize() {
		Deck deck = new Deck();
		deck.drawCard();
		assertEquals(51, deck.getSize());
	}

	@Test
	void drawingAllCardsThenReturnsNull() {
		Deck deck = new Deck();
		for (int i = 0; i < 52; i++) {
			assertNotNull(deck.drawCard());
		}
		assertEquals(0, deck.getSize());
		assertNull(deck.drawCard());
	}

	@Test
	void deckContains52DistinctCards() {
		Deck deck = new Deck();
		Set<String> seen = new HashSet<>();
		Card card;
		while ((card = deck.drawCard()) != null) {
			seen.add(card.toString());
		}
		assertEquals(52, seen.size());
	}

	@Test
	void shufflingKeepsAllCards() {
		Deck deck = new Deck();
		deck.shuffleDeck();
		assertEquals(52, deck.getSize());

		Set<String> seen = new HashSet<>();
		Card card;
		while ((card = deck.drawCard()) != null) {
			seen.add(card.toString());
		}
		assertEquals(52, seen.size());
	}
}
