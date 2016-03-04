package Main;
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

	private Statement stmt = null;
	private Statement stmt2 = null;
	protected HashMap<String, String> moduleCode_moduleTitle;
	private int size;
	private int studentID;
	private String studentName;
	private final String USER = "root";
	private final String PASS = "";
	private final String DB_URL = "jdbc:mysql://localhost:3306/test";
	protected Connection conn;
	private ResultSet rs;
	protected String moduleCode = null;
	protected String moduleTitle = null;
	private LinkedHashMap<Integer, String> student_ids = new LinkedHashMap<Integer, String>();
	private List<String> list_moduleCodes = new LinkedList<String>();
	private List<String> list_moduleTitles = new LinkedList<String>();

	protected void getConnection() throws SQLException {
		this.conn = (Connection) DriverManager.getConnection(DB_URL, USER, PASS);
	}

	protected void pushStudentData(int id, String studentName) throws SQLException {
		stmt = conn.createStatement();

		String insertSql = "INSERT INTO STUDENT(ID, StudentName) VALUES ('" + id + "', + '" + studentName + "')";
		System.out.println("Inserting into student.. [" + id + "]" + "[" + studentName + "]");
		stmt.executeUpdate(insertSql);
	}

	protected void pushModuleCodes(String moduleCode, String moduleTitle, Integer duration) throws SQLException {
		fetchModuleCode(moduleCode);
		fetchModuleTitle(moduleTitle);
		String insertExam = "INSERT INTO Exam(ModuleCode, Duration) VALUES ('" + moduleCode + "', + '" + duration
				+ "')";
		stmt2 = conn.createStatement();
		stmt2.executeUpdate(insertExam);

		char[] chars = moduleCode.toCharArray();
		stmt = conn.createStatement();

		ResultSet rs2;
		for (char c : chars) {
			if (!Character.isLetter(c)) {
				String letter = Character.toString(c);
				if (letter.equals("1")) {
					System.out.println("Compulsory");
					String upToNCharacters = moduleCode.substring(0, Math.min(moduleCode.length(), 4));
					String query = "SELECT * FROM COHORTS WHERE Cohort='" + upToNCharacters + "'";
					rs = stmt.executeQuery(query);
					while (rs.next()) {
						this.size = rs.getInt(2);
					}
					String query2 = "SELECT * FROM STUDENT";
					rs2 = stmt.executeQuery(query2);
					int counter = 0;
					while (rs2.next()) {
						if (counter < size) {
							int student = rs2.getInt(1);
							String insertSql = "INSERT INTO RegisteredStudents(ID, ModuleCode, ModuleTitle) VALUES ('"
									+ student + "', + '" + moduleCode + "', + '" + moduleTitle + "')";
							stmt2 = conn.createStatement();
							stmt2.executeUpdate(insertSql);
							counter++;
						}
					}
				} else if (letter.equals("2")) {
					return;
					// System.out.println("Optional");
				} else if (letter.equals("3")) {
					return;
					// System.out.println("Optional");
					// System.out.println("get size of the cohort and module and
					// arbitrary assign students to it");
				} else if (letter.equals("6")) {
					return;
					// System.out.println("Optional");
				} else if (letter.equals("8")) {
					return;
					// System.out.println("Optional");
				} else if (letter.equals("9")) {
					return;
					// System.out.println("Optional");
				} else {
					return;
				}
			}
		}
	}

	protected void populateCohorts(String[] cohorts) throws SQLException {
		for (int i = 0; i < cohorts.length; i++) {
			String insertSql = "INSERT INTO Cohorts(Cohort, Size) VALUES ('" + cohorts[i] + "', + '" + randInt(25, 250)
					+ "')";
			stmt = conn.createStatement();
			stmt.executeUpdate(insertSql);
		}
	}

	public static int randInt(int min, int max) {
		Random rand = new Random();
		int randomNum = rand.nextInt((max - min) + 1) + min;

		return randomNum;
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
		if (!student_ids.containsKey(studentID)) {
			student_ids.put(studentID, name);
		}
	}
}