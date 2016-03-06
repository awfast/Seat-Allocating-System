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
	ArrayList<Integer> sessions;
	private float availableStudents = 0f;
	private int lastSession = 0;
	private int numberOfSeats = 0;
	private boolean isFilled = false;
	private AstarSearchAlgo search = new AstarSearchAlgo();
	private HashMap<Integer, Integer> student_session = new HashMap<Integer, Integer>();
	private HashMap<Integer, Integer> building_room = new HashMap<Integer, Integer>();
	private HashMap<HashMap<Integer, Integer>, Integer> occupiedSessions = new HashMap<HashMap<Integer, Integer>, Integer>();
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
		Node initial = null;
		Node goal = null;

		for (Node key2 : ts2.keySet()) {
			key2.adjacencies = new Edge[] { new Edge(key2, ts2.get(key2))};
			initial = key2;
			
			key2.adjacencies = new Edge[] { new Edge(key2, ts2.get(key2)) };
			goal = key2;
			
			search.AstarSearch(initial, goal);
			//List<Node> path = search.printPath(goal);
			search.printPathTest(goal);
			
		}
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
			isFilled = false;
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
						System.out.println("->" + schedule + " + optCost -> " +optimalCost);
						ts.put(node, nonOptimalCost);
						System.out.println(optimalBuilding + "Seats: " + numberOfSeats);
					}
				}
				if (areAvailable()) {
					for (Integer stud : oneSessionMap.keySet()) {
						schedule = new Schedule(stud, moduleCode, sessions.get(counter), optimalBuilding, optimalRoom);
						node = new Node(schedule, optimalCost);
						System.out.println("->" + schedule + " + optCost -> " +optimalCost);
						ts.put(node, optimalCost);
						System.out.println(optimalBuilding + "Seats: " + numberOfSeats);
					}
					sessions.remove(counter);
					counter = 0;
					counter++;
					isFilled = true;
					break;
				} else {
					nonOptimalCost++;
					for (Integer stud : oneSessionMap.keySet()) {
						schedule = new Schedule(stud, moduleCode, sessions.get(counter), optimalBuilding, optimalRoom);
						node = new Node(schedule, nonOptimalCost);
						System.out.println("->" + schedule + " + nonOptCost -> " +nonOptimalCost);
						ts.put(node, nonOptimalCost);
					}
					sessions.remove(counter);
					counter = 0;
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
		triggerSearch(ts);
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
					schedule = new Schedule(student_id, moduleCode, student_session.get(student_id),optimalBuilding, optimalRoom);
					node = new Node(schedule, nonOptimalCost);
					System.out.println("->" + schedule + " + nonOptCost -> " + nonOptimalCost);
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
	
	//public Schedule(int studentID, String moduleCode, Integer sessionID, int buildingNumber, int roomNumber) {
	/*this.studentID = studentID;
	this.moduleCode = moduleCode;
	this.sessionID = sessionID;
	this.buildingNumber = buildingNumber;
	this.roomNumber = roomNumber;
	*/
	
	public int getStudentID(Schedule schedule) {
		return schedule.studentID;
	}
	
	public String getModuleCode(Schedule schedule) {
		return schedule.moduleCode;
	}
	
	public int getSessionID(Schedule schedule) {
		return schedule.sessionID;
	}
	
	public int getBuildingNumber(Schedule schedule) {
		return schedule.buildingNumber;
	}
	
	public int getRoomNumber(Schedule schedule) {
		return schedule.roomNumber;
	}
}