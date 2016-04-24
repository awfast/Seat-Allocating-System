package Controller;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/*
 * @author Damyan Rusinov
 * 
 */

public class Main extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			Pane page = FXMLLoader.load(View.LocalConnection.class.getResource("MainInterface.fxml"));
			Scene scene = new Scene(page);
			primaryStage.setScene(scene);
			primaryStage.setTitle("Seat Allocating System");
			primaryStage.toFront();
			primaryStage.setResizable(false);
			primaryStage.show();
		} catch (Exception ex) {
			Logger.getLogger(View.LocalConnection.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
