package com.taxi.be.graph.engines;

import com.taxi.be.graph.Solution;
import com.taxi.be.graph.elements.CityEdge;
import com.taxi.be.graph.elements.CityVertex;
import com.taxi.be.input.user.Taxi;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.GraphWalk;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.function.Function;

public class ShortestPathCalculator {

    private SimpleWeightedGraph<CityVertex,CityEdge> grid;
    private ArrayList<Taxi> taxis;
    private CityVertex source;
    private CityVertex target;
    private Taxi chosenTaxi;

    public ShortestPathCalculator(SimpleWeightedGraph<CityVertex,CityEdge> grid, ArrayList<Taxi> taxis, CityVertex source, CityVertex target) {
        this.grid = grid;
        this.taxis = taxis;
        this.source = source;
        this.target = target;
    }

    public Solution calculate() {
        // Producing a weightless version of our graph in order to safely get the shortest path
        // Hashmap stores the pats (Taxis are the key). Utility ArrayList is for quickly sorting out the best choice
        DijkstraShortestPath<CityVertex, CityEdge> dijkstra_shortest = new DijkstraShortestPath<>(grid);
        HashMap<Taxi, GraphWalk<CityVertex,CityEdge>> paths = new HashMap<>();
        ArrayList<RoutesLength> routesLength = new ArrayList<>();

        for(Taxi taxi : taxis) {
            GraphWalk<CityVertex,CityEdge> shortPath = (GraphWalk<CityVertex, CityEdge>) dijkstra_shortest.getPath(taxi.getPositionAsCityVertex(), source);
            paths.put(taxi,shortPath);
            routesLength.add(new RoutesLength(taxi,shortPath.getLength()));
        }
        routesLength.sort(Comparator.comparingInt(taxi -> taxi.length));
        chosenTaxi = routesLength.get(0).taxi;
        GraphWalk<CityVertex,CityEdge> quickestPath = paths.get(chosenTaxi);

        // Calculating the road from the user to the target
        GraphWalk<CityVertex,CityEdge> userToTarget = (GraphWalk<CityVertex, CityEdge>) dijkstra_shortest.getPath(source, target);
        // Joining the two parts of the route
        Function<GraphWalk<CityVertex,CityEdge>,Double> calculateTotalWeight = graph -> graph.getEdgeList().stream()
                                                                                                .mapToDouble((x) -> grid.getEdgeWeight(x))
                                                                                                .sum();
        GraphWalk<CityVertex,CityEdge> completeRoute = quickestPath.concat(userToTarget,calculateTotalWeight);
        return new Solution(quickestPath,completeRoute,chosenTaxi);
    }

    public Taxi getChosenTaxi() {
        return chosenTaxi;
    }

    // A utility class to track to order taxis by the length of their route
    private class RoutesLength {
        Taxi taxi;
        int length;

        RoutesLength(Taxi taxi, int length) {
            this.taxi = taxi;
            this.length = length;
        }
    }

}
