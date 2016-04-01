package Main;

public class Building {

	private int buildingNumber;
	private int sessionID;
	private int roomNumber;
	
	public Building(int buildingNumber, int roomNumber, int sessionID) {
		this.buildingNumber = buildingNumber;
		this.sessionID = sessionID;
	}
	
	public int getBuildingNumber() {
		return this.buildingNumber;
	}
	
	public int getSessionID() {
		return sessionID;
	}
	
	public int getRoomNumber() {
		return roomNumber;
	}
}
