package esw.blackjack;

// Declara o factory method createCard, deixando
// as subclasses decidir que tipo concreto de Card e instanciado.
public abstract class CardFactory {
	public abstract Card createCard(String value, String type);
}
