package Controller;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.TreeSet;

/*
 * @author Damyan Rusinov
 * @This helper class identifies the locations available
 */
public class Location {

	protected Connection conn = null;
	private Statement stmt = null;
	protected int roomNumber = 0;
	private int buildingNumber = 0;
	@SuppressWarnings("unused")
	private int numberOfSeats = 0;
	@SuppressWarnings("unused")
	private int numberOfAccessibleSeats = 0;
	protected int studentsNumber = 0;
	@SuppressWarnings("unused")
	private ResultSet rs = null;
	private int optimalSeats;
	protected TreeSet<Integer> ts = new TreeSet<Integer>();
	private ArrayList<Integer> seats = new ArrayList<Integer>();
	private LinkedHashMap<Integer, Integer> map1 = new LinkedHashMap<Integer, Integer>();
	private LinkedHashMap<Integer, Integer> map2 = new LinkedHashMap<Integer, Integer>();
	
	protected void storeLocationInformation(Connection conn, int buildings, int rooms, int numberOfSeats, int numberOfAccessibleSeats) throws IOException, SQLException {
		this.buildingNumber = buildings;
		this.roomNumber = rooms;
		this.numberOfSeats = numberOfSeats;
		this.numberOfAccessibleSeats = numberOfAccessibleSeats;
		this.conn = conn;
		
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
	}

	protected void pushLocationData(int buildings, int rooms, int numberOfSeats, int numberOfAccessibleSeats) throws SQLException {
		stmt = conn.createStatement();
		String insertSql = "INSERT INTO LOCATION(BuildingNumber, RoomNumber, SeatNumber, AccessibleSeatsNumber) VALUES ('"
				+ buildings + "', + '" + rooms + "', + '" + numberOfSeats + "', + '"
				+ numberOfAccessibleSeats + "')";
		stmt.executeUpdate(insertSql);
		
	}
	
	protected int findValue(int optimal) throws SQLException {
		try {
			String query = "select count(*) from location";
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
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
		try {
			String query ="SELECT * FROM LOCATION WHERE SeatNumber ="+ value;
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				buildingNumber = rs.getInt(1);
			}
		} catch (Exception ex) {
		}
		return buildingNumber;
	}
	
	protected int getRoomNumber(int value) throws SQLException {
		int room = 0;
		try {
			String query ="SELECT * FROM LOCATION WHERE BuildingNumber ="+ value;
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				room = rs.getInt(2);
				this.roomNumber = room;
			}
		} catch (Exception ex) {
		}
		return room;
	}

	protected int getNumberOfStudents(int numberOfStudents) throws SQLException {
		return this.studentsNumber = numberOfStudents;
	}
}
