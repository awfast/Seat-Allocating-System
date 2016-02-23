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
	private ArrayList<Integer> studentIDs;
	private int numberOfStudents = 0;
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
		int id=0;		
		studentIDs = new ArrayList<Integer>();
		Connection mysqlConn = DriverManager.getConnection(DB_URL, USER, PASS);
		try {
			String query ="SELECT * FROM RegisteredStudents WHERE ModuleCode ='"+ moduleCode + "'";
			Statement st = mysqlConn.prepareStatement(query);
			ResultSet rs = st.executeQuery(query);
			while (rs.next()) {
				id = rs.getInt(1);
				if(!studentIDs.contains(id)) {
					studentIDs.add(id);
					this.numberOfStudents++;		
				}	
			}
		} catch (SQLException e) {
			System.out.println(e);
		}
		
		fetchAllInformation(moduleCode, this.numberOfStudents);
		
		return this.numberOfStudents;
	}
	
	private void fetchAllInformation(String moduleCode, int numberOfStudents) throws SQLException {
		int optimalBuilding = dataReader.db.location.getBuildingNumber(dataReader.db.location.findValue(dataReader.db.location.ts.ceiling(this.numberOfStudents)));
		int optimalRoom = dataReader.db.location.getRoomNumber(optimalBuilding);
		
		System.out.println("----------------------------------------------------------------");
		System.out.println("|Module Code - " + moduleCode + "|");
		System.out.println("|Registered Students - " + numberOfStudents + "|");
		System.out.println("|Student ids - " + studentIDs + "|");		
		System.out.println("|Optimal Building - " + optimalBuilding + "|");
		System.out.println("|Optimal room - " + optimalRoom + "|");
		System.out.println("");
		
	}
}
