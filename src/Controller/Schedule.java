package Controller;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import Model.Building;
import Model.Capacity;
import Model.Room;

/*
 * @author Damyan Rusinov
 * @This class covers the functionality of the system, dealing with a new schedule
 */

public class Schedule {

	//connection
	protected Connection conn = null;
	
	//statements to query the database
	private Statement stmt = null;
	
	private Statement stmt2 = null;
	//reader for the files to be imported
	
	@SuppressWarnings("unused")
	private DataReader dataReader = null;
	
	/*the following 12 variables represent what each schedule consists of
	 * a schedule has a:
	 * module code
	*/ 
	private String moduleCode = null;
	
	//a module title
	private String moduleTitle = null;
	
	//an accessible seat inside a room specified
	@SuppressWarnings("unused")
	private String accessible;
	
	//location, made out of a building room, room number and seat number
	private String location;
	
	//day of the exam
	private String day;
	
	//date of the exam
	private String date;
	
	//every schedule has a session, representing whether the exam is in the morning or afternoon(i.e. am, pm)
	private String session;
	
	//a student ID
	private int studentID;
	
	//a session ID
	private int sessionID;
	
	//a building number
	private int buildingNumber;
	
	//a room number
	private int roomNumber;
	
	//this variable is used when the calculation of the most optimal location for a certain schedule is to be found
	private int closestAccessibleSeats;
	
	//alphabet list used for seat allocation
	private ArrayList<String> alphabet = new ArrayList<String>();
	
	//counter used for allocating a seat with a different letter per a certain number of students
	private int seatCounter10 = 0;
	
	private String tempModule = null;
	
	private boolean goalReached = false;
	
	//list of all modules
	private List<String> modules = new ArrayList<String>();
	
	//a linked hashmap keeping the building number and room number
	private Map<Building, Room> locations_buildingRoom = new LinkedHashMap<Building, Room>();
	
	//a linked hashmap keeping the room number and its capacity
	private Map<Room, Integer> locations_roomCapacity = new LinkedHashMap<Room, Integer>();
	
	// all modules, and students registered for them.s
	private HashMap<String, ArrayList<Integer>> moduleVstudents = new HashMap<String, ArrayList<Integer>>();
	
	// all modules and their sessions assigned
	private HashMap<String, Integer> moduleVsession = new HashMap<String, Integer>();
	
	// all available sessions
	private List<Integer> sessions = new ArrayList<Integer>();
	
	// the already assigned students
	private ArrayList<Integer> assignedStudents;
	
	/* the already assigned students are assigned based on the modules they are registered on
	 * once a module is assigned to all students, it is marked as an 'assignedModule'
	*/
	private List<String> assignedModules = new ArrayList<String>();
	
	//modules to be assigned
	private List<String> modulesToCheck = new ArrayList<String>();
	
	//a hashmap storing a module code against all the students assigned to it
	private Map<String, ArrayList<Schedule>> schedules = new HashMap<String, ArrayList<Schedule>>();
	
	// a list containing the capacity of all buildings
	private List<Capacity> list_Capacity_Buildings = new ArrayList<Capacity>();
	
	// once a building is occupied, it is marked as unavailable
	private Map<String, Building> unavailableBuildings = new HashMap<String, Building>();
	
	// a schedule, containing all of the aforementioned 7 variables is kept in the following list
	private ArrayList<Schedule> finalizedSchedules = new ArrayList<Schedule>();
	
	public Schedule(int studentID, String moduleCode, int sessionID, String date, String accessible, int buildingNumber,
			int roomNumber) {
		this.studentID = studentID;
		this.moduleCode = moduleCode;
		this.sessionID = sessionID;
		this.accessible = accessible;
		this.buildingNumber = buildingNumber;
		this.roomNumber = roomNumber;
		this.date = date;
	}

	public Schedule(int studentID, String moduleCode, String moduleTitle, String day, String date, String session,
			String location) {
		this.studentID = studentID;
		this.moduleCode = moduleCode;
		this.moduleTitle = moduleTitle;
		this.day = day;
		this.date = date;
		this.session = session;
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

	public String getSessionString() {
		return session;
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

	//method, being called from the main method
	//triggers the whole system's functionality
	public void generateInformation(Connection conn, DataReader dataReader) throws SQLException {
		this.dataReader = dataReader;
		this.conn = conn;
		getAllSessions();
	}

	// get all schedules
	private void getSchedules() throws SQLException {
		for (String module : moduleVsession.keySet()) {
			String query = "SELECT * FROM RegisteredStudents WHERE ModuleCode='" + module + "' ";
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			ArrayList<Schedule> arr = new ArrayList<Schedule>();
			while (rs.next()) {
				int studentID = rs.getInt(1);
				String accessible = rs.getString("AccessibleSeat");
				Schedule schedule = new Schedule(studentID, module, moduleVsession.get(module),
						getDatePerSessionID(moduleVsession.get(module)), accessible, 0, 0);
				arr.add(schedule);
			}
			this.schedules.put(module, arr);
		}
	}

	// get the list of all the finalized schedules
	public ArrayList<Schedule> getFinalSchedules(Connection conn) {
		this.conn = conn;
		return finalizedSchedules;
	}

	// insert into our 'Schedule' table the finalized schedules
	// and add them to our list of finalized schedules
	public Map<String, ArrayList<Schedule>> insertSchedulesIntoTable(Map<String, ArrayList<Schedule>> s)
			throws SQLException {
		for (String moduleCode : s.keySet()) {
			for (int i = 0; i < s.get(moduleCode).size(); i++) {
				//do the insertion
				insertIntoTableSchedule(schedules.get(moduleCode).get(i).getStudentID(), moduleCode,
						schedules.get(moduleCode).get(i).getSessionID(), schedules.get(moduleCode).get(i).getDate(),
						schedules.get(moduleCode).get(i).getBuildingNumber(),
						schedules.get(moduleCode).get(i).getRoomNumber());

				String location = "Building: " + schedules.get(moduleCode).get(i).getBuildingNumber() + ", Room: "
						+ schedules.get(moduleCode).get(i).getRoomNumber() + ", Seat: " + checkSeat(moduleCode);
				Schedule finalSchedule = new Schedule(schedules.get(moduleCode).get(i).getStudentID(), moduleCode,
						getModuleTitle(moduleCode, conn), getDayOfTheWeek(schedules.get(moduleCode).get(i).getDate()),
						schedules.get(moduleCode).get(i).getDate(),
						schedules.get(moduleCode).get(i).getSessionID() + " ("
								+ schedules.get(moduleCode).get(i).getSessionName(sessionID, conn).toUpperCase() + ")",
						location);
				//add them to the list of finalized schedules
				finalizedSchedules.add(finalSchedule);
			}

		}
		return s;
	}

	//if a student has no location, assign them with one
	public Map<String, ArrayList<Schedule>> assign(Map<String, ArrayList<Schedule>> schedules) throws SQLException {
		for (String moduleCode : schedules.keySet()) {
			for (int i = 0; i < schedules.get(moduleCode).size(); i++) {
				if (schedules.get(moduleCode).get(i).getBuildingNumber() == 0
						|| schedules.get(moduleCode).get(i).getRoomNumber() == 0) {
					//find the building and room
					findBuildingAndRoom(schedules.get(moduleCode).get(i));
					for (int j = 0; j < schedules.get(moduleCode).size(); j++) {
						if (schedules.get(moduleCode).get(j).getModuleCode().equals(moduleCode)) {
							//and set it
							schedules.get(moduleCode).get(j)
									.setBuildingNumber(schedules.get(moduleCode).get(i).getBuildingNumber());
							schedules.get(moduleCode).get(j)
									.setRoomNumber(schedules.get(moduleCode).get(i).getRoomNumber());				
						}
					}
					break;
				}
			}
		}
		return schedules;
	}

	//a goal state must cover all of the constraints specified below
	public boolean goal(List<Schedule> schedules) throws SQLException {
		boolean isWrong = false;
		for (int i = 0; i < schedules.size(); i++) {
			if (schedules.get(i).getBuildingNumber() == 0 || schedules.get(i).getRoomNumber() == 0
					|| schedules.get(i).getModuleCode() == null || schedules.get(i).getStudentID() == 0
					|| schedules.get(i).getSessionID() == 0 || schedules.get(i).getDay() == null
					|| schedules.get(i).getDate() == null) {
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

	// find an available building and room
	private void findBuildingAndRoom(Schedule s) throws SQLException {
		int room = 0;
		int building = 0;
		locations_buildingRoom.clear();
		locations_roomCapacity.clear();
		list_Capacity_Buildings.clear();
		getLocations();
		while (!list_Capacity_Buildings.isEmpty()) {
			//get the capacity of the most optimal location, based on the number of students registered on a module code
			int capacity = closest(getStudentsForThisModuleCode(s.getModuleCode()));
			//find the room based on the capacity provided
			room = findRoomNumber(capacity, closestAccessibleSeats);
			//and its corresponding building
			building = findBuildingNumber(room);

			//if the building and room are available for the session given
			if (checkIfAvailable(s.getModuleCode(), building, room, s.getSessionID())) {
				//set as a location
				s.setBuildingNumber(building);
				s.setRoomNumber(room);
				return;
			} else {
				for (int x = 0; x < list_Capacity_Buildings.size();) {
					for (Room key : locations_roomCapacity.keySet()) {
						//otherwise, remove the room from the list of available rooms.
						if (key.getRoomNumber() == room) {
							locations_roomCapacity.remove(key);
							list_Capacity_Buildings.remove(list_Capacity_Buildings.get(x));
							break;
						}
					}
					break;

				}
			}
		}
	}

	//check if a building is available for a given sesion
	private boolean checkIfAvailable(String moduleCode, int buildingNumber, int room, int sessionID) {
		if (unavailableBuildings.isEmpty()) {
			Building b = new Building(buildingNumber, room, sessionID, moduleCode);
			unavailableBuildings.put(moduleCode, b);
			return true;
		} else {
			if (unavailableBuildings.containsKey(moduleCode)) {
				return true;
			} else {
				for (String module : unavailableBuildings.keySet()) {
					if (unavailableBuildings.get(module).getBuildingNumber() == buildingNumber
							&& unavailableBuildings.get(module).getRoomNumber() == room
							&& getSessionID() == sessionID) {
						return false;
					}
				}
			}
		}
		return true;
	}

	// allocate students with a session
	private void findSessions() throws SQLException {
		//for all the modules having students registered on
		for (int i = 0; i < modulesToCheck.size(); i++) {
			//get all the students for the first module
			ArrayList<Integer> module = moduleVstudents.get(modulesToCheck.get(i));
			//loop through every session
			modulesToCheckLoop: for (int j = 0; j < sessions.size(); j++) {
				//base case if no modules have been assigned a session already
				sessionLoop: if (assignedModules.isEmpty()) {
					assignedModules.add(modulesToCheck.get(i));
					moduleVsession.put(modulesToCheck.get(i), sessions.get(j));
					break modulesToCheckLoop;
				} else {
					//inductive case for all the assigned modules
					for (int x = 0; x < assignedModules.size(); x++) {
						//get all students already assigned a session and store them in the following list
						ArrayList<Integer> assignedModule = moduleVstudents.get(assignedModules.get(x));
						//for all the students for the first module
						for (int y = 0; y < module.size(); y++) {
							//get their session
							int currentSession = moduleVsession.get(assignedModules.get(x));
							//if it is the same as the session being passed, that breaks a constraint
							//so we get the next session(no student must be allocated two exams for the same session)
							if (assignedModule.contains(module.get(y)) && getDatePerSessionID(currentSession)
									.equals(getDatePerSessionID(sessions.get(j)))) {
								break sessionLoop;
							}
						}
						//get the next module if all the assigned students have been processed.
						if (x == assignedModules.size() - 1) {
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
		//shuffle in the attempt to find a better allocation(could be extended in future)
		if (assignedModules.size() != modulesToCheck.size()) {
			Collections.shuffle(modulesToCheck);
		}
	}
	


	//insert data into table schedule
	private void insertIntoTableSchedule(int studentID, String moduleCode, int sessionID, String date,
			int buildingNumber, int roomNumber) throws SQLException {
		String query = "INSERT INTO Schedule(StudentID, ModuleCode, SessionID, Date, BuildingNumber, RoomNumber) VALUES ('"
				+ studentID + "', + '" + moduleCode + "', + '" + sessionID + "', + '" + date + "', + '" + buildingNumber
				+ "', + '" + roomNumber + "')";
		stmt = conn.createStatement();
		stmt.executeUpdate(query);
	}

	
	//find the most optimal location, given a number of students registered on a moduleCode
	public int closest(int of) throws SQLException {
		int min = Integer.MAX_VALUE;
		int closesetCapacity = 0;
		int secondClosestCapacity = of;
		closestAccessibleSeats = 0;

		//for all the buildings and their capacities
		for (int i = 0; i < list_Capacity_Buildings.size(); i++) {
			//get the difference between the capacity of the building and the number 'of' students registered for a given module code
			final int diff = Math.abs(list_Capacity_Buildings.get(i).getCapacity() - of);
			//if the location has more seats than number of students
			if (diff < min) {
				//get its capacity
				secondClosestCapacity = list_Capacity_Buildings.get(i).getCapacity();
				// if it is exactly equal to the number of students and there are enough accessible seats available
				if (secondClosestCapacity == of && list_Capacity_Buildings.get(i)
						.getNumberOfAccessibleSeats() >= findNumberOfAccessibleSeatsNeeded(moduleCode)) {
					closesetCapacity = secondClosestCapacity;
					closestAccessibleSeats = list_Capacity_Buildings.get(i).getNumberOfAccessibleSeats();
					//return this location as most optimal
					return closesetCapacity;
					//otherwise, if the location has more seats than the number of students
				} else if (secondClosestCapacity > of & list_Capacity_Buildings.get(i)
						.getNumberOfAccessibleSeats() >= findNumberOfAccessibleSeatsNeeded(moduleCode)) {
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
	
	// fill the list with the letters of the English alphabet
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
	
	// for every 35th student, change the seat letter to the next one of the alphabet
	private String checkSeat(String moduleCode) {
		fillAlphabet();
		if (tempModule == null) {
			tempModule = moduleCode;
			String str = alphabet.get(0).toUpperCase();
			return str + seatCounter10;
		} else if (!tempModule.equals(moduleCode)) {
			alphabet.clear();
			fillAlphabet();
			tempModule = moduleCode;
			seatCounter10 = 0;
			String str = alphabet.get(0).toUpperCase();
			seatCounter10++;
			return str + 0;
		} else {
			seatCounter10++;
			if (seatCounter10 == 35) {
				alphabet.remove(0);
				String str = alphabet.get(0).toUpperCase();
				seatCounter10 = 0;
				return str + seatCounter10;
			} else {
				String str = alphabet.get(0).toUpperCase();
				return str + seatCounter10;
			}
		}
	}
	
	//get the day of the week given the date
	public String getDayOfTheWeek(String date) {
		String x = date.substring(0, 2);
		String y = date.substring(3, 5);
		String z = date.substring(6, 10);

		int day = Integer.parseInt(x);
		int month = Integer.parseInt(y);
		int year = Integer.parseInt(z);

		LocalDate a = LocalDate.of(year, month, day);
		return a.getDayOfWeek().name();
	}
	

	//query the database for the date, according to the session provided
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

	//get the id of the session
	public String getSessionName(int sessionID, Connection conn) throws SQLException {
		String query = "SELECT * FROM Session WHERE ID=" + sessionID;
		stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		String session = null;
		while (rs.next()) {
			session = rs.getString("MorningAfternoon");
			return session;
		}
		return date;
	}

	//get the title of the module provided
	public String getModuleTitle(String moduleCode, Connection conn) throws SQLException {
		String query2 = "SELECT * FROM RegisteredStudents WHERE ModuleCode ='" + moduleCode + "'";
		stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(query2);
		while (rs.next()) {
			String title = rs.getString("ModuleTitle");
			return title;
		}
		return null;
	}

	//given a date, get its session id
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
	
	// find all the students per the module code passed
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

	// find the number of the room, provided the capacity and the number of accessible seats it has
	public int findRoomNumber(int capacity, int accessibleSeats) throws SQLException {
		String query = "SELECT * FROM Location WHERE SeatNumber='" + capacity + "' AND AccessibleSeatsNumber='"
				+ accessibleSeats + "'";
		stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(query);

		while (rs.next()) {
			int room = rs.getInt(2);
			return room;
		}

		return 0;
	}
	
	// find the total number of accessible seats needed
	public int findNumberOfAccessibleSeatsNeeded(String moduleCode) throws SQLException {
		String query2 = "SELECT * FROM RegisteredStudents WHERE ModuleCode='" + moduleCode + "'";
		stmt2 = conn.createStatement();
		ResultSet rs2 = stmt2.executeQuery(query2);
		int seatsNeeded = 0;
		while (rs2.next()) {
			String accessibleFlag = rs2.getString("AccessibleSeat");
			if (accessibleFlag.equals("YES")) {
				seatsNeeded++;
			}
		}

		return seatsNeeded;
	}

	// find the total number of accessible seats available
	public int findNumberOfAccessibleSeatsAvailable(int buildingNumber, int roomNumber) throws SQLException {
		String query2 = "SELECT * FROM Location WHERE BuildingNumber='" + buildingNumber + "' AND RoomNumber='"
				+ roomNumber + "'";
		stmt2 = conn.createStatement();
		ResultSet rs2 = stmt2.executeQuery(query2);
		int availableSeats = 0;
		while (rs2.next()) {
			availableSeats = rs2.getInt(4);
		}

		return availableSeats;
	}

	// find the building number, given the room
	public int findBuildingNumber(int roomNumber) throws SQLException {
		String query = "SELECT * FROM Location WHERE RoomNumber='" + roomNumber + "'";
		stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(query);

		while (rs.next()) {
			int building = rs.getInt(1);
			return building;
		}

		return 0;
	}
	
	// get all the sessions
	private void getAllSessions() throws SQLException {
		String query = "SELECT * FROM Session";
		stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {
			int sessionID = rs.getInt(1);
			sessions.add(sessionID);
		}
		// then populate the modules with students
		populateModules();
		//after they are populated and the allocation has taken place
		getSchedules();
		//insert the finalized schedules into the given table
		insertSchedulesIntoTable(assign(schedules));
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

	// get all students registered to all modules
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

	// populate the lists with locations
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
}