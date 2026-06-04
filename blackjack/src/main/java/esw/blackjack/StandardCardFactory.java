package esw.blackjack;

// Concrete creator para as cartas normais de um baralho
public class StandardCardFactory extends CardFactory {
	@Override
	public Card createCard(String value, String type) {
		return new Card(value, type);
	}
}
