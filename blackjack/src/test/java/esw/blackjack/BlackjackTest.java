package esw.blackjack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// Maquina de estados / fluxo de varios jogadores. O baralho e aleatorio, por
// isso verificam-se transicoes e invariantes, nao um vencedor especifico.
class BlackjackTest {

	private Human p1;
	private Human p2;
	private ArrayList<Human> players;
	private Blackjack game;

	@BeforeEach
	void setUp() {
		p1 = new Human("Jogador 1", 1000);
		p2 = new Human("Jogador 2", 1000);
		players = new ArrayList<>();
		players.add(p1);
		players.add(p2);
		game = new Blackjack(players);
	}

	// joga a ronda ate ao fim mandando cada jogador ficar (stand)
	private void standEveryone() {
		while (game.getState() == Blackjack.State.PLAYERS_TURN) {
			game.playerStand(game.getCurrentPlayer());
		}
	}

	@Test
	void startsInBettingWithNoResults() {
		assertEquals(Blackjack.State.BETTING, game.getState());
		assertNull(game.getResult(p1));
		assertNull(game.getResult(p2));
	}

	@Test
	void noCurrentPlayerBeforeDealing() {
		assertNull(game.getCurrentPlayer());
	}

	@Test
	void placeBetMovesChips() {
		assertTrue(game.placeBet(p1, 100));
		assertEquals(900, p1.getBalance());
		assertEquals(100, p1.getCurrentBet());
		assertEquals(Blackjack.State.BETTING, game.getState());
	}

	@Test
	void cannotPlaceBetAfterDealing() {
		game.placeBet(p1, 100);
		game.placeBet(p2, 100);
		game.deal();
		assertFalse(game.placeBet(p1, 50));
	}

	@Test
	void dealGivesTwoCardsToEachPlayerAndDealer() {
		game.placeBet(p1, 100);
		game.placeBet(p2, 100);
		game.deal();
		assertEquals(2, p1.getHand().size());
		assertEquals(2, p2.getHand().size());
		assertEquals(2, game.getDealer().getHand().size());
	}

	@Test
	void dealingTwiceThrows() {
		game.placeBet(p1, 100);
		game.placeBet(p2, 100);
		game.deal();
		assertThrows(IllegalStateException.class, () -> game.deal());
	}

	@Test
	void standingMovesPastThatPlayer() {
		game.placeBet(p1, 100);
		game.placeBet(p2, 100);
		game.deal();
		if (game.getState() == Blackjack.State.PLAYERS_TURN) {
			Human current = game.getCurrentPlayer();
			game.playerStand(current);
			assertTrue(current.getStanding());
			// a vez ja nao e deste jogador (passou a outro ou ao dealer)
			assertNotSame(current, game.getCurrentPlayer());
		}
	}

	@Test
	void actionsForAPlayerWhoIsNotCurrentAreIgnored() {
		game.placeBet(p1, 100);
		game.placeBet(p2, 100);
		game.deal();
		if (game.getState() == Blackjack.State.PLAYERS_TURN) {
			Human current = game.getCurrentPlayer();
			Human other = (current == p1) ? p2 : p1;
			int before = other.getHand().size();
			game.playerHit(other); // nao e a vez dele
			assertEquals(before, other.getHand().size());
		}
	}

	@Test
	void everyPlayerIsSettledOnceTheRoundEnds() {
		game.placeBet(p1, 100);
		game.placeBet(p2, 100);
		game.deal();
		standEveryone();

		assertEquals(Blackjack.State.ROUND_OVER, game.getState());
		assertNull(game.getCurrentPlayer());
		assertNotNull(game.getResult(p1)); // cada jogador tem o SEU resultado
		assertNotNull(game.getResult(p2));
		assertEquals(0, p1.getCurrentBet()); // apostas acertadas
		assertEquals(0, p2.getCurrentBet());
	}

	@Test
	void newRoundClearsTheTable() {
		game.placeBet(p1, 100);
		game.placeBet(p2, 100);
		game.deal();
		standEveryone();
		game.newRound();

		assertEquals(Blackjack.State.BETTING, game.getState());
		assertNull(game.getResult(p1));
		assertNull(game.getResult(p2));
		assertEquals(0, p1.getHand().size());
		assertEquals(0, p2.getHand().size());
		assertEquals(0, game.getDealer().getHand().size());
		assertEquals(0, p1.getCurrentBet());
	}
}
