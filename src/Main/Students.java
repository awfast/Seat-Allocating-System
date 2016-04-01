package Main;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Students {

	public Random rand = new Random();
	private Statement stmt = null;
	private Statement stmt2 = null;
	private int size;
	private final String USER = "root";
	private final String PASS = "";
	private final String DB_URL = "jdbc:mysql://localhost:3306/test";
	private ResultSet rs;
	private LinkedHashMap<Integer, String> student_ids = new LinkedHashMap<Integer, String>();
	private List<String> list_moduleCodes = new LinkedList<String>();
	private List<String> list_moduleTitles = new LinkedList<String>();
	private List<String> list_moduleCodes_test = new LinkedList<String>();
	private Map<Integer, Integer> alreadyRegisteredStudent = new HashMap<Integer, Integer>();
	protected HashMap<String, String> moduleCode_moduleTitle;
	protected Connection conn;
	protected String moduleCode = null;
	protected String moduleTitle = null;
	

	protected void getConnection() throws SQLException {
		this.conn = (Connection) DriverManager.getConnection(DB_URL, USER, PASS);
	}

	protected void pushStudentData(int id, String studentName) throws SQLException {
		stmt = conn.createStatement();

		String insertSql = "INSERT INTO STUDENT(ID, StudentName) VALUES ('" + id + "', + '" + studentName + "')";
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
							if(alreadyRegisteredStudent.containsKey(student)) {
								if(alreadyRegisteredStudent.get(student) <= 4) {
									int numberOfModulesAssigned = alreadyRegisteredStudent.get(student) + 1;
									alreadyRegisteredStudent.put(student, numberOfModulesAssigned);
									String insertSql = "INSERT INTO RegisteredStudents(ID, ModuleCode, ModuleTitle) VALUES ('"
											+ student + "', + '" + moduleCode + "', + '" + moduleTitle + "')";
									stmt2 = conn.createStatement();
									stmt2.executeUpdate(insertSql);
									counter++;
									if(!list_moduleCodes_test.contains(moduleCode)) {
										list_moduleCodes_test.add(moduleCode);								
									}
								}
							} else {
								alreadyRegisteredStudent.put(student, 1);
								String insertSql = "INSERT INTO RegisteredStudents(ID, ModuleCode, ModuleTitle) VALUES ('"
										+ student + "', + '" + moduleCode + "', + '" + moduleTitle + "')";
								stmt2 = conn.createStatement();
								stmt2.executeUpdate(insertSql);
								counter++;
								if(!list_moduleCodes_test.contains(moduleCode)) {
									list_moduleCodes_test.add(moduleCode);								
								}
							}
						}
					}
					break;
				} else if (letter.equals("2")) {
					String upToNCharacters = moduleCode.substring(0, Math.min(moduleCode.length(), 4));
					String query = "SELECT * FROM COHORTS WHERE Cohort='" + upToNCharacters + "'";
					rs = stmt.executeQuery(query);
					while (rs.next()) {
						this.size = rs.getInt(2);
					}
					String query2 = "SELECT * FROM STUDENT";
					rs2 = stmt.executeQuery(query2);
					int counter = 0;
					int subtractedStudents = randomizeOptinalModulesNumber();
					while (rs2.next()) {
						if (counter < size - (subtractedStudents*3)) {
							int student = rs2.getInt(1);
							if(alreadyRegisteredStudent.containsKey(student)) {
								if(alreadyRegisteredStudent.get(student) <= 4) {
									int numberOfModulesAssigned = alreadyRegisteredStudent.get(student) + 1;
									alreadyRegisteredStudent.put(student, numberOfModulesAssigned);
									String insertSql = "INSERT INTO RegisteredStudents(ID, ModuleCode, ModuleTitle) VALUES ('"
											+ student + "', + '" + moduleCode + "', + '" + moduleTitle + "')";
									stmt2 = conn.createStatement();
									stmt2.executeUpdate(insertSql);
									counter++;
									if(!list_moduleCodes_test.contains(moduleCode)) {
										list_moduleCodes_test.add(moduleCode);								
									}
								}
							} else {
								alreadyRegisteredStudent.put(student, 1);
								String insertSql = "INSERT INTO RegisteredStudents(ID, ModuleCode, ModuleTitle) VALUES ('"
										+ student + "', + '" + moduleCode + "', + '" + moduleTitle + "')";
								stmt2 = conn.createStatement();
								stmt2.executeUpdate(insertSql);
								counter++;
								if(!list_moduleCodes_test.contains(moduleCode)) {
									list_moduleCodes_test.add(moduleCode);								
								}
							}
						}
					}
					break;
				} else if (letter.equals("3")) {
					// System.out.println("Optional");
					String upToNCharacters = moduleCode.substring(0, Math.min(moduleCode.length(), 4));
					String query = "SELECT * FROM COHORTS WHERE Cohort='" + upToNCharacters + "'";
					rs = stmt.executeQuery(query);
					while (rs.next()) {
						this.size = rs.getInt(2);
					}
					String query2 = "SELECT * FROM STUDENT";
					rs2 = stmt.executeQuery(query2);
					int counter = 0;
					int subtractedStudents = randomizeOptinalModulesNumber();
					while (rs2.next()) {
						if (counter < size - (subtractedStudents*3)) {
							int student = rs2.getInt(1);
							if(alreadyRegisteredStudent.containsKey(student)) {
								if(alreadyRegisteredStudent.get(student) <= 4) {
									int numberOfModulesAssigned = alreadyRegisteredStudent.get(student) + 1;
									alreadyRegisteredStudent.put(student, numberOfModulesAssigned);
									String insertSql = "INSERT INTO RegisteredStudents(ID, ModuleCode, ModuleTitle) VALUES ('"
											+ student + "', + '" + moduleCode + "', + '" + moduleTitle + "')";
									stmt2 = conn.createStatement();
									stmt2.executeUpdate(insertSql);
									counter++;
									if(!list_moduleCodes_test.contains(moduleCode)) {
										list_moduleCodes_test.add(moduleCode);								
									}
								}
							} else {
								alreadyRegisteredStudent.put(student, 1);
								String insertSql = "INSERT INTO RegisteredStudents(ID, ModuleCode, ModuleTitle) VALUES ('"
										+ student + "', + '" + moduleCode + "', + '" + moduleTitle + "')";
								stmt2 = conn.createStatement();
								stmt2.executeUpdate(insertSql);
								counter++;
								if(!list_moduleCodes_test.contains(moduleCode)) {
									list_moduleCodes_test.add(moduleCode);								
								}
							}
						}
					}
					break;
				} else if (letter.equals("6")) {
					String upToNCharacters = moduleCode.substring(0, Math.min(moduleCode.length(), 4));
					String query = "SELECT * FROM COHORTS WHERE Cohort='" + upToNCharacters + "'";
					rs = stmt.executeQuery(query);
					while (rs.next()) {
						this.size = rs.getInt(2);
					}
					String query2 = "SELECT * FROM STUDENT";
					rs2 = stmt.executeQuery(query2);
					int counter = 0;
					int subtractedStudents = randomizeOptinalModulesNumber();
					while (rs2.next()) {
						if (counter < size - (subtractedStudents*3)) {
							int student = rs2.getInt(1);
							if(alreadyRegisteredStudent.containsKey(student)) {
								if(alreadyRegisteredStudent.get(student) <= 4) {
									int numberOfModulesAssigned = alreadyRegisteredStudent.get(student) + 1;
									alreadyRegisteredStudent.put(student, numberOfModulesAssigned);
									String insertSql = "INSERT INTO RegisteredStudents(ID, ModuleCode, ModuleTitle) VALUES ('"
											+ student + "', + '" + moduleCode + "', + '" + moduleTitle + "')";
									stmt2 = conn.createStatement();
									stmt2.executeUpdate(insertSql);
									counter++;
									if(!list_moduleCodes_test.contains(moduleCode)) {
										list_moduleCodes_test.add(moduleCode);								
									}
								}
							} else {
								alreadyRegisteredStudent.put(student, 1);
								String insertSql = "INSERT INTO RegisteredStudents(ID, ModuleCode, ModuleTitle) VALUES ('"
										+ student + "', + '" + moduleCode + "', + '" + moduleTitle + "')";
								stmt2 = conn.createStatement();
								stmt2.executeUpdate(insertSql);
								counter++;
								if(!list_moduleCodes_test.contains(moduleCode)) {
									list_moduleCodes_test.add(moduleCode);								
								}
							}
						}
					}
					break;
					// System.out.println("Optional");
				} else if (letter.equals("8")) {
					String upToNCharacters = moduleCode.substring(0, Math.min(moduleCode.length(), 4));
					String query = "SELECT * FROM COHORTS WHERE Cohort='" + upToNCharacters + "'";
					rs = stmt.executeQuery(query);
					while (rs.next()) {
						this.size = rs.getInt(2);
					}
					String query2 = "SELECT * FROM STUDENT";
					rs2 = stmt.executeQuery(query2);
					int counter = 0;
					int subtractedStudents = randomizeOptinalModulesNumber();
					while (rs2.next()) {
						if (counter < size - (subtractedStudents*3)) {
							int student = rs2.getInt(1);
							if(alreadyRegisteredStudent.containsKey(student)) {
								if(alreadyRegisteredStudent.get(student) <= 4) {
									int numberOfModulesAssigned = alreadyRegisteredStudent.get(student) + 1;
									alreadyRegisteredStudent.put(student, numberOfModulesAssigned);
									String insertSql = "INSERT INTO RegisteredStudents(ID, ModuleCode, ModuleTitle) VALUES ('"
											+ student + "', + '" + moduleCode + "', + '" + moduleTitle + "')";
									stmt2 = conn.createStatement();
									stmt2.executeUpdate(insertSql);
									counter++;
									if(!list_moduleCodes_test.contains(moduleCode)) {
										list_moduleCodes_test.add(moduleCode);								
									}
								}
							} else {
								alreadyRegisteredStudent.put(student, 1);
								String insertSql = "INSERT INTO RegisteredStudents(ID, ModuleCode, ModuleTitle) VALUES ('"
										+ student + "', + '" + moduleCode + "', + '" + moduleTitle + "')";
								stmt2 = conn.createStatement();
								stmt2.executeUpdate(insertSql);
								counter++;
								if(!list_moduleCodes_test.contains(moduleCode)) {
									list_moduleCodes_test.add(moduleCode);								
								}
							}
						}
					}
					break;
					// System.out.println("Optional");
				} else if (letter.equals("9")) {
					String upToNCharacters = moduleCode.substring(0, Math.min(moduleCode.length(), 4));
					String query = "SELECT * FROM COHORTS WHERE Cohort='" + upToNCharacters + "'";
					rs = stmt.executeQuery(query);
					while (rs.next()) {
						this.size = rs.getInt(2);
					}
					String query2 = "SELECT * FROM STUDENT";
					rs2 = stmt.executeQuery(query2);
					int counter = 0;
					int subtractedStudents = randomizeOptinalModulesNumber();
					while (rs2.next()) {
						if (counter < size - (subtractedStudents*3)) {
							int student = rs2.getInt(1);
							if(alreadyRegisteredStudent.containsKey(student)) {
								if(alreadyRegisteredStudent.get(student) <= 4) {
									int numberOfModulesAssigned = alreadyRegisteredStudent.get(student) + 1;
									alreadyRegisteredStudent.put(student, numberOfModulesAssigned);
									String insertSql = "INSERT INTO RegisteredStudents(ID, ModuleCode, ModuleTitle) VALUES ('"
											+ student + "', + '" + moduleCode + "', + '" + moduleTitle + "')";
									stmt2 = conn.createStatement();
									stmt2.executeUpdate(insertSql);
									counter++;
									if(!list_moduleCodes_test.contains(moduleCode)) {
										list_moduleCodes_test.add(moduleCode);								
									}
								}
							} else {
								alreadyRegisteredStudent.put(student, 1);
								String insertSql = "INSERT INTO RegisteredStudents(ID, ModuleCode, ModuleTitle) VALUES ('"
										+ student + "', + '" + moduleCode + "', + '" + moduleTitle + "')";
								stmt2 = conn.createStatement();
								stmt2.executeUpdate(insertSql);
								counter++;
								if(!list_moduleCodes_test.contains(moduleCode)) {
									list_moduleCodes_test.add(moduleCode);								
								}
							}
						}
					}
					break;
					// System.out.println("Optional");
				} else {
					String upToNCharacters = moduleCode.substring(0, Math.min(moduleCode.length(), 4));
					String query = "SELECT * FROM COHORTS WHERE Cohort='" + upToNCharacters + "'";
					rs = stmt.executeQuery(query);
					while (rs.next()) {
						this.size = rs.getInt(2);
					}
					String query2 = "SELECT * FROM STUDENT";
					rs2 = stmt.executeQuery(query2);
					int counter = 0;
					int subtractedStudents = randomizeOptinalModulesNumber();
					while (rs2.next()) {
						if (counter < size - (subtractedStudents*3)) {
							int student = rs2.getInt(1);
							if(alreadyRegisteredStudent.containsKey(student)) {
								if(alreadyRegisteredStudent.get(student) <= 4) {
									int numberOfModulesAssigned = alreadyRegisteredStudent.get(student) + 1;
									alreadyRegisteredStudent.put(student, numberOfModulesAssigned);
									String insertSql = "INSERT INTO RegisteredStudents(ID, ModuleCode, ModuleTitle) VALUES ('"
											+ student + "', + '" + moduleCode + "', + '" + moduleTitle + "')";
									stmt2 = conn.createStatement();
									stmt2.executeUpdate(insertSql);
									counter++;
									if(!list_moduleCodes_test.contains(moduleCode)) {
										list_moduleCodes_test.add(moduleCode);								
									}
								}
							} else {
								alreadyRegisteredStudent.put(student, 1);
								String insertSql = "INSERT INTO RegisteredStudents(ID, ModuleCode, ModuleTitle) VALUES ('"
										+ student + "', + '" + moduleCode + "', + '" + moduleTitle + "')";
								stmt2 = conn.createStatement();
								stmt2.executeUpdate(insertSql);
								counter++;
								if(!list_moduleCodes_test.contains(moduleCode)) {
									list_moduleCodes_test.add(moduleCode);								
								}
							}
						}
					}
					break;
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

	public int randInt(int min, int max) {
		int randomNum = rand.nextInt((max - min) + 1) + min;
		return randomNum;
	}
	
	public int randomizeOptinalModulesNumber() {
		int randomNum = (rand.nextInt((11 - 4) + 1) + 4);
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
		return this.list_moduleCodes_test;
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