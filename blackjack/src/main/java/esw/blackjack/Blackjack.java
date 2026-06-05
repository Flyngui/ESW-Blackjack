package esw.blackjack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Blackjack implements Blackjack_i {
	// 1 - ATRIBUTOS
	private Deck deck;
	private final ArrayList<Human> players;
	private final Dealer dealer;
	private State state;
	private int currentPlayerIndex;
	private final Map<Human, Result> results = new HashMap<>(); // resultado de cada jogador

	// 2 - ACESSORES
	@Override
	public ArrayList<Human> getPlayers() {
		return players;
	}

	@Override
	public Dealer getDealer() {
		return dealer;
	}

	@Override
	public State getState() {
		return state;
	}

	// jogador a jogar agora; null se nao estamos na fase dos jogadores
	@Override
	public Human getCurrentPlayer() {
		if (state != State.PLAYERS_TURN) {
			return null;
		}
		return players.get(currentPlayerIndex);
	}

	// resultado de um jogador
	@Override
	public Result getResult(Human player) {
		return results.get(player);
	}

	// 3.1 - CONSTRUTOR
	public Blackjack(ArrayList<Human> players) {
		this.players = players;
		this.dealer = new Dealer();
		this.state = State.BETTING;
	}

	// 4 - COMPORTAMENTOS
	// limpa tudo e reinicia
	@Override
	public void newRound() {
		for (Human player : players) {
			player.clearHand();
		}
		dealer.clearHand();
		results.clear();
		state = State.BETTING;
	}

	@Override
	public boolean placeBet(Human player, int amount) {
		if (state != State.BETTING) {
			return false;
		}
		return player.placeBet(amount);
	}

	@Override
	public void deal() {
		if (state != State.BETTING) {
			throw new IllegalStateException("Can only deal from the BETTING state.");
		}

		// deck novo todas as jogadas garante que nao acabam as cartas
		this.deck = new Deck();
		results.clear();

		// uma carta para cada jogador ate todos terem 2 cartas
		while (dealer.getHand().size() < 2) {
			for (Human player : players) {
				player.hit(deck.drawCard());
			}
			dealer.hit(deck.drawCard());
		}

		// blackjack direto acaba jogada
		for (Human player : players) {
			if (player.hasBlackJack()) {
				player.stand();
			}
		}

		state = State.PLAYERS_TURN;
		currentPlayerIndex = -1;
		advanceTurn(); // escolhe o primeiro jogador vivo (ou passa logo ao dealer)
	}

	// passa a vez ao proximo jogador que ainda nao acabou
	private void advanceTurn() {
		currentPlayerIndex++;
		while (currentPlayerIndex < players.size() && isDone(players.get(currentPlayerIndex))) {
			currentPlayerIndex++;
		}
		if (currentPlayerIndex >= players.size()) {
			playDealer();
		}
	}

	// um jogador acaba a vez quando fica (stand) ou rebenta
	private boolean isDone(Human player) {
		return player.getStanding() || player.isBust();
	}

	@Override
	public void playerHit(Human player) {
		if (state != State.PLAYERS_TURN || player != getCurrentPlayer()) {
			return;
		}
		player.hit(deck.drawCard());
		if (player.isBust()) {
			advanceTurn();
		}
	}

	@Override
	public void playerStand(Human player) {
		if (state != State.PLAYERS_TURN || player != getCurrentPlayer()) {
			return;
		}
		player.stand();
		advanceTurn();
	}

	// jogador duplica a aposta e pede UMA carta
	@Override
	public void playerDoubleDown(Human player) {
		if (state != State.PLAYERS_TURN || player != getCurrentPlayer()) {
			return;
		}
		if (player.doubleDown(deck.drawCard()) == -1) {
			return; // saldo insuficiente; continua a ser a vez dele
		}
		advanceTurn();
	}

	// dealer revela a carta tapada e pede ate 17
	private void playDealer() {
		state = State.DEALER_TURN;
		dealer.reveal();
		while (dealer.shouldHit()) {
			dealer.hit(deck.drawCard());
		}
		for (Human player : players) {
			settle(player);
		}
		state = State.ROUND_OVER;
	}

	// resultado e pagamento de um jogador
	private void settle(Human player) {
		Result result;
		if (player.isBust()) {
			result = Result.PLAYER_BUST;
			player.loseBet();
		} else if (player.hasBlackJack() && dealer.hasBlackJack()) {
			result = Result.TIE;
			player.tieBet();
		} else if (player.hasBlackJack()) {
			result = Result.PLAYER_BLACKJACK;
			player.winBlackJack();
		} else if (dealer.isBust()) {
			result = Result.DEALER_BUST;
			player.winBet();
		} else if (player.sum() > dealer.sum()) {
			result = Result.PLAYER_WINS;
			player.winBet();
		} else if (player.sum() < dealer.sum()) {
			result = Result.DEALER_WINS;
			player.loseBet();
		} else {
			result = Result.TIE;
			player.tieBet();
		}
		results.put(player, result);
	}
}
