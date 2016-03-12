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
import java.util.TreeSet;

import TreeWithNodesAndSearch.*;

public class Schedule {

	protected Connection conn = null;
	private Statement stmt = null;
	private Statement stmt2 = null;
	private Statement stmt3 = null;
	private Node node = null;
	private HashMap<Integer, String> oneSessionMap = new HashMap<Integer, String>();
	private DataReader dataReader = null;
	private ArrayList<Integer> studentIDs = new ArrayList<Integer>();
	private HashMap<Integer, String> checkedStudents = new HashMap<Integer, String>();
	private int numberOfStudents = 0;
	private int studentID;
	private String moduleCode = null;
	private int sessionID;
	private int optimalCost = 0;
	private int nonOptimalCost = 0;
	private int buildingNumber;
	private int roomNumber;
	private int optimalBuilding = 0;
	private int optimalRoom = 0;
	private Schedule schedule;
	private int returned_true = 0;
	private int returned_false = 0;
	private int counter = 0;
	ArrayList<Integer> sessions = new ArrayList<Integer>();
	private float availableStudents = 0f;
	private int lastSession = 0;
	private int numberOfSeats = 0;
	private boolean isFilled;
	boolean roomOccupied = false;
	private List<String> modules = new ArrayList<String>();
	boolean buildingUnavailable = false;
	Map<HashMap<Integer, String>, Integer> students_total = new HashMap<HashMap<Integer, String>, Integer>();
	HashMap<Integer, String> students_moduleCode;
	HashMap<Integer, Boolean> students_alreadyChecked = new HashMap<Integer, Boolean>();
	Map<String, Integer> moduleCode_sessionID = new HashMap<String, Integer>();
	Map<Integer, Integer> locations_buildingRoom = new HashMap<Integer, Integer>();
	Map<Integer, Integer> locations_roomCapacity = new HashMap<Integer, Integer>();
	HashMap<Schedule, Integer> uncompletedSchedules = new HashMap<Schedule, Integer>();
	ArrayList<Schedule> completedSchedules = new ArrayList<Schedule>();

	public Schedule(int studentID, String moduleCode, int sessionID, int buildingNumber, int roomNumber) {
		this.studentID = studentID;
		this.moduleCode = moduleCode;
		this.sessionID = sessionID;
		this.buildingNumber = buildingNumber;
		this.roomNumber = roomNumber;
	}

	public void generateInformation(Connection conn, DataReader dataReader) throws SQLException {
		this.dataReader = dataReader;
		this.conn = conn;
		getAllSessions();
		populateModules();
		getAllStudentsPerModules();
		getLocations();
		System.out.println(students_total.size());
		for (HashMap<Integer, String> students : students_total.keySet()) {
			for (Integer st : students.keySet()) {
				System.out.println(st);
				Schedule s = new Schedule(st, null, 0, 0, 0);
				uncompletedSchedules.put(s, 0);
				counter++;
			}
		}

		System.out.println(counter);
		assign(uncompletedSchedules);

		printSchedules(uncompletedSchedules);
		System.out.println("Completed.");
	}

	private void printSchedules(HashMap<Schedule, Integer> s) {
		int j = 1;
		for (Schedule i : s.keySet()) {
			System.out.println(j + ".(Student ID: " + i.getStudentID() + ", Module Code: " + i.getModuleCode()
					+ ", Session ID: " + i.getSessionID() + ", Building Number: " + i.getBuildingNumber()
					+ ", Room Number:" + i.getRoomNumber() + ")");
			j++;
		}
	}

	public HashMap<Schedule, Integer> assign(HashMap<Schedule, Integer> uncompletedSchedules) {
		if (goal(uncompletedSchedules)) {
			return uncompletedSchedules;
		} else {
			System.out.println(uncompletedSchedules.size());
			for (Schedule i : uncompletedSchedules.keySet()) {
				if (i.getModuleCode() == null) {
					i.moduleCode = getModuleCodePerStudentID(i.getStudentID());
					return assign(uncompletedSchedules);
				}

				else if (i.getBuildingNumber() == 0) {
					// check capacity for all the students and then allocate
					// building
					for (Integer k : locations_buildingRoom.keySet()) {
						i.buildingNumber = getBuildingNumberPerStudentID(i.getStudentID());
						System.out.println(i.buildingNumber);
						return assign(uncompletedSchedules);
					}
				}

				else if (i.getRoomNumber() == 0) {
					for (Integer k : locations_buildingRoom.keySet()) {
						i.roomNumber = getRoomNumber(i.getStudentID());
						System.out.println(i.roomNumber);
						return assign(uncompletedSchedules);
					}
				}

				else if (i.sessionID == 0) {
					for (HashMap<Integer, String> stud_modules : students_total.keySet()) {
						for (Integer key : stud_modules.keySet()) {
							if (i.studentID == key) {
								System.out.println(i.sessionID);
								i.sessionID = assignSession(i.getStudentID(), i.moduleCode);
								System.out.println(i.sessionID);
								return assign(uncompletedSchedules);
							}
						}
					}
				}
			}
			return uncompletedSchedules;
		}
	}

	public boolean goal(HashMap<Schedule, Integer> schedules) {
		for (Schedule i : schedules.keySet()) {
			// session not added
			if (i.getStudentID() == 0 || i.getModuleCode() == null || i.sessionID == 0 || i.getBuildingNumber() == 0
					|| i.getRoomNumber() == 0) {
				return false;
			}

		}
		return true;
	}

	private int getCapacity(int numberOfStudents, int room) {
		int min = Integer.MAX_VALUE;
		int closest = numberOfStudents;
		final int diff = Math.abs(locations_roomCapacity.get(room) - closest);

		if (diff < min) {
			min = diff;
			closest = locations_roomCapacity.get(room);
		}

		return closest;
	}

	private int assignSession(int studentID, String moduleCode) {
		for (int j = 0; j < modules.size(); j++) {
			for (int i = 0; i < sessions.size(); i++) {
				if (moduleCode_sessionID.isEmpty()) {
					moduleCode_sessionID.put(modules.get(j), sessions.get(i));
					System.out.println(sessions.get(i));
					System.out.println("->>>"+sessions.get(i));
					return sessions.get(i);
				}
			}
		}
		for (int j = 0; j < modules.size(); j++) {
			for (int i = 0; i < sessions.size(); i++) {
				for (String mc : moduleCode_sessionID.keySet()) {
					if(mc == moduleCode) {
						System.out.println("->>>>>>"+moduleCode_sessionID.get(mc));
						return moduleCode_sessionID.get(mc);
					}
					else {
						if(!moduleCode_sessionID.containsKey(moduleCode) && !moduleCode_sessionID.containsValue(sessions.get(i))) {
							moduleCode_sessionID.put(moduleCode, sessions.get(i));
							System.out.println("->>>>>>>>>>>>>>"+sessions.get(i));
							return sessions.get(i);
						}
						continue;
					}
					
				}
			}
		}
		return 0;
	}

	private int getBuildingNumberPerStudentID(int studentID) {
		for (HashMap<Integer, String> key : students_total.keySet()) {
			for (Integer stud : key.keySet()) {
				if (stud == studentID) {
					for (Integer location : locations_buildingRoom.keySet()) {
						System.out.println(location);
						return location;
						/*
						 * if (isBigEnough(students_total.get(key),
						 * getCapacity(students_total.get(key), i))) { return
						 * getCapacity(students_total.get(key), i); }
						 */
					}
				}
			}

		}
		return 0;
	}

	private int getRoomNumber(int studentID) {
		for (HashMap<Integer, String> key : students_total.keySet()) {
			for (Integer stud : key.keySet()) {
				if (stud == studentID) {
					for (Integer location : locations_roomCapacity.keySet()) {
						System.out.println(locations_roomCapacity.get(location));
						return locations_roomCapacity.get(location);
						/*
						 * if (isBigEnough(students_total.get(key),
						 * getCapacity(students_total.get(key), i))) { return
						 * getCapacity(students_total.get(key), i); }
						 */
					}
				}
			}

		}
		return 0;
	}

	private boolean isBigEnough(int totalNumberOfStudents, int roomNumber) {
		if (totalNumberOfStudents < roomNumber) {
			return true;
		}
		return false;
	}

	// function to check if the room is available inside the getRoomNumber
	// method

	private String getModuleCodePerStudentID(int studentID) {
		System.out.println(students_moduleCode.size());
		for (HashMap<Integer, String> total : students_total.keySet()) {
			for (Integer key : total.keySet()) {
				System.out.println(key + "<=>" + studentID);
				if (key == studentID) {
					if (students_alreadyChecked.isEmpty()) {
						students_alreadyChecked.put(key, true);
						System.out.println(total.get(key));
						return total.get(key);
					} else {
						for (Integer i : students_alreadyChecked.keySet()) {
							if (key != i || students_alreadyChecked.get(key) == false) {
								students_alreadyChecked.put(key, true);
								System.out.println(i + total.get(key));
								return total.get(key);
							} else {
								continue;
							}
						}
					}
				} else {
					continue;
				}
			}
		}
		return "";
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
		int numberOfStudents = 0;
		for (int i = 0; i < modules.size(); i++) {
			System.out.println(modules.get(i));
			String query2 = "SELECT * FROM RegisteredStudents WHERE ModuleCode ='" + modules.get(i) + "'";
			stmt2 = conn.createStatement();
			ResultSet rs2 = stmt2.executeQuery(query2);
			while (rs2.next()) {
				students_moduleCode = new HashMap<Integer, String>();
				int stud = rs2.getInt(1);
				students_moduleCode.put(stud, modules.get(i));
				System.out.println(students_moduleCode);
				students_total.put(students_moduleCode, numberOfStudents);
				numberOfStudents++;
			}
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

	public int closest(int of, List<Integer> in) {
		int min = Integer.MAX_VALUE;
		int closest = of;

		for (int v : in) {
			final int diff = Math.abs(v - of);

			if (diff < min) {
				min = diff;
				closest = v;
			}
		}

		return closest;
	}

	/*
	 * private boolean areAvailable() { availableStudents = ((returned_false +
	 * returned_true) * 100.f) / numberOfStudents; System.out.println(
	 * "Available students: " + availableStudents + "%"); if(availableStudents >
	 * 90.0 ) { return true; } else { availableStudents = 0; return false; } }
	 * private int getNumberOfStudentsPerModuleCode(String moduleCode) throws
	 * SQLException { int id = 0; try { System.out.println("->" + moduleCode);
	 * String query = "SELECT * FROM RegisteredStudents WHERE ModuleCode ='" +
	 * moduleCode + "'"; stmt = conn.createStatement(); ResultSet rs =
	 * stmt.executeQuery(query); while (rs.next()) { id = rs.getInt(1); if
	 * (!studentIDs.contains(id)) { studentIDs.add(id); this.numberOfStudents++;
	 * } } } catch (SQLException e) { System.out.println(e); } return 0; }
	 * 
	 * //A* checks //pass an initial schedule private void
	 * triggerSearch(Map<Node, Integer> ts2) { Node initial = null; Node goal =
	 * null;
	 * 
	 * for (Node key2 : ts2.keySet()) { key2.adjacencies = new Edge[] { new
	 * Edge(key2, ts2.get(key2))}; initial = key2;
	 * 
	 * key2.adjacencies = new Edge[] { new Edge(key2, ts2.get(key2)) }; goal =
	 * key2;
	 * 
	 * search.AstarSearch(initial, goal); //List<Node> path =
	 * search.printPath(goal); search.printPathTest(goal);
	 * 
	 * } }
	 * 
	 * private boolean getStudentsAvailability(int studentID, Integer session,
	 * String moduleCode) throws SQLException { if(oneSessionMap.isEmpty()) {
	 * oneSessionMap.put(studentID, moduleCode); returned_true++; return true; }
	 * else { if(oneSessionMap.containsKey(studentID)) { for(Integer i:
	 * oneSessionMap.keySet()) { if(i == studentID &&
	 * oneSessionMap.get(i).equals(moduleCode)) { returned_false++; return
	 * false; } returned_true++; } } else { student_session.put(studentID,
	 * session); oneSessionMap.put(studentID, moduleCode); returned_true++;
	 * return true; }
	 * 
	 * } return false; } //end private ArrayList<Integer> getAllSessions()
	 * throws SQLException { ArrayList<Integer> sessions = new
	 * ArrayList<Integer>(); String query = "SELECT * FROM Session"; stmt =
	 * conn.createStatement(); ResultSet rs = stmt.executeQuery(query); while
	 * (rs.next()) { int sessionID = rs.getInt(1); sessions.add(sessionID); }
	 * return sessions; }
	 * 
	 * private void browseStudents() throws SQLException { List<String> modules
	 * = dataReader.db.students.getAllModuleCodes(); sessions =
	 * getAllSessions(); for (int j = 0; j < sessions.size(); j++) { for (int i
	 * = 0; i < modules.size(); i++) {
	 * getNumberOfStudentsPerModuleCode(modules.get(i)); optimalBuilding =
	 * dataReader.db.location.getBuildingNumber(
	 * dataReader.db.location.findValue(dataReader.db.location.ts.ceiling(
	 * numberOfStudents))); optimalRoom =
	 * dataReader.db.location.getRoomNumber(optimalBuilding);
	 * 
	 * String query = "SELECT * FROM Location WHERE RoomNumber ='" + optimalRoom
	 * + "'"; stmt = conn.createStatement(); ResultSet rs =
	 * stmt.executeQuery(query); while (rs.next()) { numberOfSeats =
	 * rs.getInt(3); }
	 * 
	 * System.out.println("Sessions->" + sessions + sessions.get(j));
	 * 
	 * if (isFilled == true && j == sessions.size() - 1) { optimalBuilding =
	 * getCorrespondingBuildingNumber(findAnotherRoom(numberOfSeats));
	 * optimalRoom = getCorrespondingRoomNumber(findAnotherRoom(numberOfSeats));
	 * continue; } int test = 0; if (roomNotOccupied(sessions.get(j),
	 * optimalBuilding, optimalRoom, optimalCost)) {
	 * System.out.println(modules.get(i)); String query2 =
	 * "SELECT * FROM RegisteredStudents WHERE ModuleCode ='" + modules.get(i) +
	 * "'"; stmt2 = conn.createStatement(); ResultSet rs2 =
	 * stmt2.executeQuery(query2); while (rs2.next()) { int stud =
	 * rs2.getInt(1); checkedStudents.put(stud, modules.get(i));
	 * System.out.println(stud + modules.get(i)); getStudentsAvailability(stud,
	 * sessions.get(j), modules.get(i)); test++; }
	 * 
	 * System.out.println(test); test = 0; if (areAvailable()) { returned_false
	 * = 0; returned_true = 0; if (isNotFilled(optimalRoom, isFilled)) { for
	 * (Integer stud : oneSessionMap.keySet()) { schedule = new Schedule(stud,
	 * modules.get(i), sessions.get(j), optimalBuilding, optimalRoom); node =
	 * new Node(schedule, optimalCost); ts.put(node, optimalCost);
	 * System.out.println("Building Number: " + optimalBuilding + ", Seats: " +
	 * numberOfSeats); }
	 * 
	 * isFilled = true; if(i == modules.size()-1) { break; }
	 * modules.remove(modules.get(i)); i--; continue; } else { isFilled = false;
	 * for (Integer stud : oneSessionMap.keySet()) {
	 * generateUnavailableStudents(stud, modules.get(i), sessions.get(j),
	 * optimalBuilding, optimalRoom); } } } else { for (Integer stud :
	 * oneSessionMap.keySet()) { generateUnavailableStudents(stud,
	 * modules.get(i), sessions.get(j), optimalBuilding, optimalRoom); } } }
	 * else { if(roomOccupied) { roomOccupied = false; continue; } else {
	 * roomOccupied = true; j--; System.out.println(ts.size()); break; } } } } }
	 * 
	 * private void generateUnavailableStudents(int student, String moduleCode,
	 * int session, int optimalBuilding, int optimalRoom) throws SQLException {
	 * for (int i = 0; i < sessions.size(); i++) { if
	 * (roomNotOccupied(sessions.get(i), optimalBuilding, optimalRoom,
	 * optimalCost)) { if (isNotFilled(optimalRoom, isFilled)) { schedule = new
	 * Schedule(student, moduleCode, sessions.get(i), optimalBuilding,
	 * optimalRoom); node = new Node(schedule, optimalCost); ts.put(node,
	 * optimalCost); System.out.println("Building Number: " + optimalBuilding +
	 * ", Seats: " + numberOfSeats); break; } else { optimalBuilding =
	 * getCorrespondingBuildingNumber(findAnotherRoom(numberOfSeats));
	 * optimalRoom = getCorrespondingRoomNumber(findAnotherRoom(numberOfSeats));
	 * continue; } } else { continue; } } }
	 * 
	 * private boolean isNotFilled(int room, boolean isFilled) {
	 * if(roomAvailability.isEmpty()) { roomAvailability.put(room, isFilled);
	 * return true; } else { for(Integer key: roomAvailability.keySet()) {
	 * if(key == room && isFilled == true) { return false; } else { return true;
	 * } } return false; } }
	 * 
	 * private boolean checkLastSession(int nonOptimalCost) throws SQLException
	 * { String query = "SELECT * FROM Session"; stmt = conn.createStatement();
	 * ResultSet rs = stmt.executeQuery(query); rs.afterLast(); if
	 * (rs.previous()) { lastSession = rs.getInt(1); System.out.println(
	 * "Last session: " + lastSession); if (lastSession == sessionID) {
	 * nonOptimalCost++; for (Integer student_id : student_session.keySet()) {
	 * schedule = new Schedule(student_id, moduleCode,
	 * student_session.get(student_id),optimalBuilding, optimalRoom); node = new
	 * Node(schedule, nonOptimalCost); ts.put(node, nonOptimalCost); } // last
	 * row return true; } } return false; }
	 * 
	 * private int findAnotherRoom(int seats) throws SQLException {
	 * ArrayList<Integer> arr = new ArrayList<Integer>(); String query3 =
	 * "SELECT * FROM Location"; stmt3 = conn.createStatement(); ResultSet rs3 =
	 * stmt3.executeQuery(query3); while (rs3.next()) { int seatNumber =
	 * rs3.getInt(3); arr.add(seatNumber); } int smallest = Integer.MAX_VALUE;
	 * int secondSmallest = Integer.MAX_VALUE; for (int i = 0; i < arr.size();
	 * i++) { if(arr.get(i)==smallest){ secondSmallest=smallest; } else if
	 * (arr.get(i) < smallest) { secondSmallest = smallest; smallest =
	 * arr.get(i); } else if (arr.get(i) < secondSmallest) { secondSmallest =
	 * arr.get(i); } } System.out.println("Smallest: " + smallest); return
	 * smallest; }
	 * 
	 * private int getCorrespondingBuildingNumber(int seats) throws SQLException
	 * { int building = 0; String query3 =
	 * "SELECT * FROM Location WHERE SeatNumber=" + seats; stmt3 =
	 * conn.createStatement(); ResultSet rs3 = stmt3.executeQuery(query3); while
	 * (rs3.next()) { building = rs3.getInt(1); } return building; }
	 * 
	 * private int getCorrespondingRoomNumber(int seats) throws SQLException {
	 * int room = 0; String query3 = "SELECT * FROM Location WHERE SeatNumber="
	 * + seats; stmt3 = conn.createStatement(); ResultSet rs3 =
	 * stmt3.executeQuery(query3); while (rs3.next()) { room = rs3.getInt(2); }
	 * return room; }
	 * 
	 * private boolean roomNotOccupied(int session, int optimalBuilding, int
	 * optimalRoom, int cost) { if (occupiedSessions.isEmpty()) { building_room
	 * = new HashMap<Integer, Integer>(); building_room.put(optimalBuilding,
	 * optimalRoom); occupiedSessions.put(building_room, session); return true;
	 * } else { building_room = new HashMap<Integer, Integer>();
	 * building_room.put(optimalBuilding, optimalRoom); for(HashMap<Integer,
	 * Integer> key : occupiedSessions.keySet()) { if(key == building_room &&
	 * occupiedSessions.get(key) == session) { return false; } else {
	 * occupiedSessions.put(building_room, session); nonOptimalCost++; return
	 * true; } } } return false; }
	 */

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
}