package Main;
import java.awt.List;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Array;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.csvreader.CsvReader;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class DataReader {

	private String dateFrom = null;
	private String dateTo = null;
	private CsvReader reader;
	protected FileChooser fileChooser = new FileChooser();
	protected DB db = null;
	private Stage stage = new Stage();
	private Connection conn = null;
	private String[] cohorts = { "ADMN", "ANTH", "ARCH", "ARTD", "AUDI", "BIOL", "CENV", "CHEM", "CHIN", "CHIN", "COMP",
			"CRIM", "CZEC", "DUTC", "ECON", "ECSG", "EDUC", "ELEC", "ENTR", "FEEG", "FILM", "FREN", "GEOG", "GERM",
			"GREE", "HIST", "HLTH", "HPRS", "HSQM", "HUMA", "IFYP", "IPLU", "ITAL", "JAPA", "LANG", "LATI", "LAWS",
			"LING", "LLLL", "MANG", "MATH", "MEDI", "MUSI", "NPCG", "NPCH", "NPMH", "NPMS", "NQCG", "NURS", "OCCT",
			"OPTO", "PAIR", "PHIL", "PHYS", "PODY", "POLS", "PORT", "PRES", "PSIO", "PSYC", "RESM", "RUSS", "SESG",
			"SESM", "SESS", "SOCI", "SOES", "SPAN", "SAAS", "STAT", "UOSM", "WEBS" };

	public void createExamPeriod(DB db, Connection conn, String examPeriodFrom, String examPeriodTo)
			throws SQLException, ParseException {
		this.db = db;
		this.conn = conn;
		db.createTableSession(conn, examPeriodFrom, examPeriodTo);
		db.createTableSchedule(conn);
		db.session.testDate(conn, examPeriodFrom, examPeriodTo);
		this.dateFrom = examPeriodFrom;
		this.dateTo = examPeriodTo;
		db.students.getConnection();
	}

	// student data reader
	public void getStudentID(int id, String studentName) throws SQLException, IOException {
		//File file = fileChooser.showOpenDialog(stage);
		db.createTableStudents(conn);
		/*if (file != null) {
			String path = file.getAbsolutePath();*/
		String path = "F:\\ProjectData\\StudentData.csv";
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
		} 
	/*else {
			System.out.println("File's empty!");
		}*/
	//}

	public void generateRegisteredStudentsData() throws IOException, SQLException {
		db.createTableCohort(conn);
		db.createTableExam(conn);
		db.createTableRegisteredStudents(conn);
		db.students.populateCohorts(cohorts);
		String path = "F:\\ProjectData\\RegistrationData2.csv";
		System.out.println(path);
		reader = new CsvReader(path);
		reader.readHeaders();
		while (reader.readRecord()) {
			String col1 = reader.get("ModuleCode");
			String col2 = reader.get("Title");
			String col3 = reader.get("Duration");
			int valCol3 = Integer.valueOf(col3);
			db.students.pushModuleCodes(col1, col2, valCol3);
		}
		System.out.println("Completed.");
	}

	public void getLocations() throws IOException, SQLException {
		db.createTableLocation(conn);
		getAvailableBuildings();
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
			db.location.storeLocationInformation(conn, buildings, rooms, numberOfSeats, numberOfAccessibleSeats);
		}
		db.location.processData();
	}
}
