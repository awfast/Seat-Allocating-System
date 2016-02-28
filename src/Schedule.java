import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

public class Schedule {

	protected Connection conn = null;
	private Statement stmt = null;
	private Statement stmt2 = null;
	private LinkedHashMap<Integer, HashMap<String, String>> sessionInfo = new LinkedHashMap<Integer, HashMap<String, String>>();
	private LinkedHashMap<String, String> date_morningAfternoon;
	private DataReader dataReader = null;
	private ArrayList<Integer> studentIDs;
	private int numberOfStudents = 0;
	private int studentID;
	private String moduleCode;
	private int sessionID;
	private int buildingNumber;
	private int roomNumber;
	private int counter;
	private HashMap<Integer, Integer> student_session = new HashMap<Integer, Integer>();
	private HashMap<Integer, Integer> building_room = new HashMap<Integer, Integer>();
	private HashMap<HashMap<Integer, Integer>, Integer> occupiedSessions = new HashMap<HashMap<Integer, Integer>, Integer>();

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
		for (String str : dataReader.db.students.getAllModuleCodes()) {
			getNumberOfStudentsPerModuleCode(str);
		}
	}

	private void getNumberOfStudentsPerModuleCode(String moduleCode) throws SQLException {
		int id = 0;
		studentIDs = new ArrayList<Integer>();
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
		fetchAllInformation(moduleCode, this.numberOfStudents);
	}

	private void fetchAllInformation(String moduleCode, int numberOfStudents) throws SQLException {

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
			this.numberOfStudents = 0;
			System.out.println("----------------------------------------------------------------");
		}
	}

	private void generateSchedule(String moduleCode, int numberOfStudents, ArrayList<Integer> studentIDs,
			int optimalBuilding, int optimalRoom) throws SQLException {
		if (!studentIDs.isEmpty()) {
			try {
				String query = "SELECT * FROM Session";
				stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				while (rs.next()) {
					int sessionID = rs.getInt(1);
					String query2 = "SELECT * FROM Session WHERE ID=" + sessionID;
					stmt2 = conn.createStatement();
					ResultSet rs2 = stmt2.executeQuery(query2);
					while (rs2.next()) {
						date_morningAfternoon = new LinkedHashMap<String, String>();
						String date = rs2.getString(2);
						String morningAfternoon = rs2.getString(3);
						date_morningAfternoon.put(date, morningAfternoon);
						sessionInfo.put(sessionID, date_morningAfternoon);
					}
				}
			} catch (SQLException e) {
				System.out.println(e);
			}

			System.out.print("|Session IDs/dates - " + "[");
			String query = "SELECT * FROM RegisteredStudents";
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);

			while (rs.next()) {
				int studentID = rs.getInt(1);
				for (Integer key : sessionInfo.keySet()) {
					// Generate a new schedule
					do {
						building_room.put(optimalBuilding, optimalRoom);
					}
					while (isBuildingAvailable(optimalBuilding, optimalRoom, key)); {
						if(isStudentAvailable(studentID, key)) {
							createNewSchedule(studentID, moduleCode, key, optimalBuilding, optimalRoom);
							System.out.print(key + ", " + sessionInfo.get(key) + ", ");
						} else {
							System.out.println("Building not available");
						}
					}
					// To be added.
					// System.out.println("All sessions for this building are
					// occupied already.");
					// System.out.println("Find an alternative building.");
				}
				System.out.print("]");
			}
		} else {
			return;
		}
	}

	private void createNewSchedule(int studentID, String moduleCode, Integer sessionID, int buldingNumber,
			int roomNumber) throws SQLException {
		String insertSql = "INSERT INTO Schedule(StudentID, ModuleCode, SessionID, BuildingNumber, RoomNumber) VALUES ('"
				+ studentID + "', + '" + moduleCode + "', + '" + sessionID + "', + '" + buldingNumber + "', + '"
				+ roomNumber + "')";
		System.out.println("Inserting into Schedule.. [" + studentID + "]" + "[" + moduleCode + "]" + "[" + sessionID
				+ "]" + "[" + buildingNumber + "]" + "[" + roomNumber + "]");
		stmt2 = conn.createStatement();
		stmt2.executeUpdate(insertSql);
		Schedule schedule = new Schedule(studentID, moduleCode, sessionID, buldingNumber, roomNumber);
	}

	private boolean isBuildingAvailable(int buildingNumber, int roomNumber, Integer session) {
		for(Integer building: building_room.keySet()) {
			System.out.println(building_room.containsKey(building));
			if (!building_room.containsKey(building) && !building_room.containsValue(building_room.get(building))) {
				building_room.put(buildingNumber, roomNumber);
				for (HashMap<Integer, Integer> key : occupiedSessions.keySet()) {
					if ((occupiedSessions.containsKey(key) && !occupiedSessions.containsValue(session))
							|| (!occupiedSessions.containsKey(key) && occupiedSessions.containsValue(session))) {
						occupiedSessions.put(building_room, session);
						System.out.println("available");
						return true;
					} else {
						System.out.println("unavailable");
						return false;
					}
				}
			}			
		}
		return false;
	}

	private boolean isStudentAvailable(int studentID, Integer session) {
		counter++;
		float percent = ((counter * 100.0f) / 100);
		System.out.println("Percent-> " + percent);
		if (!student_session.containsKey(studentIDs) && !student_session.containsValue(session)) {
			student_session.put(studentID, session);
			return true;
		} else {
			// if > 90%
			// change the building/date for these students
			return false;
		}
	}
}