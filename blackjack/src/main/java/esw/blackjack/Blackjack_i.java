package esw.blackjack;

import java.util.ArrayList;

public interface Blackjack_i {

	// estados de jogo
	enum State {
		BETTING,
		PLAYERS_TURN,
		DEALER_TURN,
		ROUND_OVER
	}

	// resultados possiveis (por jogador)
	enum Result {
		PLAYER_BLACKJACK,
		PLAYER_WINS,
		DEALER_WINS,
		TIE,
		PLAYER_BUST,
		DEALER_BUST
	}

	// acessores
	ArrayList<Human> getPlayers();

	Dealer getDealer();

	State getState();

	// jogador atual
	Human getCurrentPlayer();

	// resultado de um jogador
	Result getResult(Human player);

	// comportamentos
	void newRound();

	boolean placeBet(Human player, int amount);

	void deal();

	void playerHit(Human player);

	void playerStand(Human player);

	void playerDoubleDown(Human player);
}
