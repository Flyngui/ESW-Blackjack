package esw.blackjack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DealerTest {

	@Test
	void newDealerHidesItsHoleCard() {
		assertTrue(new Dealer().getHiddenCard());
	}

	@Test
	void revealUnhidesTheHoleCard() {
		Dealer dealer = new Dealer();
		dealer.reveal();
		assertFalse(dealer.getHiddenCard());
	}

	@Test
	void upCardIsTheSecondCardDealt() {
		Dealer dealer = new Dealer();
		Card hole = new Card("K", "Hearts");
		Card up = new Card("7", "Clubs");
		dealer.hit(hole);
		dealer.hit(up);
		assertSame(up, dealer.getUpCard());
	}

	@Test
	void visibleSumHidesTheHoleCardValue() {
		Dealer dealer = new Dealer();
		dealer.hit(new Card("K", "Hearts")); // hole
		dealer.hit(new Card("7", "Clubs")); // up
		assertEquals(7, dealer.getVisibleSum()); // only the up card counts while hidden
	}

	@Test
	void visibleSumShowsFullTotalOnceRevealed() {
		Dealer dealer = new Dealer();
		dealer.hit(new Card("K", "Hearts"));
		dealer.hit(new Card("7", "Clubs"));
		dealer.reveal();
		assertEquals(17, dealer.getVisibleSum());
	}

	@Test
	void clearHandHidesTheHoleCardAgain() {
		Dealer dealer = new Dealer();
		dealer.hit(new Card("K", "Hearts"));
		dealer.reveal();
		dealer.clearHand();
		assertTrue(dealer.getHiddenCard());
		assertEquals(0, dealer.getHand().size());
	}

	@Test
	void defaultStrategyDrawsBelow17() {
		Dealer dealer = new Dealer();
		dealer.hit(new Card("10", "Hearts"));
		dealer.hit(new Card("6", "Clubs")); // 16
		assertTrue(dealer.shouldHit());
	}

	@Test
	void defaultStrategyStandsAt17() {
		Dealer dealer = new Dealer();
		dealer.hit(new Card("10", "Hearts"));
		dealer.hit(new Card("7", "Clubs")); // 17
		assertFalse(dealer.shouldHit());
	}
}
