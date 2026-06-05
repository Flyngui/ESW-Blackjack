package esw.blackjack;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

// Controlador JavaFX: a "View". So traduz cliques -> engine e estado -> ecra.
// Toda a logica de jogo vive na classe Blackjack (Facade).
public class GameController {

	@FXML
	private HBox dealerCards;
	@FXML
	private HBox playersBox; // um painel por jogador (construido em codigo)
	@FXML
	private HBox chipBox;
	@FXML
	private Label dealerScore;
	@FXML
	private Label statusLabel;
	@FXML
	private Button dealBtn;
	@FXML
	private Button hitBtn;
	@FXML
	private Button standBtn;
	@FXML
	private Button doubleBtn;
	@FXML
	private Button newBtn;

	private static final double CARD_HEIGHT = 120.0;
	private static final double CHIP_HEIGHT = 52.0;
	private static final int START_BALANCE = 1000;
	private static final int MAX_PLAYERS = 4; // limite que cabe bem na mesa

	// denominacao da ficha -> sprite (sprites/PNG/Chips)
	private static final Map<Integer, String> CHIPS = new LinkedHashMap<>();
	static {
		CHIPS.put(5, "chipWhiteBlue");
		CHIPS.put(25, "chipRedWhite");
		CHIPS.put(50, "chipGreenWhite");
		CHIPS.put(100, "chipBlackWhite");
		CHIPS.put(500, "chipBlueWhite");
	}

	private ArrayList<Human> players;
	private Blackjack game;
	private Image cardBack;

	private int bettingIndex = 0; // que jogador esta a apostar (fase BETTING)
	private int pendingBet = 0; // aposta a ser montada para esse jogador

	@FXML
	public void initialize() {
		players = askPlayers();
		game = new Blackjack(players);
		cardBack = loadImage("/sprites/PNG/Cards/cardBack_blue2.png");
		buildChips();
		refresh();
	}

	// --- Configuracao inicial (dialogos, equivalente ao input do ConsoleGame) ---

	// Pergunta quantos jogadores e, para cada um, o nome e as fichas iniciais.
	private ArrayList<Human> askPlayers() {
		int count = askInt("Configuração", "Número de jogadores (1-" + MAX_PLAYERS + "):", 2, 1, MAX_PLAYERS);
		ArrayList<Human> result = new ArrayList<>();
		for (int i = 1; i <= count; i++) {
			String name = askName(i);
			int chips = askInt(name, "Fichas iniciais de " + name + ":", START_BALANCE, 1, 1_000_000);
			result.add(new Human(name, chips));
		}
		return result;
	}

	// Le um inteiro dentro de [min, max]; repete enquanto for invalido, usa def se
	// cancelar.
	private int askInt(String title, String prompt, int def, int min, int max) {
		while (true) {
			TextInputDialog dialog = new TextInputDialog(String.valueOf(def));
			dialog.setTitle(title);
			dialog.setHeaderText(null);
			dialog.setContentText(prompt);
			Optional<String> answer = dialog.showAndWait();
			if (answer.isEmpty()) {
				return def; // cancelou -> usa o valor por defeito
			}
			try {
				int value = Integer.parseInt(answer.get().trim());
				if (value >= min && value <= max) {
					return value;
				}
			} catch (NumberFormatException ignored) {
				// volta a pedir
			}
		}
	}

	// Le o nome de um jogador; usa "Jogador i" se ficar vazio ou cancelar.
	private String askName(int index) {
		TextInputDialog dialog = new TextInputDialog("Jogador " + index);
		dialog.setTitle("Jogador " + index);
		dialog.setHeaderText(null);
		dialog.setContentText("Nome do jogador " + index + ":");
		String name = dialog.showAndWait().map(String::trim).orElse("");
		return name.isEmpty() ? "Jogador " + index : name;
	}

	// --- View -> engine -------------------------------------------------

	// Na fase de apostas o botao "Distribuir" confirma a aposta do jogador atual
	// e passa ao seguinte; quando todos apostaram, distribui as cartas.
	@FXML
	private void onDeal() {
		if (game.getState() != Blackjack.State.BETTING) {
			return;
		}
		Human bettor = currentBettor();
		if (bettor == null) {
			return;
		}
		if (pendingBet > 0) {
			game.placeBet(bettor, pendingBet);
		}
		pendingBet = 0;
		advanceBettor();
		if (currentBettor() == null) {
			game.deal(); // todos apostaram
		}
		refresh();
	}

	@FXML
	private void onHit() {
		game.playerHit(game.getCurrentPlayer());
		refresh();
	}

	@FXML
	private void onStand() {
		game.playerStand(game.getCurrentPlayer());
		refresh();
	}

	@FXML
	private void onDouble() {
		game.playerDoubleDown(game.getCurrentPlayer());
		refresh();
	}

	@FXML
	private void onNew() {
		// quem ficou sem fichas e eliminado antes da proxima ronda
		String eliminated = removeBrokePlayers();
		if (!eliminated.isEmpty()) {
			showInfo("Eliminados", eliminated + " sem fichas. Saem do jogo.");
		}
		if (players.isEmpty()) {
			refresh(); // fim de jogo
			return;
		}
		game.newRound();
		pendingBet = 0;
		bettingIndex = 0;
		skipBrokePlayers();
		refresh();
	}

	// Remove os jogadores sem fichas; devolve os nomes eliminados (vazio se nenhum).
	// players e a mesma lista que o engine usa, por isso isto remove-os do jogo.
	private String removeBrokePlayers() {
		StringBuilder eliminated = new StringBuilder();
		Iterator<Human> it = players.iterator();
		while (it.hasNext()) {
			Human player = it.next();
			if (player.getBalance() <= 0) {
				if (eliminated.length() > 0) {
					eliminated.append(", ");
				}
				eliminated.append(player.getName());
				it.remove();
			}
		}
		return eliminated.toString();
	}

	private void addToBet(int amount) {
		Human bettor = currentBettor();
		if (game.getState() != Blackjack.State.BETTING || bettor == null) {
			return;
		}
		if (pendingBet + amount <= bettor.getBalance()) {
			pendingBet += amount;
			refresh();
		}
	}

	// jogador que esta a apostar agora (null quando todos ja apostaram)
	private Human currentBettor() {
		return bettingIndex < players.size() ? players.get(bettingIndex) : null;
	}

	private void advanceBettor() {
		bettingIndex++;
		skipBrokePlayers();
	}

	// jogadores sem fichas nao apostam, sao saltados
	private void skipBrokePlayers() {
		while (bettingIndex < players.size() && players.get(bettingIndex).getBalance() == 0) {
			bettingIndex++;
		}
	}

	// --- Engine -> View -------------------------------------------------

	// Le o estado do engine e reconstroi o ecra. Chamado apos cada acao.
	private void refresh() {
		Blackjack.State state = game.getState();
		Dealer dealer = game.getDealer();
		boolean hideHole = dealer.getHiddenCard();
		ArrayList<Human> players = game.getPlayers();

		// dealer
		renderHand(dealerCards, dealer.getHand(), hideHole);
		dealerScore.setText(dealer.getHand().isEmpty() ? ""
				: String.valueOf(hideHole ? dealer.getVisibleSum() : dealer.sum()));

		// um painel por jogador, com destaque no jogador ativo
		Human highlight = (state == Blackjack.State.BETTING) ? currentBettor() : game.getCurrentPlayer();
		playersBox.getChildren().clear();
		for (Human player : players) {
			playersBox.getChildren().add(playerPanel(player, state, highlight));
		}

		updateButtons(state);
		statusLabel.setText(statusText(state));
	}

	private VBox playerPanel(Human player, Blackjack.State state, Human highlight) {
		VBox panel = new VBox(4.0);
		panel.setAlignment(Pos.CENTER);
		panel.getStyleClass().add("player-panel");
		if (player == highlight) {
			panel.getStyleClass().add("player-current");
		}

		Label name = new Label(player.getName());
		name.getStyleClass().add("player-name");

		HBox cards = new HBox(6.0);
		cards.setAlignment(Pos.CENTER);
		renderHand(cards, player.getHand(), false);

		boolean betting = state == Blackjack.State.BETTING;
		String betText = (betting && player == currentBettor())
				? "aposta: " + pendingBet + " (a montar)"
				: "aposta: " + player.getCurrentBet();
		String total = player.getHand().isEmpty() ? "" : "total " + player.getSum() + "  |  ";
		Label info = new Label(total + betText + "  |  saldo: " + player.getBalance());
		info.getStyleClass().add("player-info");

		panel.getChildren().addAll(name, cards, info);

		if (state == Blackjack.State.ROUND_OVER) {
			Label res = new Label(describe(game.getResult(player)));
			res.getStyleClass().add("player-result");
			panel.getChildren().add(res);
		}
		return panel;
	}

	private void renderHand(HBox box, List<Card> hand, boolean hideFirst) {
		box.getChildren().clear();
		for (int i = 0; i < hand.size(); i++) {
			Image img = (hideFirst && i == 0) ? cardBack : loadImage(hand.get(i).getImagePath());
			ImageView view = new ImageView(img);
			view.setFitHeight(CARD_HEIGHT);
			view.setPreserveRatio(true);
			box.getChildren().add(view);
		}
	}

	// os botoes espelham apenas o que o engine permite na fase atual
	private void updateButtons(Blackjack.State state) {
		if (players.isEmpty()) { // todos eliminados: nada a fazer
			chipBox.setDisable(true);
			dealBtn.setDisable(true);
			hitBtn.setDisable(true);
			standBtn.setDisable(true);
			doubleBtn.setDisable(true);
			newBtn.setDisable(true);
			return;
		}

		boolean betting = state == Blackjack.State.BETTING;
		boolean playersTurn = state == Blackjack.State.PLAYERS_TURN;
		boolean roundOver = state == Blackjack.State.ROUND_OVER;

		Human bettor = currentBettor();
		Human current = game.getCurrentPlayer();

		// chips: so na fase de apostas e se o jogador tiver saldo
		chipBox.setDisable(!betting || bettor == null || bettor.getBalance() == 0);

		// "Distribuir" confirma a aposta (ou salta jogador sem fichas)
		boolean canConfirm = betting && bettor != null && (pendingBet > 0 || bettor.getBalance() == 0);
		dealBtn.setDisable(!canConfirm);
		dealBtn.setText(betting && bettor != null ? "Apostar (" + bettor.getName() + ")" : "Distribuir");

		hitBtn.setDisable(!playersTurn);
		standBtn.setDisable(!playersTurn);
		doubleBtn.setDisable(!playersTurn || current == null || current.getBalance() < current.getCurrentBet());

		newBtn.setDisable(!roundOver);
	}

	// --- helpers --------------------------------------------------------

	private void buildChips() {
		for (Map.Entry<Integer, String> chip : CHIPS.entrySet()) {
			chipBox.getChildren().add(chipButton(chip.getKey(), chip.getValue()));
		}
		Button clear = new Button("Limpar");
		clear.getStyleClass().add("action");
		clear.setOnAction(e -> {
			if (game.getState() == Blackjack.State.BETTING) {
				pendingBet = 0;
				refresh();
			}
		});
		chipBox.getChildren().add(clear);
	}

	private Button chipButton(int amount, String sprite) {
		ImageView icon = new ImageView(loadImage("/sprites/PNG/Chips/" + sprite + ".png"));
		icon.setFitHeight(CHIP_HEIGHT);
		icon.setPreserveRatio(true);

		Button button = new Button(String.valueOf(amount), icon);
		button.setContentDisplay(ContentDisplay.TOP); // numero por baixo da ficha
		button.getStyleClass().add("chip-btn");
		button.setOnAction(e -> addToBet(amount));
		return button;
	}

	private String statusText(Blackjack.State state) {
		if (players.isEmpty()) {
			return "Fim de jogo — todos sem fichas";
		}
		switch (state) {
			case BETTING:
				Human bettor = currentBettor();
				if (bettor == null) {
					return "Carregue em Distribuir";
				}
				return bettor.getName() + ": escolha fichas (aposta: " + pendingBet + ")";
			case PLAYERS_TURN:
				Human current = game.getCurrentPlayer();
				return (current == null ? "" : current.getName()) + ": pedir, ficar ou dobrar";
			case DEALER_TURN:
				return "Vez do dealer...";
			case ROUND_OVER:
				return "Ronda terminada — Nova ronda para jogar outra vez";
			default:
				return "";
		}
	}

	private String describe(Blackjack.Result result) {
		if (result == null) {
			return "";
		}
		switch (result) {
			case PLAYER_BLACKJACK:
				return "BLACKJACK! 3:2";
			case PLAYER_WINS:
				return "Ganhou!";
			case DEALER_WINS:
				return "Dealer ganhou";
			case TIE:
				return "Empate";
			case PLAYER_BUST:
				return "Rebentou";
			case DEALER_BUST:
				return "Dealer rebentou — ganhou!";
			default:
				return result.toString();
		}
	}

	// Mostra um aviso simples ao jogador.
	private void showInfo(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	// Carrega um sprite do classpath, com erro claro se faltar.
	private Image loadImage(String path) {
		InputStream in = getClass().getResourceAsStream(path);
		if (in == null) {
			throw new IllegalStateException("Sprite nao encontrado no classpath: " + path);
		}
		return new Image(in);
	}
}
