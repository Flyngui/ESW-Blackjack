package esw.blackjack;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

// Cobre o padrao Strategy: a regra de pedir do dealer e intercambiavel.
class StrategyTest {

	private Dealer dealerWithTotal(Card a, Card b) {
		Dealer dealer = new Dealer();
		dealer.hit(a);
		dealer.hit(b);
		return dealer;
	}

	@Test
	void parar17DrawsBelow17() {
		Dealer dealer = dealerWithTotal(new Card("9", "Hearts"), new Card("7", "Clubs")); // 16
		assertTrue(new Parar17().shouldHit(dealer));
	}

	@Test
	void parar17StandsAt17() {
		Dealer dealer = dealerWithTotal(new Card("10", "Hearts"), new Card("7", "Clubs")); // 17
		assertFalse(new Parar17().shouldHit(dealer));
	}

	@Test
	void dealerDelegatesToTheInjectedStrategy() {
		DealerStrategy alwaysHit = d -> true;
		Dealer dealer = new Dealer(alwaysHit);
		dealer.hit(new Card("K", "Hearts"));
		dealer.hit(new Card("K", "Clubs")); // 20 — default would stand
		assertTrue(dealer.shouldHit()); // but the injected strategy says hit
	}

	@Test
	void setStrategySwapsTheBehaviourAtRuntime() {
		Dealer dealer = new Dealer();
		dealer.hit(new Card("10", "Hearts"));
		dealer.hit(new Card("6", "Clubs")); // 16 — default Parar17 draws
		assertTrue(dealer.shouldHit());

		dealer.setStrategy(d -> false); // never-hit strategy
		assertFalse(dealer.shouldHit());
	}
}
