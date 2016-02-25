import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import com.mysql.jdbc.Connection;

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
	private DataReader dataReader = new DataReader();
	int studentID = 0;
	int moduleCode = 0;
	String moduleTitle = null;
	String day = null;
	String date = null;
	String duration = null;
	String location = null;
	private Schedule exam;
	private String studentName = null;
	private Connection conn;

	protected void loadGUI(Scene appScene, BorderPane componentLayout, Stage stage) {
		db.getConnection(conn);
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
				if (dateFrom.getValue().isAfter(dateTo.getValue())) {
					System.out.println("From can't be after to");
					return;
				} else {
					examPeriodFrom = dateFrom.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
					examPeriodTo = dateTo.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
					stage.close();
					try {
						dataReader.createExamPeriod(db, db.getConnection(conn), examPeriodFrom, examPeriodTo);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					loadMainGUI();
				}
			}
		});
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void loadMainGUI() {
		int id = 0;
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
					dataReader.getStudentID(id, studentName);
					// db.createTableStudents();
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
					dataReader.generateRegisteredStudentsData();
					// db.createTableRegisteredStudents();
				} catch (SQLException | IOException e) {
					e.printStackTrace();
				}
			}
		});

		locationBtn.setOnAction(new EventHandler() {
			@Override
			public void handle(Event arg0) {
				try {
					dataReader.getLocations();
					// db.createTableLocation();
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
				try {
					Schedule exam = new Schedule(studentID, moduleCode, moduleTitle, day, date, duration, location);
					exam.generateInformation(db.getConnection(conn), dataReader);
					System.out.printf(
							"Generating an exam schedule for the period: " + examPeriodFrom + " - " + examPeriodTo);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});

		stage.setScene(newAppScene);
		stage.show();
	}
}
