import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

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
	private int moduleCode;
	private int sessionID;
	private String buildingNumber;
	private String roomNumber;
	private HashMap<Integer, Integer> student_session;

	public Schedule(int studentID, int moduleCode, int sessionID, String buildingNumber, String roomNumber) {
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
				dataReader.db.location.findValue(dataReader.db.location.ts.ceiling(this.numberOfStudents)));
		int optimalRoom = dataReader.db.location.getRoomNumber(optimalBuilding);
		
		if(numberOfStudents == 0) {
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
	
	private void generateSchedule(String moduleCode, int numberOfStudents, ArrayList<Integer> studentIDs, int optimalBuilding, int optimalRoom) {
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
					while(rs2.next()) {
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
			for(Integer key: sessionInfo.keySet()) {
				//Generate a new schedule
				System.out.print(key + ", " + sessionInfo.get(key) + ", ");
			}
			System.out.print("]");
		} else {
			return;
		}
	}
	
	private boolean createNewSchedule(String moduleCode, int numberOfStudents, ArrayList<Integer> studentIDs,int optimalBuilding, int optimalRoom, LinkedHashMap<Integer, HashMap<String, String>> sessionInfo) {
		student_session = new HashMap<Integer, Integer>();
		for (int i = 0; i < studentIDs.size(); i++) {
			for (Integer key : sessionInfo.keySet()) {
				if(!student_session.containsKey(student_session.get(key)) && !student_session.containsValue(studentIDs.get(i))) {
					student_session.put(studentIDs.get(i), key);
				} else {
					
				}
			}
		}
		return false;
	}
	
}