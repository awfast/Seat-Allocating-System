package Controller;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

/*
 * @author Damyan Rusinov
 * @This class populates the session given in the db.
 */

public class Session {

	protected String moduleCode = null;
	protected Connection conn = null;
	private Statement stmt2;
	private Statement stmt3;
	private final String USER = "root";
	private final String PASS = "";
	private final String DB_URL = "jdbc:mysql://localhost:3306/db";
	private LinkedHashMap<String, String> sessionDate_sessionOccurance = new LinkedHashMap<String, String>();
	private LinkedHashMap<Integer, HashMap<String, String>> sessionID_sessionDate = new LinkedHashMap<Integer, HashMap<String, String>>();
	protected int numberOfSessions = 0;

	protected Connection getConnection() {
		try {
			//establish a connection
			conn = (Connection) DriverManager.getConnection(DB_URL, USER, PASS);

		} catch (SQLException se) {
		} catch (Exception e) {
		}
		return conn;
	}

	public void testDate(Connection conn, String dateFrom, String dateTo) throws ParseException, SQLException {
		int i=1;
		SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy");
		Date dt1 = format1.parse(dateFrom);
		Date dt2 = format1.parse(dateTo);
		String finalFromDate = format1.format(dt1);

		long diff = dt2.getTime() - dt1.getTime();
		int diffDays = (int) (diff / (24 * 1000 * 60 * 60));
		this.numberOfSessions = diffDays;
		while(i<=(diffDays*2) + 1) {			
			insertIntoSessionAM(conn, i, finalFromDate);
			i++;
			insertIntoSessionPM(conn, i, finalFromDate);
			finalFromDate = getNextDate(finalFromDate);
			i++;			
		}
	}
	
	private void insertIntoSessionAM(Connection conn, int id, String date) throws SQLException {
		String am = "am";
		String insertSessionsAm = "INSERT INTO SESSION(ID, Date, MorningAfternoon) VALUES ('" + id + "', + '"
				+ date + "', + '" + am + "')";
		sessionDate_sessionOccurance.put(date, am);
		sessionID_sessionDate.put(id, sessionDate_sessionOccurance);
		stmt2 = conn.createStatement();
		stmt2.executeUpdate(insertSessionsAm);
	}
	
	private void insertIntoSessionPM(Connection conn, int id, String date) throws SQLException {
		String pm = "pm";
		String insertSessionsPm = "INSERT INTO SESSION(ID, Date, MorningAfternoon) VALUES ('" + id + "', + '"
				+ date + "', + '" + pm + "')";
		sessionDate_sessionOccurance.put(date, pm);
		sessionID_sessionDate.put(id, sessionDate_sessionOccurance);
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
}
