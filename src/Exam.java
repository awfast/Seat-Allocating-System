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
	private String moduleCode = null;
	private String moduleTitle = null;
	private String day = "Thursday";
	private String date = "14/01/2016";
	private String session = "am";
	private String location = "Building 46. Rooms 3001, 2003, 2005";
	
	public Exam() {
		
	}
	
	public Exam(String moduleCode,String title,String day,String date,String session,String duration,String location) {
		this.moduleCode = moduleCode;
		this.moduleTitle = title;
		this.day = day;
		this.date = date;
		this.session = session;
		this.examDuration = duration;
		this.location = location;
	}
	
	protected void pushExamData() {
		try {
			conn = (Connection) DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.createStatement();
			String userID = "SELECT * FROM REGISTRATION";
			rs = stmt.executeQuery(userID);

			while (rs.next()) {
				generateDuration();
				String moduleCode = rs.getString("ModuleCode");
				String moduleTitle = rs.getString("Title");
				
				String insertSql = "INSERT INTO EXAM(Code, Title, Day, Date, Session, Duration, Location) VALUES ('"
						+ moduleCode + "', + '" + moduleTitle + "', + '" + day + "', +'" + date + "', + '" + session + "', + '"
						+ this.examDuration + "', + '" + location + "')";

				stmt = conn.createStatement();
				stmt.executeUpdate(insertSql);
			}
		} catch (SQLException e) {
			System.out.println(e);
		}
	}

	// A* algorithm to be implemented in here in stead of a random assignment
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

	private void generateExam() {

	}
	
	/*Map<Exam, String> leastCost = new HashMap<Exam, String>();
	Map<Exam, String> leastCostThroughStates = new HashMap<Exam, String>();
	
	private Node aStar(Node<Exam, String> start, Node goal) {
	
		long startTime = new Date().getTime();
		Set<Exam> visited = new HashSet<Exam>();// stores the visited nodes
		PriorityQueue open = new PriorityQueue();
		leastCostThroughStates.put(start, 0);
		leastCost.put(start, leastCostThroughStates.get(start));// + manhattanDistance(start.get(), goal.get()));
		open.add(start);
		Node current = null;

		while (!open.isEmpty()) {
			current = open.poll();
			if (current.get().equals(goal.get())) {// return the current node if
													// it's the same as the goal
				System.out.println("A* -> " + visited.size() + " nodes!");
				long EndTime = new Date().getTime();
				long difference = EndTime - startTime;
				System.out.println("A* runtime: " + difference + " " + TimeUnit.MILLISECONDS
						+ "\n" + "---------------");
				System.out.println(((Object) current).get());
				return current;

			} // otherwise, add the new Node in the visited HashSet, Generate
				// the new Nodes and do nothing with it if it has already been
				// visited.

			visited.add(current.get());
			current = generateNodes(current);
			for (Node<Exam> child : current.getNodes()) {
				if (visited.contains(child.get())) {
					continue;
				}
				// take the newly generated nodes and compare if their distance
				// is less than the last least cost distance
				int temporary = leastCost.get(current) + manhattanDistance(current.get(), child.get());
				if (!open.contains(child) || temporary < leastCost.get(child)) {
					leastCost.put(child, temporary);
					leastCostThroughStates.put(child,
							leastCost.get(child) + manhattanDistance(child.get(), goal.get()));
					if (!open.contains(child)) {
						open.add(child);
					}
				}
			}

		}
		return null;
	}

	// compares the path difference between 2 tiles

	private static int manhattanDistance(Exam root, Exam goal) {

		int startingDistance = 0;

		for (int i = 0; i < root.tile.length; i++) {

			startingDistance += goal.tile[i].manhattan(goal.tile[i]);

		}

		return startingDistance;

	}*/
}
