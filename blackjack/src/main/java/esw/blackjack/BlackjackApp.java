package esw.blackjack;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class BlackjackApp extends Application {

	@Override
	public void start(Stage stage) {
		// Placeholder View
		Label label = new Label("Blackjack Engine Initialized");
		Scene scene = new Scene(new StackPane(label), 800, 600);

		stage.setTitle("Blackjack");
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}