import java.util.ArrayList;
import java.util.LinkedList;

public class Dijkstra {
    
    
    private static class Node implements Comparable<Node>{
        int totalCost;
        int hops;        
        String hostId;
        boolean isVisited;
        Node parent;
        
        
        private static final ArrayList<String> buffer1 = new ArrayList<>();
        private static final ArrayList<String> buffer2 = new ArrayList<>();
        
        Node(String id, int cost, int hops, boolean visited, Node parent){
            this.totalCost = cost;
            this.hops = hops; 
            this.hostId = id;
            this.isVisited = visited;
            this.parent = parent;
        }
        
        @Override
        public int compareTo(Node other){
            // 1. Compare Total Latency
            int costCompare = Integer.compare(this.totalCost, other.totalCost);
            if (costCompare != 0) return costCompare;
            
            // 2. Compare Hop Count (Fewest segments preferred)
            int hopsCompare = Integer.compare(this.hops, other.hops);
            if (hopsCompare != 0) return hopsCompare;
            
            // 3. Compare Path Lexicographically (Optimized)
            return comparePaths(this, other);
        }
        
        private int comparePaths(Node n1, Node n2) {
            buffer1.clear();
            buffer2.clear();
            
            Node curr = n1;
            while(curr != null) { buffer1.add(curr.hostId); curr = curr.parent; }
            
            curr = n2;
            while(curr != null) { buffer2.add(curr.hostId); curr = curr.parent; }
            
            int size1 = buffer1.size();
            int size2 = buffer2.size();
            int minSize = Math.min(size1, size2);

            for (int i = 1; i <= minSize; i++) {
                String id1 = buffer1.get(size1 - i);
                String id2 = buffer2.get(size2 - i);
                int cmp = id1.compareTo(id2);
                if (cmp != 0) return cmp;
            }
            return Integer.compare(size1, size2);
        }
        
        public LinkedList<String> getPath() {
            LinkedList<String> path = new LinkedList<>();
            Node curr = this;
            
            while (curr != null) {
                // addFirst is O(1) for LinkedList
                path.addFirst(curr.hostId);
                curr = curr.parent;
            }
            
            return path;
        }
    }
    
    Graph graph;
    
    Dijkstra(Graph graph){
        this.graph = graph;
    }
    
    public void setGraph(Graph g) {
		graph = g;
	}

    public String traceRoute(String sourceId, String destId, int minBandwidth) {
        Host sourceHost = graph.getHost(sourceId);
        Host destHost = graph.getHost(destId);

        if (sourceHost == null || destHost == null) return "Error: Invalid host.";
        if (sourceId.equals(destId)) return "Source and Dest are the same.";

        PriorityQueue<Node> pq = new PriorityQueue<>();
        // Map tracks the BEST Node found so far for each Host ID
        HashMap<String, Node> bestPaths = new HashMap<>();
        // Set tracks which Hosts we have effectively "closed/settled"
        HashSet<String> visitedHosts = new HashSet<>();

        Node sourceNode = new Node(sourceId, 0, 0, null);
        bestPaths.put(sourceId, sourceNode);
        pq.add(sourceNode);

        while (!pq.isEmpty()) {
            Node current = pq.poll();
            String currentId = current.hostId;

            // 1. LAZY DELETION CHECK:
            // If we have already visited this Host ID, or if the node we just 
            // popped is worse than a path we found later, skip it.
            if (visitedHosts.contains(currentId)) continue;
            visitedHosts.add(currentId);

            // Found destination?
            if (currentId.equals(destId)) {
                return formatSuccessOutput(sourceId, destId, current);
            }

            Host host = graph.getHost(currentId);

            for (Tunnel tunnel : host.getConnections()) {
                // Constraints
                if (tunnel.isSealed()) continue;
                if (tunnel.getBandwidth() < minBandwidth) continue;
                if (host.getClearance() < tunnel.getFirewallLevel()) continue;

                Host neighborHost = tunnel.getDestination();
                String neighborId = neighborHost.getId();

                // Optimization: If neighbor is already settled, skip
                if (visitedHosts.contains(neighborId)) continue;

                int newCost = current.totalCost + tunnel.getLatency();
                int newHops = current.hops + 1;

                Node oldBest = bestPaths.get(neighborId);
                
                // 2. LOGIC: Is this new path better?
                // If we haven't seen this node, OR this path is cheaper/shorter
                if (oldBest == null || isBetterPath(newCost, newHops, oldBest)) {
                    
                    // Create NEW node (Immutable style)
                    Node newNode = new Node(neighborId, newCost, newHops, current);
                    
                    // Update map and PQ
                    bestPaths.put(neighborId, newNode);
                    pq.add(newNode);
                }
            }
        }
        return "No route found.";
    }

    // Helper to determine if the new values are better than the old Node
    private boolean isBetterPath(int newCost, int newHops, Node oldNode) {
        if (newCost < oldNode.totalCost) return true;
        if (newCost > oldNode.totalCost) return false;
        // Costs are equal, prefer fewer hops
        return newHops < oldNode.hops; 
        // (You can add lexicographical tie-breaker here if strictly required, 
        // but avoid full path reconstruction if possible)
    }
    private String formatSuccessOutput(String sourceId, String destId, Node finalState) {
        StringBuilder sb = new StringBuilder();
        sb.append("Optimal route ");
        sb.append(sourceId).append(" -> ").append(destId).append(": ");
        
        LinkedList<String> path = finalState.getPath();
        
        for (int i = 0; i < path.size(); i++) {
            sb.append(path.get(i));
            if (i < path.size() - 1) {
                sb.append(" -> ");
            }
        }
        
        sb.append(" (Latency = ").append(finalState.totalCost).append("ms)");
        return sb.toString();
    }
}