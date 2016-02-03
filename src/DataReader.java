import java.awt.List;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import com.csvreader.CsvReader;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class DataReader {

	private CsvReader reader;
	protected FileChooser fileChooser = new FileChooser();
	private DB db;
	private Stage stage = new Stage();
	private Students students = new Students();
	ArrayList<String> moduleCodes = new ArrayList<String>();

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
					students.getStudentInfo(id, name);
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
	protected void readRegisteredStudentsData() throws IOException {
		String path = "F:\\ProjectData\\RegistrationData.csv";
		System.out.println(path);
		db = new DB();
		reader = new CsvReader(path);
		reader.readHeaders();
		while (reader.readRecord()) {
			String col1 = reader.get("ModuleCode");
			moduleCodes.add(col1);
		}
		storedRegisteredStudentsData(moduleCodes);
	}

	// location data reader
	protected void readLocationData(int buildingNumber, int roomNumber, int seatNumber, int accessibleSeatsNumber)
			throws IOException {
		Location location = new Location();
		File file = fileChooser.showOpenDialog(stage);
		db = new DB();
		if (file != null) {
			String path = file.getAbsolutePath();
			reader = new CsvReader(path);
			reader.readHeaders();
			while (reader.readRecord()) {
				String col1 = reader.get("BuildingNumber");
				String col2 = reader.get("RoomNumber");
				String col3 = reader.get("Capacity");
				String col4 = reader.get("Accessible");

				buildingNumber = Integer.valueOf(col1);
				roomNumber = Integer.valueOf(col2);
				seatNumber = Integer.valueOf(col3);
				accessibleSeatsNumber = Integer.valueOf(col4);
				try {
					location.getLocationInfo(buildingNumber, roomNumber, seatNumber, accessibleSeatsNumber);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} else {
			System.out.println("File's empty!");
		}
	}

	protected void storedRegisteredStudentsData(ArrayList moduleCodes) {
		this.moduleCodes = moduleCodes;
		students.pushRegisteredStudentsData(moduleCodes);
	}

}
