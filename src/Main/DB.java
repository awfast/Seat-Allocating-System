package Main;
import java.io.IOException;
import java.sql.*;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Random;

import javax.management.timer.Timer;

import javafx.scene.control.DatePicker;

public class DB {
	private final String USER = "root";
	private final String PASS = "";
	protected Connection conn = null;
	private Statement stmt = null;
	private final String DB_URL = "jdbc:mysql://localhost:3306/db";
	protected String moduleCode = null;	
	protected Students students;
	protected Session session;
	protected Location location;
	
	public DB(){
		students = new Students();
		location = new Location();		
		session = new Session();
	}
	
	public Connection getConnection(Connection conn) {
		try {
			conn = (Connection) DriverManager.getConnection(DB_URL, USER, PASS);
		} catch (SQLException se) {
		} catch (Exception e) {
		}
		return conn;
	}

	// Student table
	protected void createTableStudents(Connection conn) throws SQLException, IOException {
		// STEP 4: Execute a query
		stmt = conn.createStatement();
		String drop = "DROP TABLE IF EXISTS STUDENT ";
		String table = "CREATE TABLE IF NOT EXISTS STUDENT " + "(ID INTEGER not NULL, " + "StudentName VARCHAR(255), " + "AccessibleSeat VARCHAR(255))";

		stmt.executeUpdate(drop);
		stmt.executeUpdate(table);
	}

	
	// Location table
	protected void createTableLocation(Connection conn) throws SQLException, IOException {
		// STEP 4: Execute a query
		stmt = conn.createStatement();
		String drop = "DROP TABLE IF EXISTS Location ";
		String table = "CREATE TABLE IF NOT EXISTS LOCATION " + "(BuildingNumber INTEGER not NULL, "
				+ "RoomNumber INTEGER not NULL, " + "SeatNumber INTEGER not NULL, "
				+ "AccessibleSeatsNumber INTEGER not NULL)";

		stmt.executeUpdate(drop);
		stmt.executeUpdate(table);
	}
	
	protected void createTableCohort(Connection conn) throws SQLException, IOException {
		// STEP 4: Execute a query
		stmt = conn.createStatement();
		String drop = "DROP TABLE IF EXISTS Cohorts ";
		String table = "CREATE TABLE IF NOT EXISTS Cohorts " + "(Cohort varchar(255), "
				+ "Size INTEGER not NULL)";

		stmt.executeUpdate(drop);
		stmt.executeUpdate(table);
	}
	
	protected void createTableExam(Connection conn) throws SQLException, IOException {
		// STEP 4: Execute a query
		stmt = conn.createStatement();
		String drop = "DROP TABLE IF EXISTS Exam";
		String table = "CREATE TABLE IF NOT EXISTS Exam" + "(ModuleCode varchar(255), " + "ModuleTitle varchar(255), "
				+ "Duration INTEGER not NULL)";

		stmt.executeUpdate(drop);
		stmt.executeUpdate(table);
	}
	
	protected void createTableRegisteredStudents(Connection conn) throws SQLException, IOException {
		// STEP 4: Execute a query
		stmt = conn.createStatement();
		String drop = "DROP TABLE IF EXISTS RegisteredStudents";
		String table = "CREATE TABLE IF NOT EXISTS RegisteredStudents" + "(ID INTEGER not NULL, " + "ModuleCode VARCHAR(255)," 
				+ "ModuleTitle VARCHAR(255), " + "AccessibleSeat VARCHAR(255))";

		stmt.executeUpdate(drop);
		stmt.executeUpdate(table);
	}

	// table SESSION
	protected void createTableSession(Connection conn, String dateFrom, String dateTo) throws SQLException {
		// STEP 4: Execute a query
		stmt = conn.createStatement();
		String drop = "DROP TABLE IF EXISTS SESSION ";
		String table = "CREATE TABLE IF NOT EXISTS SESSION " + "(ID INTEGER not NULL, " + "date VARCHAR(255), " + "MorningAfternoon VARCHAR(255))";

		stmt.executeUpdate(drop);
		stmt.executeUpdate(table);
	}
	
	protected void createTableSchedule(Connection conn) throws SQLException {
		// STEP 4: Execute a query
		stmt = conn.createStatement();
		String drop = "DROP TABLE IF EXISTS Schedule ";
		String table = "CREATE TABLE IF NOT EXISTS Schedule " + "(StudentID INTEGER not NULL, " + "ModuleCode VARCHAR(255), " + "SessionID INTEGER not NULL, " + "Date VARCHAR(255), "+ "BuildingNumber INTEGER not NULL, " + "RoomNumber INTEGER not NULL)";

		stmt.executeUpdate(drop);
		stmt.executeUpdate(table);
	}
}