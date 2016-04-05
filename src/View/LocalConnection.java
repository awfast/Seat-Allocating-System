package View;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.format.DateTimeFormatter;

import Main.DB;
import Main.DataReader;
import Main.Schedule;
import javafx.scene.control.DatePicker;

public class LocalConnection {

	private Connection conn;
	private DB db = new DB();
	private DataReader dataReader = new DataReader();
	private String examPeriodFrom, examPeriodTo;
	
	public Connection getConnection() {
		return this.conn = db.getConnection(conn);
	}
	
	public void browseForStudentData() throws SQLException, IOException {
		getConnection();
		dataReader.getStudentID(db, conn);
	}
	
	public void browseForRegistrationData() throws SQLException, IOException {
		dataReader.generateRegisteredStudentsData();
	}
	
	public void browseForLocationData() throws SQLException, IOException {
		dataReader.getLocations();
		Schedule exam = new Schedule(1, "", 1, " ", "", 0, 0);
		exam.generateInformation(db.getConnection(conn), dataReader);
	}
	
	public void processExamPeriod(DatePicker dateFrom, DatePicker dateTo) throws SQLException, ParseException {
		examPeriodFrom = dateFrom.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		examPeriodTo = dateTo.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		dataReader.createExamPeriod(db, db.getConnection(conn), examPeriodFrom, examPeriodTo);
	}

		
}
