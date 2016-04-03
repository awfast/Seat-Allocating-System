package Main;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class Schedule {

	protected Connection conn = null;
	private Statement stmt = null;
	private Statement stmt2 = null;
	private DataReader dataReader = null;
	private String moduleCode = null;
	private String accessible;
	private String date;
	private int studentID;
	private int sessionID;
	private int buildingNumber;
	private int roomNumber;
	private int capacity = 0;
	private boolean goalReached = false;
	private List<String> modules = new ArrayList<String>();
	public Map<HashMap<Integer, String>, Integer> students_total = new HashMap<HashMap<Integer, String>, Integer>();
	private Map<Building, Room> locations_buildingRoom = new LinkedHashMap<Building, Room>();
	private Map<Room, Integer> locations_roomCapacity = new LinkedHashMap<Room, Integer>();
	// all modules, and students registered for them.s
	private HashMap<String, ArrayList<Integer>> moduleVstudents = new HashMap<String, ArrayList<Integer>>();
	//all modules and their sessions assigned
	private HashMap<String, Integer> moduleVsession = new HashMap<String, Integer>();
	//all available sessions
	private List<Integer> sessions = new ArrayList<Integer>();
	private ArrayList<Integer> assignedStudents;
	private List<String> assignedModules = new ArrayList<String>();
	private List<String> modulesToCheck = new ArrayList<String>();
	private Map<String, ArrayList<Schedule>> schedules = new HashMap<String,ArrayList<Schedule>>();
	private List<Integer> list_Capacity_Buildings = new ArrayList<Integer>();
	private Map<String, Building> unavailableBuildings = new HashMap<String, Building>();
	private Map<String, Building> unavailableRooms = new HashMap<String, Building>();
	
	private List<Schedule> unavailableSchedules = new ArrayList<Schedule>();
	
	public Schedule(int studentID, String moduleCode, int sessionID, String date, String accessible, int buildingNumber, int roomNumber) {
		this.studentID = studentID;
		this.moduleCode = moduleCode;
		this.sessionID = sessionID;
		this.accessible = accessible;
		this.buildingNumber = buildingNumber;
		this.roomNumber = roomNumber;
		this.date = date;
	}

	public void generateInformation(Connection conn, DataReader dataReader) throws SQLException {
		this.dataReader = dataReader;
		this.conn = conn;
		getAllSessions();
	}
	
	//once the sessions for every module have been assigned, find the students
	private void getSchedules() throws SQLException {
		for(String module : moduleVsession.keySet()) {
			String query = "SELECT * FROM RegisteredStudents WHERE ModuleCode='" + module + "' ";
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			ArrayList<Schedule> arr = new ArrayList<Schedule>();
			while (rs.next()) {
				int studentID = rs.getInt(1);
				String accessible = rs.getString("AccessibleSeat");
				Schedule schedule = new Schedule(studentID, module, moduleVsession.get(module), getDatePerSessionID(moduleVsession.get(module)), accessible, 0, 0);
				arr.add(schedule);
			}
			this.schedules.put(module, arr);
		}
	}

	private void printSchedules(Map<String, ArrayList<Schedule>> s) throws SQLException {
		int j = 1;
		for (String moduleCode: s.keySet()) {
			for(int i=0; i<s.get(moduleCode).size(); i++) {
				insertIntoTableSchedule(schedules.get(moduleCode).get(i).getStudentID(), moduleCode,
						schedules.get(moduleCode).get(i).getSessionID(), schedules.get(moduleCode).get(i).getDate(), schedules.get(moduleCode).get(i).getBuildingNumber(),
						schedules.get(moduleCode).get(i).getRoomNumber());
				System.out.println(j + ".(Student ID: " + schedules.get(moduleCode).get(i).getStudentID() + ", Module Code: "
						+ moduleCode + ", Session ID: " + schedules.get(moduleCode).get(i).getSessionID() + ", Date: "
						+ schedules.get(moduleCode).get(i).getDate() + ", Building Number: " + schedules.get(moduleCode).get(i).getBuildingNumber()
						+ ", Room Number:" + schedules.get(moduleCode).get(i).getRoomNumber() + ")");
				j++;				
			}
		}
		System.out.println("Schedules not scheduled" + unavailableSchedules);
	}

	public Map<String, ArrayList<Schedule>> assign(Map<String, ArrayList<Schedule>> schedules) throws SQLException {
		for(String moduleCode: schedules.keySet()) {
			for(int i=0; i<schedules.get(moduleCode).size(); i++) {
				if(schedules.get(moduleCode).get(i).getBuildingNumber() == 0 || schedules.get(moduleCode).get(i).getRoomNumber() == 0) {
					getLocations();
					findBuildingAndRoom(schedules.get(moduleCode).get(i));
					for(int j=0; j<schedules.get(moduleCode).size(); j++) {
						schedules.get(moduleCode).get(j).setBuildingNumber(schedules.get(moduleCode).get(i).getBuildingNumber());
						schedules.get(moduleCode).get(j).setRoomNumber(schedules.get(moduleCode).get(i).getRoomNumber());
					}
					break;
				}
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

	public boolean goal(List<Schedule> schedules) throws SQLException {
		boolean isWrong = false;
		for (int i = 0; i < schedules.size(); i++) {
			if (schedules.get(i).getBuildingNumber() == 0 || schedules.get(i).getRoomNumber() == 0) {
				System.out.println("goal not reached");
				isWrong = true;
			} else {
				System.out.println("goal reached");
				isWrong = false;
			}
		}
		if (isWrong == true) {
			return false;
		} else {
			return true;
		}

	}

	private void findBuildingAndRoom(Schedule s) throws SQLException {
		int room = 0;
		int building = 0;
		int tempCapacity = 0;
		while(!list_Capacity_Buildings.isEmpty()) {
			int capacity = closest(getStudentsForThisModuleCode(s.getModuleCode()));
			for(Room rooms: locations_roomCapacity.keySet()) {
				if(locations_roomCapacity.get(rooms) == capacity){
					room = rooms.getRoomNumber();
					tempCapacity = locations_roomCapacity.get(rooms);
					break;
				}
			}
			
			for(Building buildingToFind: locations_buildingRoom.keySet()) {
				if(locations_buildingRoom.get(buildingToFind).getRoomNumber() == room) {
					System.out.println(locations_buildingRoom.get(buildingToFind).getRoomNumber());
					System.out.println(room);
					building = buildingToFind.getBuildingNumber();
					break;
				}
			}
			
			if(checkIfAvailable(s.getModuleCode(), building, room,  s.getSessionID())) {
				s.setBuildingNumber(building);
				s.setRoomNumber(room);
				return;
			} else {
				for(int x=0; x<list_Capacity_Buildings.size(); x++) {
					if(list_Capacity_Buildings.get(x) == tempCapacity) {
						list_Capacity_Buildings.remove(list_Capacity_Buildings.get(x));						
						break;
					}
				}
			}
		}
	}

	// check if building is available for this date.
	private int getStudentsForThisModuleCode(String moduleCode) throws SQLException {
		String query = "SELECT * FROM RegisteredStudents WHERE ModuleCode='" + moduleCode + "'";
		stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		int count = 0;

		while (rs.next()) {
			count++;
		}
	
		return count;
	}
	
	private boolean checkIfAvailable(String moduleCode, int buildingNumber, int room, int sessionID) {
		if(unavailableBuildings.isEmpty()) {
			Building b = new Building(buildingNumber, room, sessionID, moduleCode);
			unavailableBuildings.put(moduleCode, b);
			return true;
		} else {
			if(unavailableBuildings.containsKey(moduleCode)) {
				return true;
			} else {
				for(String module: unavailableBuildings.keySet()) {
					if(unavailableBuildings.get(module).getBuildingNumber() == buildingNumber && unavailableBuildings.get(module).getRoomNumber() == room && getSessionID() == sessionID) {
						return false;
					}
				}
			}
		}
		return true;
	}

	private void getAllSessions() throws SQLException {
		String query = "SELECT * FROM Session";
		stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {
			int sessionID = rs.getInt(1);
			sessions.add(sessionID);
		}
		populateModules();
		getAllStudentsPerModules();
		findSessions();
		getSchedules();
		printSchedules(assign(schedules));
	}
	
	private void findSessions() throws SQLException {
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
			Building b = new Building(building);
			Room r = new Room(room);
			locations_buildingRoom.put(b, r);
			locations_roomCapacity.put(r, capacity);
			list_Capacity_Buildings.add(capacity);
		}
	}

	private void insertIntoTableSchedule(int studentID, String moduleCode, int sessionID, String date,
			int buildingNumber, int roomNumber) throws SQLException {
		String query = "INSERT INTO Schedule(StudentID, ModuleCode, SessionID, Date, BuildingNumber, RoomNumber) VALUES ('"
				+ studentID + "', + '" + moduleCode + "', + '" + sessionID + "', + '" + date + "', + '" + buildingNumber
				+ "', + '" + roomNumber + "')";
		stmt = conn.createStatement();
		stmt.executeUpdate(query);
	}

	public int closest(int of) throws SQLException {
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
	
	public int findNumberOfAccessibleSeatsNeeded(String moduleCode) throws SQLException {
		String query2 = "SELECT * FROM RegisteredStudents WHERE ModuleCode='" + moduleCode + "'";
		stmt2 = conn.createStatement();
		ResultSet rs2 = stmt2.executeQuery(query2);
		int seatsNeeded = 0;
		while (rs2.next()) {
			String accessibleFlag = rs2.getString("AccessibleSeat");
			if(accessibleFlag.equals("YES")) {
				seatsNeeded++;				
			}
		}
		
		return seatsNeeded;
	}
	
	public int findNumberOfAccessibleSeatsAvailable(int buildingNumber, int roomNumber) throws SQLException {
		String query2 = "SELECT * FROM Location WHERE BuildingNumber='"+buildingNumber+ "' AND RoomNumber='"+roomNumber+"'";
		stmt2 = conn.createStatement();
		ResultSet rs2 = stmt2.executeQuery(query2);
		int availableSeats = 0;
		while (rs2.next()) {
			availableSeats = rs2.getInt(4);
		}
		
		return availableSeats;
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