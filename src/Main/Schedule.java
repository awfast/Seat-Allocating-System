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
	private Schedule schedule;
	private int returned_true = 0;
	private int returned_false = 0;
	private int counter = 0;
	private String date;
	private ArrayList<Integer> sessions = new ArrayList<Integer>();
	private boolean roomOccupied = false;
	private List<String> modules = new ArrayList<String>();
	private boolean buildingUnavailable = false;
	private RejectedGoal rg = new RejectedGoal();
	private Map<HashMap<Integer, String>, Integer> students_total = new HashMap<HashMap<Integer, String>, Integer>();
	private HashMap<Integer, String> students_moduleCode;
	private Map<HashMap<Integer, String>, Boolean> students_alreadyChecked = new HashMap<HashMap<Integer, String>, Boolean>();
	private Map<String, Integer> moduleCode_sessionID = new HashMap<String, Integer>();
	private Map<Integer, Integer> locations_buildingRoom = new HashMap<Integer, Integer>();
	private Map<Integer, Integer> locations_roomCapacity = new HashMap<Integer, Integer>();
	private Map<Integer, Integer> building_sessionID = new HashMap<Integer, Integer>();
	private Map<Integer, Integer> room_sessionID = new HashMap<Integer, Integer>();
	private Map<HashMap<Integer, String>, HashMap<Integer, Boolean>> students_available = new HashMap<HashMap<Integer, String>, HashMap<Integer, Boolean>>();
	private HashMap<Schedule, String> uncompletedSchedules = new HashMap<Schedule, String>();
	private ArrayList<Schedule> completedSchedules = new ArrayList<Schedule>();
	private HashMap<String, Integer> dates_students;
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
				int room = getRoomNumber(moduleCode);
				dates_students = new HashMap<String, Integer>();

				dates_students.put(date, student);
				students_dates_map.put(fakeCounter, dates_students);
				fakeCounter++;
				Schedule s = new Schedule(student, moduleCode, session, date, building, room);
				uncompletedSchedules.put(s, "");
				counter++;
			}
		}
		System.out.println(students_dates_map.size());
		assign(uncompletedSchedules);
		printSchedules(uncompletedSchedules);
		System.out.println("Completed.");
	}

	private void printSchedules(HashMap<Schedule, String> s) throws SQLException {
		int j = 1;
		for (Schedule i : s.keySet()) {
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

			if (rg.multipleSessionsOnTheSameDay == true) {
				//for every rejected schedule
				//find an alternative session
				//recursive call
				System.out.println(rg.fetchNumberOfRejectedSchedules(conn));
			}
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
						if (tempDate.equals(date) && occurance > 0) {
							occurance = 0;
							System.out.println("Goal failed.");
							rg.multipleSessionsOnTheSameDay = true;
							rg.studentID_sessionID = new HashMap<Integer, Integer>();
							rg.studentID_sessionID.put(i.getStudentID(), i.getSessionID());
							rg.rejectedSchedules.put(rg.studentID_sessionID, i.getModuleCode());
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

	private int getOptimalBuilding(String moduleCode) {
		for (HashMap<Integer, String> map : students_total.keySet()) {
			for (Integer students : map.keySet()) {
				if (map.get(students).equals(moduleCode)) {
					for (Integer building : locations_buildingRoom.keySet()) {
						for (Integer room : locations_roomCapacity.keySet()) {
							if (locations_roomCapacity.get(room) == closest(students_total.get(map),
									locations_roomCapacity)) {
								if (locations_buildingRoom.get(building) == room) {
									return building;
								} else {
									continue;
								}
							}
						}
					}
				}
			}
		}
		return 0;
	}

	private boolean areAvailable(String moduleCode) {
		for (HashMap<Integer, String> key : students_total.keySet()) {
			for (Integer i : key.keySet()) {
				if (key.get(i).equals(moduleCode)) {
					return true;
					/*
					 * availableStudents = ((returned_false + returned_true) *
					 * 100.f) / students_total.get(key); if(availableStudents >
					 * 90.0) { System.out.println("Available students: " +
					 * availableStudents + "%"); return true; } else {
					 * System.out.println("Available students: " +
					 * availableStudents + "%"); return false; }
					 */
				}
			}
		}
		return false;
	}

	private boolean checkstudentsAvailability(String moduleCode, int studentID, int sessionID) {
		for (Integer key : students_moduleCode.keySet()) {
			if (students_moduleCode.get(key).equals(moduleCode)) {
				if (!students_available.isEmpty()) {
					for (HashMap<Integer, String> map : students_available.keySet()) {
						for (Integer i : map.keySet()) {
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
					for (Integer i : students_moduleCode.keySet()) {
						if (students_moduleCode.get(i) == moduleCode) {
							HashMap<Integer, String> s = new HashMap<Integer, String>();
							s.put(i, moduleCode);
							HashMap<Integer, Boolean> m = new HashMap<Integer, Boolean>();
							m.put(sessionID, false);
							returned_false++;
							students_available.put(s, m);
							return true;
						}
					}
				}
			}
			continue;
		}
		return false;
	}

	private boolean isBuildingAvailable(int building, int session) {
		if (building_sessionID.isEmpty()) {
			building_sessionID.put(building, session);
			return true;
		} else {
			for (Integer key : building_sessionID.keySet()) {
				if (key == building && building_sessionID.get(key) == session) {
					return false;
				} else {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isRoomAvailable(int room, int session) {
		if (room_sessionID.isEmpty()) {
			room_sessionID.put(room, session);
			return true;
		} else {
			for (Integer key : room_sessionID.keySet()) {
				if (key == room && room_sessionID.get(key) == session) {
					return false;
				} else {
					return true;
				}
			}
		}
		return false;
	}

	private int assignSession(int studentID, String moduleCode) {
		for (int j = 0; j < modules.size(); j++) {
			for (int i = 0; i < sessions.size(); i++) {
				if (moduleCode_sessionID.isEmpty()) {
					moduleCode_sessionID.put(modules.get(j), sessions.get(i));
					return sessions.get(i);
				}
			}
		}
		for (int j = 0; j < modules.size(); j++) {
			for (int i = 0; i < sessions.size(); i++) {
				for (String mc : moduleCode_sessionID.keySet()) {
					if (mc == moduleCode) {
						return moduleCode_sessionID.get(mc);
					} else {
						if (!moduleCode_sessionID.containsKey(moduleCode)
								&& !moduleCode_sessionID.containsValue(sessions.get(i))) {
							moduleCode_sessionID.put(moduleCode, sessions.get(i));
							return sessions.get(i);
						}
						continue;
					}

				}
			}
		}
		return 0;
	}

	// function to check if the room is available inside the getRoomNumber
	// method
	private int getRoomNumber(String moduleCode) {
		for (HashMap<Integer, String> key : students_total.keySet()) {
			for (Integer student : key.keySet()) {
				if (moduleCode.equals(key.get(student))) {
					for (Integer room : locations_roomCapacity.keySet()) {
						if (locations_roomCapacity.get(room) == closest(students_total.get(key),
								locations_roomCapacity)) {
							return room;
						}
					}
				}
			}
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
		}
	}

	public int closest(int of, Map<Integer, Integer> room_capacity) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (int v : room_capacity.keySet()) {
			list.add(room_capacity.get(v));
		}
		int min = Integer.MAX_VALUE;
		int closest = 0;
		int secondClosest = of;

		for (int i = 0; i < list.size(); i++) {
			final int diff = Math.abs(list.get(i) - of);
			if (diff < min) {
				secondClosest = list.get(i);
				if (secondClosest > of) {
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
}