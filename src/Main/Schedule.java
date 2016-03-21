package Main;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JComboBox.KeySelectionManager;

import java.util.TreeSet;

import TreeWithNodesAndSearch.*;

public class Schedule {

	protected Connection conn = null;
	private Statement stmt = null;
	private Statement stmt2 = null;
	private DataReader dataReader = null;
	private int studentID;
	private String moduleCode = null;
	private int sessionID;
	private int buildingNumber;
	private int roomNumber;
	private int capacity = 0;
	private int counter = 0;
	private float availableStudents = 0;
	private String date;
	private ArrayList<Integer> sessions = new ArrayList<Integer>();
	private List<String> modules = new ArrayList<String>();
	private RejectedGoal rg = new RejectedGoal();
	public Map<HashMap<Integer, String>, Integer> students_total = new HashMap<HashMap<Integer, String>, Integer>();
	private HashMap<Integer, String> students_moduleCode;
	private HashMap<Integer, String> student_date = new HashMap<Integer, String>();
	private HashMap<HashMap<Integer, String>, Integer> assigned_sessions = new HashMap<HashMap<Integer, String>, Integer>();
	private Map<Integer, String> sessionID_moduleCode = new HashMap<Integer, String>();
	private Map<Integer, Integer> locations_buildingRoom = new HashMap<Integer, Integer>();
	private Map<Integer, Integer> locations_roomCapacity = new HashMap<Integer, Integer>();
	private HashMap<Schedule, String> uncompletedSchedules = new HashMap<Schedule, String>();
	private HashMap<String, Integer> dates_students;
	private ArrayList<Integer> list = new ArrayList<Integer>();
	private Map<Integer, HashMap<String, Integer>> students_dates_map = new HashMap<Integer, HashMap<String, Integer>>();

	public Schedule(int studentID, String moduleCode, int sessionID, String date, int buildingNumber, int roomNumber) {
		this.studentID = studentID;
		this.moduleCode = moduleCode;
		this.sessionID = sessionID;
		this.buildingNumber = buildingNumber;
		this.roomNumber = roomNumber;
		this.date = date;
	}

	public void generateInformation(Connection conn, DataReader dataReader) throws SQLException {
		this.dataReader = dataReader;
		this.conn = conn;
		getAllSessions();
		populateModules();
		getAllStudentsPerModules();
		getLocations();
		int fakeCounter = 0;
		for (HashMap<Integer, String> students : students_total.keySet()) {
			for (Integer student : students.keySet()) {
				String moduleCode = students.get(student);
				int session = assignSession(student, moduleCode);
				String date = getDatePerSessionID(session);
				int building = getOptimalBuilding(students.get(student));
				int room = getRoomNumber(building, capacity);
				dates_students = new HashMap<String, Integer>();
				dates_students.put(date, student);
				students_dates_map.put(fakeCounter, dates_students);
				fakeCounter++;
				Schedule s = new Schedule(student, moduleCode, session, date, building, room);
				uncompletedSchedules.put(s, "");
				counter++;
			}
		}
		assign(uncompletedSchedules);
		printSchedules(uncompletedSchedules);
		System.out.println("Completed.");
	}

	private void printSchedules(HashMap<Schedule, String> s) throws SQLException {
		int j = 1;
		for (Schedule i : s.keySet()) {
			insertIntoTableSchedule(i.getStudentID(), i.getModuleCode(), i.getSessionID(), i.getDate(), i.getBuildingNumber(), i.getRoomNumber());
			System.out.println(j + ".(Student ID: " + i.getStudentID() + ", Module Code: " + i.getModuleCode()
					+ ", Session ID: " + i.getSessionID() + ", Date: " + i.getDate() + ", Building Number: "
					+ i.getBuildingNumber() + ", Room Number:" + i.getRoomNumber() + ")");
			j++;
		}
	}

	public HashMap<Schedule, String> assign(HashMap<Schedule, String> uncompletedSchedules) throws SQLException {
		if (goal(uncompletedSchedules)) {
			return uncompletedSchedules;
		} else {

			/*
			 * if (i.getRoomNumber() == 0) { for (Integer k :
			 * locations_buildingRoom.keySet()) { i.roomNumber =
			 * getRoomNumber(i.getStudentID());
			 * System.out.println(i.roomNumber); System.out.println(
			 * "------------------------------------------------------------");
			 * //return assign(uncompletedSchedules); } }
			 */

			/*
			 * if (i.sessionID == 0) { for (HashMap<Integer, String>
			 * stud_modules : students_total.keySet()) { for (Integer key :
			 * stud_modules.keySet()) { if (i.studentID == key) {
			 * System.out.println(i.sessionID); i.sessionID =
			 * assignSession(i.getStudentID(), i.moduleCode);
			 * System.out.println(i.sessionID); return
			 * assign(uncompletedSchedules); } } } }
			 */

		}
		return uncompletedSchedules;
	}

	public String getDatePerSessionID(int sessionID) throws SQLException {
		String query = "SELECT * FROM Session WHERE ID=" + sessionID;
		stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		String date = null;
		while (rs.next()) {
			date = rs.getString("date");
			return date;
		}
		return date;
	}

	public boolean goal(HashMap<Schedule, String> schedules) throws SQLException {
		for (Schedule i : schedules.keySet()) {
			String tempDate = i.getDate();
			int occurance = 0;
			for (Integer studentDate : students_dates_map.keySet()) {
				for (String date : students_dates_map.get(studentDate).keySet()) {
					if (students_dates_map.get(studentDate).get(date) == i.getStudentID()) {
						System.out.println("Found: " + students_dates_map.get(studentDate).get(date));
						System.out.println("0." + students_dates_map);
						System.out.println("+1." + i);
						System.out.println("1."+ i.getDate());
						System.out.println("2." + date);
						if (tempDate.equals(date) && occurance > 0) {
							occurance = 0;
							System.out.println("Goal failed.");
							rg.multipleSessionsOnTheSameDay = true;
							rg.studentID_sessionID = new HashMap<Integer, Integer>();
							rg.studentID_sessionID.put(i.getStudentID(), i.getSessionID());
							rg.rejectedStudents.put(rg.studentID_sessionID, i.getModuleCode());
							rg.rejectedSchedules.add(i);
						} else {
							tempDate = date;
							occurance++;
						}
					}
				}
			}
		}
		if (rg.multipleSessionsOnTheSameDay == true) {
			return false;
		} else {
			System.out.println("Everything has been checked. -> TRUE");
			return true;
		}
	}

	private int getOptimalBuilding(String moduleCode) throws SQLException {
		for (HashMap<Integer, String> map : students_total.keySet()) {
			for (Integer students : map.keySet()) {
				if (map.get(students).equals(moduleCode)) {
					for (Integer building : locations_buildingRoom.keySet()) {
						System.out.println(getBuilding(closest(students_total.get(map), locations_roomCapacity)));
						building = getBuilding(closest(students_total.get(map), locations_roomCapacity));
						return building;
						
					}
				}
			}
		}
		return 0;
	}

	public boolean areAvailable(String moduleCode) {
		for (HashMap<Integer, String> key : students_total.keySet()) {
			for (Integer i : key.keySet()) {
				if (key.get(i).equals(moduleCode)) {
					return true;
				}
				availableStudents = ((rg.returned_false + rg.returned_true) * 100.f) / students_total.get(key);
				if (availableStudents > 90.0) {
					System.out.println("Available students: " + availableStudents + "%");
					return true;
				} else {
					System.out.println("Available students: " + availableStudents + "%");
					return false;
				}

			}
		}
		return false;
	}

	private int assignSession(int studentID, String moduleCode) throws SQLException {
		for (int j = 0; j < modules.size(); j++) {
			for (int i = 0; i < sessions.size(); i++) {
				if (sessionID_moduleCode.isEmpty()) {
					sessionID_moduleCode.put(sessions.get(i), modules.get(j));
					System.out.println(sessions.get(i));
					return sessions.get(i);
				} else {
					for (Integer mc : sessionID_moduleCode.keySet()) {
						if (sessionID_moduleCode.containsKey(sessions.get(i))) {
							System.out.println(sessionID_moduleCode.get(mc));
							System.out.println(sessionID_moduleCode.get(mc));
							return mc;
						} else {
							if(doesNotHaveOtherExamsScheduledForTheSameDay(sessions.get(i), studentID,moduleCode ,getDatePerSessionID(sessions.get(i)))) {
								sessionID_moduleCode.put(sessions.get(i), moduleCode);
								return sessions.get(i);
							} else {								
								break;
							}
						}
					}
				}
				continue;
			}
		}
		return 0;
	}

	private boolean doesNotHaveOtherExamsScheduledForTheSameDay(int sessionID, int studentID, String moduleCode, String date) {
		if (uncompletedSchedules.isEmpty()) {
			return true;
		} else {
			for (Schedule schedule : uncompletedSchedules.keySet()) {
				if (schedule.getStudentID() == studentID) {
					if (schedule.getModuleCode().equals(moduleCode)) {
						return true;
					} else {
						if(schedule.getDate() == null) {
							student_date.put(studentID, date);
							assigned_sessions.put(student_date, sessionID);
							return true;
						}
						if (schedule.getDate().equals(date)) {
							return false;
						} 
						else if(assigned_sessions.containsKey(student_date) || !assigned_sessions.containsKey(student_date)) {
							for(HashMap<Integer, String> key: assigned_sessions.keySet()) {
								for(Integer i: key.keySet()) {
									if(i == studentID && key.get(i).equals(date)) {
										return false;
									} else {
										student_date.put(studentID, date);
										assigned_sessions.put(student_date, sessionID);
										return true;
									}
								}
							}
						}
						else {
							return true;							
						}
					}
				}
				continue;
			}
			return true;
		}
	}

	// function to check if the room is available inside the getRoomNumber
	// method
	private int getRoomNumber(int building, int capacity) throws SQLException {
		String query = "SELECT * FROM Location WHERE BuildingNumber="+building+ " AND SeatNumber="+capacity;
		stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {
			int room = rs.getInt(2);
			return room;
		}
		
		return 0;
	}

	private ArrayList<Integer> getAllSessions() throws SQLException {
		String query = "SELECT * FROM Session";
		stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {
			int sessionID = rs.getInt(1);
			sessions.add(sessionID);
		}
		return sessions;
	}

	private void populateModules() throws SQLException {
		String query = "SELECT * FROM Exam";
		stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {
			String moduleCode = rs.getString("ModuleCode");
			modules.add(moduleCode);
		}
	}

	private void getAllStudentsPerModules() throws SQLException {
		for (int i = 0; i < modules.size(); i++) {
			int numberOfStudents = 0;
			System.out.println(modules.get(i));
			String query2 = "SELECT * FROM RegisteredStudents WHERE ModuleCode ='" + modules.get(i) + "'";
			stmt2 = conn.createStatement();
			ResultSet rs2 = stmt2.executeQuery(query2);
			students_moduleCode = new HashMap<Integer, String>();
			while (rs2.next()) {
				int stud = rs2.getInt(1);
				students_moduleCode.put(stud, modules.get(i));
				numberOfStudents++;
			}
			students_total.put(students_moduleCode, numberOfStudents);
		}
	}

	private void getLocations() throws SQLException {
		String query2 = "SELECT * FROM Location";
		stmt2 = conn.createStatement();
		ResultSet rs2 = stmt2.executeQuery(query2);
		while (rs2.next()) {
			int building = rs2.getInt(1);
			int room = rs2.getInt(2);
			int capacity = rs2.getInt(3);
			locations_buildingRoom.put(building, room);
			locations_roomCapacity.put(room, capacity);
			list.add(capacity);
		}
	}
	
	private int getBuilding(int capacity) throws SQLException {
		this.capacity = capacity;
		String query2 = "SELECT * FROM Location WHERE SeatNumber=" + capacity;;
		stmt2 = conn.createStatement();
		ResultSet rs2 = stmt2.executeQuery(query2);
		while (rs2.next()) {
			int r = rs2.getInt(1);
			return r;
		}
		return 0;
	}
	
	private void insertIntoTableSchedule(int studentID, String moduleCode, int sessionID, String date, int buildingNumber, int roomNumber) throws SQLException {
		//String table = "CREATE TABLE IF NOT EXISTS Schedule " + "(StudentID INTEGER not NULL, " + "ModuleCode VARCHAR(255), " + "SessionID INTEGER not NULL, " + "ModuleCode VARCHAR(255), "+ "BuildingNumber INTEGER not NULL, " + "RoomNumber INTEGER not NULL)";

		String query = "INSERT INTO Schedule(StudentID, ModuleCode, SessionID, Date, BuildingNumber, RoomNumber) VALUES ('" + studentID + "', + '"
				+ moduleCode + "', + '" + sessionID + "', + '"
				+ date + "', + '"
				+ buildingNumber + "', + '"
				+ roomNumber + "')";
		stmt = conn.createStatement();
		stmt.executeUpdate(query);
	}

	public int closest(int of, Map<Integer, Integer> room_capacity) {
		int min = Integer.MAX_VALUE;
		int closest = 0;
		int secondClosest = of;

		for (int i = 0; i < list.size(); i++) {
			final int diff = Math.abs(list.get(i) - of);
			if (diff < min) {
				secondClosest = list.get(i);
				if(secondClosest == of) {
					return secondClosest;
				}
				else if (secondClosest > of) {
					if (closest == 0) {
						closest = secondClosest;
						min = secondClosest;
					} else {
						if (secondClosest < closest) {
							closest = secondClosest;
						}
					}

				}
			}
		}
		return closest;
	}

	public int getStudentID() {
		return studentID;
	}

	public String getModuleCode() {
		return moduleCode;
	}

	public int getSessionID() {
		return sessionID;
	}

	public int getBuildingNumber() {
		return buildingNumber;
	}

	public int getRoomNumber() {
		return roomNumber;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
}