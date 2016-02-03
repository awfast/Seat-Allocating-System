import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Students {
	

	private Exam examTable = new Exam();
	protected Connection conn = null;
	private Statement stmt = null;
	private Statement stmt2;
	private ArrayList registeredStudents;
	private int studentID;
	private String studentName = null;
	private final String USER = "root";
	private final String PASS = "";
	private final String DB_URL = "jdbc:mysql://localhost:3306/test";
	private ResultSet rs;
	protected String moduleCode = null;
	
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

	protected void pushRegisteredStudentsData(ArrayList registeredStudents) {
		try {
			conn = (Connection) DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.createStatement();
			this.registeredStudents = registeredStudents;
			String userID = "SELECT * FROM STUDENT";
			rs = stmt.executeQuery(userID);
			while (rs.next()) {
				int id = rs.getInt("ID");
				this.studentID = Integer.valueOf(id);
				getModuleCode();
				String insertSql = "INSERT INTO REGISTRATION(StudentID, ModuleCode) VALUES ('" + studentID + "', + '"
						+ this.moduleCode + "')";
				System.out.println("Inserting into REGISTRATION.. [" + studentID + "]" + "[" + this.moduleCode + "]");
				stmt2 = conn.createStatement();
				stmt2.executeUpdate(insertSql);
			}
		} catch (SQLException e) {
			System.out.println(e);
		}
	}

	private void getModuleCode() {
		if (!registeredStudents.isEmpty()) {
			int ran = (int) (0 + Math.random() * registeredStudents.size());
			moduleCode = registeredStudents.get(ran).toString();
		}
	}
}
