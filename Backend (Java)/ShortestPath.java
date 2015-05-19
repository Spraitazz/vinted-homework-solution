import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class ShortestPath {

	private static int distanceBetween(String travelNodes, String[] distances) {
		// simple route length calculation
		int routeLength = 0;
		for (int i = 0; i < travelNodes.length() - 1; i++) {
			boolean containsThis = false;
			for (String dist : distances) {
				if (dist.contains(travelNodes.substring(i, i + 2))) {
					containsThis = true;
					routeLength = routeLength
							+ Integer.valueOf(dist.substring(2));
				}
			}
			if (containsThis == false) {
				routeLength = 0;
				break;
			}
		}
		return routeLength;
	}

	private static HashMap<String, Integer> Dijkstra(String[] distances,
			String source) {
		// Dijkstra implementation from wiki pseudocode, no path backtracing
		HashMap<String, Integer> minDistances = new HashMap<String, Integer>();
		ArrayList<String> unvisited = new ArrayList<String>();
		minDistances.put(source, 0);

		for (String dist : distances) {
			if (!dist.substring(0, 1).equals(source)) {
				minDistances.put(dist.substring(0, 1), 999999999);
				if (!unvisited.contains(dist.substring(0, 1))) {
					unvisited.add(dist.substring(0, 1));
				}
			}
			if (!dist.substring(1, 2).equals(source)) {
				minDistances.put(dist.substring(1, 2), 999999999);
				if (!unvisited.contains(dist.substring(0, 1))) {
					unvisited.add(dist.substring(0, 1));
				}
			}
		}

		while (unvisited.size() > 0) {
			// finding unvisited vertex with minimum minDistances(vertex)
			String minDistVertex = unvisited.get(0);
			int minDist = minDistances.get(minDistVertex);
			for (int i = 1; i < unvisited.size(); i++) {
				String thisVertex = unvisited.get(i);
				int thisDist = minDistances.get(thisVertex);
				if (thisDist < minDist) {
					minDist = thisDist;
					minDistVertex = thisVertex;
				}
			}
			unvisited.remove(minDistVertex);

			// only allow movement in the correct direction (XY: X--->Y)
			for (String dist : distances) {
				if (dist.substring(0, 1).equals(minDistVertex)) {					
					int temp = minDist + Integer.valueOf(dist.substring(2));
					if (temp < minDistances.get(dist.substring(1, 2))) {
						minDistances.put(dist.substring(1, 2), temp);
					}
				}
			}
		}
		return minDistances;
	}

	private static int possibleTrips(ArrayList<String> sourceNodes,
			String endNode, String[] distances, int currentStops, int paths,
			int maxStops, boolean exact) {
		// one method to solve both 6 and 7, can be used for any like problems			
		// ArrayList to allow easy expansion and allow duplicates
		
		if (currentStops == maxStops) {
			return paths;
		} else {
			// arraylist to allow duplicates, hashset because it removes all
			// same instances
			ArrayList<String> toAdd = new ArrayList<String>();
			HashSet<String> toRemove = new HashSet<String>();

			if (currentStops == 0) {
				// only starting search - expand by one step
				for (String node : sourceNodes) {
					for (String dist : distances) {
						if (dist.substring(0, 1).equals(node)) {
							toAdd.add(dist.substring(1, 2));
							toRemove.add(node);
						}
					}
				}

				// first remove the nodes that i've left, then add the ones im
				// visiting now
				sourceNodes.removeAll(toRemove);
				sourceNodes.addAll(toAdd);

				return possibleTrips(sourceNodes, endNode, distances,
						currentStops + 1, paths, maxStops, exact);
			} else {
				// check if returning to c now, count how many times, continue
				// expanding
				int endNodeCount = 0;
				for (String node : sourceNodes) {
					for (String dist : distances) {
						if (dist.substring(0, 1).equals(node)) {
							if (dist.substring(1, 2).equals(endNode)) {
								if (exact == false) {
									// only when the stop number doesnt have to
									// be exactly maxStops
									endNodeCount++;
								} else {
									// when looking for exact (case 7)
									if (currentStops == maxStops - 1) {
										endNodeCount++;
									}
								}
							}
							// moving forward, leaving last node
							toAdd.add(dist.substring(1, 2));
							toRemove.add(node);
						}
					}
				}
				// same as before
				sourceNodes.removeAll(toRemove);
				sourceNodes.addAll(toAdd);
				return possibleTrips(sourceNodes, endNode, distances,
						currentStops + 1, paths + endNodeCount, maxStops, exact);
			}
		}
	}

	private static int routesCtoC(String[] distances, ArrayList<String> routes,
			ArrayList<Integer> lengths) {
		// potentially inefficient - looks for ALL routes out of c that are of
		// length < 30
		boolean changed = false;

		// temporary storage
		HashSet<String> toAdd = new HashSet<String>();
		HashSet<String> toRemove = new HashSet<String>();

		// all next possible steps taken here
		for (int i = 0; i < routes.size(); i++) {
			String route = routes.get(i);
			for (String dist : distances) {
				if (dist.substring(0, 1).equals(
						route.substring(route.length() - 1))) {
					// dont overstep 30
					if ((lengths.get(i) + Integer.valueOf(dist.substring(2))) < 30) {
						changed = true;
						toAdd.add(route + dist.substring(1, 2));
						toRemove.add(route);
					}
				}
			}
		}

		routes.removeAll(toRemove);
		routes.addAll(toAdd);

		// expand arraylist to sufficient size to fit lengths
		for (int i = 0; i < (routes.size() - lengths.size() + 1); i++) {
			lengths.add(0);
		}
		for (int i = 0; i < routes.size(); i++) {
			// calculate and add lengths
			lengths.set(i, distanceBetween(routes.get(i), distances));
		}

		// check if changes made over last iteration, if not - done
		if (changed == true) {
			return routesCtoC(distances, routes, lengths);
		} else {
			// finalize output - clear tails (not reached c/past c), remove
			// duplicate paths
			for (int i = 0; i < routes.size(); i++) {
				String thisRoute = routes.get(i);
				if (thisRoute.indexOf("c") != thisRoute.lastIndexOf("c")) {
					routes.set(i, thisRoute.substring(0,
							thisRoute.lastIndexOf("c") + 1));
				} else {
					routes.remove(i);
				}
			}
			HashSet<String> pathsNoDuplicates = new HashSet<String>(routes);
			return pathsNoDuplicates.size();
		}
	}

	public static void main(String[] args) {
		// not assuming input is always uppercase
		// assuming always of same format - two letters and a number (less than
		// 999999999) and allowing for different command-line inputs (simple
		// checks)
		// either "XX1, XX2, XX3" or XX1, XX2, XX3 etc. without quotes

		String input = "";
		if (args.length == 1) {
			input = args[0].toLowerCase();
		} else if (args.length == 0) {
			System.out.println("Input expected. Exit.");
			System.exit(0);
		} else {
			input = args[0].toLowerCase();
			for (int i = 1; i < args.length; i++) {
				input = input + " " + args[i].toLowerCase();
			}
		}
		String[] distances = input.split(", ");
		String[] distancePaths = { "abc", "ad", "adc", "aebcd", "aed" };
		for (int i = 1; i < distancePaths.length + 1; i++) {
			int distanceThis = distanceBetween(distancePaths[i - 1], distances);
			String printResult = "";
			if (distanceThis == 0) {
				printResult = "NO SUCH ROUTE";
			} else {
				printResult = "" + distanceThis;
			}
			System.out.println("#" + i + ": " + printResult);
		}

		// starting trip count - need arraylist to begin
		ArrayList<String> sourceNodes = new ArrayList<String>();
		sourceNodes.add("c");
		System.out.println("#6: "
				+ possibleTrips(sourceNodes, "c", distances, 0, 0, 3, false));
		sourceNodes.clear();
		sourceNodes.add("a");
		System.out.println("#7: "
				+ possibleTrips(sourceNodes, "c", distances, 0, 0, 4, true));

		// assuming it is possible to get from A to C/ from B to B, otherwise
		// output is >=999999999
		HashMap<String, Integer> resultsOfDijkstra8 = Dijkstra(distances, "a");
		System.out.println("#8: " + resultsOfDijkstra8.get("c"));

		// Finding shortest path from B to any node, then dijkstra from that
		// node
		String minDistNode = "";
		int minDist = 999999999;
		for (String dist : distances) {
			if (dist.substring(0, 1).equals("b")) {
				int thisDist = Integer.valueOf(dist.substring(2));
				if (thisDist < minDist) {
					minDist = thisDist;
					minDistNode = dist.substring(1, 2);
				}
			}
		}

		HashMap<String, Integer> resultsOfDijkstra9 = Dijkstra(distances,
				minDistNode);
		System.out.println("#9: "
				+ (((Integer) resultsOfDijkstra9.get("b")) + minDist));

		ArrayList<String> movements = new ArrayList<String>();
		ArrayList<Integer> lengths = new ArrayList<Integer>();
		movements.add("c");
		lengths.add(0);
		System.out.println("#10: " + routesCtoC(distances, movements, lengths));
	}
}