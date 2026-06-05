package esw.blackjack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CardTest {

	@Test
	void numberCardsKeepTheirValue() {
		assertEquals(2, new Card("2", "Hearts").getValue());
		assertEquals(9, new Card("9", "Clubs").getValue());
		assertEquals(10, new Card("10", "Spades").getValue());
	}

	@Test
	void faceCardsAreWorthTen() {
		assertEquals(10, new Card("J", "Spades").getValue());
		assertEquals(10, new Card("Q", "Spades").getValue());
		assertEquals(10, new Card("K", "Spades").getValue());
	}

	@Test
	void aceIsWorthElevenAndIsFlaggedAsAce() {
		Card ace = new Card("A", "Diamonds");
		assertEquals(11, ace.getValue());
		assertTrue(ace.isAce());
	}

	@Test
	void nonAceIsNotFlaggedAsAce() {
		assertFalse(new Card("K", "Spades").isAce());
		assertFalse(new Card("10", "Hearts").isAce());
	}

	@Test
	void toStringMatchesSpriteNaming() {
		assertEquals("cardClubs4", new Card("4", "Clubs").toString());
		assertEquals("cardSpadesA", new Card("A", "Spades").toString());
	}

	@Test
	void imagePathPointsAtTheSprite() {
		assertEquals("/sprites/PNG/Cards/cardSpadesA.png", new Card("A", "Spades").getImagePath());
		assertEquals("/sprites/PNG/Cards/cardHearts10.png", new Card("10", "Hearts").getImagePath());
	}
}
