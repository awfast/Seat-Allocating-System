import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Schedule {

	private final String USER = "root";
	private final String PASS = "";
	protected Connection conn = null;
	private Statement stmt = null;
	private final String DB_URL = "jdbc:mysql://localhost:3306/test";
	private int roomNumber = 0;
	private String examDuration;
	private int optimalBuildingNumber = 0;
	private int optimalRoomNumber = 0;
	private DataReader dataReader = null;
	
	public Schedule(int studentID, int moduleCode, String moduleTitle, String day, String date, String duration, String location) {
		
	}
	
	protected void generateInformation(DataReader dataReader) throws SQLException {
		this.dataReader = dataReader;
		//System.out.println("Number Of Students: " + dataReader.db.location.getNumberOfStudents());
		/*System.out.println("Optimal building number: " + dataReader.db.location.getBuildingNumber(dataReader.db.location.getNumberOfStudents()));
		System.out.println("Optimal room number: " + dataReader.db.location.getRoomNumber(dataReader.db.location.getNumberOfStudents(), roomNumber));
		System.out.println("ModuleCodes: " + dataReader.db.students.getAllModuleCodes());
		System.out.println("ModuleTitles: " + dataReader.db.students.getAllModuleTitles());
		System.out.println("Session ID and DATE: " + dataReader.db.session.getAllSessions());*/
		getModuleCode();
	}
	
	private void getModuleCode() throws SQLException {
		for(String str: dataReader.db.students.getAllModuleCodes()) {
			getNumberOfStudentsPerModuleCode(str);
		}
	}
	
	private int getNumberOfStudentsPerModuleCode(String moduleCode) throws SQLException {
		int numberOfStudents = 0;
		int id=0;
		Connection mysqlConn = DriverManager.getConnection(DB_URL, USER, PASS);
		try {
			String query ="SELECT * FROM REGISTRATION WHERE ModuleCode ='"+ moduleCode + "'";
			Statement st = mysqlConn.prepareStatement(query);
			ResultSet rs = st.executeQuery(query);
			while (rs.next()) {
				id = rs.getInt(1);
				numberOfStudents++;
			}
		} catch (SQLException e) {
			System.out.println(e);
		}
	
		int optimalBuilding = dataReader.db.location.getBuildingNumber(dataReader.db.location.findValue(dataReader.db.location.ts.ceiling(numberOfStudents)));
		int optimalRoom = dataReader.db.location.getRoomNumber(optimalBuilding);
		System.out.println("Module Code - " + moduleCode + ", Registered Students - " + numberOfStudents + ", Optimal Building - " + optimalBuilding + ", Optimal room - " + optimalRoom);
		return numberOfStudents;
	}
	
	/*private int getAllStudentIDs() {
		try {
			conn = (Connection) DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.createStatement();
			String userID = "SELECT * FROM STUDENT";
			rs = stmt.executeQuery(userID);

			while (rs.next()) {
				int id = rs.getInt("ID");
				this.studentID = Integer.valueOf(id);
				String name = rs.getString("studentName");
				this.studentName = name;
				temporary_studentList.put(studentID, studentName);
			}
			populateModules(moduleCode_moduleTitle);
		} catch (SQLException e) {
			System.out.println(e);
		}
	}*/
}
