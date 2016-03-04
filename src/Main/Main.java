package Main;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application{
	Stage stage;
	private BorderPane componentLayout = new BorderPane();
	protected Scene appScene = new Scene(componentLayout, 150, 100);
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		GUI gui = new GUI();
		
		this.stage = primaryStage;
		primaryStage.setTitle("example Gui");
		gui.loadGUI(appScene, componentLayout, primaryStage);

		primaryStage.setScene(appScene);	
		primaryStage.show();
	}
}
