package View;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import Main.DB;
import Main.DataReader;
import Main.Schedule;
import javafx.scene.control.DatePicker;

public class LocalConnection {

	private Connection conn;
	private DB db = new DB();
	private DataReader dataReader = new DataReader();
	private String examPeriodFrom, examPeriodTo;
	private Schedule exam = new Schedule(1, "", 1, "", "", 0, 0);
	
	public Connection getConnection() {
		return this.conn = db.getConnection(conn);
	}
	
	public String browseForStudentData() throws SQLException, IOException {
		getConnection();
		return dataReader.getStudentID(db, conn);
	}
	
	public String browseForRegistrationData() throws SQLException, IOException {
		return dataReader.generateRegisteredStudentsData();
	}
	
	public String browseForLocationData() throws SQLException, IOException {
		return dataReader.getLocations();
	}
	
	public void processExamPeriod(DatePicker dateFrom, DatePicker dateTo) throws SQLException, ParseException {
		examPeriodFrom = dateFrom.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		examPeriodTo = dateTo.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		dataReader.createExamPeriod(db, db.getConnection(conn), examPeriodFrom, examPeriodTo);
		exam.generateInformation(db.getConnection(conn), dataReader);
	}

	public ArrayList<Main.Schedule> getData() {
		return exam.getFinalSchedules(conn);
	}
}
