import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Location {

	private final String USER = "root";
	private final String PASS = "";
	protected Connection conn = null;
	private Statement stmt = null;
	private final String DB_URL = "jdbc:mysql://localhost:3306/test";
	private int roomNumber;
	private int buildingNumber;
	private int numberOfSeats;
	private int numberOfAccessibleSeats;
	
	protected void getLocationInfo(int buildingNumber, int roomNumber, int numberOfSeats, int numberOfAccessibleSeats)
			throws SQLException {
		this.buildingNumber = buildingNumber;
		this.roomNumber = roomNumber;
		this.numberOfSeats = numberOfSeats;
		this.numberOfAccessibleSeats = numberOfAccessibleSeats;
		pushLocationData();
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
	
}
