package View;

import javafx.beans.property.SimpleStringProperty;

public class Schedule {

	private final SimpleStringProperty studentID;
	private final SimpleStringProperty moduleCode;
	private final SimpleStringProperty moduleTitle;
	private final SimpleStringProperty day;
	private final SimpleStringProperty date;
	private final SimpleStringProperty session;
	private final SimpleStringProperty location;

	public Schedule(int studentID, String moduleCode, String moduleTitle, String day, String date, int session,
			String location) {
		String student = Integer.toString(studentID);
		String sessionName = Integer.toString(session);
		this.studentID = new SimpleStringProperty(student);
		this.moduleCode = new SimpleStringProperty(moduleCode);
		this.moduleTitle = new SimpleStringProperty(moduleTitle);
		this.day = new SimpleStringProperty(day);
		this.date = new SimpleStringProperty(date);
		this.session = new SimpleStringProperty(sessionName);
		this.location = new SimpleStringProperty(location);

	}

	public String getStudentID() {
		return studentID.get();
	}

	public void getModuleCode(String fName) {
		moduleCode.set(fName);
	}

	public String getModuleTitle() {
		return moduleTitle.get();
	}

	public String getDay() {
		return day.get();
	}

	public String getDate() {
		return date.get();
	}

	public String getSession() {
		return session.get();
	}

	public String getLocation() {
		return location.get();
	}
}
