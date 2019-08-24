package app;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

import structures.Arc;
import structures.Graph;
import structures.MinHeap; //can add this import statement
import structures.PartialTree;
import structures.Vertex;

/**
 * Stores partial trees in a circular linked list
 *
 */
public class PartialTreeList implements Iterable<PartialTree> {

	/**
	 * Inner class - to build the partial tree circular linked list
	 *
	 */
	public static class Node {
		/**
		 * Partial tree
		 */
		public PartialTree tree;

		/**
		 * Next node in linked list
		 */
		public Node next;

		/**
		 * Initializes this node by setting the tree part to the given tree, and setting
		 * next part to null
		 *
		 * @param tree Partial tree
		 */
		public Node(PartialTree tree) {
			this.tree = tree;
			next = null;
		}
	}

	/**
	 * Pointer to last node of the circular linked list
	 */
	private Node rear;

	/**
	 * Number of nodes in the CLL
	 */
	private int size;

	/**
	 * Initializes this list to empty
	 */
	public PartialTreeList() {
		rear = null;
		size = 0;
	}

	/**
	 * Adds a new tree to the end of the list
	 *
	 * @param tree Tree to be added to the end of the list
	 */
	public void append(PartialTree tree) {
		Node ptr = new Node(tree);
		if (rear == null) {
			ptr.next = ptr;
		} else {
			ptr.next = rear.next;
			rear.next = ptr;
		}
		rear = ptr;
		size++;
	}

	/**
	 * Initializes the algorithm by building single-vertex partial trees
	 *
	 * @param graph Graph for which the MST is to be found
	 * @return The initial partial tree list
	 */
	public static PartialTreeList initialize(Graph graph) { 

		PartialTreeList l = new PartialTreeList(); // Create an empty list L of partial trees.
		// Separately for each vertex v in the graph:
		for (int i = 0; i < graph.vertices.length; i++) { // loop through length of graph vertices

			Vertex v = graph.vertices[i]; // Vertex V

			PartialTree t = new PartialTree(v);// Create a partial tree T containing only v.

			// Create a priority queue (heap) P and associate it with T.
			MinHeap<Arc> heap = t.getArcs();

			// Insert all of the arcs (edges) connected to v into P. The lower the weight on
			// an arc, the higher its priority.
			Vertex.Neighbor neighbor = v.neighbors;

			while (neighbor != null) {
				Arc arc = new Arc(v, neighbor.vertex, neighbor.weight);
				heap.insert(arc); // or t.getArcs().insert(arc);

				neighbor = neighbor.next;
			}

			l.append(t); // Add the partial tree T to the list L.

		}
		Iterator<PartialTree> iter = l.iterator();
		while (iter.hasNext()) {
			PartialTree pt = iter.next();
			System.out.println(pt + " ,");
		}
		System.out.println();
		return l;
	}

	/**
	 * Executes the algorithm on a graph, starting with the initial partial tree
	 * list for that graph
	 *
	 * @param ptlist Initial partial tree list
	 * @return Array list of all arcs that are in the MST - sequence of arcs is
	 *         irrelevant
	 */
	public static ArrayList<Arc> execute(PartialTreeList ptlist) {

		ArrayList<Arc> arcsPresent = new ArrayList<Arc>();

		while (ptlist.size() > 1) {

			PartialTree PTX = ptlist.remove();

			if (PTX == null || PTX.equals(null))
				break;

			MinHeap<Arc> PQX = PTX.getArcs();

			Arc arc = PQX.deleteMin();

			while (arc != null) {
				Vertex v1 = arc.getv1();
				Vertex v2 = arc.getv2();

				PartialTree PTY = ptlist.removeTreeContaining(v1);

				if (PTY == null) { // null, then vertex 2
					PTY = ptlist.removeTreeContaining(v2);
				}
       
				if (PTY != null) {
					PTX.merge(PTY);
					arcsPresent.add(arc);
					ptlist.append(PTX);
					break;
				}  
				arc = PQX.deleteMin(); // update arc to prevent infinite loop and to iterate
			}
		}      
		for (int i = 0; i < arcsPresent.size(); i++) {
			System.out.print(arcsPresent.get(i) + ", ");
		}
		System.out.println();
		return arcsPresent;
	}     

	/**
	 * Removes the tree that is at the front of the list.
	 *
	 * @return The tree that is removed from the front
	 * @throws NoSuchElementException If the list is empty
	 */
	public PartialTree remove() throws NoSuchElementException {

		if (rear == null) {
			throw new NoSuchElementException("list is empty");
		}
		PartialTree ret = rear.next.tree;
		if (rear.next == rear) {
			rear = null;
		} else {
			rear.next = rear.next.next;
		}
		size--;
		return ret;

	}

	/**
	 * Removes the tree in this list that contains a given vertex.
	 *
	 * @param vertex Vertex whose tree is to be removed
	 * @return The tree that is removed
	 * @throws NoSuchElementException If there is no matching tree
	 */
	public PartialTree removeTreeContaining(Vertex vertex) throws NoSuchElementException {
		PartialTree treeToBeRemoved = null;
		if (rear == null) {
			throw new NoSuchElementException("list is empty");
		}
		
		Node temp = rear;
		Node prev = null;
		do {
			if (checkIfPresent(vertex, temp.tree)) {
				treeToBeRemoved = temp.tree;
				removeNodeTree(temp, vertex);
				break;
			}
			prev = temp;
			temp = temp.next;

		} while (temp != rear);

		return treeToBeRemoved;
	}

	private boolean checkIfPresent(Vertex v, PartialTree t) {
		while (v.parent != v) {
			v = v.parent;
		}
		if (v == t.getRoot()) {
			return true;
		} else {
			return false;
		}
	}

	private Node findPrev(Node n) {
		Node x = n;
		while (x.next != n) {
			x = x.next;
		}
		return x;
	}

	private void removeNodeTree(Node n, Vertex v) {
		Node next = n.next;
		Node prev = findPrev(n);
		if (next == n && prev == n && size == 1) {
			rear = null;
		} else if (next == prev) {
			if (n == rear) {
				rear = rear.next;
			}
			n.next.next = n.next;
		} else {
			if (n == rear) {
				rear = prev;
			}
			prev.next = next;
		}
		size--;
	}

	/**
	 * Gives the number of trees in this list
	 *
	 * @return Number of trees
	 */
	public int size() {
		return size;
	}

	/**
	 * Returns an Iterator that can be used to step through the trees in this list.
	 * The iterator does NOT support remove.
	 *
	 * @return Iterator for this list
	 */
	public Iterator<PartialTree> iterator() {
		return new PartialTreeListIterator(this);
	}

	private class PartialTreeListIterator implements Iterator<PartialTree> {

		private PartialTreeList.Node ptr;
		private int rest;

		public PartialTreeListIterator(PartialTreeList target) {
			rest = target.size;
			ptr = rest > 0 ? target.rear.next : null;
		}

		public PartialTree next() throws NoSuchElementException {
			if (rest <= 0) {
				throw new NoSuchElementException();
			}
			PartialTree ret = ptr.tree;
			ptr = ptr.next;
			rest--;
			return ret;
		}

		public boolean hasNext() {
			return rest != 0;
		}

		public void remove() throws UnsupportedOperationException {
			throw new UnsupportedOperationException();
		}

	}
}
