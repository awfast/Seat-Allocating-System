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
	private Connection conn = null;
	private String[] cohorts = { "ADMN", "ANTH", "ARAB", "ARCH", "ARTD", "AUDI", "BIOL", "CENV", "CHEM", "CHIN", "CHIN", "COMP",
			"CRIM", "CZEC", "DUTC", "ECON", "ECSG", "EDUC", "ELEC", "ENGL", "ENTR", "FEEG", "FILM", "FREN", "GEOG", "GERM",
			"GREE", "HIST", "HLTH", "HPRS", "HSQM", "HUMA", "IFYP", "IPLU", "ITAL", "JAPA", "LANG", "LATI", "LAWS",
			"LING", "LLLL", "MANG", "MATH", "MEDI", "MUSI", "NPCG", "NPCH", "NPMH", "NPMS", "NQCG", "NURS", "OCCT",
			"OPTO", "PAIR", "PHIL", "PHYS", "PODY", "POLS", "PORT", "PRES", "PSIO", "PSYC", "RESM", "RUSS", "SESG",
			"SESM", "SESS", "SOCI", "SOES", "SPAN", "SSAS", "STAT", "UOSM", "WEBS" };

	public void createExamPeriod(DB db, Connection conn, String examPeriodFrom, String examPeriodTo)
			throws SQLException, ParseException {
		this.db = db;
		this.conn = conn;
		db.createTableSession(conn, examPeriodFrom, examPeriodTo);
		db.createTableSchedule(conn);
		db.session.testDate(conn, examPeriodFrom, examPeriodTo);
		this.dateFrom = examPeriodFrom;
		this.dateTo = examPeriodTo;
	}

	// student data reader
	public String getStudentID(DB db, Connection conn) throws SQLException, IOException {
		Stage stage = new Stage();
		File file = fileChooser.showOpenDialog(stage);
		db.students.getConnection();
		this.db = db;
		this.conn = conn;
		db.createTableStudents(conn);
		if (file != null) {
			String path = file.getAbsolutePath();
			try {
				reader = new CsvReader(path);
				reader.readHeaders();
				while (reader.readRecord()) {
					String x = reader.get("STUDENT ID");
					int studentID = Integer.valueOf(x);
					String name = reader.get("STUDENT NAME");
					String accessible = reader.get("ACCESSIBLE");
					db.students.pushStudentData(studentID, name, accessible);
				}
				return file.getName();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
		else {
			System.out.println("File's empty!");
		}
		return null;
	}

	public String generateRegisteredStudentsData() throws IOException, SQLException {
		Stage stage = new Stage();
		File file = fileChooser.showOpenDialog(stage);
		if (file != null) {
			String path = file.getAbsolutePath();
			db.createTableCohort(conn);
			db.createTableExam(conn);
			db.createTableRegisteredStudents(conn);
			db.students.populateCohorts(cohorts);
			reader = new CsvReader(path);
			reader.readHeaders();
			while (reader.readRecord()) {
				String col1 = reader.get("ModuleCode");
				String col2 = reader.get("Title");
				String col3 = reader.get("Duration");
				int valCol3 = Integer.valueOf(col3);
				db.students.pushModuleCodes(col1, col2, valCol3);
			}
			return file.getName();
		} else {
			System.out.println("File's empty");
		}
		return null;
	}

	public String getLocations() throws IOException, SQLException {
		db.createTableLocation(conn);
		return getAvailableBuildings();
	}

	protected String getAvailableBuildings() throws IOException, SQLException {
		Stage stage = new Stage();
		File file = fileChooser.showOpenDialog(stage);
		if (file != null) {
			String path = file.getAbsolutePath();
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
			return file.getName();
		}
		return null;
	}
}
