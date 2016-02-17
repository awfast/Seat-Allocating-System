import java.awt.List;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.csvreader.CsvReader;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class DataReader {

	private CsvReader reader;
	protected FileChooser fileChooser = new FileChooser();
	private DB db;
	private Stage stage = new Stage();
	private HashMap<String, String> moduleCode_moduleTitle = new HashMap<String, String>();

	// student data reader
	protected void readStudentData(int id, String name) throws IOException {
		File file = fileChooser.showOpenDialog(stage);
		db = new DB();
		if (file != null) {
			String path = file.getAbsolutePath();
			reader = new CsvReader(path);
			reader.readHeaders();
			while (reader.readRecord()) {
				String x = reader.get("STUDENT ID");
				String studentName = reader.get("STUDENT NAME");
				int studentID = Integer.valueOf(x);
				id = studentID;
				name = studentName;
				try {
					db.examGenerator.students.getStudentInfo(id, name);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		} else {
			System.out.println("File's empty!");
		}
	}

	// registeredStudents data reader
	protected void readRegisteredStudentsData() throws IOException, SQLException {
		String path = "F:\\ProjectData\\RegistrationData.csv";
		System.out.println(path);
		db = new DB();
		reader = new CsvReader(path);
		reader.readHeaders();
		while (reader.readRecord()) {
			String col1 = reader.get("ModuleCode");
			String col2 = reader.get("Title");
			moduleCode_moduleTitle.put(col1, col2);
		}
		db.examGenerator.students.pushRegisteredStudentsData(moduleCode_moduleTitle);
	}

	// location data reader
	protected void readLocationData(int buildingNumber, int roomNumber, int seatNumber, int accessibleSeatsNumber)
			throws IOException {
		// File file = fileChooser.showOpenDialog(stage);
		db = new DB();
		// if (file != null) {

		// String path = file.getAbsolutePath();
		String path = "F:\\ProjectData\\LocationData.csv";
		reader = new CsvReader(path);
		reader.readHeaders();
		while (reader.readRecord()) {
			String col1 = reader.get("Building");
			String col2 = reader.get("Room");
			String col3 = reader.get("Capacity");
			String col4 = reader.get("Accessible");

			buildingNumber = Integer.valueOf(col1);
			roomNumber = Integer.valueOf(col2);
			seatNumber = Integer.valueOf(col3);
			accessibleSeatsNumber = Integer.valueOf(col4);
			
			db.examGenerator.location.storeBuildingsAndRoomsAvailable(buildingNumber, roomNumber);
			db.examGenerator.location.storeSeatsAvailable(seatNumber, accessibleSeatsNumber);
		}
		try {
			db.examGenerator.location.processData();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// }
		/*
		 * else { System.out.println("File's empty!"); }
		 */
	}
	
	protected void readDates(String dateFrom, String dateTo) throws SQLException {
		db = new DB();
		db.examGenerator.session.printDate(dateFrom, dateTo);
	}
	
	protected void generateExam() throws SQLException {
		db.examGenerator.generateInformation();
	}
}
