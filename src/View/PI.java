package View;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
 
public class PI extends Application {
	
	Stage stage;
	
	
    @Override
    public void start(Stage stage) {
    	stage = new Stage();    
    	this.stage = stage;
        ProgressIndicator progress = new ProgressIndicator();
		FlowPane p = new FlowPane();
		Scene scene = new Scene(p);
		stage.setScene(scene);
//		stage.setMinHeight(250);
//		stage.setMaxHeight(250);
//		stage.setMinWidth(250);
//		stage.setMaxWidth(250);
		scene.setRoot(progress);
		stage.show();
    }
        
    public static void main(String[] args) {
        launch(args);
    }
    
    public void close() {
    	stage.close();
    }
}