package View;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import com.itextpdf.text.log.SysoCounter;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import jfx.messagebox.MessageBox;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MainPanel implements Initializable {
		
	//BLOCK CAPITALS FOR EVERY NEW ENTRY!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	LocalConnection local = new LocalConnection();
	
	//has been reset previously
	boolean reset = false;
	
	//flags for data collection options
	boolean flag1, flag2, flag3 = false;
	
	//string passing the exam 'from' period
	private String examPeriodFrom;
	//string passing the exam 'to' period
	private String examPeriodTo;
	
	@FXML //fx:id="mainPane"
	private Pane mainPane;
	
	@FXML //fx:id="examPeriodPane"
	private Pane examPeriodPane;
	
	@FXML //fx:id="tablePane"
	private Pane tablePane;
	
	@FXML //fx:id="topPane"
	private Pane topPane;
	
	@FXML //fx:id="bottomPane"
	private Pane bottomPane;
	
	@FXML //fx:id="addButton"
	private Button addButton;
	
	@FXML //fx:id="deleteButton"
	private Button deleteButton;
	
	@FXML //fx:id="goButton"
	private Button goButton;
	
	@FXML //fx:id="resetButton"
	private Button resetButton;
	
	@FXML //fx:id="exportButton"
	private Button exportButton;
	
	@FXML //fx:id="importButton1"
	private Button importButton1;
	
	@FXML //fx:id="importButton2"
	private Button importButton2;
	
	@FXML //fx:id="importButton3"
	private Button importButton3;

	@FXML //fx:id="labelStudentID"
	private Text labelStudentID;
	
	@FXML //fx:id="labelModuleCode"
	private Text labelModuleCode;
	
	@FXML //fx:id="labelDate"
	private Text labelDate;
	
	@FXML //fx:id="labelBuildingNumber"
	private Text labelBuildingNumber;
	
	@FXML //fx:id="labelRoomNumber"
	private Text labelRoomNumber;
	
	@FXML //fx:id="fieldStudentID"
	private TextField fieldStudentID;
	
	@FXML //fx:id="fieldModuleCode"
	private TextField fieldModuleCode;
	
	@FXML //fx:id="fieldModuleTitle"
	private TextField fieldModuleTitle;
	
	@FXML //fx:id="fieldDay"
	private TextField fieldDay;
	
	@FXML //fx:id="fieldDate"
	private TextField fieldDate;
	
	@FXML //fx:id="fieldSession"
	private TextField fieldSession;
	
	@FXML //fx:id="fieldLocation"
	private TextField fieldLocation;
	
	@FXML //fx:id="dateFrom"
	private DatePicker dateFrom;

	@FXML //fx:id="dateTo"
	private DatePicker dateTo;
	
	@FXML //fx:id="columnCode"
	private TableColumn<Schedule, String> columnStudent;
	
	@FXML //fx:id="columnCode"
	private TableColumn<Schedule, String> columnCode;
	
	@FXML //fx:id="columnCode"
	private TableColumn<Schedule, String> columnTitle;
	
	@FXML //fx:id="columnDay"
	private TableColumn<Schedule, String> columnDay;
	
	@FXML //fx:id="columnDate"
	private TableColumn<Schedule, String> columnDate;
	
	@FXML //fx:id="columnSession"
	private TableColumn<Schedule, String> columnSession;
	
	@FXML //fx:id="columnLocation"
	private TableColumn<Schedule, String> columnLocation;
	
	@FXML //fx:id="treeTable"
	private TableView<View.Schedule> tableView;
	
	@FXML //fx:id="mainPane"
	private ChoiceBox importStudentData;
	
	@FXML //fx:id="mainPane"
	private ChoiceBox importRegistrationData;
	
	@FXML //fx:id="mainPane"
	private ChoiceBox importLocationData;
	
	private ObservableList<View.Schedule> inputData = FXCollections.observableArrayList();
	
	//list with values for column 1
	 private ObservableList<View.Schedule> data =
		        FXCollections.observableArrayList(
//		            new Test("Jacob", "Smith", "jacob.smith@example.com"),
//		            new Test("Isabella", "Johnson", "isabella.johnson@example.com"),
//		            new Test("Ethan", "Williams", "ethan.williams@example.com"),
//		            new Test("Emma", "Jones", "emma.jones@example.com"),
//		            new Test("Jacob", "Smith", "jacob.smith@example.com"),
//		            new Test("Isabella", "Johnson", "isabella.johnson@example.com"),
//		            new Test("Ethan", "Williams", "ethan.williams@example.com"),
//		            new Test("Emma", "Jones", "emma.jones@example.com"),
//		            new Test("Jacob", "Smith", "jacob.smith@example.com"),
//		            new Test("Isabella", "Johnson", "isabella.johnson@example.com"),
//		            new Test("Ethan", "Williams", "ethan.williams@example.com"),
//		            new Test("Emma", "Jones", "emma.jones@example.com"),
//		            new Test("Jacob", "Smith", "jacob.smith@example.com"),
//		            new Test("Isabella", "Johnson", "isabella.johnson@example.com"),
//		            new Test("Ethan", "Williams", "ethan.williams@example.com"),
//		            new Test("Emma", "Jones", "emma.jones@example.com"),
//		            new Test("Michael", "Brown", "michael.brown@example.com")
		        );
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		addButton.setOnAction(event -> {
			//if has been reset, add new data to the table
			//addRow();
			//TODO
		});	
		
		goButton.setOnAction(event -> {
			//if has been reset, add new data to the table
			try {
				processExamPeriod();
				populateTable();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});	
		
		/*test.setOnAction(event -> {
			examPeriodFrom = dateFrom.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
			examPeriodTo = dateTo.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
			System.out.println(examPeriodFrom +  " " + examPeriodTo); 
			openProgressBar();			
			organizeColumnCode();
			tableView.setItems(data);
		});	
		
		exportButton.setOnAction(event -> {
			PDFExport pdf = new PDFExport(); 
			pdf.export1(inputData);
			pdf.export2(data);
		});
		
		resetButton.setOnAction(event -> {
			resetEverything();
		});*/
		
		//TODO
//		deleteButton.setOnAction(event -> {
//        	//remove selected item from the table list
//        	Schedule p = tableView.getSelectionModel().getSelectedItem();
//        	if(p==null) {
//        		System.out.println("Nothing selected");
//        		return;
//        	} else {
//	            inputData.remove(p);
//	            tableView.setItems(inputData);
//        	}
//		});
		
		//checkImports();
	}
	
	private void openProgressBar() {
		ProgressIndicator progress = new ProgressIndicator();
		Stage stage = new Stage();
		FlowPane p = new FlowPane();
		Scene scene = new Scene(p);
		stage.setScene(scene);
		scene.setRoot(progress);
		stage.show();
		mainPane.setDisable(true);
	}
	
	private void organizeColumnCode() {
		columnStudent.setCellValueFactory(new PropertyValueFactory<View.Schedule, String>("studentID"));
		columnCode.setCellValueFactory(new PropertyValueFactory<View.Schedule, String>("moduleCode"));
		columnTitle.setCellValueFactory(new PropertyValueFactory<View.Schedule, String>("moduleTitle"));
		columnDay.setCellValueFactory(new PropertyValueFactory<View.Schedule, String>("day"));
		columnDate.setCellValueFactory(new PropertyValueFactory<View.Schedule, String>("date"));
		columnSession.setCellValueFactory(new PropertyValueFactory<View.Schedule, String>("session"));
		columnLocation.setCellValueFactory(new PropertyValueFactory<View.Schedule, String>("location"));
	}
	
	private void checkFields() {
		fieldStudentID.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				hideOptions(oldValue, newValue);
			}
		});
	}
	
//	private void checkImports() {
//		
//		
//		importButton1.setOnAction(event -> {
//			flag1 = true;
//			//importOptionSelected();		;
//		});
//		
//		importButton2.setOnAction(event -> {
//			flag2 = true;
//			//importOptionSelected();		
//		});
//		
//		importButton3.setOnAction(event -> {
//			//importOptionSelected();		
//			if(flag1 == true && flag2 == true) {
//				topPane.getChildren().remove(examPeriodPane);
//			}
//		});
//		
//	}
	
	//loading bar
	private boolean allFieldsAreFilledUp() {
		if(fieldStudentID.getText().trim().isEmpty() || fieldModuleCode.getText().trim().isEmpty() || fieldModuleTitle.getText().trim().isEmpty()){
			return false;
		} else {
			return true;
		}
	}
	
	private void triggerPopUp() {		
		Stage s = new Stage();
		 MessageBox.show(s,
		         "Sample of information dialog.\n\nDialog option is below.\n[MessageBox.ICON_INFORMATION | MessageBox.OK | MessageBox.CANCEL]",
		         "Information dialog",
		         MessageBox.ICON_INFORMATION | MessageBox.OK);
	}
	
	private void hideOptions(String oldValue, String newValue) {
		if(oldValue.startsWith(" ") || oldValue.isEmpty() || newValue.isEmpty() || newValue.startsWith(" ")) {
			resetEverything();
		} else {
			//manualOptionSelected();	
			oldValue = "";
			newValue = "";
		}
	}
	
	/*private void importOptionSelected() {
		labelStudentID.setDisable(true);
		labelModuleCode.setDisable(true);
		labelDate.setDisable(true);
		labelBuildingNumber.setDisable(true);
		labelRoomNumber.setDisable(true);
		
		fieldStudentID.setDisable(true);
		fieldModuleCode.setDisable(true);
		fieldDate.setDisable(true);
		
	}
	
	private void manualOptionSelected() {
		importStudentData.setDisable(true);
		importRegistrationData.setDisable(true);
		importLocationData.setDisable(true);
		
		importButton1.setDisable(true);
		importButton2.setDisable(true);
		importButton3.setDisable(true);
	}*/
	
	private void resetEverything() {
		reset = true;
		labelStudentID.setDisable(false);
		labelModuleCode.setDisable(false);
		labelDate.setDisable(false);
		labelBuildingNumber.setDisable(false);
		labelRoomNumber.setDisable(false);
		
		fieldStudentID.setDisable(false);
		fieldModuleCode.setDisable(false);
		fieldDate.setDisable(false);
		
		importStudentData.setDisable(false);
		importRegistrationData.setDisable(false);
		importLocationData.setDisable(false);
		
		importButton1.setDisable(false);
		importButton2.setDisable(false);
		importButton3.setDisable(false);
		
		//topPane.getChildren().add(examPeriodPane);
		//bottomPane.getChildren().remove(tableView);
	}
	
	//TODO
//	private void addRow() {
//		if(reset == true) {
//			inputData.clear();
//			if(allFieldsAreFilledUp()) {
//				reset = false;
//				tableView.setLayoutX(214);
//				tableView.setLayoutY(14);
//				tableView.setPrefSize(1073, 371);
//				inputData.add(new Schedule(fieldStudentID.getText(), fieldModuleCode.getText(), fieldModuleTitle.getText()));
//				bottomPane.getChildren().add(tableView);
//				organizeColumnCode();
//				this.tableView.setItems(inputData);	
//				//fieldStudentID.clear();
//				//fieldModuleCode.clear();
//			} else {
//				//prompt blank fields
//				triggerPopUp();
//			}
//		} else {
//			if(allFieldsAreFilledUp()) {
//				organizeColumnCode();
//				//if tableview exists, add a new row to it.
//				inputData.add(new Schedule(fieldStudentID.getText(), fieldModuleCode.getText(), fieldModuleTitle.getText()));
//				tableView.setItems(inputData);
//				//fieldStudentID.clear();
//				//fieldModuleCode.clear();
//			} else {
//				//prompt blank fields
//				triggerPopUp();
//			}
//		}
//	}
	
	public void processExamPeriod() throws SQLException, ParseException, IOException {
		local.processExamPeriod(dateFrom, dateTo);
		local.browseForStudentData();
		local.browseForRegistrationData();
		local.browseForLocationData();
	}
	
	public void populateTable() throws SQLException {
		for(int i=0; i<local.getData().size(); i++) {
			Schedule s = new Schedule(local.getData().get(i).getStudentID(), local.getData().get(i).getModuleCode(), local.getData().get(i).getModuleTitle(), local.getData().get(i).getDay(), local.getData().get(i).getDate(), local.getData().get(i).getSessionID(), local.getData().get(i).getLocation());
			data.add(s);
			organizeColumnCode();
			tableView.setItems(data);
		}
 //		for(int i=0; i<local.getData().size(); i++) {
//			Schedule s = new Schedule(local.getData().get(i).getStudentID(), local.getData().get(i).getModuleCode(), local.getData().get(i).getModuleTitle(local.getData().get(i).getModuleCode()), local.getData().get(i).getDay(), local.getData().get(i).getDate(), local.getData().get(i).getSessionID(), local.getData().get(i).getLocation());
//			data.add(s);
//			organizeColumnCode();
//			tableView.setItems(data);	
//		}
	}
	
	//fill tableview
}
