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
	private LinkedHashMap<Integer, HashMap<String, String>> sessionInfo = new LinkedHashMap<Integer, HashMap<String, String>>();
	private LinkedHashMap<String, String> date_morningAfternoon;
	private DataReader dataReader = null;
	private ArrayList<Integer> studentIDs = new ArrayList<Integer>();
	private HashMap<Integer, String> checkedStudents = new HashMap<Integer, String>();
	private int numberOfStudents = 0;
	private int studentID;
	private String moduleCode = null;
	private int sessionID;
	private int buildingNumber;
	private int roomNumber;
	private int returned_true = 0;
	private int returned_false = 0;
	private String moduleCodeCheck = null;
	private float availableStudents = 0f;
	private int lastSession = 0;
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
		int optimalBuilding = dataReader.db.location.getBuildingNumber(
				dataReader.db.location.findValue(dataReader.db.location.ts.ceiling(numberOfStudents)));
		int optimalRoom = dataReader.db.location.getRoomNumber(optimalBuilding);
		List<String> modules = dataReader.db.students.getAllModuleCodes();
		
		String query = "SELECT * FROM Session";
		stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {
			int sessionID = rs.getInt(1);
			for(int i=0; i<modules.size(); i++) {
				getNumberOfStudentsPerModuleCode(modules.get(i));
				getStudentID(studentID, sessionID, modules.get(i));
				System.out.println("module " + modules.get(i));
			}
		}
		for(int i=0; i<modules.size(); i++) {			
			browseStudents(optimalBuilding, optimalRoom, modules.get(i));	
		}
	}

	private boolean areAvailable() {
		availableStudents = ((returned_false + returned_true) * 100.f) / numberOfStudents;
		System.out.println("Available students: " + availableStudents);
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
	
	private int getStudentID(int studentID, int sessionID, String moduleCode) throws SQLException {
		try {
			String query = "SELECT * FROM RegisteredStudents WHERE ModuleCode ='" + moduleCode + "'";
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				int id = rs.getInt(1);
				studentID = id;
				checkedStudents.put(id, moduleCode);
			}

		} catch (SQLException e) {
			System.out.println(e);
		}
		return studentID;
	}
	
	//A* checks
	//pass an initial schedule	
	private void triggerSearch(Map<Node, Integer> ts2) {
		 Entry<Node, Integer> entry=ts2.entrySet().iterator().next();
		 Node key= entry.getKey();
		 Integer value=entry.getValue();
		 
		 Node initial = null;
		 Node goal = null;
		for(Node key2 : ts2.keySet()) {
			if(key == key2 && value == ts2.get(key2)) {
				key2.adjacencies = new Edge[] { new Edge(key2, value)};
				initial = key2;
			} else if(ts2.get(key2) != ts2.size()-1){
				key2.adjacencies = new Edge[] { new Edge(key2, ts2.get(key))};	
				goal = key2;
			} else {
				search.AstarSearch(initial, goal);
				List<Node> path = search.printPath(goal);
				for(int i=0; i<path.size(); i++) {
				System.out.println("Path: " + path.get(i));				
				}
			}
		}	
	}
	
	/*private Schedule getTotalCost(Schedule existingSchedule, int cost) {
		Schedule initialSchedule = new Schedule();
		
		getModuleCost(existingSchedule, cost);
		return new Schedule(existingSchedule,cost);
	}
	
	private int getModuleCost(Schedule schedule, int moduleCost) {
		if(this.moduleCode.equals(schedule.moduleCode)) {
			
		} else {
			
		}
		return moduleCost;
	}*/
	
	private String getModule(String module) throws SQLException {
		if(this.moduleCodeCheck.equals(module)) {
			return module;
		} else {
			this.moduleCodeCheck = module;
			return module;
		}
	}
	
	private boolean getStudentsAvailability(int studentID, Integer session, String moduleCode) throws SQLException {
		if(student_session.containsKey(studentID) && student_session.containsValue(session)) {
			returned_false++;
			return false;					
		} else {
			student_session.put(studentID, session);
			returned_true++;
			return true;
		}
	}
	//end
	
	private void browseStudents(int optimalBuilding, int seats, String moduleCode) throws SQLException {
		int cost = 0;
		int cost2 = 0;
		String query = "SELECT * FROM Session";
		stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {
			int sessionID = rs.getInt(1);
			System.out.println("Here-> " + sessionID);
			
			//Step 1. if more than 90% of the students are available to that session, proceed to Step 2.
				for(Integer student: checkedStudents.keySet()) {
					getStudentsAvailability(student, sessionID, checkedStudents.get(student)); 
					if(areAvailable()) {
						//Step 2. if room exists for the same session -> search and schedule students; high priority
						if(roomNotOccupied(sessionID, optimalBuilding, seats, cost)) {
							for(Integer student_id: student_session.keySet()) {
								Schedule schedule = new Schedule(student_id, moduleCode, student_session.get(student_id), optimalBuilding, seats);
								node = new Node(schedule, cost2);
								ts.put(node,  cost2);
							}
						//If room doesn't exist for that session -> less likely to be selected first
						} else {
							rs.afterLast();
							while (rs.previous()) {
							  lastSession = rs.getInt(1);
							  //last row
							  if(lastSession == sessionID ) {
								  cost++;
								  for(Integer student_id: student_session.keySet()) {
									  Schedule schedule = new Schedule(student_id, moduleCode, student_session.get(student_id), optimalBuilding, seats);
									  node = new Node(schedule, cost2);
									  ts.put(node,  cost2);
								  }
								  //last row
							  } else {
								  continue;						  
							  }
							  //find another room and repeat
							  browseStudents(getCorrespondingBuildingNumber(findAnotherRoom(seats), 0),getCorrespondingRoomNumber(findAnotherRoom(seats), 0), moduleCode);
							}
						}
					//If less than 90% of the students are available for that session -> less likely to be allocated first
					} else {
						cost++;
						Schedule schedule = new Schedule(student, moduleCode, sessionID, optimalBuilding, seats);
						node = new Node(schedule, cost); 
						ts.put(node, cost);					
						continue;
					}
				}
				returned_false = 0;
				returned_true = 0;
				System.out.println(student_session);				
			} 
		triggerSearch(ts);
	}
	
	private int findAnotherRoom(int seats) throws SQLException {
		ArrayList<Integer> arr = new ArrayList<Integer>();
		String query3 = "SELECT * FROM Location";
		stmt3 = conn.createStatement();
		ResultSet rs3 = stmt3.executeQuery(query3);
		while (rs3.next()) {
			int sessionID = rs3.getInt(3);
			arr.add(sessionID);
		}
	    int smallest = Integer.MAX_VALUE;
	    int secondSmallest = Integer.MAX_VALUE;
	    for (int i = 0; i < arr.size(); i++) {
	    if (arr.get(i) < seats) {
	            secondSmallest = smallest;
	            smallest = arr.get(i);
	            seats = secondSmallest;
	        } else if (arr.get(i) > secondSmallest) {
	            secondSmallest = arr.get(i);
	            seats = secondSmallest;
	        }
	    }
		return seats;
	}
	
	private int getCorrespondingBuildingNumber(int seats, int buildingNumber) throws SQLException {
		String query3 = "SELECT * FROM Location WHERE BuildingNumber=" + seats;
		stmt3 = conn.createStatement();
		ResultSet rs3 = stmt3.executeQuery(query3);
		while (rs3.next()) {
			int building = rs3.getInt(1);
			buildingNumber = building;
		}
		return buildingNumber;
	}
	
	private int getCorrespondingRoomNumber(int seats, int roomNumber) throws SQLException {
		String query3 = "SELECT * FROM Location WHERE RoomNumber=" + seats;
		stmt3 = conn.createStatement();
		ResultSet rs3 = stmt3.executeQuery(query3);
		while (rs3.next()) {
			int room = rs3.getInt(1);
			roomNumber = room;
		}
		return roomNumber;
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
			if (occupiedSessions.containsKey(building_room) && occupiedSessions.containsValue(session)) {
				return false;
			} else {
				cost++;
				for (Integer student_id : student_session.keySet()) {
					Schedule schedule = new Schedule(student_id, moduleCode, student_session.get(student_id), optimalBuilding, optimalRoom);
					Node initialNode = new Node(schedule, cost);
					ts.put(initialNode, cost);
				}
				occupiedSessions.put(building_room, session);
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