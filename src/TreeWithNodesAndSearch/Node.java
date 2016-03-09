package TreeWithNodesAndSearch;
import Main.*;
public class Node {
	
	public final Schedule value;
	public double g_scores;
	public final double h_scores;
	public double f_scores = 0;
	public Edge[] adjacencies;
	public Node parent;
	public Node target = null;
	int key;
	public Node leftChild;
	public Node rightChild;

	
	public Node(Schedule val, double hVal) {
		value = val;
		h_scores = hVal;
	}
	
	public Schedule getSchedule() {
		return value;
	}
	
	public Schedule toString(Schedule schedule, double cost) {
		System.out.println(schedule + " has the key " + cost);
		return schedule;
	}
}
