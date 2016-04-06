package View;

import javafx.beans.property.SimpleStringProperty;

public class Schedule {

	private final SimpleStringProperty studentID;
	private final SimpleStringProperty moduleCode;
	private final SimpleStringProperty moduleTitle;
	private final SimpleStringProperty day;
	private final SimpleStringProperty date;
	private final SimpleStringProperty sessionName;
	private final SimpleStringProperty location;

	public Schedule(int studentID, String moduleCode, String moduleTitle, String day, String date, String sessionName,
			String location) {
		String student = Integer.toString(studentID);
		this.studentID = new SimpleStringProperty(student);
		this.moduleCode = new SimpleStringProperty(moduleCode);
		this.moduleTitle = new SimpleStringProperty(moduleTitle);
		this.day = new SimpleStringProperty(day);
		this.date = new SimpleStringProperty(date);
		this.sessionName = new SimpleStringProperty(sessionName);
		this.location = new SimpleStringProperty(location);

	}

	public String getStudentID() {
		return studentID.get();
	}

	public String getModuleCode() {
		return moduleCode.get();
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

	public String getSessionName() {
		return sessionName.get();
	}

	public String getLocation() {
		return location.get();
	}
}
