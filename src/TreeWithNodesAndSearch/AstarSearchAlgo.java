package TreeWithNodesAndSearch;

import java.util.PriorityQueue;
import java.util.HashSet;
import java.util.Set;

import Main.Schedule;

import java.util.List;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.Collections;

public class AstarSearchAlgo {
	int studentID = 0;
	String moduleCode = null;
	
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
		Schedule schedule = null;

		for (Node node = target; node != null; node = node.parent) {
			path.add(node);
		}
		Collections.reverse(path);
		
		for(int i=0; i<path.size(); i++) {
			schedule = path.get(i).getSchedule();
			studentID = schedule.getStudentID(schedule);
			moduleCode = schedule.getModuleCode(schedule);
			int sessionID = schedule.getSessionID(schedule);
			int buildingNumber = schedule.getBuildingNumber(schedule);
			int roomNumber = schedule.getRoomNumber(schedule);
			System.out.println("Path: -> (Student ID: " + studentID + ", ModuleCode: " + moduleCode + ", sessionID: " + sessionID + ", Building Number: " + buildingNumber + ", Room Number: " + roomNumber + ")");
		}
	}

	public void AstarSearch(Node source, Node goal) {
		Set<Node> explored = new HashSet<Node>();
		PriorityQueue<Node> queue = new PriorityQueue<Node>(20, new Comparator<Node>() {
			// override compare method
			public int compare(Node i, Node j) {
				if (i.f_scores > j.f_scores) {
					return 1;
				}
				else if (i.f_scores < j.f_scores) {
					return -1;
				}
				else {
					return 0;
				}
			}
		});
		// cost from start
		source.g_scores = 0;
		queue.add(source);
		boolean found = false;
		while ((!queue.isEmpty()) && (!found)) {
			// the node in having the lowest f_score value
			Node current = queue.poll();
			explored.add(current);
			// goal found
			if (current.value.equals(goal.value)) {
				found = true;
			}
			// check every child of current node
			for (Edge e : current.adjacencies) {
				Node child = e.target;
				double cost = e.cost;
				double temp_g_scores = current.g_scores + cost;
				double temp_f_scores = temp_g_scores + child.h_scores;
				/*
				 * if child node has been evaluated and the newer f_score is
				 * higher, skip
				 */
				if ((explored.contains(child)) && (temp_f_scores >= child.f_scores)) {
					continue;
				}

				/*
				 * else if child node is not in queue or newer f_score is lower
				 */

				else if ((!queue.contains(child)) || (temp_f_scores < child.f_scores)) {
					child.parent = current;
					child.g_scores = temp_g_scores;
					child.f_scores = temp_f_scores;
					if (queue.contains(child)) {
						queue.remove(child);
					}
					queue.add(child);
				}
			}
		}
	}
}
