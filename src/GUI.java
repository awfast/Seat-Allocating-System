import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GUI {

	private FlowPane pane = null;
	private BorderPane flow = new BorderPane();
	private Button browseBtn;
	private Button studentBtn;
	private Button registrationListBtn;
	private Button examGeneratorBtn;
	private DatePicker dateFrom;
	private DatePicker dateTo;
	private Stage stage;
	private DB db = new DB();
	private Button locationBtn;
	private String examPeriodFrom;
	private String examPeriodTo;
	private Session session;

	protected void loadGUI(Scene appScene, BorderPane componentLayout, Stage stage) {
		db.getConnection();
		VBox vbox = new VBox();

		dateFrom = new DatePicker();
		dateTo = new DatePicker();
		browseBtn = new Button("Go");
		vbox.getChildren().addAll(dateFrom, dateTo, browseBtn);
		
		// put the flowpane in the top area of the BorderPane
		componentLayout.setCenter(vbox);
		this.stage = stage;
		getExamPeriod(stage);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void getExamPeriod(Stage stage) {
		browseBtn.setOnAction(new EventHandler() {
			public void handle(Event t) {
				/*examPeriodFrom = "01/06/2015";
				examPeriodTo = "01/07/2015";
				
				stage.close();
				try {
					db.createTableSession(examPeriodFrom, examPeriodTo);
					loadMainGUI();
				} catch (SQLException e) {
					e.printStackTrace();
				}*/
			
			if (dateFrom.getValue().isAfter(dateTo.getValue())) {
				System.out.println("From can't be after to");
				return;
			} else {
				examPeriodFrom = dateFrom.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
				examPeriodTo = dateTo.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));				
				stage.close();
				try {
					db.createTableSession(db.getConnection(), examPeriodFrom, examPeriodTo);
					loadMainGUI();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			}
		});
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void loadMainGUI(){
		Scene newAppScene = new Scene(flow, 400, 350);
		pane = new FlowPane();
		studentBtn = new Button("StudentInfo");
		registrationListBtn = new Button("RegisteredStudents");
		locationBtn = new Button("LocationInfo");
		examGeneratorBtn = new Button("Go");
		
		pane.getChildren().add(studentBtn);
		pane.getChildren().add(registrationListBtn);
		pane.getChildren().add(locationBtn);
		pane.getChildren().add(examGeneratorBtn);
		
		flow.setCenter(pane);
			
		studentBtn.setOnAction(new EventHandler() {
			@Override
			public void handle(Event arg0) {
				try {
					db.createTableStudents();
				} catch (SQLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		registrationListBtn.setOnAction(new EventHandler() {
			@Override
			public void handle(Event arg0) {
				try {
					db.createTableRegisteredStudents();
				} catch (SQLException | IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		locationBtn.setOnAction(new EventHandler() {
			@Override
			public void handle(Event arg0) {
				try {
					db.createTableLocation();
				} catch (SQLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		examGeneratorBtn.setOnAction(new EventHandler() {
			@Override
			public void handle(Event arg0) {
				//A* algorithm to be called in here
				try {
					db.createTableExam();
					System.out.printf("Generating an exam schedule for the period: " + examPeriodFrom + " - " + examPeriodTo);
				} catch (SQLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		stage.setScene(newAppScene);
		stage.show();
	}
}
