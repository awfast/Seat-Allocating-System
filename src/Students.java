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
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

public class Students {

	protected Connection conn = null;
	private Statement stmt = null;
	private Statement stmt2;
	protected HashMap<String, String> moduleCode_moduleTitle;
	private int studentID;
	private String studentName;
	private final String USER = "root";
	private final String PASS = "";
	private final String DB_URL = "jdbc:mysql://localhost:3306/test";
	private ResultSet rs;
	protected String moduleCode = null;
	protected String moduleTitle = null;
	private LinkedHashMap<Integer, String> temporary_studentList = new LinkedHashMap<Integer, String>();
	private LinkedHashMap<Integer, String> student_ids = new LinkedHashMap<Integer, String>();
	private List<String> list_moduleCodes = new LinkedList<String>();
	private List<String> list_moduleTitles = new LinkedList<String>();

	protected void pushStudentData(int id, String studentName) throws SQLException {
		conn = (Connection) DriverManager.getConnection(DB_URL, USER, PASS);
		stmt = conn.createStatement();

		String insertSql = "INSERT INTO STUDENT(ID, StudentName) VALUES ('" + id + "', + '" + studentName + "')";
		System.out.println("Inserting into student.. [" + id + "]" + "[" + studentName + "]");
		stmt.executeUpdate(insertSql);
	}

	protected HashMap<String, String> pushRegisteredStudentsData(HashMap<String, String> moduleCode_moduleTitle) throws IOException, SQLException {
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
		return moduleCode_moduleTitle;
	}
	
	// populate students to module codes
		private void populateModules(HashMap<String, String> moduleCode_moduleTitle) throws SQLException {
			conn = (Connection) DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.createStatement();
			System.out.println(temporary_studentList);

			for (int i = 0; i < temporary_studentList.size(); i++) {
				i--;
				Integer key = (Integer) temporary_studentList.keySet().toArray()[new Random().nextInt(temporary_studentList.keySet().toArray().length)];
				storeStudentID(key, temporary_studentList.get(key));
				temporary_studentList.keySet().remove(key);
				getModule(key, moduleCode_moduleTitle);
			}
		}

		private void getModule(int student, HashMap<String, String> moduleCode_moduleTitle) throws SQLException {
			int counter = 0;
			while (counter < 4) {
				String key = (String) moduleCode_moduleTitle.keySet().toArray()[new Random().nextInt(moduleCode_moduleTitle.keySet().toArray().length)];
				fetchModuleCode(key);
				fetchModuleTitle(moduleCode_moduleTitle.get(key));
				String insertSql = "INSERT INTO REGISTRATION(StudentID, ModuleCode, Title) VALUES ('" + student + "', + '"
						+ key + "', + '" + moduleCode_moduleTitle.get(key) + "')";
				stmt2 = conn.createStatement();
				stmt2.executeUpdate(insertSql);
				counter++;
			}
		}

	private String fetchModuleCode(String moduleCode) {
		if (!list_moduleCodes.contains(moduleCode)) {
			list_moduleCodes.add(moduleCode);
		}
		return moduleCode;
	}
	
	protected String fetchModuleTitle(String moduleTitle) {
		if (!list_moduleTitles.contains(moduleTitle)) {
			list_moduleTitles.add(moduleTitle);
		}
		return moduleTitle;
	}
	
	protected List<String> getAllModuleCodes() {
		return this.list_moduleCodes;
	}
	
	protected List<String> getAllModuleTitles() {
		return this.list_moduleTitles;
	}
	
	protected void storeStudentID(Integer studentID, String name) {
		student_ids.put(studentID, name);
	}
	
	/*protected String getStudentName(String name) {
		return student_ids.get(studentID);
	}*/

}