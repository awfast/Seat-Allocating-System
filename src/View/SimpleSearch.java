package View;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

public class SimpleSearch {
	 ObservableList<View.Schedule> entries = FXCollections.observableArrayList();    
	    
	 public void handleSearchByKey(TableView tableView, String oldVal, String newVal) {
	        // If the number of characters in the text box is less than last time
	        // it must be because the user pressed delete
	        if ( oldVal != null && (newVal.length() < oldVal.length()) ) {
	            // Restore the lists original set of entries 
	            // and start from the beginning
	            tableView.setItems( entries );
	        }
	         
	        // Change to upper case so that case is not an issue
	        newVal = newVal.toUpperCase();
	 
	        // Filter out the entries that don't contain the entered text
	        ObservableList<String> subentries = FXCollections.observableArrayList();
	        for (Object entry: tableView.getItems() ) {
	        	String entryText = entry.toString();
	            if ( entryText.toUpperCase().contains(newVal) ) {
	                subentries.add(entryText);
	            }
	        }
	        
	        tableView.setItems(subentries);
	    }	 
}
