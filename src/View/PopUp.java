package View;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import jfx.messagebox.MessageBox;

public class PopUp {

	
	public void notFilledIn() {
		Stage s = new Stage();
		MessageBox.show(s,
				"\nPlease make sure that no fields are left blank.",
				"Warning", MessageBox.ICON_WARNING | MessageBox.OK);
	}
	
	public void invalidID() {
		Stage s = new Stage();
		MessageBox.show(s,
				"\n\nThe Student ID you have entered is of invalid format \n\n                        Please try again.",
				"Warning!", MessageBox.ICON_ERROR | MessageBox.OK);
	}
	
	public void deleteIndexNotSelected() {				
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("Warning");
		alert.setHeaderText("Schedule not selected.");
		alert.setContentText("Please ensure that you have selected a schedule from the table before you press 'DELETE'!");
		alert.showAndWait();
	}
	
	public boolean extraCheck() {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmation");
		alert.setHeaderText("Confirm deletion.");
		alert.setContentText("Are you sure you want to delete this schedule?");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
		   return true;
		}
		return false;
	}
	
	public boolean confirmExamPeriod(String from, String to) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmation");
		alert.setHeaderText("Please confirm the exam period selected");
		alert.setContentText("                                     Is this correct?  \n" + "                             " +from + "-" + to);

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
		   return true;
		}
		return false;
	}
	
	public void examPeriodNotSelected() {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("Warning");
		alert.setHeaderText("Exam Period not selected.");
		alert.setContentText("Please specify the exam period.");
		alert.showAndWait();
	}
	
	public boolean fileNotImported() {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("File not imported.");
		alert.setHeaderText("An important file has not been imported.");
		alert.setContentText("Please make sure you have imported all the files needed.");
		alert.showAndWait();
		return false;
	}
	
	public boolean examPeriodFormat() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Exam period Information.");
		alert.setHeaderText("The exam period format you have specified is invalid.");
		alert.setContentText("Please specify the exam period in the following format, e.g. dd/mm/yy");
		alert.showAndWait();
		return false;
	}
	
	public boolean studentsImportedSuccessfully() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Students data successfully imported");
		alert.setHeaderText("All students have been successfully imported.");
		alert.showAndWait();
		return false;
	}
	
	public boolean registrationImportedSuccessfully() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Registration data successfully imported");
		alert.setHeaderText("All students have been successfully registered to a module.");
		alert.showAndWait();
		return false;
	}
	
	public boolean locationsImportedSuccessfully() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Location data successfully imported");
		alert.setHeaderText("All location data has been successfully imported");
		alert.showAndWait();
		return false;
	}
	
	public boolean wrongFileFormat() {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Alert");
		alert.setHeaderText("Wrong file format or file empty.");
		alert.setContentText("Please make sure that the file is not empty or is of correct format.");
		alert.showAndWait();
		return false;
	}
}
