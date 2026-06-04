package esw.blackjack;

// encapsula a regra de parar do dealer
// permite alterar a regra sem mexer no Dealer
public interface DealerStrategy {
	boolean shouldHit(Dealer dealer);
}
