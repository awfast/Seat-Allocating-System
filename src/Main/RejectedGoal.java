package Main;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RejectedGoal {

	private Statement stmt = null;
	protected Connection conn = null;
	public int returned_true = 0;
	public int returned_false = 0;
	private float availableStudents = 0f;
	private ArrayList<String> moduleCodes = new ArrayList<String>();
	public boolean multipleSessionsOnTheSameDay = false;
	private ArrayList<Integer> availableSessions = new ArrayList<Integer>();
	public HashMap<Integer, Integer> studentID_sessionID;
	public HashMap<HashMap<Integer, Integer>, String> rejectedStudents = new HashMap<HashMap<Integer, Integer>, String>();
	private Map<HashMap<Integer, String>, HashMap<Integer, Boolean>> students_available = new HashMap<HashMap<Integer, String>, HashMap<Integer, Boolean>>();
	public ArrayList<Schedule> rejectedSchedules = new ArrayList<Schedule>();

	public boolean schedulesExist() throws SQLException {
		String query2 = "SELECT * FROM Schedule";
		ResultSet rs = stmt.executeQuery(query2);
		while (rs.next()) {
			System.out.println("True.");
			return true;
		}
		System.out.println("False.");
		return false;
	}
}
