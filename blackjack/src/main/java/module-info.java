module esw.blackjack {
	requires javafx.controls;
	requires javafx.fxml;

	opens esw.blackjack to javafx.fxml;
	opens esw.blackjack.view to javafx.fxml;

	exports esw.blackjack;
}