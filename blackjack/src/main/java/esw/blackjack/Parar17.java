package esw.blackjack;

// Dealer para de pedir cartas no 17
public class Parar17 implements DealerStrategy {
	@Override
	public boolean shouldHit(Dealer dealer) {
		return dealer.sum() < 17;
	}
}
