package toritools.pathing;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

/**
 * This class contains a generic A* implementation.
 *
 * @author toriscope
 *
 * @param <D>
 *            the data type of the data within the nodes.
 */
public class AStarModule<D> {

    /**
     * Current graph.
     */
    private AStarGraph<D> graph;

    /**
     * Current goal node.
     */
    private AStarNode<D> goal;

    /*
     * Compares a list of Node<D>'s by F-score, or G + H. G is the edge distance from the start
     * node to the current. H is the approximated distance to the goal.
     */
    private Comparator<AStarNode<D>> scoreSorter = new Comparator<AStarNode<D>>() {

        @Override
        public int compare(final AStarNode<D> o1, final AStarNode<D> o2) {
            Double f1 =
                    o1.getDistanceFromStart()
                            + getGraph().getApproxDistance(o1.getData(), getGoal().getData());
            Double f2 =
                    o2.getDistanceFromStart()
                            + getGraph().getApproxDistance(o2.getData(), getGoal().getData());
            return f1.compareTo(f2);
        }
    };

    /**
     * Create an A* module with the specified graph. You may still add and remove nodes from
     * the graph.
     *
     * @param graph
     *            the graph
     */
    public AStarModule(final AStarGraph<D> graph) {
        this.graph = graph;
    }

    /**
     * Find a path from the start node to the end node, inclusive.
     *
     * @param start
     *            the starting node. Will be part of the path.
     * @param goal
     *            the goal node, will be part of the path.
     * @return the compiled List of nodes that form a path from start to goal.
     * @throws UnreachableNodeException
     *             thrown when it is not possible to reach the goal node from the start node.
     */
    public List<AStarNode<D>> findPathTo(final AStarNode<D> start, final AStarNode<D> goal)
            throws UnreachableNodeException {

        this.setGraph(graph);
        this.setGoal(goal);

        if (start == goal) {
            LinkedList<AStarNode<D>> n = new LinkedList<AStarNode<D>>();
            n.add(start);
            return n;
        }

        for (Entry<D, AStarNode<D>> entry : this.graph.getNodeMap().entrySet()) {
            entry.getValue().setDistanceFromStart(0);
            entry.getValue().setParent(null);
        }

        List<AStarNode<D>> openList = new LinkedList<AStarNode<D>>(), closedList = new LinkedList<AStarNode<D>>();

        // a* algorithm

        AStarNode<D> currNode = start;
        while (currNode != goal) {
            // Dump currNode children into openList
            for (Entry<AStarNode<D>, Double> child : currNode.getChildren().entrySet()) {
                if (closedList.contains(child.getKey())) {
                    continue;
                }
                if (!openList.contains(child.getKey())) {
                    openList.add(child.getKey());
                    child.getKey().setParent(currNode);
                    child.getKey().setDistanceFromStart(
                            currNode.getDistanceFromStart() + child.getValue());
                } else {
                    // if the current square makes a better parent
                    if (child.getKey().getDistanceFromStart() > currNode.getDistanceFromStart()
                            + child.getValue()) {
                        child.getKey().setParent(currNode);
                        child.getKey().setDistanceFromStart(
                                currNode.getDistanceFromStart() + child.getValue());
                    }
                }
            }
            openList.remove(currNode);
            closedList.add(currNode);
            Collections.sort(openList, this.getScoreSorter());
            if (openList.isEmpty()) {
                throw new UnreachableNodeException(start, goal);
            }
            currNode = openList.get(0);
        }

        // Reconstruct the path
        List<AStarNode<D>> path = new LinkedList<AStarNode<D>>();
        while (currNode != start) {
            path.add(currNode);
            currNode = currNode.getParent();
        }
        path.add(start);
        Collections.reverse(path);
        return path;
    }

    /**
     * An A* node.
     *
     * @author toriscope
     *
     * @param <D>
     *            the datatype of the data
     */
    public static class AStarNode<D> {

        private D data;
        private AStarNode<D> parent;
        private HashMap<AStarNode<D>, Double> children;
        private Point2D.Double point;
        private double dist;

        /**
         * Node with given data at given point.
         *
         * @param data
         *            data in node
         * @param point
         *            double position
         */
        public AStarNode(final D data, final Point2D.Double point) {
            this.setData(data);
            this.setPoint(point);
            this.setChildren(new HashMap<AStarNode<D>, Double>());
        }

        /**
         * Add a bi-directional link to this node to another, with a given cost to move there.
         *
         * @param node
         *            neighboring node
         * @param cost
         *            cost to move there.
         */
        public void addOmniLink(final AStarNode<D> node, final Double cost) {
            this.addUniLink(node, cost);
            node.addUniLink(this, cost);
        }

        /**
         * Add a bi-directional link with the path cost automatically set to be the distance.
         *
         * @param node
         *            neighboring node.
         */
        public void addOmniLink(final AStarNode<D> node) {
            this.addOmniLink(node, this.getPoint().distance(node.getPoint()));
        }

        /**
         * Add a 1-directional link to this node to another, with a given cost to move there.
         *
         * @param node
         *            neighboring node
         * @param cost
         *            cost to move there.
         */
        public void addUniLink(final AStarNode<D> node, final Double cost) {
            this.getChildren().put(node, cost);
        }

        /**
         * Add a 1-directional link with the path cost automatically set to be the distance.
         *
         * @param node
         *            neighboring node.
         */
        public void addUniLink(final AStarNode<D> node) {
            this.addUniLink(node, this.getPoint().distance(node.getPoint()));
        }

        /**
         * Remove the path link from THIS to a given node, if it exists. If the other node has
         * a link to THIS, then thet connection is still present.
         *
         * @param node
         *            node to remove the link to.
         */
        public void removeUniLink(final AStarNode<D> node) {
            this.getChildren().remove(node);
        }

        /**
         * Removes all links between this node and a given node.
         *
         * @param node
         *            node to remove the links to/from.
         */
        public void removeOmniLink(final AStarNode<D> node) {
            this.removeUniLink(node);
            node.removeUniLink(this);
        }

        /**
         * Add path cost to the given link.
         *
         * @param node
         *            the node that the path goes to.
         * @param cost
         *            the additional cost.
         */
        public void addPathCost(final AStarNode<D> node, final double cost) {
            this.children.put(node, this.children.get(node) + cost);
        }

        public D getData() {
            return this.data;
        }

        public void setData(final D data) {
            this.data = data;
        }

        private void setParent(final AStarNode<D> parent) {
            this.parent = parent;
        }

        private AStarNode<D> getParent() {
            return this.parent;
        }

        public HashMap<AStarNode<D>, Double> getChildren() {
            return this.children;
        }

        public void setChildren(final HashMap<AStarNode<D>, Double> children) {
            this.children = children;
        }

        @Override
        public String toString() {
            String s =
                    "[" + this.getData().toString() + "]" + "[#Children:"
                            + this.getChildren().size() + "]";
            return s;
        }

        /**
         * toString with children and cost information.
         *
         * @return verbose toString output
         */
        public String toStringVerbose() {
            String s = this.toString();
            for (Entry<AStarNode<D>, Double> entry : this.getChildren().entrySet()) {
                s += "\n\t" + "(N: " + entry.getKey() + ", C: " + entry.getValue() + ")";
            }
            return s;
        }

        public java.awt.geom.Point2D.Double getPoint() {
            return this.point;
        }

        public void setPoint(final java.awt.geom.Point2D.Double point) {
            this.point = point;
        }

        public void setDistanceFromStart(final double dist) {
            this.dist = dist;
        }

        /**
         * Get the distance from the start of the path to here.
         *
         * @return the total path distance.
         */
        public double getDistanceFromStart() {
            return this.dist;
        }
    }

    /**
     * Generic graph, holding nodes and capable of guessing the distances between them. Uses a
     * map for storing, so no nodes with duplicate data should be used. If you'd link to use
     * your own distance approximator, use setDistanceApproximator() and the static interface
     * DistanceApproximator to do so.
     *
     * @author toriscope
     *
     * @param <D>
     *            the data type of the node data.
     */
    public static class AStarGraph<D> {

        /**
         * Interface for guessing the distance betweentwo nodes.
         *
         * @author toriscope
         *
         * @param <D>
         *            data type of the node data.
         */
        public static interface DistanceApproximator<D> {
            /**
             * Get the approximate distance between two nodes.
             *
             * @param a
             *            node a
             * @param b
             *            node b
             * @return the approximate distance.
             */
            double getApproxDistance(final AStarNode<D> a, final AStarNode<D> b);
        }

        /**
         * The current method for approximating distance. Uses the straight-line distance.
         */
        private DistanceApproximator<D> distanceApproximator = new DistanceApproximator<D>() {
            @Override
            public double getApproxDistance(final AStarNode<D> a, final AStarNode<D> b) {
                return a.getPoint().distance(b.getPoint());
            }
        };

        /**
         * Set a custom function for approximating distance between two nodes. Default simply
         * uses the direct distance.
         *
         * @param distanceApproximator
         *            new instance of DistanceApproximator
         */
        public void setDistanceApproximator(final DistanceApproximator<D> distanceApproximator) {
            this.distanceApproximator = distanceApproximator;
        }

        private HashMap<D, AStarNode<D>> nodeMap = new HashMap<D, AStarNode<D>>();

        /**
         * Add a node to the graph. The node miust have its data filled in.
         *
         * @param node
         *            the node with data filled in.
         */
        public void addNode(final AStarNode<D> node) {
            this.getNodeMap().put(node.getData(), node);
        }

        /**
         * Get back the map of data matched to nodes.
         *
         * @return the map of data to nodes
         */
        public HashMap<D, AStarNode<D>> getNodeMap() {
            return this.nodeMap;
        }

        /**
         * Get a node based on given data.
         *
         * @param data
         *            data to index into the node map by.
         * @return the node, if it exists. Otherwise, MissingNodeException is thrown.
         */
        public AStarNode<D> getNode(final D data) {
            AStarNode<D> node = this.getNodeMap().get(data);
            if (node == null) {
                throw new MissingNodeException(data.toString());
            }
            return node;
        }

        /**
         * Get a node based on given pos.
         *
         * @param pos
         *            pos to search in the node map by.
         * @return the node, if it exists. Otherwise, MissingNodeException is thrown.
         */
        public AStarNode<D> getNode(final Point.Double pos) {
            for (Entry<D, AStarNode<D>> n : getNodeMap().entrySet()) {
                if (n.getValue().getPoint().equals(pos)) {
                    return n.getValue();
                }
            }
            throw new MissingNodeException(pos.toString());
        }

        /**
         * Get the approximate distance between two nodes.
         *
         * @param a
         *            node a
         * @param b
         *            node b
         * @return the distance
         * @throws MissingNodeException
         *             thrown if the node cannot be found.
         */
        public double getApproxDistance(final D a, final D b) {

            return this.distanceApproximator.getApproxDistance(this.getNode(a), this.getNode(b));
        }

        /**
         * The requested node cannot be found in the graph. Either the data does not match, or
         * the node has not yet been added to the graph.
         *
         * @author toriscope
         *
         */
        public static class MissingNodeException extends RuntimeException {
            private static final long serialVersionUID = 1L;

            /**
             * Displays a nice little error message when a node is not found.
             *
             * @param nodeInfo
             *            information about the data in the node.
             */
            public MissingNodeException(final String nodeInfo) {
                super("The requested node \"" + nodeInfo.toString()
                        + "\" cannot be found in the graph. Did you forget to add it?");
            }
        }

    }

    public Comparator<AStarNode<D>> getScoreSorter() {
        return scoreSorter;
    }

    /**
     * Set a custom fScore sorter for the algorithm to sort the open list by.
     *
     * @param scoreSorter
     *            the new scoreSorter
     */
    public void setScoreSorter(final Comparator<AStarNode<D>> scoreSorter) {
        this.scoreSorter = scoreSorter;
    }

    public AStarGraph<D> getGraph() {
        return graph;
    }

    public void setGraph(final AStarGraph<D> graph) {
        this.graph = graph;
    }

    public void setGoal(final AStarNode<D> goal) {
        this.goal = goal;
    }

    public AStarNode<D> getGoal() {
        return goal;
    }

    /**
     * The start and goal nodes are not connected by any series of edges.
     *
     * @author toriscope
     *
     */
    public static class UnreachableNodeException extends Exception {
        private static final long serialVersionUID = 1L;

        /**
         * Standard const. Reports that a path could not be built between the two given nodes.
         *
         * @param start
         *            start node
         * @param goal
         *            end node
         */
        public UnreachableNodeException(final AStarNode<?> start, final AStarNode<?> goal) {
            super("The goal node [" + goal.toString()
                    + "] cannot be reached from the start node [" + start.toString() + "].");
        }
    }

    /**
     * Common usage scenario.
     *
     * @param args
     *            ignored
     */
    public static void main(final String[] args) {
        AStarNode<String> atlanta = new AStarNode<String>("Atlanta", new Point2D.Double(0, 0));
        AStarNode<String> ontario = new AStarNode<String>("Ontario", new Point2D.Double(25, 0));
        AStarNode<String> hollywood = new AStarNode<String>("Hollywood", new Point2D.Double(25, 12));
        AStarNode<String> canada = new AStarNode<String>("Canada", new Point2D.Double(0, 12));
        AStarNode<String> nepal = new AStarNode<String>("Nepal", new Point2D.Double(0, 25));
        AStarNode<String> moon = new AStarNode<String>("Moon", new Point2D.Double(0, 10000));
        AStarNode<String> spaceStation =
                new AStarNode<String>("Kennedy Space Center", new Point2D.Double(76, 54));

        // Build the graph
        AStarGraph<String> graph = new AStarGraph<String>();
        graph.addNode(atlanta);
        graph.addNode(ontario);
        graph.addNode(hollywood);
        graph.addNode(canada);
        graph.addNode(nepal);
        graph.addNode(moon);
        graph.addNode(spaceStation);

        // Add roads/edges
        atlanta.addOmniLink(hollywood);
        atlanta.addOmniLink(canada);
        hollywood.addOmniLink(canada);
        nepal.addOmniLink(canada);
        ontario.addOmniLink(hollywood);
        hollywood.addOmniLink(spaceStation);
        // slow rail between nepal and hollywood
        hollywood.addOmniLink(nepal, 100d);
        // One way ride to the moon!
        spaceStation.addUniLink(moon);
        // Factor in the amount of money it costs for a space program.
        spaceStation.addPathCost(moon, 2000);

        System.out.println(atlanta.toStringVerbose());
        System.out.println(ontario.toStringVerbose());
        System.out.println(hollywood.toStringVerbose());
        System.out.println(canada.toStringVerbose());
        System.out.println(ontario.toStringVerbose());

        // Load the graph into aStar and run a calculation
        AStarModule<String> astar = new AStarModule<String>(graph);
        try {
            List<AStarNode<String>> path = astar.findPathTo(nepal, moon);
            System.err.println("Path from " + path.get(0).getData() + " to "
                    + path.get(path.size() - 1).getData() + ":");
            for (AStarNode<String> node : path) {
                System.err.println(node.getData());
            }
        } catch (final UnreachableNodeException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
    }
}
