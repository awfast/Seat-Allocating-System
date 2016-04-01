package Main;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
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
	private String date;
	private List<String> modules = new ArrayList<String>();
	public Map<HashMap<Integer, String>, Integer> students_total = new HashMap<HashMap<Integer, String>, Integer>();
	private Map<Integer, Integer> locations_buildingRoom = new HashMap<Integer, Integer>();
	private Map<Integer, Integer> locations_roomCapacity = new HashMap<Integer, Integer>();
	// all modules, and students registered for them.
	private HashMap<String, ArrayList<Integer>> moduleVstudents = new HashMap<String, ArrayList<Integer>>();
	//all modules and their sessions assigned
	private HashMap<String, Integer> moduleVsession = new HashMap<String, Integer>();
	//all available sessions
	private List<Integer> sessions = new ArrayList<Integer>();
	private ArrayList<Integer> assignedStudents;
	private List<String> assignedModules = new ArrayList<String>();
	private List<String> modulesToCheck = new ArrayList<String>();
	private List<Schedule> schedules = new ArrayList<Schedule>();
	private List<Integer> list_Capacity_Buildings = new ArrayList<Integer>();
	private List<Building> unavailableBuildings = new ArrayList<Building>();
	
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
		test();
		getSchedules(schedules);
		printSchedules(assign(schedules));
		// initializeScheduling();

	}

	private void test() throws SQLException {
		for (int i = 0; i < modulesToCheck.size(); i++) {
			ArrayList<Integer> module = moduleVstudents.get(modulesToCheck.get(i));
			modulesToCheckLoop: for (int j = 0; j < sessions.size(); j++) {
				sessionLoop: 
					if (assignedModules.isEmpty()) {
					assignedModules.add(modulesToCheck.get(i));
					moduleVsession.put(modulesToCheck.get(i), sessions.get(j));
					break modulesToCheckLoop;
				} else {
					for (int x = 0; x < assignedModules.size(); x++) {
						ArrayList<Integer> assignedModule = moduleVstudents.get(assignedModules.get(x));
						for (int y = 0; y < module.size(); y++) {
							int currentSession = moduleVsession.get(assignedModules.get(x));
							if (assignedModule.contains(module.get(y)) && getDatePerSessionID(currentSession).equals(getDatePerSessionID(sessions.get(j)))) {
								break sessionLoop;
							}
						}
						if (x == assignedModules.size()-1) {
							assignedModules.add(modulesToCheck.get(i));
							moduleVsession.put(modulesToCheck.get(i), sessions.get(j));
							break modulesToCheckLoop;
						} else {
							continue;
						}
					}
				}
			}
		}
		
		if(assignedModules.size() != modulesToCheck.size()) {
			Collections.shuffle(modulesToCheck);			
		}
	}
	
	//once the sessions for every module have been assigned, find the students
	private List<Schedule> getSchedules(List<Schedule> schedules) throws SQLException {
		for(String module : moduleVsession.keySet()) {
			String query = "SELECT * FROM RegisteredStudents WHERE ModuleCode='" + module + "' ";
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				int studentID = rs.getInt(1);
				Schedule schedule = new Schedule(studentID, module, moduleVsession.get(module), getDatePerSessionID(moduleVsession.get(module)), 0, 0);
				schedules.add(schedule);
			}
		}
		return schedules;
	}

	private void printSchedules(List<Schedule> s) throws SQLException {
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

	public List<Schedule> assign(List<Schedule> schedules) throws SQLException {
		for (int i = 0; i < schedules.size(); i++) {
			getLocations();
			int availableBuilding = getOptimalBuilding(schedules.get(i).getModuleCode(), schedules.get(i).getSessionID());
			int availableRoom = getOptimalRoomNumber(schedules.get(i).getModuleCode());
			if(checkIfAvailable(availableBuilding, availableRoom, schedules.get(i).getSessionID())) {
				schedules.get(i).setBuildingNumber(availableBuilding);
				schedules.get(i).setRoomNumber(availableRoom);				
			} else {
				locations_buildingRoom.remove(availableBuilding);
				int building = getOptimalBuilding(schedules.get(i).getModuleCode(), schedules.get(i).getSessionID());
				int room = getOptimalRoomNumber(schedules.get(i).getModuleCode());
				if(building == 0 || room == 0) {
					System.out.println("No Building available: " + " Building: " + building + ", Room: " + room);
				}
				schedules.get(i).setBuildingNumber(building);
				schedules.get(i).setRoomNumber(room);
			}
		}
		return schedules;		
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


	// check if building is available for this date.
	private int getOptimalBuilding(String moduleCode, int sessionID) throws SQLException {
		String query = "SELECT * FROM RegisteredStudents WHERE ModuleCode='" + moduleCode + "'";
		stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		int count = 0;

		while (rs.next()) {
			count++;
		}

		for (Integer buildingAvailable : locations_buildingRoom.keySet()) {
			buildingAvailable = getBuilding(closest(count, locations_roomCapacity));
			return buildingAvailable;
		}

		return 0;
	}

	// function to check if the room is available inside the getRoomNumber
	// method
	private int getOptimalRoomNumber(String moduleCode) throws SQLException {
		String query = "SELECT * FROM RegisteredStudents WHERE ModuleCode='" + moduleCode + "'";
		stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		int count = 0;
		while (rs.next()) {
			count++;
		}

		for (Integer room : locations_buildingRoom.keySet()) {
			room = getRoom(closest(count, locations_roomCapacity));
			return room;

		}
		
		return 0;
	}
	
	private boolean checkIfAvailable(int building, int room, int sessionID) {
		if(unavailableBuildings.isEmpty()) {
			Building b = new Building(building, room, sessionID);
			unavailableBuildings.add(b);
			return true;
		} else {
			for(int i=0; i<unavailableBuildings.size(); i++) {
				if(unavailableBuildings.get(i).getBuildingNumber() == building && unavailableBuildings.get(i).getSessionID() == sessionID) {
					return false;
				}
			}
		}
		return true;
	}

	private List<Integer> getAllSessions() throws SQLException {
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
		stmt2 = conn.createStatement();
		ResultSet rs2 = stmt2.executeQuery(query2);
		while (rs2.next()) {
			int building = rs2.getInt(1);
			return building;
		}
		return 0;
	}

	private int getRoom(int capacity) throws SQLException {
		this.capacity = capacity;
		String query2 = "SELECT * FROM Location WHERE SeatNumber=" + capacity;
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