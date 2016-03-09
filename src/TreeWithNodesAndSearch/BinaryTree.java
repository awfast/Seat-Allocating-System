package TreeWithNodesAndSearch;

import Main.Schedule;

public class BinaryTree {

	Node root;

	public void addNode(Schedule schedule, int cost) {

		// Create a new Node and initialize it

		Node newNode = new Node(schedule, cost);

		// If there is no root this becomes root

		if (root == null) {

			root = newNode;

		} else {

			// Set root as the Node we will start
			// with as we traverse the tree

			Node focusNode = root;

			// Future parent for our new Node

			Node parent;

			while (true) {

				// root is the top parent so we start
				// there

				parent = focusNode;

				// Check if the new node should go on
				// the left side of the parent node

				if (cost < focusNode.h_scores) {

					// Switch focus to the left child

					focusNode = focusNode.leftChild;

					// If the left child has no children

					if (focusNode == null) {

						// then place the new node on the left of it

						parent.leftChild = newNode;
						return; // All Done

					}

				} else { // If we get here put the node on the right

					focusNode = focusNode.rightChild;

					// If the right child has no children

					if (focusNode == null) {

						// then place the new node on the right of it

						parent.rightChild = newNode;
						return; // All Done

					}

				}

			}
		}

	}
	
	public Node findNode(int key) {

		// Start at the top of the tree

		Node focusNode = root;

		// While we haven't found the Node
		// keep looking

		while (focusNode.key != key) {

			// If we should search to the left

			if (key < focusNode.key) {

				// Shift the focus Node to the left child

				focusNode = focusNode.leftChild;

			} else {

				// Shift the focus Node to the right child

				focusNode = focusNode.rightChild;

			}

			// The node wasn't found

			if (focusNode == null)
				return null;

		}

		return focusNode;

	}
}