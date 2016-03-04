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
	private final String DB_URL = "jdbc:mysql://localhost:3306/test";
	protected String moduleCode = null;
	private int lastDayOfTheMonth = 0;
	
	private int studentID;
	private String studentName;
	
	protected Students students;
	protected Session session;
	protected Location location;
	
	public DB() {
		students = new Students();
		location = new Location();		
		session = new Session();
	}
	
	protected Connection getConnection(Connection conn) {
		try {
			System.out.println("Connecting to database...");
			conn = (Connection) DriverManager.getConnection(DB_URL, USER, PASS);
			System.out.println("Database initialised successfully");

		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}

	// Student table
	protected void createTableStudents(Connection conn) throws SQLException, IOException {
		// STEP 4: Execute a query
		System.out.println("Creating table 'STUDENT' in the given database...");
		stmt = conn.createStatement();
		String drop = "DROP TABLE IF EXISTS STUDENT ";
		String table = "CREATE TABLE IF NOT EXISTS STUDENT " + "(ID INTEGER not NULL, "
				+ "StudentName varchar(255))";

		stmt.executeUpdate(drop);
		stmt.executeUpdate(table);
		
		//----->>>>>>     dataReader.readStudentData(studentID, studentName);
	}

	
	// Location table
	protected void createTableLocation(Connection conn) throws SQLException, IOException {
		// STEP 4: Execute a query
		System.out.println("Creating table in given database...");
		stmt = conn.createStatement();
		String drop = "DROP TABLE IF EXISTS Location ";
		String table = "CREATE TABLE IF NOT EXISTS LOCATION " + "(BuildingNumber INTEGER not NULL, "
				+ "RoomNumber INTEGER not NULL, " + "SeatNumber INTEGER not NULL, "
				+ "AccessibleSeatsNumber INTEGER not NULL)";

		stmt.executeUpdate(drop);
		stmt.executeUpdate(table);
		System.out.println("Created table 'Location' in given database...");
		
		//----->>>>>>     populateLocationTable();
	}
	
	protected void createTableCohort(Connection conn) throws SQLException, IOException {
		// STEP 4: Execute a query
		System.out.println("Creating table in given database...");
		stmt = conn.createStatement();
		String drop = "DROP TABLE IF EXISTS Cohorts ";
		String table = "CREATE TABLE IF NOT EXISTS Cohorts " + "(Cohort varchar(255), "
				+ "Size INTEGER not NULL)";

		stmt.executeUpdate(drop);
		stmt.executeUpdate(table);
		System.out.println("Created table 'Cohort' in given database...");
		
		//----->>>>>>     populateLocationTable();
	}
	
	protected void createTableExam(Connection conn) throws SQLException, IOException {
		// STEP 4: Execute a query
		System.out.println("Creating table in given database...");
		stmt = conn.createStatement();
		String drop = "DROP TABLE IF EXISTS Exam";
		String table = "CREATE TABLE IF NOT EXISTS Exam" + "(ModuleCode varchar(255), "
				+ "Duration INTEGER not NULL)";

		stmt.executeUpdate(drop);
		stmt.executeUpdate(table);
		System.out.println("Created table 'Cohort' in given database...");
		
		//----->>>>>>     populateLocationTable();
	}
	
	protected void createTableRegisteredStudents(Connection conn) throws SQLException, IOException {
		// STEP 4: Execute a query
		System.out.println("Creating table in given database...");
		stmt = conn.createStatement();
		String drop = "DROP TABLE IF EXISTS RegisteredStudents";
		String table = "CREATE TABLE IF NOT EXISTS RegisteredStudents" + "(ID INTEGER not NULL, "
				+ "ModuleCode VARCHAR(255), " + "ModuleTitle VARCHAR(255))";

		stmt.executeUpdate(drop);
		stmt.executeUpdate(table);
		System.out.println("Created table 'RegisteredStudents' in given database...");
		
		//----->>>>>>     populateLocationTable();
	}

	protected void populateLocationTable() throws SQLException, IOException {
		//----->>>>>>     dataReader.readLocationData(buildingNumber, roomNumber, numberOfSeats, numberOfAccessibleSeats);
	}

	// table SESSION
	protected void createTableSession(Connection conn, String dateFrom, String dateTo) throws SQLException {
		// STEP 4: Execute a query
		System.out.println("Creating table in given database...");
		stmt = conn.createStatement();
		String drop = "DROP TABLE IF EXISTS SESSION ";
		String table = "CREATE TABLE IF NOT EXISTS SESSION " + "(ID INTEGER not NULL, " + "date VARCHAR(255), " + "MorningAfternoon VARCHAR(255))";

		stmt.executeUpdate(drop);
		stmt.executeUpdate(table);
		System.out.println("Created table 'SESSION' in given database...");
		pushSessionData(conn, dateFrom, dateTo);
	}
	
	protected void createTableSchedule(Connection conn) throws SQLException {
		// STEP 4: Execute a query
		System.out.println("Creating table in given database...");
		stmt = conn.createStatement();
		String drop = "DROP TABLE IF EXISTS Schedule ";
		String table = "CREATE TABLE IF NOT EXISTS Schedule " + "(StudentID INTEGER not NULL, " + "ModuleCode VARCHAR(255), " + "SessionID INTEGER not NULL, " +"BuildingNumber INTEGER not NULL, " + "RoomNumber INTEGER not NULL)";

		stmt.executeUpdate(drop);
		stmt.executeUpdate(table);
		System.out.println("Created table 'Schedule' in given database...");
	}
	
	protected void pushSessionData(Connection conn, String dateFrom, String dateTo) {
		System.out.println(dateFrom + "<--->" + dateTo);
	}
}