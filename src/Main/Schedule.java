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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

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
	private boolean goalReached = false;
	private int counter = 0;
	private int totalNumberOfSchedules = 0;
	private String date;
	private ArrayList<Integer> sessions = new ArrayList<Integer>();
	private List<String> modules = new ArrayList<String>();
	public Map<HashMap<Integer, String>, Integer> students_total = new HashMap<HashMap<Integer, String>, Integer>();
	private HashMap<Integer, String> students_moduleCode;
	private Map<Integer, Integer> locations_buildingRoom = new HashMap<Integer, Integer>();
	private Map<Integer, Integer> locations_roomCapacity = new HashMap<Integer, Integer>();
	private ArrayList<Schedule> schedules = new ArrayList<Schedule>();
	private ArrayList<Integer> list_Capacity_Buildings = new ArrayList<Integer>();
	// all modules, and students registered for them.
	private HashMap<String, ArrayList<Integer>> moduleVstudents = new HashMap<String, ArrayList<Integer>>();
	private HashMap<String, Integer> moduleVsession = new HashMap<String, Integer>();
	// assigned students
	ArrayList<Integer> assignedStudents;
	// checked modules
	private ArrayList<String> assignedModules = new ArrayList<String>();
	private ArrayList<String> assignedModules_accessOnly = new ArrayList<String>();
	private ArrayList<String> modulesToCheck = new ArrayList<String>();

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
		this.totalNumberOfSchedules = findTotalSchedulesToBeMade();
		getAllSessions();
		populateModules();
		getAllStudentsPerModules();
		getLocations();
		test();
		printTest();
		// initializeScheduling();
		// assign(schedules);
		// printSchedules(schedules);

	}

	private void test() throws SQLException {
		for (int i = 0; i < modulesToCheck.size(); i++) {
			ArrayList<Integer> module = moduleVstudents.get(modulesToCheck.get(i));
			System.out.println("-> " + modulesToCheck.get(i));
			modulesToCheckLoop: for (int j = 0; j < sessions.size(); j++) {
				sessionLoop: 
					if (assignedModules.isEmpty()) {
					assignedModules.add(modulesToCheck.get(i));
					moduleVsession.put(modulesToCheck.get(i), sessions.get(j));
					break modulesToCheckLoop;
				} else {
					for (int x = 0; x < assignedModules.size(); x++) {
						printAssignedModules();
						ArrayList<Integer> assignedModule = moduleVstudents.get(assignedModules.get(x));
						for (int y = 0; y < module.size(); y++) {
							int currentSession = moduleVsession.get(assignedModules.get(x));
							if (assignedModule.contains(module.get(y)) && getDatePerSessionID(currentSession).equals(getDatePerSessionID(sessions.get(j)))) {
								System.out.println(module.get(y));
								break sessionLoop;
							}

						}
						if (x == assignedModules.size()-1) {
							assignedModules.add(modulesToCheck.get(i));
							moduleVsession.put(modulesToCheck.get(i), sessions.get(j));
							System.out.println("size: " + moduleVsession.size());
							break modulesToCheckLoop;
						} else {
							continue;
						}
					}
				}
			}
		}
	}
	
	private void printAssignedModules() {
		for(int i=0; i<assignedModules.size(); i++) {
			System.out.println(i + ". [ " + assignedModules.get(i) + "]");
		}
	}

	private void printTest() {
		for (String module : moduleVsession.keySet()) {
			System.out.println(module + ", Session: " + moduleVsession.get(module));
		}
	}

	private int findTotalSchedulesToBeMade() throws SQLException {
		int count = 0;
		String query = "SELECT COUNT(*) FROM RegisteredStudents";
		stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {
			count = rs.getInt(1);
		}
		return count;
	}

	private void printSchedules(ArrayList<Schedule> s) throws SQLException {
		int j = 1;
		for (int i = 0; i < schedules.size(); i++) {
			insertIntoTableSchedule(schedules.get(i).getStudentID(), schedules.get(i).getModuleCode(),
					schedules.get(i).getSessionID(), schedules.get(i).getDate(), schedules.get(i).getBuildingNumber(),
					schedules.get(i).getRoomNumber());
			System.out.println(j + ".(Student ID: " + schedules.get(i).getStudentID() + ", Module Code: "
					+ schedules.get(i).getModuleCode() + ", Session ID: " + schedules.get(i).getSessionID() + ", Date: "
					+ schedules.get(i).getDate() + ", Building Number: " + schedules.get(i).getBuildingNumber()
					+ ", Room Number:" + schedules.get(i).getRoomNumber() + ")");
			j++;
		}
	}

	public ArrayList<Schedule> assign(ArrayList<Schedule> schedules) throws SQLException {
		if (goal(schedules)) {
			return schedules;
		} else {
			for (int i = 0; i < schedules.size(); i++) {
				if (schedules.get(i).getBuildingNumber() == 0) {
					int building = getOptimalBuilding(schedules.get(i).getModuleCode());
					schedules.get(i).setBuildingNumber(building);
				}

				if (schedules.get(i).getRoomNumber() == 0) {
					int room = getOptimalRoomNumber(schedules.get(i).getModuleCode());
					schedules.get(i).setRoomNumber(room);
				}
			}
			return assign(schedules);
		}
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

	public int getSessionIDPerDate(String date) throws SQLException {
		String query = "SELECT * FROM Session WHERE date='" + date + "'";
		stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {
			int sessionID = rs.getInt(1);
			return sessionID;
		}
		return 0;
	}

	public boolean goal(ArrayList<Schedule> schedules) throws SQLException {
		boolean isWrong = false;
		for (int i = 0; i < schedules.size(); i++) {
			if (schedules.get(i).getBuildingNumber() == 0 || schedules.get(i).getRoomNumber() == 0) {
				isWrong = true;
			} else {
				isWrong = false;
			}
		}
		if (isWrong == true) {
			return false;
		} else {
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

	// function to check if the room is available inside the getRoomNumber
	// method
	private int getOptimalRoomNumber(String moduleCode) throws SQLException {
		for (HashMap<Integer, String> map : students_total.keySet()) {
			for (Integer students : map.keySet()) {
				if (map.get(students).equals(moduleCode)) {
					for (Integer building : locations_buildingRoom.keySet()) {
						System.out.println(getBuilding(closest(students_total.get(map), locations_roomCapacity)));
						building = getRoom(closest(students_total.get(map), locations_roomCapacity));
						return building;
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
			String query2 = "SELECT * FROM RegisteredStudents WHERE ModuleCode ='" + modules.get(i) + "'";
			stmt2 = conn.createStatement();
			ResultSet rs2 = stmt2.executeQuery(query2);
			assignedStudents = new ArrayList<Integer>();
			while (rs2.next()) {
				int studentID = rs2.getInt(1);
				assignedStudents.add(studentID);
			}
			modulesToCheck.add(modules.get(i));
			moduleVstudents.put(modules.get(i), assignedStudents);
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
			list_Capacity_Buildings.add(capacity);
		}
	}

	private int getBuilding(int capacity) throws SQLException {
		this.capacity = capacity;
		String query2 = "SELECT * FROM Location WHERE SeatNumber=" + capacity;
		;
		stmt2 = conn.createStatement();
		ResultSet rs2 = stmt2.executeQuery(query2);
		while (rs2.next()) {
			int r = rs2.getInt(1);
			return r;
		}
		return 0;
	}

	private int getRoom(int capacity) throws SQLException {
		this.capacity = capacity;
		String query2 = "SELECT * FROM Location WHERE SeatNumber=" + capacity;
		;
		stmt2 = conn.createStatement();
		ResultSet rs2 = stmt2.executeQuery(query2);
		while (rs2.next()) {
			int room = rs2.getInt(2);
			return room;
		}
		return 0;
	}

	private void insertIntoTableSchedule(int studentID, String moduleCode, int sessionID, String date,
			int buildingNumber, int roomNumber) throws SQLException {
		// String table = "CREATE TABLE IF NOT EXISTS Schedule " + "(StudentID
		// INTEGER not NULL, " + "ModuleCode VARCHAR(255), " + "SessionID
		// INTEGER not NULL, " + "ModuleCode VARCHAR(255), "+ "BuildingNumber
		// INTEGER not NULL, " + "RoomNumber INTEGER not NULL)";

		String query = "INSERT INTO Schedule(StudentID, ModuleCode, SessionID, Date, BuildingNumber, RoomNumber) VALUES ('"
				+ studentID + "', + '" + moduleCode + "', + '" + sessionID + "', + '" + date + "', + '" + buildingNumber
				+ "', + '" + roomNumber + "')";
		stmt = conn.createStatement();
		stmt.executeUpdate(query);
	}

	public int closest(int of, Map<Integer, Integer> room_capacity) {
		int min = Integer.MAX_VALUE;
		int closest = 0;
		int secondClosest = of;

		for (int i = 0; i < list_Capacity_Buildings.size(); i++) {
			final int diff = Math.abs(list_Capacity_Buildings.get(i) - of);
			if (diff < min) {
				secondClosest = list_Capacity_Buildings.get(i);
				if (secondClosest == of) {
					return secondClosest;
				} else if (secondClosest > of) {
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

	public void setSessionID(int sessionID) {
		this.sessionID = sessionID;
	}

	public boolean getGoalReached() {
		return goalReached;
	}

	public void setGoalReached(boolean goalReached) {
		this.goalReached = goalReached;
	}

	public void setBuildingNumber(int buildingNumber) {
		this.buildingNumber = buildingNumber;
	}

	public void setRoomNumber(int roomNumber) {
		this.roomNumber = roomNumber;
	}
}