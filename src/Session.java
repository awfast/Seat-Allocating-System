import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Session {

	protected String moduleCode = null;
	protected Connection conn = null;
	private Statement stmt;
	private ResultSet rs;
	private Statement stmt2;
	private Statement stmt3;
	private int lastDayOfTheMonth = 0;
	private ArrayList<Integer> arr = new ArrayList<Integer>();
	private final String USER = "root";
	private final String PASS = "";
	private final String DB_URL = "jdbc:mysql://localhost:3306/test";
	private HashMap<Integer, String> sessionID_sessionDate = new HashMap<Integer, String>();

	protected Connection getConnection() {
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

	public void testDate(Connection conn, String dateFrom, String dateTo) throws ParseException, SQLException {
		int i=0;
		SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy");
		Date dt1 = format1.parse(dateFrom);
		Date dt2 = format1.parse(dateTo);
		String finalFromDate = format1.format(dt1);

		long diff = dt2.getTime() - dt1.getTime();
		int diffDays = (int) (diff / (24 * 1000 * 60 * 60));

		System.out.println(diffDays);
		while(i<=diffDays*2) {
			insertIntoSessionAM(conn, i, finalFromDate);
			i++;
			insertIntoSessionPM(conn, i, finalFromDate);
			finalFromDate = getNextDate(finalFromDate);
			i++;			
		}
	}
	
	private void insertIntoSessionAM(Connection conn, int id, String date) throws SQLException {
		String am = "am";
		String pm = "pm";
		String insertSessionsAm = "INSERT INTO SESSION(ID, Date, MorningAfternoon) VALUES ('" + id + "', + '"
				+ date + "', + '" + am + "')";

		stmt2 = conn.createStatement();
		stmt2.executeUpdate(insertSessionsAm);
	}
	
	private void insertIntoSessionPM(Connection conn, int id, String date) throws SQLException {
		String am = "am";
		String pm = "pm";
		String insertSessionsPm = "INSERT INTO SESSION(ID, Date, MorningAfternoon) VALUES ('" + id + "', + '"
				+ date + "', + '" + pm + "')";

		stmt3 = conn.createStatement();
		stmt3.executeUpdate(insertSessionsPm);
	}


	private String getNextDate(String curDate) throws ParseException {
		final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		final Date date = format.parse(curDate);
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_YEAR, 1);

		return format.format(calendar.getTime());
	}

	protected HashMap<Integer, String> getAllSessions() {
		return this.sessionID_sessionDate;
	}
}
