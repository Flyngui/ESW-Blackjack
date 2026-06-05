package esw.blackjack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class ConsoleGame {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);

		System.out.println("=== BLACKJACK ===");
		ArrayList<Human> players = new ArrayList<>();
		System.out.printf("Insert player count: ");
		int n = Integer.parseInt(sc.nextLine().trim());
		for (int i = 1; i <= n; i++) {
			System.out.printf("Insert player %d's name: ", i);
			String name = sc.nextLine().trim();
			System.out.printf("Insert player %d's chips: ", i);
			int chips = Integer.parseInt(sc.nextLine().trim());
			players.add(new Human(name, chips));
		}

		Blackjack game = new Blackjack(players);

		boolean running = true;
		while (running && !players.isEmpty()) {
			// --- Fase de apostas ---
			game.newRound();
			for (Human player : players) {
				int bet = askBet(sc, player);
				if (bet > 0) {
					game.placeBet(player, bet);
				}
			}
			game.deal();

			// --- Turno dos jogadores (um de cada vez) ---
			while (game.getState() == Blackjack.State.PLAYERS_TURN) {
				Human player = game.getCurrentPlayer();
				printTable(game, false);
				System.out.printf("%s -> [H]it, [S]tand", player.getName());
				if (player.getBalance() >= player.getCurrentBet()) {
					System.out.print(", [D]ouble down");
				}
				System.out.print(": ");
				if (!sc.hasNextLine()) {
					running = false;
					break;
				}
				switch (sc.nextLine().trim().toLowerCase()) {
					case "h":
						game.playerHit(player);
						break;
					case "s":
						game.playerStand(player);
						break;
					case "d":
						game.playerDoubleDown(player);
						break;
					default:
						System.out.println("Comando invalido.");
				}
			}
			if (!running) {
				break;
			}

			// --- Resultados (um por jogador) ---
			printTable(game, true);
			for (Human player : players) {
				System.out.printf(">>> %s: %s (saldo %d)%n",
						player.getName(), describe(game.getResult(player)), player.getBalance());
			}

			// --- Elimina quem ficou sem fichas (fora do for-each, sem CME) ---
			Iterator<Human> it = players.iterator();
			while (it.hasNext()) {
				Human player = it.next();
				if (player.getBalance() <= 0) {
					System.out.printf("%s ficou sem fichas e foi eliminado.%n", player.getName());
					it.remove();
				}
			}
			if (players.isEmpty()) {
				break;
			}

			System.out.print("\nOutra ronda? [S/n]: ");
			if (!sc.hasNextLine() || sc.nextLine().trim().toLowerCase().equals("n")) {
				running = false;
			}
		}

		System.out.println("\nObrigado por jogar!");
		sc.close();
	}

	// Pede uma aposta valida; devolve 0 se inserir "q" (passa a ronda)
	private static int askBet(Scanner in, Human player) {
		while (true) {
			System.out.printf("%n%s aposta (1 - %d, ou 'q' para passar): ", player.getName(), player.getBalance());
			if (!in.hasNextLine()) {
				return 0;
			}
			String line = in.nextLine().trim().toLowerCase();
			if (line.equals("q")) {
				return 0;
			}
			try {
				int bet = Integer.parseInt(line);
				if (bet >= 1 && bet <= player.getBalance()) {
					return bet;
				}
			} catch (NumberFormatException ignored) {
				// nao ha numero, pede outro
			}
			System.out.println("Aposta invalida.");
		}
	}

	// Printa a mesa. Marca com '>' o jogador a jogar; revealDealer mostra as duas
	// cartas do dealer.
	private static void printTable(Blackjack game, boolean revealDealer) {
		Dealer dealer = game.getDealer();
		System.out.println();
		if (revealDealer) {
			System.out.printf("  Dealer    -> %s (%d)%n", formatHand(dealer.getHand()), dealer.sum());
		} else {
			System.out.printf("  Dealer    -> [ ## %s ] (%d)%n", formatCard(dealer.getUpCard()),
					dealer.getVisibleSum());
		}
		Human current = game.getCurrentPlayer();
		for (Human player : game.getPlayers()) {
			String marker = (player == current) ? ">" : " ";
			System.out.printf("%s %-9s -> %s (%d) [bet=%d, saldo=%d]%n",
					marker, player.getName(), formatHand(player.getHand()), player.getSum(),
					player.getCurrentBet(), player.getBalance());
		}
	}

	// formata todas as cartas de uma mao
	private static String formatHand(List<Card> hand) {
		StringBuilder sb = new StringBuilder("[ ");
		for (Card c : hand) {
			sb.append(formatCard(c)).append(" ");
		}
		return sb.append("]").toString();
	}

	// formata o value e type da carta ja que o toString() da Card iguala a path do
	// sprite
	private static String formatCard(Card c) {
		if (c == null) {
			return "Carta nao encontrada";
		}
		return c.value + suitSymbol(c.type);
	}

	// simbolos utf-8 giros
	private static String suitSymbol(String type) {
		switch (type) {
			case "Clubs":
				return "♣";
			case "Diamonds":
				return "♦";
			case "Hearts":
				return "♥";
			case "Spades":
				return "♠";
			default:
				return "?";
		}
	}

	// manda o string que representa o resultado da ronda de um jogador
	private static String describe(Blackjack.Result result) {
		if (result == null) {
			return "Ronda em curso.";
		}
		switch (result) {
			case PLAYER_BLACKJACK:
				return "Blackjack direto. Ganha 3:2.";
			case PLAYER_WINS:
				return "Ganha a ronda";
			case DEALER_WINS:
				return "Dealer ganha a ronda";
			case TIE:
				return "Empate";
			case PLAYER_BUST:
				return "Rebentou, dealer ganha";
			case DEALER_BUST:
				return "Dealer rebentou, jogador ganha";
			default:
				return result.toString();
		}
	}
}
