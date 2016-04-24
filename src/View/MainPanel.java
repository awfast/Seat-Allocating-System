package View;

import java.awt.Font;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;

import com.itextpdf.text.log.SysoCounter;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.application.Platform;
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
import javafx.util.Callback;
public class MainPanel implements Initializable {

	// BLOCK CAPITALS FOR EVERY NEW
	// ENTRY!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	LocalConnection local = new LocalConnection();
	SimpleSearch search = new SimpleSearch();
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
			// if has been reset, add new data to the table
			addRow();
			// TODO
		});

		deleteButton.setOnAction(event -> {
			// remove selected item from the table list
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
				/*
				 * Stage st = new Stage(); st.setX(1438); st.setY(456); PI pi =
				 * new PI(); pi.start(st);
				 */
				fieldImportStudentData.setText(processStudentData());
				fieldImportStudentData.setDisable(true);
				importButton1.setDisable(true);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});

		importButton2.setOnAction(event -> {
			try {
				/*
				 * Stage st = new Stage(); st.setX(1438); st.setY(456); PI pi =
				 * new PI(); pi.start(st);
				 */
				fieldimportRegistrationData.setText(processsRegistrationData());
				fieldimportRegistrationData.setDisable(true);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});

		importButton3.setOnAction(event -> {
			try {
				/*
				 * Stage st = new Stage(); st.setX(1438); st.setY(456); PI pi =
				 * new PI(); pi.start(st);
				 */
				fieldImportLocationData.setText(processLocationData());
				fieldImportLocationData.setDisable(true);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});

		goButton.setOnAction(event -> {
			try {
				if (dateFrom.getValue() != null && dateTo.getValue() != null) {
					String examPeriodFrom = dateFrom.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
					String examPeriodTo = dateTo.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
					if (!dateFrom.equals(examPeriodFrom) || !dateTo.equals(examPeriodTo)) {
						pop.examPeriodFormat();
					} else {
						if (pop.confirmExamPeriod(examPeriodFrom, examPeriodTo)) {
							if (importButton1.isDisabled() || importButton2.isDisabled()
									|| importButton3.isDisabled()) {
								processExamPeriod();
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
					}
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

	public void processExamPeriod() throws SQLException, ParseException, IOException {
		long startTime = System.currentTimeMillis();
		local.processExamPeriod(dateFrom, dateTo);
		long estimatedTime = System.currentTimeMillis() - startTime;
		System.out.println(estimatedTime);
	}

	public void populateTable() throws SQLException {
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
		// If the number of characters in the text box is less than last time
		// it must be because the user pressed delete
		if (oldVal != null && (newVal.length() < oldVal.length())) {
			// Restore the lists original set of entries
			// and start from the beginning
			tableView.setItems(data);
		}

		// Break out all of the parts of the search text
		// by splitting on white space
		String[] parts = newVal.toUpperCase().split(" ");

		// Filter out the entries that don't contain the entered text
		ObservableList<View.Schedule> subentries = FXCollections.observableArrayList();
		for (Object entry : tableView.getItems()) {
			boolean match = true;
			Schedule schedule = (Schedule) entry;
			for (String part : parts) {
				// The entry needs to contain all portions of the
				// search string *but* in any order
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
		final Callback<DatePicker, DateCell> dayCellFactory = 
	            new Callback<DatePicker, DateCell>() {
	                @Override
	                public DateCell call(final DatePicker datePicker) {
	                    return new DateCell() {
	                        @Override
	                        public void updateItem(LocalDate item, boolean empty) {
	                            super.updateItem(item, empty);
	                           
	                            if (item.isBefore(
	                                    dateFrom.getValue().plusDays(1))
	                                ) {
	                                    setDisable(true);
	                                    setStyle("-fx-background-color: #ffc0cb;");
	                            }   
	                            long p = ChronoUnit.DAYS.between(
	                                    dateFrom.getValue(), item
	                            );
	                            setTooltip(new Tooltip(
	                                "The exam period your are about to select is " + p + " days long.")
	                            );
	                    }
	                };
	            }
	        };
	        dateTo.setDayCellFactory(dayCellFactory);
	}

	public void preSetText() {
		fieldImportStudentData
				.setStyle("" + "-fx-font-style: italic;" + "-fx-text-fill: grey;" + "-fx-font-family: Arial;");
		fieldImportStudentData.setText("e.g. StudentData.csv");

		fieldimportRegistrationData
				.setStyle("" + "-fx-font-style: italic;" + "-fx-text-fill: grey;" + "-fx-font-family: Arial;");
		fieldimportRegistrationData.setText("e.g. RegistrationData.csv");

		fieldImportLocationData
				.setStyle("" + "-fx-font-style: italic;" + "-fx-text-fill: grey;" + "-fx-font-family: Arial;");
		fieldImportLocationData.setText("e.g. LocationData.csv");

		fieldSearch.setStyle("" + "-fx-font-style: italic;" + "-fx-text-fill: grey;" + "-fx-font-family: Arial;");
		fieldSearch.setText("Search ID");

		fieldStudentID.setStyle("" + "-fx-font-style: italic;" + "-fx-text-fill: grey;" + "-fx-font-family: Arial;");
		fieldStudentID.setText("Student ID");

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
