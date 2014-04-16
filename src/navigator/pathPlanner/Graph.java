package navigator.pathPlanner;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import utilities.Point;

/**
 * Graph.java
 * ECSE 211 - TEAM 16
 *
 * Hoai Phuoc Truong - 260526454
 * Francis O'brien - 260582444
 * Alex Reiff - 260504962
 * Juan Morency Trudel - 260481762
 * Vlad Kesler - 260501714
 * Henry Wang - 260580986
 * 
 * Date : 09 April 2014
 */

/**
 * Graph implementation using adjacency list to express the edges.
 * This is a directed graph.
 * This is graph of points, to be used to help RRT.
 * @see RRT.java
 *
 */
public class Graph {

	private final ArrayList<Point> vertices;
	private final ArrayList<ArrayList<Edge>> adjacentList;
	private final ArrayList<Integer> parents;
	private final ArrayList<Boolean> visited;

	public Graph() {
		vertices = new ArrayList<Point>();
		adjacentList = new ArrayList<ArrayList<Edge>>();
		parents = new ArrayList<Integer>();
		visited = new ArrayList<Boolean>();
	}

	/**
	 * Add a vertex to the graph
	 * @param point the point that the new vertex is going to represent
	 * @return an integer indicating the number of the vertex in the graph. Afterwards, client can only
	 * refer to this vertex using this number.
	 */
	public int addVertex(Point point) {
		vertices.add(point);
		parents.add(-1);
		visited.add(false);
		adjacentList.add(new ArrayList<Edge>());
		return sizeV() - 1;
	}

	/**
	 * Add an edge to the existing graph
	 * @param start starting vertex of the edge
	 * @param end ending vertex of the edge
	 * @param weight edge weight
	 */
	public void addEdge(int start, int end, double weight) {
		if (!adjacentList.get(checkValid(start)).contains(checkValid(end))) {
			adjacentList.get(start).add(new Edge(end, weight));
		} else {
			throw new IllegalStateException("Edge existed");
		}
	}

	/**
	 * Do a bfs search from a vertex of the graph to every other vertices of the graph.
	 * @param vertex the starting vertex for bfs
	 */
	public void bfs(int vertex) {
		checkValid(vertex);

		for (int i = 0; i < sizeV(); i++) {
			parents.set(i, -1);
			visited.set(i, false);
		}

		Queue<Integer> queue = new Queue<Integer>();
		queue.push(vertex);

		while (!queue.isEmpty()) {
			int current = (Integer) queue.pop();
			for (Edge adjacent : adjacent(current)) {
				if (!visited.get(adjacent.endVertex)) {
					queue.push(adjacent.endVertex);
					visited.set(current, true);
					parents.set(adjacent.endVertex, current);
				}
			}
		}
	}

	/**
	 * Inefficient dijkstra implementation. Can be improved by implementing a priority queue.
	 * 
	 * @param start
	 *            starting vertex of the dijkstra
	 */
	public void dijkstra(int start) {
		LinkedList<Integer> waiting = new LinkedList<Integer>();
		double[] distances = new double[sizeV()];
		boolean[] waitListed = new boolean[sizeV()];

		for (int i = 0; i < sizeV(); i++) {
			distances[i] = Double.MAX_VALUE;
		}

		distances[start] = 0.0;
		waiting.add(start);

		/**
		 * Forward propagation
		 * This also marks parent for the vertices.
		 */
		while (!waiting.isEmpty()) {
			double min = Double.MAX_VALUE;
			int minVertexIndex = -1;

			// This loop is inefficient. Implement a heap to fix this.
			for (int i = 0; i < waiting.size(); i++) {
				int vertex = waiting.get(i);
				if (distances[vertex] < min) {
					min = distances[vertex];
					minVertexIndex = i;
				}
			}

			int minVertex = waiting.remove(minVertexIndex);
			visited.set(minVertex, true);

			for (Edge edge : adjacentList.get(minVertex)) {
				if (!visited.get(edge.endVertex)) {
					double newDistance = distances[minVertex] + edge.weight;
					if (distances[edge.endVertex] > newDistance) {
						distances[edge.endVertex] = newDistance;
						parents.set(edge.endVertex, minVertex);
					}

					if (!waitListed[edge.endVertex]) {
						waitListed[edge.endVertex] = true;
						waiting.add(edge.endVertex);
					}
				}
			}
		}
	}

	/**
	 * Get parent of a vertex.
	 * @param vertex child vertex
	 * @return parent of the input. null if there is no parent
	 */
	public int getParent(int vertex) {
		return parents.get(checkValid(vertex));
	}

	/**
	 * Set parent for a vertex.
	 * @param child child vertex
	 * @param parent parent vertex that will take child as a child
	 */
	protected void setParent(int child, int parent) {
		parents.set(child, parent);
	}

	/**
	 * Return the original point that the vertex represents
	 * @param vertex vertex number provided by addVertex() method
	 * @return the point that the vertex represents
	 */
	public Point getVertex(int vertex) {
		return vertices.get(checkValid(vertex));
	}

	/**
	 * Get list of adjacent vertices of a vertex
	 * @param vertex interested vertex
	 * @return list of adjacent vertices of the interested vertex
	 */
	public ArrayList<Edge> adjacent(int vertex) {
		return adjacentList.get(checkValid(vertex));
	}

	/**
	 * Check if a vertex query from client is valid
	 * @param vertex the vertex that is going to be checked
	 * @return the input if there is no problem. Otherwise throw an exception
	 * @throws IllegalStateException if the vertex query refers to a non-existing vertex
	 */
	private int checkValid(int vertex) {
		if (vertex >= sizeV()) {
			throw new IllegalStateException("Invalid vertex!");
		} else {
			return vertex;
		}
	}

	/**
	 * Get number of vertices in the graph
	 * @return number of vertices in the graph 
	 */
	public int sizeV() {
		return vertices.size();
	}

	/**
	 * Implementation for graph edge
	 * @author ptruon4
	 *
	 */
	protected static class Edge {
		int endVertex;
		double weight;

		private Edge(int endVertex, double weight) {
			this.endVertex = endVertex;
			this.weight = weight;
		}
	}
}