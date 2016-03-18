package Main;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class RejectedGoal {
	protected Connection conn = null;
	private ArrayList<String> moduleCodes = new ArrayList<String>();
	public boolean multipleSessionsOnTheSameDay = false;
	public HashMap<Integer, Integer> studentID_sessionID;
	public HashMap<HashMap<Integer, Integer>, String> rejectedSchedules = new HashMap<HashMap<Integer, Integer>, String>();
	

	public int fetchNumberOfRejectedSchedules(Connection conn) throws SQLException {
		int counter = 0;
		this.conn = conn;
		for(HashMap<Integer, Integer> m : rejectedSchedules.keySet()) {
			if(!moduleCodes.contains(rejectedSchedules.get(m))) {
				System.out.println(rejectedSchedules.get(m));
				counter++;
				moduleCodes.add(rejectedSchedules.get(m));
			}
		}
		return counter;
	}
}
