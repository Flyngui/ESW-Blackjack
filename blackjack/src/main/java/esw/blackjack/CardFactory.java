package esw.blackjack;

// FACTORY METHOD: o "creator". Declara o metodo-fabrica createCard, deixando
// as subclasses decidir que tipo concreto de Card e instanciado.
// 
public abstract class CardFactory {
	public abstract Card createCard(String value, String type);
}
