import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.TreeSet;

public class Location {

	private final String USER = "root";
	private final String PASS = "";
	protected Connection conn = null;
	private Statement stmt = null;
	private final String DB_URL = "jdbc:mysql://localhost:3306/test";
	private int roomNumber = 0;
	private int buildingNumber = 0;
	private int numberOfSeats = 0;
	private int numberOfAccessibleSeats = 0;
	protected int studentsNumber = 0;
	private ResultSet rs = null;
	private int optimalSeats;
	private TreeSet<Integer> ts = new TreeSet<Integer>();
	private ArrayList<Integer> seats = new ArrayList<Integer>();
	private LinkedHashMap<Integer, Integer> map1 = new LinkedHashMap<Integer, Integer>();
	private LinkedHashMap<Integer, Integer> map2 = new LinkedHashMap<Integer, Integer>();
	
	
	protected void storeBuildingsAndRoomsAvailable(int buildingNumber, int roomNumber) {
		this.buildingNumber = buildingNumber;
		this.roomNumber = roomNumber;
		map1.put(buildingNumber, roomNumber);
	}

	protected void storeSeatsAvailable(int numberOfSeats, int numberOfAccessibleSeats) {
		this.numberOfSeats = numberOfSeats;
		this.numberOfAccessibleSeats = numberOfAccessibleSeats;
		map2.put(numberOfSeats, numberOfAccessibleSeats);

		try {
			pushLocationData();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	protected void processData() throws SQLException {		
		for (Integer key : map2.keySet()) {
			seats.add(key);
		}
		
		for(int i: seats) {
			ts.add(i);
		}
		
		studentsNumber = getNumberOfStudents();
		System.out.println("For "+studentsNumber + " students, "+ "building number "+getBuildingNumber(findValue(ts.ceiling(studentsNumber))) + ", room number " + getRoomNumber(optimalSeats, 2) + ".");
	
	}

	protected void pushLocationData() throws SQLException {
		conn = (Connection) DriverManager.getConnection(DB_URL, USER, PASS);
		stmt = conn.createStatement();
		String insertSql = "INSERT INTO LOCATION(BuildingNumber, RoomNumber, SeatNumber, AccessibleSeatsNumber) VALUES ('"
				+ this.buildingNumber + "', + '" + this.roomNumber + "', + '" + this.numberOfSeats + "', + '"
				+ this.numberOfAccessibleSeats + "')";
		System.out.println("Inserting into location.. [" + this.buildingNumber + "]" + "[" + this.roomNumber + "]" + "["
				+ this.numberOfSeats + "]" + "[" + this.numberOfAccessibleSeats + "]");
		stmt.executeUpdate(insertSql);
		
	}
	
	protected int findValue(int optimal) throws SQLException {
		Connection mysqlConn = DriverManager.getConnection(DB_URL, USER, PASS);
		try {
			String query = "select count(*) from location";
			Statement st = mysqlConn.prepareStatement(query);
			ResultSet rs = st.executeQuery(query);
			while (rs.next()) {
				studentsNumber = rs.getInt(optimal);
			}
		} catch (Exception ex) {
			
		}
		optimalSeats = optimal;
		
		return optimalSeats;
	}
	
	protected int getBuildingNumber(int value) throws SQLException {
		int buildingNumber = 0;
		Connection mysqlConn = DriverManager.getConnection(DB_URL, USER, PASS);

		try {
			String query ="SELECT * FROM LOCATION WHERE SeatNumber ="+ value;
			Statement st = mysqlConn.prepareStatement(query);
			ResultSet rs = st.executeQuery(query);
			while (rs.next()) {
				buildingNumber = rs.getInt(1);
			}
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		return buildingNumber;
	}
	
	protected int getRoomNumber(int value, int room) throws SQLException {
		Connection mysqlConn = DriverManager.getConnection(DB_URL, USER, PASS);
		try {
			String query ="SELECT * FROM LOCATION WHERE SeatNumber ="+ value;
			Statement st = mysqlConn.prepareStatement(query);
			ResultSet rs = st.executeQuery(query);
			while (rs.next()) {
				room = rs.getInt(2);
			}
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		return room;
	}

	protected int getNumberOfStudents() throws SQLException {
		Connection mysqlConn = DriverManager.getConnection(DB_URL, USER, PASS);
		try {
			String query = "select count(*) from student";
			Statement st = mysqlConn.prepareStatement(query);
			ResultSet rs = st.executeQuery(query);
			while (rs.next()) {
				studentsNumber = rs.getInt("count(*)");
			}
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		return studentsNumber;
	}
}
