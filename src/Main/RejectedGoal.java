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

	/*public int fetchNumberOfRejectedSchedules(Connection conn) throws SQLException {
		int counter = 0;
		this.conn = conn;
		for (HashMap<Integer, Integer> m : rejectedStudents.keySet()) {
			if (!moduleCodes.contains(rejectedStudents.get(m))) {
				System.out.println(rejectedStudents.get(m));
				counter++;
				moduleCodes.add(rejectedStudents.get(m));
			}
		}
		return counter;
	}

	public boolean checkstudentsAvailability(String moduleCode, int studentID, int sessionID) {
		for (int i = 0; i < rejectedSchedules.size(); i++) {
			if (rejectedSchedules.get(i).getModuleCode().equals(moduleCode)) {
				if (!students_available.isEmpty()) {
					for (HashMap<Integer, String> map : students_available.keySet()) {
						for (Integer j : map.keySet()) {
							if (i == studentID) {
								for (Integer session : students_available.get(map).keySet()) {
									if (students_available.get(map).get(session) == true) {
										returned_true++;
										students_available.get(map).put(session, false);
										return true;
										// available
									} else {
										returned_false++;
										return false;
										// unavailable
									}
								}
							}
						}
					}
				} else {
					HashMap<Integer, String> s = new HashMap<Integer, String>();
					s.put(rejectedSchedules.get(i).getStudentID(), moduleCode);
					HashMap<Integer, Boolean> m = new HashMap<Integer, Boolean>();
					m.put(sessionID, false);
					returned_true++;
					students_available.put(s, m);
					return true;
				}
			}
		}
		return false;
	}
	
	public HashMap<Schedule, String> changeSessionsForTheseSchedules(HashMap<Schedule, String> uncompletedSchedules, ArrayList<Integer> sessions) throws SQLException {
		String date = null;
		for(int i =0; i<sessions.size(); i++) {
			for(Schedule s: uncompletedSchedules.keySet()) {
				for(int j=0; j<rejectedSchedules.size(); j++) {
					if(date == null) {
						date = s.getDatePerSessionID(s.getSessionID());
						checkstudentsAvailability(s.getModuleCode(), s.getStudentID(), sessions.get(i));
						continue;
					}
					else if(s == rejectedSchedules.get(j)) {
						date = rejectedSchedules.get(j).getDate();
						checkstudentsAvailability(s.getModuleCode(), s.getStudentID(), sessions.get(i));
					}
					else {
						break;
					}
				}
				if(areAvailable(s)) {
					
				}
			}
		}
		return uncompletedSchedules;
	}
	
	private void getSessionIDPerDate(String date) throws SQLException {
		String query2 = "SELECT * FROM Session WHERE date ='" + date + "'";
		stmt = conn.createStatement();
		ResultSet rs2 = stmt.executeQuery(query2);
		while (rs2.next()) {
			int sessionID = rs2.getInt(1);
			availableSessions.add(sessionID);
		}
	}
	
	public boolean areAvailable(Schedule schedule) {
		for (HashMap<Integer, String> key : schedule.students_total.keySet()) {
			for (Integer i : key.keySet()) {
				if(key.get(i).equals(schedule.getModuleCode())) {
					availableStudents = ((returned_false + returned_true) * 100.f) / schedule.students_total.get(key);
					if (availableStudents > 90.0) {
						System.out.println("Available students: " + availableStudents + "%");
						return true;
					} else {
						System.out.println("Available students: " + availableStudents + "%");
						return false;
					}
				}
			}
		}
		return false;
	}*/
}
