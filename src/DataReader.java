import java.awt.List;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.csvreader.CsvReader;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class DataReader {

	private String dateFrom = null;
	private String dateTo = null;
	private String studentName = null;
	private CsvReader reader;
	protected FileChooser fileChooser = new FileChooser();
	protected DB db = null;
	private Stage stage = new Stage();
	private HashMap<String, String> moduleCode_moduleTitle = new HashMap<String, String>();
	private Connection conn = null;
	
	protected void createExamPeriod(DB db, Connection conn, String examPeriodFrom, String examPeriodTo) throws SQLException {
		this.db = db;
		this.conn = conn;
		db.createTableSession(conn, examPeriodFrom, examPeriodTo);
		this.dateFrom = examPeriodFrom;
		this.dateTo = examPeriodTo;
	}
	
	// student data reader	
	protected void getStudentID(int id, String studentName) throws SQLException, IOException {
		File file = fileChooser.showOpenDialog(stage);
		db.createTableStudents(conn);
		if (file != null) {
			String path = file.getAbsolutePath();
			try {
				reader = new CsvReader(path);
				reader.readHeaders();
				
				while (reader.readRecord()) {
					String x = reader.get("STUDENT ID");
					int studentID = Integer.valueOf(x);
					id = studentID;
					String name = reader.get("STUDENT NAME");
					studentName = name;
					db.students.pushStudentData(id, name);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		} else {
			System.out.println("File's empty!");
		}
	}

	// registeredStudents data reader
	protected void readRegisteredStudentsData() throws IOException, SQLException {
		db.createTableRegisteredStudents(conn);
		String path = "F:\\ProjectData\\RegistrationData.csv";
		System.out.println(path);
		reader = new CsvReader(path);
		reader.readHeaders();
		while (reader.readRecord()) {
			String col1 = reader.get("ModuleCode");
			String col2 = reader.get("Title");
			moduleCode_moduleTitle.put(col1, col2);
		}
		db.students.pushRegisteredStudentsData(moduleCode_moduleTitle);
		//db.examGenerator.students.pushRegisteredStudentsData(moduleCode_moduleTitle);
	}
	
	protected void getLocations() throws IOException, SQLException {
		db.createTableLocation(conn);
		getAvailableBuildings();
		//db.location.storeLocationInformation(getAvailableBuildings(buildings), getAvailableRooms(rooms), getAvailableSeats(numberOfSeats), getAvailableAccessibleSeats(numberOfAccessibleSeats));		
	}
	
	protected void getAvailableBuildings() throws IOException, SQLException {
		String path = "F:\\ProjectData\\LocationData.csv";
		reader = new CsvReader(path);
		reader.readHeaders();
		while (reader.readRecord()) {
			String col1 = reader.get("Building");
			String col2 = reader.get("Room");
			String col3 = reader.get("Capacity");
			String col4 = reader.get("Accessible");
			int buildings = Integer.valueOf(col1);
			int rooms = Integer.valueOf(col2);
			int numberOfSeats = Integer.valueOf(col3);
			int numberOfAccessibleSeats = Integer.valueOf(col4);
			
			db.location.storeLocationInformation(buildings, rooms, numberOfSeats, numberOfAccessibleSeats);	
		}
	}
}
