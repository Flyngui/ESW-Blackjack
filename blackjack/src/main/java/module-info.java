module esw.blackjack {
	requires javafx.controls;
	requires javafx.fxml;

	opens esw.blackjack to javafx.fxml;

	exports esw.blackjack;
}