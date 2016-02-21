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

	protected void populateLocationTable() throws SQLException, IOException {
		//----->>>>>>     dataReader.readLocationData(buildingNumber, roomNumber, numberOfSeats, numberOfAccessibleSeats);
	}

	// registeredStudents table
	protected void createTableRegisteredStudents(Connection conn) throws SQLException, IOException {
		// STEP 4: Execute a query
		System.out.println("Creating table in given database...");
		stmt = conn.createStatement();
		String drop = "DROP TABLE IF EXISTS REGISTRATION ";
		String table = "CREATE TABLE IF NOT EXISTS REGISTRATION " + "(StudentID INTEGER not NULL, "
				+ "ModuleCode varchar(255), " + "Title VARCHAR(255))";

		stmt.executeUpdate(drop);
		stmt.executeUpdate(table);
		System.out.println("Created table 'REGISTRATION' in given database...");
		
		//----->>>>>>     dataReader.readRegisteredStudentsData();
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
	
	protected void pushSessionData(Connection conn, String dateFrom, String dateTo) {
		System.out.println(dateFrom + "<--->" + dateTo);
	}
	
	// table EXAM
	protected void createTableExam(String examPeriodFrom, String examPeriodTo) throws SQLException, IOException {
		// STEP 4: Execute a query
		System.out.println("Creating table in given database...");
		stmt = conn.createStatement();
		String drop = "DROP TABLE IF EXISTS EXAM ";
		String table = "CREATE TABLE IF NOT EXISTS EXAM " + "(Code varchar(255), " + "Title VARCHAR(255), "
				+ "Day VARCHAR(255), "
				+"Date VARCHAR(255), " + "Session VARCHAR(255), " + "Duration VARCHAR(255), " + "Location VARCHAR(255))";

		stmt.executeUpdate(drop);
		stmt.executeUpdate(table);
		System.out.println("Created table 'EXAM' in given database...");
		//----->>>>>>     dataReader.generateExam();
	}
	
	/*private void printDate(String dateFrom, String dateTo) {
		String[] fromDates = dateFrom.split("/");
		String[] toDates = dateTo.split("/");
		String fromDay = fromDates[0];
		String fromMonth = fromDates[1];
		String fromYear = fromDates[2];
		String toDay = toDates[0];
		String toMonth = toDates[1];
		String toYear = toDates[2];
		
		int fD = Integer.valueOf(fromDay);
		int fM = Integer.valueOf(fromMonth);
		int fY = Integer.valueOf(fromYear);
		int toD = Integer.valueOf(toDay);
		int toM = Integer.valueOf(toMonth);
		int toY = Integer.valueOf(toYear);
		getLastDayOfTheMonth(fM, fY);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		String examFromDay1 = new StringBuilder().append(fD).append("/").append(fM).append("/").append(fY).toString();
		String examToDay1 = new StringBuilder().append(toD).append("/").append(toM).append("/").append(toY).toString();
		long sessionN = 0;
		try {
			Date date = sdf.parse(dateFrom);
			Date dat1 = sdf.parse(examFromDay1);
			Date dat2 = sdf.parse(examToDay1);

			long mili1 = dat1.getTime();
			long mili2 = dat2.getTime();

			//calculate the difference in millisecond between two dates
			long diffInMilli = mili2-mili1;
			long diffInDays = diffInMilli / (24* 60 * 60 * 1000);
			sessionN = diffInDays;
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	
		try {
			for (int i = 0; i < sessionN; i++) {
				String examFromDay = new StringBuilder().append(fD).append("/").append(fM).append("/").append(fY).toString();
				String examToDay = new StringBuilder().append(toD).append("/").append(toM).append("/").append(toY).toString();
				int y = i++;
				int x=i;
				int z = x++;
				Date date = sdf.parse(dateFrom);
				Date dat1 = sdf.parse(examFromDay);
				Date dat2 = sdf.parse(examToDay);
				
				System.out.println("Diff in Days "+sessionN);
				//System.out.println(dat2);

				String[] splitDate1 = dat1.toString().split(" ");
				String dayOfTheWeek1 = splitDate1[0];
				String dayOfTheMonth1 = splitDate1[1];
				System.out.println(dayOfTheWeek1 + dayOfTheMonth1);
				System.out.println("dayOfTheWeek1 " + dayOfTheWeek1);
				System.out.println("test" + fD);
				String[] splitDate2 = dat2.toString().split(" ");
				String dayOfTheWeek2 = splitDate2[0];
				String dayOfTheMonth2 = splitDate2[1];
				System.out.println(dayOfTheMonth1);
				System.out.println(dayOfTheMonth2);
				if (fD == lastDayOfTheMonth) {
					if(x==0){
						return;
					} else {
						if(fM == 12) {
							fM = 1;
						} else {
							lastDayOfTheMonth = 1;
							fD=lastDayOfTheMonth;
							i--;
							z--;
							System.out.println("here------");
						}
					}
				}
				else if(fD == 1) {
					int o = i+1;
					int specialCase = fD;
					String firstOfTheMonth = new StringBuilder().append(specialCase).append("/").append(fM).append("/").append(fY).toString();
					System.out.println("here");
					System.out.println(y + "-Y");
					String am = "am";
					String pm = "pm";
					String insertSessionsAm = "INSERT INTO SESSION(ID, Date, MorningAfternoon) VALUES ('" + o + "', + '"+ firstOfTheMonth + "', + '" + am + "')";
					String insertSessionsPm = "INSERT INTO SESSION(ID, Date, MorningAfternoon) VALUES ('" + z + "', + '"+ firstOfTheMonth + "', + '" + pm + "')";

					stmt2 = conn.createStatement();
					stmt3 = conn.createStatement();
					stmt2.executeUpdate(insertSessionsAm);
					stmt3.executeUpdate(insertSessionsPm);
					fD++;
					System.out.println("Inserting into SESSION.." + "[" + i + "]" + "[" + firstOfTheMonth + "]" + "[" + am + "]");
					System.out.println("Inserting into SESSION.." + "[" + y + "]" + "[" + firstOfTheMonth + "]" + "[" + pm + "]");
					System.out.println("now here-------");
				} else if(dayOfTheWeek1.equals("Sat") || dayOfTheWeek1.equals("Sun")) {
					if (dayOfTheWeek1.equals("Sat")) {
						fD = fD+2;
						i--;
						y--;
						System.out.println("Skipping Saturday");
						System.out.println("....");
					}
					else if(dayOfTheWeek1.equals("Sun")) {
						fD++;
						i--;
						y--;
						System.out.println("Skipping Sunday");
						System.out.println("!!!!!!!!");
					}
					}
					else {
						y = i + 1;
						System.out.println(y + "-Y");
						String am = "am";
						String pm = "pm";
						String insertSessionsAm = "INSERT INTO SESSION(ID, Date, MorningAfternoon) VALUES ('" + i + "', + '"+ examFromDay + "', + '" + am + "')";
						String insertSessionsPm = "INSERT INTO SESSION(ID, Date, MorningAfternoon) VALUES ('" + y + "', + '"+ examFromDay + "', + '" + pm + "')";
	
						stmt2 = conn.createStatement();
						stmt3 = conn.createStatement();
						stmt2.executeUpdate(insertSessionsAm);
						stmt3.executeUpdate(insertSessionsPm);
						fD++;
						System.out.println("Inserting into SESSION.." + "[" + i + "]" + "[" + examFromDay + "]" + "[" + am + "]");
						System.out.println("Inserting into SESSION.." + "[" + y + "]" + "[" + examFromDay + "]" + "[" + pm + "]");
						System.out.println("--------");
					}
				}
		} catch (ParseException e) {
		} catch (SQLException e) {
			System.out.println(e);
		}
		finally {
			System.out.println("here");

			String asd = "SELECT * FROM SESSION";
			System.out.println(asd);
			try {
				rs = stmt.executeQuery(asd);
				rs.afterLast();
				while(rs.previous()){
				String id = rs.getString("ID");
				String lastSession = rs.getString("MorningAfternoon");
				String lastExam = rs.getString("date");
				System.out.println("Last session" + lastSession);
					if(lastSession.equals("am")) {
						lastSession = "am";
						int i = Integer.valueOf(id);
						i++;
						String insertSessionsAm = "INSERT INTO SESSION(ID, Date, MorningAfternoon) VALUES ('" + i + "', + '"+ lastExam + "', + '" + lastSession + "')";
						stmt3 = conn.createStatement();
						stmt3.executeUpdate(insertSessionsAm);
						
					}
					else {
						return;
					}
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}*/
	
	private void getLastDayOfTheMonth(int month, int year) {
		   Calendar calendar = Calendar.getInstance();
		    // passing month-1 because 0-->jan, 1-->feb... 11-->dec
		    calendar.set(year, month - 1, 1);
		    calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
		    Date date = calendar.getTime();
		    DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
		    String[] splitDate = DATE_FORMAT.format(date).split("/");
		    String lastDayStr = splitDate[0];
		    int lastDayOfTheMonth = Integer.valueOf(lastDayStr);
		    this.lastDayOfTheMonth = lastDayOfTheMonth;
	}
}