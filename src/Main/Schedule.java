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
	private boolean isFilled;
	boolean roomOccupied;
	boolean buildingUnavailable = false;
	private AstarSearchAlgo search = new AstarSearchAlgo();
	private HashMap<Integer, Integer> student_session = new HashMap<Integer, Integer>();
	private HashMap<Integer, Integer> building_room = new HashMap<Integer, Integer>();
	private HashMap<HashMap<Integer, Integer>, Integer> occupiedSessions = new HashMap<HashMap<Integer, Integer>, Integer>();
	private Map<Node,Integer> ts = new LinkedHashMap<Node, Integer>();
	private Map<Integer, Boolean> roomAvailability = new HashMap<Integer, Boolean>();
	BinaryTree bt = new BinaryTree();
	

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
		triggerSearch(ts);
	}

	private void getModuleCode() throws SQLException {
		List<String> modules = dataReader.db.students.getAllModuleCodes();
		sessions = getAllSessions();
		
		for(int j=0; j<sessions.size(); j++) {				
			for (int i = 0; i < modules.size(); i++) {
				getNumberOfStudentsPerModuleCode(modules.get(i));
				optimalBuilding = dataReader.db.location.getBuildingNumber(dataReader.db.location.findValue(dataReader.db.location.ts.ceiling(numberOfStudents)));
				optimalRoom = dataReader.db.location.getRoomNumber(optimalBuilding);
				
				String query = "SELECT * FROM Location WHERE RoomNumber ='" + optimalRoom + "'";
				stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				while (rs.next()) {
					numberOfSeats = rs.getInt(3);
				}
				
				System.out.println("Sessions->" + sessions + sessions.get(j));
				browseStudents(optimalBuilding, optimalRoom, modules.get(i), sessions.get(j));
			}
			if(isFilled == true && j == modules.size()-1) {
				break;
			} else {
				continue;
			}
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
			int sessionID = rs.getInt(1);
			sessions.add(sessionID);
		}
		return sessions;
	}

	private void browseStudents(int optimalBuilding, int optimalRoom, String moduleCode, int session)
			throws SQLException {
		if(!buildingUnavailable) {
			while (roomNotOccupied(session, optimalBuilding, optimalRoom, optimalCost)) {
				String query2 = "SELECT * FROM RegisteredStudents WHERE ModuleCode ='" + moduleCode + "'";
				stmt2 = conn.createStatement();
				ResultSet rs2 = stmt2.executeQuery(query2);
				while (rs2.next()) {
					int stud = rs2.getInt(1);
					checkedStudents.put(stud, moduleCode);
					System.out.println(stud + moduleCode);
					getStudentsAvailability(stud, session, moduleCode);
				}
				
				if (areAvailable()) {
					if(isNotFilled(optimalRoom, isFilled)) {
						for (Integer stud : oneSessionMap.keySet()) {
							schedule = new Schedule(stud, moduleCode, session, optimalBuilding, optimalRoom);
							node = new Node(schedule, optimalCost);
							ts.put(node, optimalCost);
							System.out.println("Building Number: " + optimalBuilding + ", Seats: " + numberOfSeats);
						}
						isFilled = true;
						break;							
					}
					else {
						isFilled = false;
						optimalBuilding = getCorrespondingBuildingNumber(findAnotherRoom(numberOfSeats));
						optimalRoom = getCorrespondingRoomNumber(findAnotherRoom(numberOfSeats));
						this.roomNumber = optimalRoom;
						continue;
					}
				} else {
					break;
				}
			}			
			System.out.println(ts.size());
		} else {
			buildingUnavailable = false;
			optimalBuilding = getCorrespondingBuildingNumber(findAnotherRoom(numberOfSeats));
			optimalRoom = getCorrespondingRoomNumber(findAnotherRoom(numberOfSeats));
			browseStudents(optimalBuilding, optimalRoom, moduleCode, session);
		}
	}
	
	private boolean isNotFilled(int room, boolean isFilled) {
		if(roomAvailability.isEmpty()) {
			roomAvailability.put(room, isFilled);
			return true;
		} else {
			for(Integer key: roomAvailability.keySet()) {
				if(key == room && isFilled == true) {
					return false;
				} else {					
					return true;
				}
			}
			return false;
		}
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
					ts.put(node, nonOptimalCost);
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
			room = rs3.getInt(2);
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
			if (occupiedSessions.containsKey(building_room) && occupiedSessions.containsValue(session)) {
				return false;
			} else {
				occupiedSessions.put(building_room, session);
				nonOptimalCost++;
				return true;
			}
		}
	}
	
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