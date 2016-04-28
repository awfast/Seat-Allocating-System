package View;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Callback;

public class MainPanel implements Initializable {

	// BLOCK CAPITALS FOR EVERY NEW
	// ENTRY!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	LocalConnection local = new LocalConnection();
	PopUp pop = new PopUp();
	// has been reset previously
	boolean reset = false;

	// flags for data collection options
	boolean flag1, flag2, flag3 = false;

	@FXML // fx:id="mainPane"
	private Pane mainPane;

	@FXML // fx:id="examPeriodPane"
	private Pane examPeriodPane;

	@FXML // fx:id="tablePane"
	private Pane tablePane;

	@FXML // fx:id="topPane"
	private Pane topPane;

	@FXML // fx:id="bottomPane"
	private Pane bottomPane;

	@FXML // fx:id="addButton"
	private Button addButton;

	@FXML // fx:id="deleteButton"
	private Button deleteButton;

	@FXML // fx:id="goButton"
	private Button goButton;

	@FXML // fx:id="searchButton"
	private Button searchButton;

	@FXML // fx:id="resetButton"
	private Button resetButton;

	@FXML // fx:id="exportButton"
	private Button exportButton;

	@FXML // fx:id="importButton1"
	private Button importButton1;

	@FXML // fx:id="importButton2"
	private Button importButton2;

	@FXML // fx:id="importButton3"
	private Button importButton3;

	@FXML // fx:id="labelStudentID"
	private Text labelStudentID;

	@FXML // fx:id="labelModuleCode"
	private Text labelModuleCode;

	@FXML // fx:id="labelDate"
	private Text labelDate;

	@FXML // fx:id="labelBuildingNumber"
	private Text labelBuildingNumber;

	@FXML // fx:id="labelRoomNumber"
	private Text labelRoomNumber;

	@FXML // fx:id="fieldStudentID"
	private TextField fieldStudentID;

	@FXML // fx:id="fieldModuleCode"
	private TextField fieldModuleCode;

	@FXML // fx:id="fieldModuleTitle"
	private TextField fieldModuleTitle;

	@FXML // fx:id="fieldDay"
	private TextField fieldDay;

	@FXML // fx:id="fieldDate"
	private TextField fieldDate;

	@FXML // fx:id="fieldSession"
	private TextField fieldSession;

	@FXML // fx:id="fieldImportStudentData"
	private TextField fieldImportStudentData;

	@FXML // fx:id="fieldimportRegistrationData"
	private TextField fieldimportRegistrationData;

	@FXML // fx:id="fieldImportLocationData"
	private TextField fieldImportLocationData;

	@FXML // fx:id="fieldLocation"
	private TextField fieldLocation;

	@FXML // fx:id="fieldSearch"
	private TextField fieldSearch;

	@FXML // fx:id="dateFrom"
	private DatePicker dateFrom;

	@FXML // fx:id="dateTo"
	private DatePicker dateTo;

	@FXML // fx:id="columnCode"
	private TableColumn<Schedule, String> columnStudent;

	@FXML // fx:id="columnCode"
	private TableColumn<Schedule, String> columnCode;

	@FXML // fx:id="columnCode"
	private TableColumn<Schedule, String> columnTitle;

	@FXML // fx:id="columnDay"
	private TableColumn<Schedule, String> columnDay;

	@FXML // fx:id="columnDate"
	private TableColumn<Schedule, String> columnDate;

	@FXML // fx:id="columnSession"
	private TableColumn<Schedule, String> columnSession;

	@FXML // fx:id="columnLocation"
	private TableColumn<Schedule, String> columnLocation;

	@FXML // fx:id="treeTable"
	private TableView<View.Schedule> tableView;

	// list with values for column 1
	private ObservableList<View.Schedule> data = FXCollections.observableArrayList();

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		preSetText();
		preSetDatePicker();

		fieldStudentID.setOnMouseClicked(event -> {
			fieldStudentID.clear();
		});

		fieldModuleCode.setOnMouseClicked(event -> {
			fieldModuleCode.clear();
		});

		fieldModuleTitle.setOnMouseClicked(event -> {
			fieldModuleTitle.clear();
		});

		fieldDay.setOnMouseClicked(event -> {
			fieldDay.clear();
		});

		fieldDate.setOnMouseClicked(event -> {
			fieldDate.clear();
		});

		fieldSession.setOnMouseClicked(event -> {
			fieldSession.clear();
		});

		fieldLocation.setOnMouseClicked(event -> {
			fieldLocation.clear();
		});

		fieldSearch.setOnMouseClicked(event -> {
			fieldSearch.clear();
		});

		fieldImportStudentData.setOnMouseClicked(event -> {
			fieldImportStudentData.clear();
		});

		fieldimportRegistrationData.setOnMouseClicked(event -> {
			fieldimportRegistrationData.clear();
		});

		fieldImportLocationData.setOnMouseClicked(event -> {
			fieldImportLocationData.clear();
		});

		addButton.setOnAction(event -> {
			addRow();
		});

		deleteButton.setOnAction(event -> {
			Schedule p = tableView.getSelectionModel().getSelectedItem();
			if (p == null) {
				pop.deleteIndexNotSelected();
				return;
			} else {
				if (pop.extraCheck()) {
					data.remove(p);
					tableView.setItems(data);
				} else {
					return;
				}
			}
		});

		exportButton.setOnAction(event -> {
			PDFExport pdf = new PDFExport();
			pdf.export2(data);
		});

		importButton1.setOnAction(event -> {
			try {
				fieldImportStudentData.setText(processStudentData());
				fieldImportStudentData.setDisable(true);
				importButton1.setDisable(true);
				pop.studentsImportedSuccessfully();
			} catch (Exception e) {
			}
		});

		importButton2.setOnAction(event -> {
			try {
				fieldimportRegistrationData.setText(processsRegistrationData());
				fieldimportRegistrationData.setDisable(true);
				pop.registrationImportedSuccessfully();

			} catch (Exception e) {
			}
		});

		importButton3.setOnAction(event -> {
			try {
				fieldImportLocationData.setText(processLocationData());
				fieldImportLocationData.setDisable(true);
				pop.locationsImportedSuccessfully();
			} catch (Exception e) {
			}
		});

		goButton.setOnAction(event -> {
			try {
				if (dateFrom.getValue() != null && dateTo.getValue() != null) {
					String examPeriodFrom = dateFrom.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
					String examPeriodTo = dateTo.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
					if (pop.confirmExamPeriod(examPeriodFrom, examPeriodTo)) {
						if (importButton1.isDisabled() || importButton2.isDisabled() || importButton3.isDisabled()) {
							populateTable();
							importButton1.setDisable(false);
							importButton2.setDisable(false);
							importButton3.setDisable(false);
							fieldImportStudentData.setDisable(false);
							fieldimportRegistrationData.setDisable(false);
							fieldImportLocationData.setDisable(false);
						} else {
							pop.fileNotImported();
						}

					}
					return;
				} else {
					pop.examPeriodNotSelected();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		fieldSearch.textProperty().addListener(new ChangeListener<Object>() {
			public void changed(ObservableValue<?> observable, Object oldVal, Object newVal) {

				handleSearchByKey((String) oldVal, (String) newVal);

			}
		});
	}

	private void organizeColumnCode() {
		columnStudent.setCellValueFactory(new PropertyValueFactory<View.Schedule, String>("studentID"));
		columnCode.setCellValueFactory(new PropertyValueFactory<View.Schedule, String>("moduleCode"));
		columnTitle.setCellValueFactory(new PropertyValueFactory<View.Schedule, String>("moduleTitle"));
		columnDay.setCellValueFactory(new PropertyValueFactory<View.Schedule, String>("day"));
		columnDate.setCellValueFactory(new PropertyValueFactory<View.Schedule, String>("date"));
		columnSession.setCellValueFactory(new PropertyValueFactory<View.Schedule, String>("sessionName"));
		columnLocation.setCellValueFactory(new PropertyValueFactory<View.Schedule, String>("location"));
	}

	// loading bar
	private boolean allFieldsAreFilledUp() {
		if (fieldStudentID.getText().trim().isEmpty() || fieldModuleCode.getText().trim().isEmpty()
				|| fieldModuleTitle.getText().trim().isEmpty()) {
			return false;
		} else {
			return true;
		}
	}

	private void addRow() {
		if (reset == true) {
			if (allFieldsAreFilledUp()) {
				reset = false;
				tableView.setLayoutX(14);
				tableView.setLayoutY(58);
				tableView.setPrefSize(1235, 584);
				int studentID = Integer.getInteger(fieldStudentID.getText());
				data.add(new Schedule(studentID, fieldModuleCode.getText(), fieldModuleTitle.getText(),
						fieldDay.getText(), fieldDate.getText(), fieldSession.getText(), fieldLocation.getText()));
				organizeColumnCode();
				this.tableView.setItems(data);
				fieldStudentID.clear();
				fieldModuleCode.clear();
				fieldModuleTitle.clear();
				fieldDay.clear();
				fieldDate.clear();
				fieldSession.clear();
				fieldLocation.clear();
			} else {
				pop.invalidID();
			}
		} else {
			if (allFieldsAreFilledUp()) {
				reset = false;
				tableView.setLayoutX(14);
				tableView.setLayoutY(58);
				tableView.setPrefSize(1235, 584);
				String str = fieldStudentID.getText();
				if (validateID()) {
					int studentID = Integer.parseInt(str);
					data.add(new Schedule(studentID, fieldModuleCode.getText(), fieldModuleTitle.getText(),
							fieldDay.getText(), fieldDate.getText(), fieldSession.getText(), fieldLocation.getText()));
					organizeColumnCode();
					this.tableView.setItems(data);
					fieldStudentID.clear();
					fieldModuleCode.clear();
					fieldModuleTitle.clear();
					fieldDay.clear();
					fieldDate.clear();
					fieldSession.clear();
					fieldLocation.clear();
				} else {
					pop.invalidID();
				}

			} else {
				pop.notFilledIn();
			}
		}
	}

	public String processStudentData() throws SQLException, IOException {
		return local.browseForStudentData();
	}

	public String processsRegistrationData() throws SQLException, IOException {
		return local.browseForRegistrationData();
	}

	public String processLocationData() throws SQLException, IOException {
		return local.browseForLocationData();
	}

	public void populateTable() throws SQLException, ParseException {
		local.processExamPeriod(dateFrom, dateTo);
		for (int i = 0; i < local.getData().size(); i++) {
			String session = local.getData().get(i).getSessionString();
			Schedule s = new Schedule(local.getData().get(i).getStudentID(), local.getData().get(i).getModuleCode(),
					local.getData().get(i).getModuleTitle(), local.getData().get(i).getDay().substring(0, 3),
					local.getData().get(i).getDate(), session, local.getData().get(i).getLocation());
			data.add(s);
			organizeColumnCode();
			tableView.setItems(data);
		}
	}

	private boolean validateID() {
		if (!fieldStudentID.getText().matches("\\d+")) {
			pop.invalidID();
			return false;
		}
		return true;
	}

	public void handleSearchByKey(String oldVal, String newVal) {
		if (oldVal != null && (newVal.length() < oldVal.length())) {
			tableView.setItems(data);
		}

		String[] parts = newVal.toUpperCase().split(" ");

		ObservableList<View.Schedule> subentries = FXCollections.observableArrayList();
		for (Object entry : tableView.getItems()) {
			boolean match = true;
			Schedule schedule = (Schedule) entry;
			for (String part : parts) {
				if (!schedule.getStudentID().toUpperCase().contains(part)) {
					match = false;
					break;
				}
			}

			if (match) {
				Schedule s = (Schedule) entry;
				subentries.add(s);
			}
		}
		tableView.setItems(subentries);
	}

	public void preSetDatePicker() {
		final Callback<DatePicker, DateCell> dayCellFactory = new Callback<DatePicker, DateCell>() {
			@Override
			public DateCell call(final DatePicker datePicker) {
				return new DateCell() {
					@Override
					public void updateItem(LocalDate item, boolean empty) {
						super.updateItem(item, empty);

						if (dateFrom.getValue() == null || item.isBefore(dateFrom.getValue().plusDays(1))) {
							setDisable(true);
							setStyle("-fx-background-color: #ffc0cb;");
						}
						if (dateFrom.getValue() == null) {
							return;
						}
						long p = ChronoUnit.DAYS.between(dateFrom.getValue(), item);
						setTooltip(new Tooltip("The exam period your are about to select is " + p + " days long."));
					}
				};
			}
		};
		dateTo.setDayCellFactory(dayCellFactory);
	}

	public void preSetText() {
		fieldImportStudentData
				.setStyle("" + "-fx-font-style: italic;" + "-fx-text-fill: grey;" + "-fx-font-family: Arial;");
		fieldImportStudentData.setText("E.g. StudentData.csv");

		fieldimportRegistrationData
				.setStyle("" + "-fx-font-style: italic;" + "-fx-text-fill: grey;" + "-fx-font-family: Arial;");
		fieldimportRegistrationData.setText("E.g. RegistrationData.csv");

		fieldImportLocationData
				.setStyle("" + "-fx-font-style: italic;" + "-fx-text-fill: grey;" + "-fx-font-family: Arial;");
		fieldImportLocationData.setText("E.g. LocationData.csv");

		fieldSearch.setStyle("" + "-fx-font-style: italic;" + "-fx-text-fill: grey;" + "-fx-font-family: Arial;");
		fieldSearch.setText("Search ID");

		fieldStudentID.setStyle("" + "-fx-font-style: italic;" + "-fx-text-fill: grey;" + "-fx-font-family: Arial;");
		fieldStudentID.setText("E.g. 2583827");

		fieldModuleCode.setStyle("" + "-fx-font-style: italic;" + "-fx-text-fill: grey;" + "-fx-font-family: Arial;");
		fieldModuleCode.setText("E.g. CENV6141");

		fieldModuleTitle.setStyle("" + "-fx-font-style: italic;" + "-fx-text-fill: grey;" + "-fx-font-family: Arial;");
		fieldModuleTitle.setText("E.g. Bioenergy ");

		fieldDay.setStyle("" + "-fx-font-style: italic;" + "-fx-text-fill: grey;" + "-fx-font-family: Arial;");
		fieldDay.setText("E.g. MON");

		fieldDate.setStyle("" + "-fx-font-style: italic;" + "-fx-text-fill: grey;" + "-fx-font-family: Arial;");
		fieldDate.setText("E.g. 26/04/2016");

		fieldSession.setStyle("" + "-fx-font-style: italic;" + "-fx-text-fill: grey;" + "-fx-font-family: Arial;");
		fieldSession.setText("E.g. 3 (AM)");

		fieldLocation.setStyle("" + "-fx-font-style: italic;" + "-fx-text-fill: grey;" + "-fx-font-family: Arial;");
		fieldLocation.setText("E.g. Building 3, Room 2001, Seat: A1");
	}
}
