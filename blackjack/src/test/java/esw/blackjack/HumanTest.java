package esw.blackjack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class HumanTest {

	@Test
	void placeBetMovesChipsOutOfBalance() {
		Human human = new Human("h", 100);
		assertTrue(human.placeBet(40));
		assertEquals(60, human.getBalance());
		assertEquals(40, human.getCurrentBet());
	}

	@Test
	void placeBetRejectsNonPositiveAmounts() {
		Human human = new Human("h", 100);
		assertFalse(human.placeBet(0));
		assertFalse(human.placeBet(-5));
		assertEquals(100, human.getBalance());
		assertEquals(0, human.getCurrentBet());
	}

	@Test
	void placeBetRejectsAmountAboveBalance() {
		Human human = new Human("h", 100);
		assertFalse(human.placeBet(101));
		assertEquals(100, human.getBalance());
		assertEquals(0, human.getCurrentBet());
	}

	@Test
	void winBetPaysDoubleTheStake() {
		Human human = new Human("h", 100);
		human.placeBet(40); // balance 60, bet 40
		human.winBet();
		assertEquals(140, human.getBalance()); // 60 + 40*2
		assertEquals(0, human.getCurrentBet());
	}

	@Test
	void blackjackPaysThreeToTwo() {
		Human human = new Human("h", 100);
		human.placeBet(100); // balance 0, bet 100
		human.winBlackJack();
		assertEquals(250, human.getBalance()); // 0 + 100 + 150
		assertEquals(0, human.getCurrentBet());
	}

	@Test
	void tieRefundsTheStake() {
		Human human = new Human("h", 100);
		human.placeBet(40); // balance 60
		human.tieBet();
		assertEquals(100, human.getBalance());
		assertEquals(0, human.getCurrentBet());
	}

	@Test
	void losingKeepsBalanceAndClearsBet() {
		Human human = new Human("h", 100);
		human.placeBet(40); // balance 60
		human.loseBet();
		assertEquals(60, human.getBalance());
		assertEquals(0, human.getCurrentBet());
	}

	@Test
	void doubleDownDoublesStakeAndTakesOneCard() {
		Human human = new Human("h", 100);
		human.placeBet(40); // balance 60, bet 40
		int total = human.doubleDown(new Card("5", "Hearts"));
		assertEquals(20, human.getBalance()); // 60 - 40
		assertEquals(80, human.getCurrentBet());
		assertEquals(5, total);
		assertTrue(human.getStanding());
	}

	@Test
	void doubleDownFailsWithoutEnoughBalance() {
		Human human = new Human("h", 100);
		human.placeBet(100); // balance 0, bet 100
		int result = human.doubleDown(new Card("5", "Hearts"));
		assertEquals(-1, result);
		assertEquals(0, human.getBalance());
		assertEquals(100, human.getCurrentBet());
		assertEquals(0, human.getHand().size()); // no card was taken
	}

	@Test
	void clearHandAlsoClearsTheBet() {
		Human human = new Human("h", 100);
		human.placeBet(40);
		human.hit(new Card("5", "Hearts"));
		human.clearHand();
		assertEquals(0, human.getCurrentBet());
		assertEquals(0, human.getHand().size());
	}
}
