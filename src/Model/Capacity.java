package Model;

public class Capacity {

	private int capacity;
	private int numberOfAccessibleSeats;
	
	public Capacity(int capacity, int numberOfAccessibleSeats) {
		this.capacity = capacity;
		this.numberOfAccessibleSeats = numberOfAccessibleSeats;
	}
	
	public int getCapacity() {
		return this.capacity;
	}
	
	public int getNumberOfAccessibleSeats() {
		return this.numberOfAccessibleSeats;
	}
}
