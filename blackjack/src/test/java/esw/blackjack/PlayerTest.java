package esw.blackjack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PlayerTest {

	@Test
	void hitAddsCardAndReturnsRunningTotal() {
		Player player = new Player("p");
		int total = player.hit(new Card("5", "Hearts"));
		assertEquals(1, player.getHand().size());
		assertEquals(5, total);
	}

	@Test
	void sumsPlainCards() {
		Player player = new Player("p");
		player.hit(new Card("7", "Hearts"));
		player.hit(new Card("9", "Clubs"));
		assertEquals(16, player.sum());
	}

	@Test
	void aceCountsAsElevenWhenItFits() {
		Player player = new Player("p");
		player.hit(new Card("A", "Hearts"));
		player.hit(new Card("9", "Clubs"));
		assertEquals(20, player.sum()); // soft 20
	}

	@Test
	void aceDropsToOneToAvoidBust() {
		Player player = new Player("p");
		player.hit(new Card("A", "Hearts"));
		player.hit(new Card("9", "Clubs"));
		player.hit(new Card("5", "Spades"));
		assertEquals(15, player.sum()); // 11+9+5=25 -> ace becomes 1 -> 15
	}

	@Test
	void twoAcesCountAsTwelve() {
		Player player = new Player("p");
		player.hit(new Card("A", "Hearts"));
		player.hit(new Card("A", "Clubs"));
		assertEquals(12, player.sum()); // 11+11=22 -> one ace to 1 -> 12
	}

	@Test
	void multipleAcesOnlyDemoteAsManyAsNeeded() {
		Player player = new Player("p");
		player.hit(new Card("A", "Hearts"));
		player.hit(new Card("A", "Clubs"));
		player.hit(new Card("9", "Spades"));
		assertEquals(21, player.sum()); // 11+11+9=31 -> one ace to 1 -> 21
	}

	@Test
	void detectsBust() {
		Player player = new Player("p");
		player.hit(new Card("K", "Hearts"));
		player.hit(new Card("Q", "Clubs"));
		player.hit(new Card("5", "Spades"));
		assertTrue(player.isBust()); // 25
	}

	@Test
	void twentyOneIsNotBust() {
		Player player = new Player("p");
		player.hit(new Card("K", "Hearts"));
		player.hit(new Card("A", "Clubs"));
		assertFalse(player.isBust());
	}

	@Test
	void blackjackIsExactlyTwoCardsTotalling21() {
		Player player = new Player("p");
		player.hit(new Card("A", "Hearts"));
		player.hit(new Card("K", "Clubs"));
		assertTrue(player.hasBlackJack());
	}

	@Test
	void twentyOneInThreeCardsIsNotBlackjack() {
		Player player = new Player("p");
		player.hit(new Card("7", "Hearts"));
		player.hit(new Card("7", "Clubs"));
		player.hit(new Card("7", "Spades"));
		assertEquals(21, player.sum());
		assertFalse(player.hasBlackJack());
	}

	@Test
	void standingBlocksFurtherHits() {
		Player player = new Player("p");
		player.hit(new Card("5", "Hearts"));
		player.stand();
		assertTrue(player.getStanding());
		player.hit(new Card("9", "Clubs")); // ignored while standing
		assertEquals(1, player.getHand().size());
	}

	@Test
	void clearHandResetsEverything() {
		Player player = new Player("p");
		player.hit(new Card("5", "Hearts"));
		player.stand();
		player.clearHand();
		assertEquals(0, player.getHand().size());
		assertEquals(0, player.getSum());
		assertFalse(player.getStanding());
	}
}
