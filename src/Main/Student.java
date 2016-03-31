package Main;

public class Student {

	private int studentID;
	private int sessionID;
	private String date;
	private String moduleCode;
	private boolean available;
	
	public Student(int studentID, String moduleCode, boolean available, int sessionID, String date) {
		this.studentID = studentID;
		this.available = available;
		this.sessionID = sessionID;
		this.date = date;
		this.moduleCode = moduleCode;
	}
	
	public int getStudentID() {
		return this.studentID;
	}
	
	public boolean isStudentAvailable() {
		return this.available;
	}
	
	public String getModuleCode() {
		return this.moduleCode;
	}
	
	public int getSessionID() {
		return this.sessionID;
	}
	
	public String getDate() {
		return this.date;
	}
	
	public void setStudentDate(String date) {
		this.date = date;
	}
	
	public void setModuleCode(String moduleCode) {
		this.moduleCode = moduleCode;
	}
	
	public void setStudentSessionID(int sessionID) {
		this.sessionID = sessionID;
	}
	
	public void setStudetAvailability(boolean available) {
		this.available = available;
	}
}
