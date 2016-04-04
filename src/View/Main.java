package View;

import javafx.application.Application;
import java.util.logging.Level;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.util.logging.Logger;

public class Main {

	public static class HelloWorld extends Application {
		public static void main(String[] args) {
			launch(args);
		}

		 @Override
		    public void start(Stage primaryStage) {
		        try {
		            Pane page = FXMLLoader.load(View.Main.class.getResource("MainInterface.fxml"));
		            Scene scene = new Scene(page);
		            primaryStage.setScene(scene);
		            primaryStage.setTitle("FXML");
		            primaryStage.show();
		        } catch (Exception ex) {
		            Logger.getLogger(View.Main.class.getName()).log(Level.SEVERE, null, ex);
		        }
		    }
	}
}

