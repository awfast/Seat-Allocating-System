import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Exam {

	private final String USER = "root";
	private final String PASS = "";
	protected Connection conn = null;
	private Statement stmt = null;
	private final String DB_URL = "jdbc:mysql://localhost:3306/test";
	private ResultSet rs;
	private String examDuration;
	
	protected void pushExamData() {
		try {
			conn = (Connection) DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.createStatement();
			String userID = "SELECT * FROM REGISTRATION";
			rs = stmt.executeQuery(userID);
			while (rs.next()) {
				String id = rs.getString("ModuleCode");
				generateDuration();
				String insertSql = "INSERT INTO EXAM(ModuleCode, Duration) VALUES ('" + id + "', + '"
						+ this.examDuration + "')";
				System.out.println("Inserting into EXAM.." + "[" + id + "]" + "[" + this.examDuration + "]");
				stmt = conn.createStatement();
				stmt.executeUpdate(insertSql);
			}
		} catch (SQLException e) {
			System.out.println(e);
		}
	}

	//A* algorithm to be implemented in here in stead of a random assignment 
	private void generateDuration() {
		String duration30 = "30";
		String duration60 = "60";
		String duration90 = "90";
		String duration120 = "120";
		String duration150 = "150";

		ArrayList<String> examDurations = new ArrayList<String>();
		examDurations.add(duration150);
		examDurations.add(duration120);
		examDurations.add(duration90);
		examDurations.add(duration60);
		examDurations.add(duration30);

		if (!examDurations.isEmpty()) {
			int ran = (int) (0 + Math.random() * examDurations.size());
			this.examDuration = examDurations.get(ran).toString();
		}
	}
	// end of exam table
}
