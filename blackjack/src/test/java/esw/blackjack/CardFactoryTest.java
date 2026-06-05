package esw.blackjack;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

// Cobre o padrao Factory Method: o Deck cria cartas atraves de uma fabrica.
class CardFactoryTest {

	// Concrete creator de teste que conta quantas vezes o metodo-fabrica corre.
	private static class CountingFactory extends CardFactory {
		int created = 0;

		@Override
		public Card createCard(String value, String type) {
			created++;
			return new Card(value, type);
		}
	}

	@Test
	void standardFactoryBuildsTheRequestedCard() {
		Card card = new StandardCardFactory().createCard("A", "Spades");
		assertEquals("A", card.value);
		assertEquals("Spades", card.type);
		assertEquals(11, card.getValue());
		assertEquals("cardSpadesA", card.toString());
	}

	@Test
	void deckCreatesAll52CardsThroughTheFactory() {
		CountingFactory factory = new CountingFactory();
		Deck deck = new Deck(factory);
		assertEquals(52, factory.created); // the factory method ran once per card
		assertEquals(52, deck.getSize());
	}

	@Test
	void deckUsesWhateverTheFactoryProduces() {
		// fabrica que ignora os argumentos e devolve sempre o As de Espadas
		CardFactory allAces = new CardFactory() {
			@Override
			public Card createCard(String value, String type) {
				return new Card("A", "Spades");
			}
		};
		Deck deck = new Deck(allAces);
		Card drawn = deck.drawCard();
		assertEquals("cardSpadesA", drawn.toString());
		assertEquals(11, drawn.getValue());
	}
}
