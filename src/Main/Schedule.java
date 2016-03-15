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
	
	int test = 0;
	protected Connection conn = null;
	private Statement stmt = null;
	private Statement stmt2 = null;
	private Statement stmt3 = null;
	private DataReader dataReader = null;
	private HashMap<Integer, String> checkedStudents = new HashMap<Integer, String>();
	private int numberOfStudents = 0;
	private int studentID;
	private String moduleCode = null;
	private int sessionID;
	private int buildingNumber;
	private int roomNumber;
	private int optimalBuilding = 0;
	private int optimalRoom = 0;
	private Schedule schedule;
	private int returned_true = 0;
	private int returned_false = 0;
	private int counter = 0;
	ArrayList<Integer> sessions = new ArrayList<Integer>();
	boolean roomOccupied = false;
	private List<String> modules = new ArrayList<String>();
	boolean buildingUnavailable = false;
	Map<HashMap<Integer, String>, Integer> students_total = new HashMap<HashMap<Integer, String>, Integer>();
	HashMap<Integer, String> students_moduleCode;
	Map<HashMap<Integer, String>, Boolean> students_alreadyChecked = new HashMap<HashMap<Integer, String>, Boolean>();
	Map<String, Integer> moduleCode_sessionID = new HashMap<String, Integer>();
	Map<Integer, Integer> locations_buildingRoom = new HashMap<Integer, Integer>();
	Map<Integer, Integer> locations_roomCapacity = new HashMap<Integer, Integer>();
	Map<Integer, Integer> building_sessionID = new HashMap<Integer, Integer>();
	Map<Integer, Integer> room_sessionID = new HashMap<Integer, Integer>();
	Map<HashMap<Integer, String>, HashMap<Integer, Boolean>> students_available = new HashMap<HashMap<Integer, String>, HashMap<Integer, Boolean>>();
	HashMap<Schedule, String> uncompletedSchedules = new HashMap<Schedule, String>();
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
		System.out.println(students_total);
		getLocations();
		System.out.println(students_total.size());
		for (HashMap<Integer, String> students : students_total.keySet()) {
			for (Integer student : students.keySet()) {
				String moduleCode = students.get(student);
				int session = assignSession(student, moduleCode);
				int building = getOptimalBuilding(students.get(student));
				int room = getRoomNumber(moduleCode);
				Schedule s = new Schedule(student, moduleCode, session, building, room);
				uncompletedSchedules.put(s, "");
				counter++;
			}
		}

		System.out.println(counter);
		
		assign(uncompletedSchedules);
		printSchedules(uncompletedSchedules);
		System.out.println("Completed.");
	}

	private void printSchedules(HashMap<Schedule, String> s) {
		int j = 1;
		for (Schedule i : s.keySet()) {
			System.out.println(j + ".(Student ID: " + i.getStudentID() + ", Module Code: " + i.getModuleCode()
					+ ", Session ID: " + i.getSessionID() + ", Building Number: " + i.getBuildingNumber()
					+ ", Room Number:" + i.getRoomNumber() + ")");
			j++;
		}
	}

	public HashMap<Schedule, String> assign(HashMap<Schedule, String> uncompletedSchedules) throws SQLException {
		if (goal(uncompletedSchedules)) {
			return uncompletedSchedules;
		} else {
				

				/*if (i.getRoomNumber() == 0) {
					for (Integer k : locations_buildingRoom.keySet()) {
						i.roomNumber = getRoomNumber(i.getStudentID());
						System.out.println(i.roomNumber);
						System.out.println("------------------------------------------------------------");
						//return assign(uncompletedSchedules);
					}
				}*/

				/*if (i.sessionID == 0) {
					for (HashMap<Integer, String> stud_modules : students_total.keySet()) {
						for (Integer key : stud_modules.keySet()) {
							if (i.studentID == key) {
								System.out.println(i.sessionID);
								i.sessionID = assignSession(i.getStudentID(), i.moduleCode);
								System.out.println(i.sessionID);
								return	assign(uncompletedSchedules);
							}
						}
					}
				}*/
			
		}
		return uncompletedSchedules;
	}
	
	private boolean moduleCodeIsNull() {
		return false;
	}
	
	/*private int getOptimalRoom(int numberOfStudents) {
		if (isBigEnough(numberOfStudents, closest(numberOfStudents, locations_roomCapacity))) {
			if (closest == false) {
				return closest(numberOfStudents, locations_roomCapacity);
			} else {
				closest = true;
				locations_roomCapacity.remove(locations_roomCapacity.get(closest(numberOfStudents, locations_roomCapacity)));
				return closest(numberOfStudents, locations_roomCapacity);
			}
		}
		return 0;
	}*/

	public boolean goal(HashMap<Schedule, String> schedules) throws SQLException {
		for (Schedule i : schedules.keySet()) {
			if (i.studentID == 0) {
				return false;
			}
			
		}
		System.out.println("Everything has been checked. -> TRUE");
		return true;
	}
	
	private int getOptimalBuilding(String moduleCode) {
		for (HashMap<Integer, String> map : students_total.keySet()) {
			for (Integer students : map.keySet()) {
				if (map.get(students).equals(moduleCode)) {
					for (Integer building : locations_buildingRoom.keySet()) {
						System.out.println(students_total + " - " + map.get(students) + "-" + students_total.get(map));
						for (Integer room : locations_roomCapacity.keySet()) {
							if (locations_roomCapacity.get(room) == closest(students_total.get(map),locations_roomCapacity)) {
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
		for(HashMap<Integer, String> key: students_total.keySet()) {
			for(Integer i: key.keySet()) {
				if(key.get(i).equals(moduleCode)) {
					return true;
					/*availableStudents = ((returned_false + returned_true) * 100.f) / students_total.get(key); 
					if(availableStudents > 90.0) {
						System.out.println("Available students: " + availableStudents + "%"); 
						return true;
					} else {
						System.out.println("Available students: " + availableStudents + "%");
						return false;
					}*/
				}
			}
		}
		return false;
	}
	
	private boolean checkstudentsAvailability(String moduleCode, int studentID, int sessionID) {
		for(Integer key: students_moduleCode.keySet()) {
			if(students_moduleCode.get(key).equals(moduleCode)) {
				if(!students_available.isEmpty()) {
					for(HashMap<Integer, String> map: students_available.keySet()) {
						for(Integer i: map.keySet()) {
							if(i == studentID) {
								for(Integer session: students_available.get(map).keySet()) {
									if(students_available.get(map).get(session) == true) {
										returned_true++;
										students_available.get(map).put(session, false);
										return true;
										//available
									} else {
										returned_false++;
										return false;
										//unavailable
									}
								}
							}
						}
					}
				} else {
					for(Integer i: students_moduleCode.keySet()) {
						if(students_moduleCode.get(i) == moduleCode) {
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
		if(building_sessionID.isEmpty()) {
			building_sessionID.put(building, session);
			return true;
		} else {
			for(Integer key: building_sessionID.keySet()) {
				if(key == building && building_sessionID.get(key) == session) {
					return false;
				} else {
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean isRoomAvailable(int room, int session) {
		if(room_sessionID.isEmpty()) {
			room_sessionID.put(room, session);
			return true;
		} else {
			for(Integer key: room_sessionID.keySet()) {
				if(key == room && room_sessionID.get(key) == session) {
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
	
	// function to check if the room is available inside the getRoomNumber
	// method
	private int getRoomNumber(String moduleCode) {
		for (HashMap<Integer, String> key : students_total.keySet()) {
			for(Integer student: key.keySet()) {
				if(moduleCode.equals(key.get(student))) {
					for(Integer room: locations_roomCapacity.keySet()) {
						if(locations_roomCapacity.get(room) == closest(students_total.get(key), locations_roomCapacity)) {
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
			System.out.println(students_moduleCode);
			students_total.put(students_moduleCode, numberOfStudents);
			System.out.println(students_moduleCode);
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
		int min = Integer.MAX_VALUE;
		int closest = of;

		for (int v : room_capacity.keySet()) {
			final int diff = Math.abs(room_capacity.get(v) - of);
			if (diff < min) {
				closest = room_capacity.get(v);
				if(closest > of) {
					min = closest;
				}
			}
		}

		return min;
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
}