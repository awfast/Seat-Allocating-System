import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

public class Students {

	protected Connection conn = null;
	private Statement stmt = null;
	private Statement stmt2;
	private HashMap<String, String> registeredStudents;
	private int studentID;
	private String studentName = null;
	private final String USER = "root";
	private final String PASS = "";
	private final String DB_URL = "jdbc:mysql://localhost:3306/test";
	private ResultSet rs;
	protected String moduleCode = null;
	protected String moduleTitle = null;
	private Exam examTable = new Exam();
	private List<String> list_moduleCodes = new LinkedList<String>();
	private List<String> list_moduleTitles = new LinkedList<String>();
	protected List<String> exam_list_with_codes = new LinkedList<String>();
	protected List<String> exam_list_with_titles = new LinkedList<String>();

	protected void populateStudentsTable() throws SQLException, IOException {
		DataReader reader = new DataReader();
		reader.readStudentData(studentID, studentName);
	}

	protected void finalizeExamGeneration() {
		examTable.pushExamData();
	}

	protected void getStudentInfo(int id, String name) throws SQLException {
		this.studentID = id;
		this.studentName = name;
		pushStudentData();
	}

	protected void pushStudentData() throws SQLException {
		conn = (Connection) DriverManager.getConnection(DB_URL, USER, PASS);
		stmt = conn.createStatement();
		String insertSql = "INSERT INTO STUDENT(ID, studentName) VALUES ('" + this.studentID + "', + '"
				+ this.studentName + "')";
		System.out.println("Inserting into student.. [" + studentID + "]" + "=" + studentName);
		stmt.executeUpdate(insertSql);
	}

	protected void pushRegisteredStudentsData(HashMap<String, String> registeredStudents) {
		try {
			conn = (Connection) DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.createStatement();
			this.registeredStudents = registeredStudents;
			String userID = "SELECT * FROM STUDENT";
			rs = stmt.executeQuery(userID);
			fetchModuleCode(moduleCode);
			fetchModuleTitle(moduleTitle);
			while (rs.next()) {
				int id = rs.getInt("ID");
				this.studentID = Integer.valueOf(id);
				extractModuleCode(moduleCode);
				extractModuleTitle(moduleTitle);
				String insertSql = "INSERT INTO REGISTRATION(StudentID, ModuleCode, Title) VALUES ('" + studentID + "', + '"
						+ shuffleModules(moduleTitle) + "', + '"+ this.moduleTitle + "')";
				System.out.println("Inserting into REGISTRATION.. [" + studentID + "]" + "[" + this.moduleCode + "]");
				stmt2 = conn.createStatement();
				stmt2.executeUpdate(insertSql);
			}
			extractInfoForExam(moduleCode, moduleTitle);
		} catch (SQLException e) {
			System.out.println(e);
		}
	}

	protected String fetchModuleCode(String moduleCode) {
		for (String key : registeredStudents.keySet()) {
			moduleCode = key;
			list_moduleCodes.add(moduleCode);
			exam_list_with_codes.add(moduleCode);
		}
		System.out.println("ModuleCode" + moduleCode);
		return moduleCode;
	}

	protected String extractModuleCode(String moduleCode) {
		//moduleCode = list_moduleCodes.remove(0);
		this.moduleCode = moduleCode;
		return moduleCode;
	}
	
	private String shuffleModules(String moduleTitle) {
		double rand = Math.random();
		for(int i=0; i<list_moduleCodes.size(); i++) {
			moduleTitle = list_moduleCodes.get((int)rand);
			System.out.println(moduleTitle);
		}
		return moduleTitle;
	}

	protected String fetchModuleTitle(String moduleTitle) {
		for (String key : registeredStudents.keySet()) {
			moduleTitle = registeredStudents.get(key);
			list_moduleTitles.add(moduleTitle);
			exam_list_with_titles.add(moduleTitle);
		}
		System.out.println(list_moduleTitles.size());

		return moduleTitle;
	}

	protected String extractModuleTitle(String moduleTitle) {
		//moduleTitle = list_moduleTitles.remove(0);
		this.moduleTitle = moduleTitle;
		System.out.println(moduleTitle);
		return moduleTitle;
	}

	protected String extractInfoForExam(String moduleCode, String moduleTitle) {
		System.out.println("exam_list_with_codes " + exam_list_with_codes.size());
		System.out.println("exam_list_with_titles " + exam_list_with_titles.size());
		moduleCode = exam_list_with_codes.remove(0);
		moduleTitle = exam_list_with_titles.remove(0);
		return moduleCode + moduleTitle;
	}
	
	//populate students to module codes
	private int populateModules(int studentID) {
		
		return studentID;
	}
}
