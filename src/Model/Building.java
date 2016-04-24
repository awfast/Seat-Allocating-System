package Model;

public class Building {

	private int buildingNumber;
	private int sessionID;
	private int roomNumber;
	private String moduleCode;
	
	public Building(int buildingNumber) {
		this.buildingNumber = buildingNumber;
	}
	
	public Building(int buildingNumber, int roomNumber, int sessionID, String moduleCode) {
		this.buildingNumber = buildingNumber;
		this.sessionID = sessionID;
		this.moduleCode = moduleCode;
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
	
	public String getModuleCode() {
		return moduleCode;
	}
}
