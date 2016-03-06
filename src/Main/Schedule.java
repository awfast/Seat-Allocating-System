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
	private int testCounter = 0;
	private LinkedHashMap<Integer, HashMap<String, String>> sessionInfo = new LinkedHashMap<Integer, HashMap<String, String>>();
	private HashMap<Integer, String> oneSessionMap = new HashMap<Integer, String>();
	private LinkedHashMap<String, String> date_morningAfternoon;
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
	ArrayList<Integer> sessions;
	private float availableStudents = 0f;
	private int lastSession = 0;
	private int numberOfSeats = 0;
	private boolean isFilled = false;
	private AstarSearchAlgo search = new AstarSearchAlgo();
	private HashMap<Integer, Integer> student_session = new HashMap<Integer, Integer>();
	private HashMap<Integer, Integer> building_room = new HashMap<Integer, Integer>();
	private HashMap<HashMap<Integer, Integer>, Integer> occupiedSessions = new HashMap<HashMap<Integer, Integer>, Integer>();
	private TreeSet<Integer> costSet = new TreeSet<Integer>();
	private Map<Node,Integer> ts = new LinkedHashMap<Node, Integer>();

	public Schedule(int studentID, String moduleCode, Integer sessionID, int buildingNumber, int roomNumber) {
		this.studentID = studentID;
		this.moduleCode = moduleCode;
		this.sessionID = sessionID;
		this.buildingNumber = buildingNumber;
		this.roomNumber = roomNumber;
	}

	protected void generateInformation(Connection conn, DataReader dataReader) throws SQLException {
		this.dataReader = dataReader;
		this.conn = conn;
		getModuleCode();
	}

	private void getModuleCode() throws SQLException {
		List<String> modules = dataReader.db.students.getAllModuleCodes();
		for(int i=0; i<modules.size(); i++) {			
			getNumberOfStudentsPerModuleCode(modules.get(i));
		}

		optimalBuilding = dataReader.db.location.getBuildingNumber(
				dataReader.db.location.findValue(dataReader.db.location.ts.ceiling(numberOfStudents)));
		optimalRoom = dataReader.db.location.getRoomNumber(optimalBuilding);
		sessions =  getAllSessions();
		
		String query = "SELECT * FROM Location WHERE RoomNumber ='" + optimalRoom + "'";
		stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {
			numberOfSeats = rs.getInt(3);
		}
		
		for(int i=0; i<modules.size(); i++) {
			String query2 = "SELECT * FROM RegisteredStudents WHERE ModuleCode ='" + modules.get(i) + "'";
			stmt2 = conn.createStatement();
			ResultSet rs2 = stmt2.executeQuery(query2);
			while (rs2.next()) {
				int stud = rs2.getInt(1);
				checkedStudents.put(stud, modules.get(i));
				System.out.println(stud + modules.get(i));
				getStudentsAvailability(stud, sessions.get(i), modules.get(i));
			}
			browseStudents(optimalBuilding, optimalRoom, modules.get(i), 0);	
		}
	
	}

	private boolean areAvailable() {
		availableStudents = ((returned_false + returned_true) * 100.f) / numberOfStudents;
		System.out.println("Available students: " + availableStudents + "%");
		if(availableStudents > 90.0 ) {
			return true;
		} else {
			availableStudents = 0;
			return false;
		}
	}
	private int getNumberOfStudentsPerModuleCode(String moduleCode) throws SQLException {
		int id = 0;
		try {
			String query = "SELECT * FROM RegisteredStudents WHERE ModuleCode ='" + moduleCode + "'";
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				id = rs.getInt(1);
				if (!studentIDs.contains(id)) {
					studentIDs.add(id);
					this.numberOfStudents++;
				}
			}
		} catch (SQLException e) {
			System.out.println(e);
		}
		return 0;
	}
	
	//A* checks
	//pass an initial schedule	
	private void triggerSearch(Map<Node, Integer> ts2) {
		/*if(testCounter == 0 ) {
			Node initial = null;
			Node goal = null;

			for (Node key2 : ts2.keySet()) {
				key2.adjacencies = new Edge[] { new Edge(key2, ts2.get(key2))};
				initial = key2;
				
				key2.adjacencies = new Edge[] { new Edge(key2, ts2.get(key2)) };
				goal = key2;
				
				search.AstarSearch(initial, goal);
				List<Node> path = search.printPath(goal);
				for (int i = 0; i < path.size(); i++) {
					System.out.println("Path: " + path.get(i));
				}
			}
		}*/
		System.out.println("ts: " + ts2);
	}

	private boolean getStudentsAvailability(int studentID, Integer session, String moduleCode) throws SQLException {
		if(oneSessionMap.isEmpty()) {
			oneSessionMap.put(studentID, moduleCode);
			returned_true++;
			return true;
		} else {
			if(oneSessionMap.containsKey(studentID)) {
				for(Integer i: oneSessionMap.keySet()) {
					if(i == studentID && oneSessionMap.get(i).equals(moduleCode)) {
						returned_false++;
						return false;
					}
				}		
			} else {
				student_session.put(studentID, session);
				oneSessionMap.put(studentID, moduleCode);
				returned_true++;
				return true;
			}
			
		}
		return false;
	}
	//end
	private ArrayList<Integer> getAllSessions() throws SQLException {
		ArrayList<Integer> sessions = new ArrayList<Integer>();
		String query = "SELECT * FROM Session";
		stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {
			boolean isFilled = false;
			int sessionID = rs.getInt(1);
			sessions.add(sessionID);
		}
		return sessions;
	}
	private void browseStudents(int optimalBuilding, int optimalRoom, String moduleCode, int session) throws SQLException {	
		int tempSize = ts.size();
		while (!sessions.isEmpty()) {
			while (roomNotOccupied(sessions.get(counter), optimalBuilding, optimalRoom, optimalCost)) {
				if(ts.size() != tempSize) {
					for (Integer stud : oneSessionMap.keySet()) {
						schedule = new Schedule(stud, moduleCode, sessions.get(counter), optimalBuilding, optimalRoom);
						node = new Node(schedule, optimalCost);
						ts.put(node, nonOptimalCost);
						System.out.println(ts.size());
						System.out.println(optimalBuilding + "Seats: " + numberOfSeats);
					}
				}
				if (areAvailable()) {
					for (Integer stud : oneSessionMap.keySet()) {
						schedule = new Schedule(stud, moduleCode, sessions.get(counter), optimalBuilding, optimalRoom);
						node = new Node(schedule, optimalCost);
						ts.put(node, optimalCost);
						System.out.println(ts.size());
						System.out.println(optimalBuilding + "Seats: " + numberOfSeats);
					}
					sessions.remove(counter);
					counter++;
					isFilled = true;
					break;
				} else {
					nonOptimalCost++;
					for (Integer stud : oneSessionMap.keySet()) {
						schedule = new Schedule(stud, moduleCode, sessions.get(counter), optimalBuilding, optimalRoom);
						node = new Node(schedule, nonOptimalCost);
						ts.put(node, nonOptimalCost);
						System.out.println(ts.size());
					}
					sessions.remove(counter);
					counter++;
				}
				returned_false = 0;
				returned_true = 0;
				System.out.println("Student session: " + student_session);
			}
			if (isFilled) {
				break;
			} else if(checkLastSession(nonOptimalCost)){
				break;
			}
			// find another room and repeat
			optimalBuilding = getCorrespondingBuildingNumber(findAnotherRoom(numberOfSeats));
			optimalRoom = getCorrespondingRoomNumber(findAnotherRoom(numberOfSeats));
			continue;	
		}		
		System.out.println(ts);
		System.out.println(ts.size());
		// triggerSearch(ts);
	}
	
	private boolean checkLastSession(int nonOptimalCost) throws SQLException {
		String query = "SELECT * FROM Session";
		stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		rs.afterLast();
		if (rs.previous()) {
			lastSession = rs.getInt(1);
			System.out.println("Last session: " + lastSession);
			if (lastSession == sessionID) {
				nonOptimalCost++;
				for (Integer student_id : student_session.keySet()) {
					schedule = new Schedule(student_id, moduleCode, student_session.get(student_id),
							optimalBuilding, optimalRoom);
					node = new Node(schedule, nonOptimalCost);
					ts.put(node, nonOptimalCost);
					System.out.println(ts.size());
				}
				// last row
				return true;
			}
		}
		return false;
	}
	
	private int findAnotherRoom(int seats) throws SQLException {
		ArrayList<Integer> arr = new ArrayList<Integer>();
		String query3 = "SELECT * FROM Location";
		stmt3 = conn.createStatement();
		ResultSet rs3 = stmt3.executeQuery(query3);
		while (rs3.next()) {
			int seatNumber = rs3.getInt(3);
			arr.add(seatNumber);
		}
	    int smallest = Integer.MAX_VALUE;
	    int secondSmallest = Integer.MAX_VALUE;
	    for (int i = 0; i < arr.size(); i++) {
	    	if(arr.get(i)==smallest){
	            secondSmallest=smallest;
	          } else if (arr.get(i) < smallest) {
	              secondSmallest = smallest;
	              smallest = arr.get(i);
	          } else if (arr.get(i) < secondSmallest) {
	              secondSmallest = arr.get(i);
	          }
		    }
	    System.out.println("Smallest: " + smallest);
	    return smallest;
	}
	
	private int getCorrespondingBuildingNumber(int seats) throws SQLException {
		int building = 0;
		String query3 = "SELECT * FROM Location WHERE SeatNumber=" + seats;
		stmt3 = conn.createStatement();
		ResultSet rs3 = stmt3.executeQuery(query3);
		while (rs3.next()) {
			building = rs3.getInt(1);
		}
		return building;
	}
	
	private int getCorrespondingRoomNumber(int seats) throws SQLException {
		int room = 0;
		String query3 = "SELECT * FROM Location WHERE SeatNumber=" + seats;
		stmt3 = conn.createStatement();
		ResultSet rs3 = stmt3.executeQuery(query3);
		while (rs3.next()) {
			room = rs3.getInt(1);
		}
		return room;
	}
	
	private boolean roomNotOccupied(int session, int optimalBuilding, int optimalRoom, int cost) {
		if (occupiedSessions.isEmpty()) {
			building_room = new HashMap<Integer, Integer>();
			building_room.put(optimalBuilding, optimalRoom);
			occupiedSessions.put(building_room, session);
			return true;
		} else {
			building_room = new HashMap<Integer, Integer>();
			building_room.put(optimalBuilding, optimalRoom);
			System.out.println(optimalBuilding + ", " + optimalRoom + ", " + session);
			System.out.println(occupiedSessions.containsKey(building_room));
			System.out.println(occupiedSessions.containsValue(session));
			System.out.println("\n\n"+occupiedSessions.get(building_room));
			System.out.println("-> " + optimalBuilding + optimalRoom);
			System.out.println("Building_Room reference: "+building_room);
			if (occupiedSessions.containsKey(building_room) && occupiedSessions.containsValue(session)) {
				return false;
			} else {
				occupiedSessions.put(building_room, session);
				nonOptimalCost++;
				return true;
			}
		}
	}
	
	/*private void fetchAllInformation(String moduleCode, int numberOfStudents) throws SQLException {

		int optimalBuilding = dataReader.db.location.getBuildingNumber(
				dataReader.db.location.findValue(dataReader.db.location.ts.ceiling(numberOfStudents)));
		int optimalRoom = dataReader.db.location.getRoomNumber(optimalBuilding);

		if (numberOfStudents == 0) {
			return;
		} else {
			System.out.println("----------------------------------------------------------------");
			System.out.println("|Module Code - " + moduleCode + "|");
			System.out.println("|Registered Students - " + numberOfStudents + "|");
			System.out.println("|Student ids - " + studentIDs + "|");
			System.out.println("|Optimal Building - " + optimalBuilding + "|");
			System.out.println("|Optimal room - " + optimalRoom + "|");
			generateSchedule(moduleCode, numberOfStudents, studentIDs, optimalBuilding, optimalRoom);
			this.student_session = new HashMap<Integer, Integer>();
			this.numberOfStudents = 0;
			System.out.println("----------------------------------------------------------------");
		}
	}

	private void generateSchedule(String moduleCode, int numberOfStudents, ArrayList<Integer> studentIDs,
			int optimalBuilding, int optimalRoom) throws SQLException {
		if (!studentIDs.isEmpty()) {
			try {
				int count = 0;
				String query = "SELECT * FROM Session";
				stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				while (rs.next()) {
					int sessionID = rs.getInt(1);
					String query2 = "SELECT * FROM RegisteredStudents";
					stmt2 = conn.createStatement();
					ResultSet rs2 = stmt2.executeQuery(query2);
					while (rs2.next()) {
						if (isRoomAvailable(optimalBuilding, optimalRoom, sessionID, moduleCode)) {
							int studentID = rs2.getInt(1);
							if (isStudentAvailable(studentID, sessionID)) {
								createNewSchedule(studentID, moduleCode, sessionID, optimalBuilding, optimalRoom);
							} else {
								counter2--;
								return;
							}
						} else {
							if (count < (dataReader.db.session.numberOfSessions * 2)) {
								System.out.println("Building not available.");
								System.out.println("Searching for another session");
								count++;
								return;
							} else {
								count = 0;
								System.out.println("Searching for an alternative room.");
							}
						}
					}
					break;
				}
			} catch (SQLException e) {
				System.out.println(e);
			}
		} else {
			return;
		}
	}

	private void createNewSchedule(int studentID, String moduleCode, Integer sessionID, int optimalBuilding,
			int roomNumber) throws SQLException {
		String insertSql = "INSERT INTO Schedule(StudentID, ModuleCode, SessionID, BuildingNumber, RoomNumber) VALUES ('"
				+ studentID + "', + '" + moduleCode + "', + '" + sessionID + "', + '" + optimalBuilding + "', + '"
				+ roomNumber + "')";
		System.out.println("Inserting into Schedule.. [" + studentID + "]" + "[" + moduleCode + "]" + "[" + sessionID
				+ "]" + "[" + optimalBuilding + "]" + "[" + roomNumber + "]");
		stmt3 = conn.createStatement();
		stmt3.executeUpdate(insertSql);
		// Schedule schedule = new Schedule(studentID, moduleCode, sessionID,
		// optimalBuilding, roomNumber);
	}

	private boolean isRoomAvailable(int buildingNumber, int roomNumber, Integer session, String moduleCode) throws SQLException {
		if (building_room.isEmpty()) {
			building_room.put(buildingNumber, roomNumber);
			if (occupiedSessions.isEmpty()) {
				occupiedSessions.put(building_room, session);
				System.out.println("available");
				return true;
			}
		} else {
			building_room.put(buildingNumber, roomNumber);
			if (occupiedSessions.containsKey(building_room) && occupiedSessions.containsValue(session)) {
				return false;
			} else {
				occupiedSessions.put(building_room, session);
				System.out.println("->" + "building_room " + building_room + ", occupied" + occupiedSessions);
				return true;
			}
		}
		return false;
	}

	private boolean isStudentAvailable(int studentID, Integer session) {
		if (student_session.isEmpty()) {
			counter1++;
			student_session.put(studentID, session);
			return true;
		} else {
			for (Integer s_s : student_session.keySet()) {
				if (s_s != studentID || student_session.get(s_s) != session) {
					counter1++;
					student_session.put(studentID, session);
					return true;
				} else {
					counter2++;
					float unavailableStudents = (counter2 * 100.0f) / counter1;
					System.out.println(unavailableStudents + "%");
					if (unavailableStudents > 90.0) {
						session++;
					}
					return false;
				}
			}
		}
		return false;
	}*/
}