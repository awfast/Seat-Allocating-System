package Controller;

import java.util.PriorityQueue;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.Collections;

/*
 * @author Damyan Rusinov
 * @This class attempts to find a schedule using the A* search algorithm(goal not always discovered).
 */

public class AstarSearchAlgo {
	int studentID = 0;
	String moduleCode = null;
	private double dx;
	private double dy;
	private double D;
	
	public List<Node> printPath(Node target) {
		List<Node> path = new ArrayList<Node>();
		for (Node node = target; node != null; node = node.parent) {
			path.add(node);
		}
		Collections.reverse(path);
		return path;
	}
	
	public void printPathTest(Node target) {
		List<Node> path = new ArrayList<Node>();
		for (Node node = target; node != null; node = node.parent) {
			path.add(node);
		}
		Collections.reverse(path);
		
		for(int i=0; i<path.size(); i++) {
		/*	schedule = path.get(i).getSchedule();
			studentID = schedule.getStudentID(schedule);
			moduleCode = schedule.getModuleCode(schedule);
			int sessionID = schedule.getSessionID(schedule);
			int buildingNumber = schedule.getBuildingNumber(schedule);
			int roomNumber = schedule.getRoomNumber(schedule);*/
		//	System.out.println("(Student ID: " + studentID + ", ModuleCode: " + moduleCode + ", sessionID: " + sessionID + ", Building Number: " + buildingNumber + ", Room Number: " + roomNumber + ")");
		}
	}

	
	public void a_star_search(Node start, Node goal) {
		//already evaluated
		Set<Node> closed = new HashSet<Node>();
		//nodes to be evaluated
		PriorityQueue<Node> open = new PriorityQueue<Node>(20, new Comparator<Node>() {
			// override compare method
			public int compare(Node goal, Node current) {
				if (goal.fScore > current.fScore) {
					return 1;
				}
				else if (goal.fScore < current.fScore) {
					return -1;
				}
				else {
					return 0;
				}
			}
		});
		//start cost
		start.gScore = 0;
		boolean found = false;		
		open.add(start);
		while ((!open.isEmpty()) && (!found)) {
			Node current = open.poll();
			closed.add(current);
			// if goal found, return true
			if (current.cost.equals(goal.cost)) {
				found = true;
			}
			// check all children nodes
			for (Edge edge : current.adjacentNodes) {
				Node child = edge.target;
				
				// If the child has already been checked and its cost is lower than the euclidean estimation, continue				 
				if ((closed.contains(child)) && (getEuclideanDistance(current, goal) >= child.fScore)) {
					continue;
				}
				
				//If child node is not in open or the euclidean distance is less than the estimate
				else if ((!open.contains(child)) || (getEuclideanDistance(current, goal) < child.fScore)) {
					child.parent = current;
					child.gScore = current.gScore;
					child.fScore = current.fScore;
					if (open.contains(child)) {
						open.remove(child);
						closed.add(child);
					}
					open.add(child);
				}
			}
		}
	}
	
	public double getEuclideanDistance(Node node, Node goal) {
	    dx = Math.abs(node.getX() - node.getX());
	    dy = Math.abs(node.getY() - goal.getY());
	    return D * Math.sqrt(dx * dx + dy * dy);  
	}


}
