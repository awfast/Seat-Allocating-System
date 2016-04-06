package Main;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.itextpdf.text.log.SysoCounter;
import java.util.Arrays;

public class Schedule {

	protected Connection conn = null;
	private Statement stmt = null;
	private Statement stmt2 = null;
	private DataReader dataReader = null;
	private String moduleCode = null;
	private String moduleTitle = null;
	private String accessible;
	private String location;
	private String day;
	private String date;
	private int studentID;
	private int sessionID;
	private int buildingNumber;
	private int roomNumber;
	private int closestAccessibleSeats;
	private ArrayList<String> alphabet = new ArrayList<String>();
	private int seatCounter10 = 0;
	private int seatCounter20 = 10;
	private int seatCounter30 = 20;
	private String tempModule = null;
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
	private List<Capacity> list_Capacity_Buildings = new ArrayList<Capacity>();
	private Map<String, Building> unavailableBuildings = new HashMap<String, Building>();
	private ArrayList<Schedule> finalizedSchedules = new ArrayList<Schedule>();
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
	
	public Schedule(int studentID, String moduleCode, String moduleTitle, String day, String date, int session, String location) {
		this.studentID = studentID;
		this.moduleCode = moduleCode;
		this.moduleTitle = moduleTitle;
		this.day = day;
		this.date = date;
		this.sessionID = session;
		this.location = location;
	}
	
	public int getStudentID() {
		return studentID;
	}

	public String getModuleCode() {
		return moduleCode;
	}
	
	public String getModuleTitle() {
		return moduleTitle;
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
	
	public String getDay() {
		return this.day;
	}
	
	public String getLocation() {
		return this.location;
	}

	public void generateInformation(Connection conn, DataReader dataReader) throws SQLException {
		this.dataReader = dataReader;
		this.conn = conn;
		System.out.println("1000 students, 50 modules");
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
	
	public ArrayList<Schedule> getFinalSchedules(Connection conn) {
		this.conn = conn;
		return finalizedSchedules;
	}
	
	public Map<String, ArrayList<Schedule>> insertSchedulesIntoTable(Map<String, ArrayList<Schedule>> s)
			throws SQLException {
		int j = 1;
	
		for (String moduleCode : s.keySet()) {
			for (int i = 0; i < s.get(moduleCode).size(); i++) {
				insertIntoTableSchedule(schedules.get(moduleCode).get(i).getStudentID(), moduleCode,
						schedules.get(moduleCode).get(i).getSessionID(), schedules.get(moduleCode).get(i).getDate(),
						schedules.get(moduleCode).get(i).getBuildingNumber(),
						schedules.get(moduleCode).get(i).getRoomNumber());
				// add seat for location
				
				String location = "Building: " + schedules.get(moduleCode).get(i).getBuildingNumber() + ", Room: "
						+ schedules.get(moduleCode).get(i).getRoomNumber() + ", Seat: " + checkSeat(moduleCode);
				Schedule finalSchedule = new Schedule(schedules.get(moduleCode).get(i).getStudentID(), moduleCode,
						getModuleTitle(moduleCode, conn), getDayOfTheWeek(schedules.get(moduleCode).get(i).getDate()),
						schedules.get(moduleCode).get(i).getDate(), schedules.get(moduleCode).get(i).getSessionID(),
						location);
				finalizedSchedules.add(finalSchedule);
				j++;
			}

		}
		System.out.println("Schedules not scheduled" + unavailableSchedules);
		return s;
	}

	public Map<String, ArrayList<Schedule>> assign(Map<String, ArrayList<Schedule>> schedules) throws SQLException {
		for(String moduleCode: schedules.keySet()) {
			for(int i=0; i<schedules.get(moduleCode).size(); i++) {
				if(schedules.get(moduleCode).get(i).getBuildingNumber() == 0 || schedules.get(moduleCode).get(i).getRoomNumber() == 0) {
					getLocations();
					findBuildingAndRoom(schedules.get(moduleCode).get(i));
					int test = 0;
					for(int j=0; j<schedules.get(moduleCode).size(); j++) {
						test++;
						schedules.get(moduleCode).get(j).setBuildingNumber(schedules.get(moduleCode).get(i).getBuildingNumber());
						schedules.get(moduleCode).get(j).setRoomNumber(schedules.get(moduleCode).get(i).getRoomNumber());
					}
					System.out.println(test);
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
	
	public String getModuleTitle(String moduleCode, Connection conn) throws SQLException {
		String query2 = "SELECT * FROM RegisteredStudents WHERE ModuleCode ='" + moduleCode + "'";
		System.out.println(conn);
		stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(query2);
		while (rs.next()) {
			String title = rs.getString("ModuleTitle");
			return title;
		}
		return null;
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
			int capacity = closest(getStudentsForThisModuleCode(s.getModuleCode()),s.getModuleCode());
			room = findRoomNumber(capacity, closestAccessibleSeats);
			building = findBuildingNumber(room);
			
			if(checkIfAvailable(s.getModuleCode(), building, room,  s.getSessionID())) {
				s.setBuildingNumber(building);
				s.setRoomNumber(room);
				return;
			} else {
				for(int x=0; x<list_Capacity_Buildings.size(); x++) {
					if(list_Capacity_Buildings.get(x).getCapacity() == tempCapacity) {
						for(Room key: locations_roomCapacity.keySet()) {
							if(key.getRoomNumber() == room) {
								locations_roomCapacity.remove(key);
								break;
							}
						}
						for(Building key: locations_buildingRoom.keySet()) {
							if(key.getBuildingNumber() == building) {
								locations_buildingRoom.remove(key);		
								break;
							}
						}
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
	
	public int findRoomNumber(int capacity, int accessibleSeats) throws SQLException {
		String query = "SELECT * FROM Location WHERE SeatNumber='"+capacity+ "' AND AccessibleSeatsNumber='"+accessibleSeats+"'";
		stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(query);

		while (rs.next()) {
			int room = rs.getInt(2);
			return room;
		}
	
		return 0;
	}
	
	public int findBuildingNumber(int roomNumber) throws SQLException {
		String query = "SELECT * FROM Location WHERE RoomNumber='"+roomNumber+ "'";
		stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(query);

		while (rs.next()) {
			int building = rs.getInt(1);
			return building;
		}
	
		return 0;
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
		getSchedules();
		insertSchedulesIntoTable(assign(schedules));
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
		getAllStudentsPerModules();
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
		findSessions();
	}

	private void getLocations() throws SQLException {
		String query2 = "SELECT * FROM Location";
		stmt2 = conn.createStatement();
		ResultSet rs2 = stmt2.executeQuery(query2);
		while (rs2.next()) {
			int building = rs2.getInt(1);
			int room = rs2.getInt(2);
			int capacity = rs2.getInt(3);
			int accSeats = rs2.getInt(4);
			Building b = new Building(building);
			Room r = new Room(room);
			Capacity c = new Capacity(capacity, accSeats);
			locations_buildingRoom.put(b, r);
			locations_roomCapacity.put(r, capacity);
			list_Capacity_Buildings.add(c);
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

	public int closest(int of, String moduleCode) throws SQLException {
		int min = Integer.MAX_VALUE;
		int closesetCapacity = 0;
		int secondClosestCapacity = of;
		closestAccessibleSeats = 0;

		for (int i = 0; i < list_Capacity_Buildings.size(); i++) {
			final int diff = Math.abs(list_Capacity_Buildings.get(i).getCapacity() - of);
			if (diff < min) {
				secondClosestCapacity = list_Capacity_Buildings.get(i).getCapacity();
				if (secondClosestCapacity == of && list_Capacity_Buildings.get(i).getNumberOfAccessibleSeats() >= findNumberOfAccessibleSeatsNeeded(moduleCode)) {
					closesetCapacity = secondClosestCapacity;
					closestAccessibleSeats = list_Capacity_Buildings.get(i).getNumberOfAccessibleSeats();
					return closesetCapacity;
				} else if (secondClosestCapacity > of & list_Capacity_Buildings.get(i).getNumberOfAccessibleSeats() >= findNumberOfAccessibleSeatsNeeded(moduleCode)) {
					if (closesetCapacity == 0) {
						closesetCapacity = secondClosestCapacity;
						min = secondClosestCapacity;
						closestAccessibleSeats = list_Capacity_Buildings.get(i).getNumberOfAccessibleSeats();
					} else {
						if (secondClosestCapacity < closesetCapacity) {
							closesetCapacity = secondClosestCapacity;
							closestAccessibleSeats = list_Capacity_Buildings.get(i).getNumberOfAccessibleSeats();
						}
					}
				}
			}
		}
		return closesetCapacity;
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
	
	private void fillAlphabet() {
		alphabet.add("a");
		alphabet.add("b");
		alphabet.add("c");
		alphabet.add("d");
		alphabet.add("e");
		alphabet.add("f");
		alphabet.add("g");
		alphabet.add("h");
		alphabet.add("i");
		alphabet.add("j");
		alphabet.add("k");
		alphabet.add("l");
		alphabet.add("m");
		alphabet.add("n");
		alphabet.add("o");
		alphabet.add("p");
		alphabet.add("q");
		alphabet.add("r");
		alphabet.add("s");
		alphabet.add("t");
		alphabet.add("u");
		alphabet.add("v");
		alphabet.add("w");
		alphabet.add("x");
		alphabet.add("y");
		alphabet.add("z");
	}
	
	private String checkSeat(String moduleCode) {
		fillAlphabet();
		if (tempModule == null) {
			tempModule = moduleCode;
			String str = alphabet.get(0).toUpperCase();
			System.out.println(str);
			return str + seatCounter10;
		} else if (!tempModule.equals(moduleCode)) {
			alphabet.clear();
			fillAlphabet();
			tempModule = moduleCode;
			seatCounter10 = 0;
			String str = alphabet.get(0).toUpperCase();
			seatCounter10++;
			System.out.println(str);
			return str + 0;
		} else {
			seatCounter10++;
			if (seatCounter10 == 10) {
				alphabet.remove(0);
				String str = alphabet.get(0).toUpperCase();
				seatCounter10 = 0;
				System.out.println(str);
				return str + seatCounter10;
			} else {
				String str = alphabet.get(0).toUpperCase();
				System.out.println(str);
				return str + seatCounter10;
			}
		}
	}
	
	public String getDayOfTheWeek(String date) {
		String x = date.substring(0, 2);
		String y = date.substring(3, 5);
		String z = date.substring(6, 10);
		
		int day = Integer.parseInt(x);
		int month = Integer.parseInt(y);
		int year = Integer.parseInt(z);
		
		System.out.println(day + "-" + month + "-" +year);
		LocalDate a = LocalDate.of(year, month, day);
		return a.getDayOfWeek().name();
	}
}