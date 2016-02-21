import java.io.IOException;
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
	protected int roomNumber = 0;
	private int buildingNumber = 0;
	private int numberOfSeats = 0;
	private int numberOfAccessibleSeats = 0;
	protected int studentsNumber = 0;
	private ResultSet rs = null;
	private int optimalSeats;
	protected TreeSet<Integer> ts = new TreeSet<Integer>();
	private ArrayList<Integer> seats = new ArrayList<Integer>();
	private LinkedHashMap<Integer, Integer> map1 = new LinkedHashMap<Integer, Integer>();
	private LinkedHashMap<Integer, Integer> map2 = new LinkedHashMap<Integer, Integer>();
	
	protected void storeLocationInformation(int buildings, int rooms, int numberOfSeats, int numberOfAccessibleSeats) throws IOException, SQLException {
		this.buildingNumber = buildings;
		this.roomNumber = rooms;
		this.numberOfSeats = numberOfSeats;
		this.numberOfAccessibleSeats = numberOfAccessibleSeats;
		
		map1.put(buildingNumber, roomNumber);
		map2.put(numberOfSeats, numberOfAccessibleSeats);
		pushLocationData(buildings, rooms, numberOfSeats, numberOfAccessibleSeats);
	}
	

	protected void processData() throws SQLException {		
		for (Integer key : map2.keySet()) {
			seats.add(key);
		}
		
		for(int i: seats) {
			ts.add(i);
		}
		
		//System.out.println("For "+studentsNumber + " students, "+ "building number "+getBuildingNumber(findValue(ts.ceiling(studentsNumber))) + ", room number " + getRoomNumber(optimalSeats, 2) + ".");
	}

	protected void pushLocationData(int buildings, int rooms, int numberOfSeats, int numberOfAccessibleSeats) throws SQLException {
		conn = (Connection) DriverManager.getConnection(DB_URL, USER, PASS);
		stmt = conn.createStatement();
		String insertSql = "INSERT INTO LOCATION(BuildingNumber, RoomNumber, SeatNumber, AccessibleSeatsNumber) VALUES ('"
				+ buildings + "', + '" + rooms + "', + '" + numberOfSeats + "', + '"
				+ numberOfAccessibleSeats + "')";
		System.out.println("Inserting into location.. [" + buildings + "]" + "[" + rooms + "]" + "["
				+ numberOfSeats + "]" + "[" + numberOfAccessibleSeats + "]");
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
	
	protected int getRoomNumber(int value) throws SQLException {
		int room = 0;
		Connection mysqlConn = DriverManager.getConnection(DB_URL, USER, PASS);
		try {
			String query ="SELECT * FROM LOCATION WHERE BuildingNumber ="+ value;
			Statement st = mysqlConn.prepareStatement(query);
			ResultSet rs = st.executeQuery(query);
			while (rs.next()) {
				room = rs.getInt(2);
				this.roomNumber = room;
			}
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		return room;
	}

	protected int getNumberOfStudents(int numberOfStudents) throws SQLException {
		return this.studentsNumber = numberOfStudents;
	}
}
