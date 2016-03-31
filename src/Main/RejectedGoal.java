/*package Main;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class RejectedGoal {

	int recSess = 0;
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
	public Map<HashMap<Integer, String>, Integer> students_total = new HashMap<HashMap<Integer, String>, Integer>();
	public ArrayList<Schedule> rejectedSchedules = new ArrayList<Schedule>();
	public HashMap<String, Integer> moduleCode_sessionID = new HashMap<String, Integer>();
	private ArrayList<Integer> sessions = new ArrayList<Integer>();
	public HashMap<Integer, Integer> session_availability = new HashMap<Integer, Integer>();
	// moduleCode-percentage, date-studentIDdate-studentID(available for this
	// date),
	public HashMap<String, Boolean> moduleCode_percentage;
	public HashMap<String, Integer> date_studentID;
	public HashMap<HashMap<String, Boolean>, HashMap<String, Integer>> date_studentID_availabilityLevel_yesOrNo = new HashMap<HashMap<String, Boolean>, HashMap<String, Integer>>();

	public HashMap<Schedule, String> reAllocateRejectedSchedules(Connection conn, ArrayList<Integer> sessions,
			Map<HashMap<Integer, String>, Integer> students_total, HashMap<Schedule, String> schedules)
					throws SQLException {
		this.sessions = sessions;
		this.conn = conn;
		this.students_total = students_total;
		while (recSess < sessions.size()) {
			for (HashMap<Integer, String> key : students_total.keySet()) {
				int total = students_total.get(key);
				for (Integer student : key.keySet()) {
					int counter = 0;
					for (Schedule schedule : schedules.keySet()) {
						System.out.println(schedule.getModuleCode());
						System.out.println(key.get(student));
						if (schedule.getModuleCode().equals(key.get(student))) {
							counter++;
							//studentAvailability(schedule);
						}
						if (counter == total) {
							availableStudents = (calculateStudentAvailability(date_studentID_availabilityLevel_yesOrNo, recSess,schedule.getModuleCode(), total)) * 100.f / total;
							if(availableStudents > 90.0) {
								return reAssignSessions(schedules, schedule.getModuleCode(), recSess);
							} 
							else {
								recSess++;
								reAllocateRejectedSchedules(conn, sessions, students_total, schedules);
							}
							// if true 90%
							// alocate
							// else -> recurse + recsess++
						} else {
							continue;
						}
					}					 
					// print schedules if > 90% else recurse
				}
			}
		}
		return schedules;
	}

	private void studentAvailability(Schedule schedule) throws SQLException {
		moduleCode_percentage = new HashMap<String, Boolean>();
		date_studentID = new HashMap<String, Integer>();
		if (date_studentID_availabilityLevel_yesOrNo.isEmpty()) {
			System.out.println(schedule.getSessionID());
			String date = getDatePerSessionID(schedule.getSessionID());
			date_studentID.put(date, schedule.getStudentID());
			moduleCode_percentage.put(schedule.getModuleCode(), true);
			date_studentID_availabilityLevel_yesOrNo.put(moduleCode_percentage, date_studentID);
		} else {
			for (Entry<HashMap<String, Boolean>, HashMap<String, Integer>> entry : date_studentID_availabilityLevel_yesOrNo
					.entrySet()) {
				HashMap<String, Boolean> mod_percent = entry.getKey();
				HashMap<String, Integer> dat_stud = entry.getValue();
				for (String dateStudent : dat_stud.keySet()) {
					for (String modulePercent : mod_percent.keySet()) {
						if(!mod_percent.containsKey(schedule.getModuleCode()) || !dat_stud.containsValue(schedule.getStudentID())) {
							String date = getDatePerSessionID(schedule.getSessionID());
							date_studentID.put(date, schedule.getStudentID());
							moduleCode_percentage.put(schedule.getModuleCode(), true);
							date_studentID_availabilityLevel_yesOrNo.put(moduleCode_percentage, date_studentID);
						}
						if (dat_stud.get(dateStudent) == schedule.getStudentID() && modulePercent.equals(schedule.getModuleCode())) {
							if (dateStudent != getDatePerSessionID(schedule.getSessionID())) {
								if (schedule.doesNotHaveOtherExamsScheduledForTheSameDay(recSess,schedule.getStudentID(), modulePercent, dateStudent)) {
									String date = getDatePerSessionID(schedule.getSessionID());
									date_studentID.put(date, schedule.getStudentID());
									moduleCode_percentage.put(schedule.getModuleCode(), true);
									date_studentID_availabilityLevel_yesOrNo.put(moduleCode_percentage, date_studentID);
								} else {
									String date = getDatePerSessionID(schedule.getSessionID());
									date_studentID.put(date, schedule.getStudentID());
									moduleCode_percentage.put(schedule.getModuleCode(), false);
									date_studentID_availabilityLevel_yesOrNo.put(moduleCode_percentage, date_studentID);
								}
							}
						}
					}
				}
			}
		}
	}

	private String getDatePerSessionID(int sessionID) throws SQLException {
		String query2 = "SELECT * FROM Session WHERE ID ="+sessionID;
		stmt = conn.createStatement();
		ResultSet rs2 = stmt.executeQuery(query2);
		while (rs2.next()) {
			String date = rs2.getString("date");
			return date;
		}
		return null;
	}

	private int calculateStudentAvailability(HashMap<HashMap<String, Boolean>, HashMap<String, Integer>> date_studentID_availabilityLevel_yesOrNo,int counter, String moduleCode, int totalStudentsRegisteredOnThisModule) {
		for (HashMap<String, Boolean> key : date_studentID_availabilityLevel_yesOrNo.keySet()) {
			for(String str :key.keySet()) {
				if (str.equals(moduleCode)) {
					if (key.get(str) == true) {
						returned_true++;
					} else {
						returned_false--;
					}
				}
			}
		}
		return returned_true + returned_false;
	}
	
	private HashMap<Schedule, String> reAssignSessions(HashMap<Schedule, String> schedules, String moduleCode, int sessionID) {
		for (Schedule schedule : schedules.keySet()) {
			if (schedule.getModuleCode().equals(moduleCode)) {
				schedule.setGoalReached(true);
				schedule.setSessionID(sessionID);
			}
		}
		return schedules;
	}
}
*/