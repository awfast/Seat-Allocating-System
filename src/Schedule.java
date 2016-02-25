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

	public Schedule(int studentID, int moduleCode, String moduleTitle, String day, String date, String duration,
			String location) {

	}

	protected void generateInformation(Connection conn, DataReader dataReader) throws SQLException {
		this.dataReader = dataReader;
		this.conn = conn;
		// System.out.println("Number Of Students: " +
		// dataReader.db.location.getNumberOfStudents());
		/*
		 * System.out.println("Optimal building number: " +
		 * dataReader.db.location.getBuildingNumber(dataReader.db.location.
		 * getNumberOfStudents())); System.out.println("Optimal room number: " +
		 * dataReader.db.location.getRoomNumber(dataReader.db.location.
		 * getNumberOfStudents(), roomNumber)); System.out.println(
		 * "ModuleCodes: " + dataReader.db.students.getAllModuleCodes());
		 * System.out.println("ModuleTitles: " +
		 * dataReader.db.students.getAllModuleTitles()); System.out.println(
		 * "Session ID and DATE: " + dataReader.db.session.getAllSessions());
		 */
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
			getSessions();
			this.numberOfStudents = 0;	
			System.out.println("----------------------------------------------------------------");
		}
	}
	
	private void getSessions() {
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
				System.out.print(key + ", " + sessionInfo.get(key) + ", ");
			}
			System.out.print("]");
		} else {
			return;
		}
	}
}