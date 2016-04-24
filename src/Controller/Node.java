package Controller;
public class Node {
	
	public final Schedule cost;
	public double gScore;
	public final double h_scores;
	public double fScore = 0;
	public Edge[] adjacentNodes;
	public Node parent;
	public Node target = null;
	private int key;
	private int x;
	private int y;
	public Node leftChild;
	public Node rightChild;

	
	public Node(Schedule val, double hVal, int x, int y) {
		cost = val;
		h_scores = hVal;
		this.x = x;
		this.y = y;
	}
	
	public Schedule getSchedule() {
		return cost;
	}
	
	public Schedule toString(Schedule schedule, double cost) {
		return schedule;
	}
	
	public int getKey() {
		return this.key;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
}
