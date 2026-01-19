import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class RouteFinder {
	
	Graph graph;
    
	ArrayList<State>[] bestStates; 
    
    private ArrayList<String> buffer1 = new ArrayList<>();
    private ArrayList<String> buffer2 = new ArrayList<>();
	
    @SuppressWarnings("unchecked")
	RouteFinder(Graph g){
		graph = g;
        
		bestStates = (ArrayList<State>[]) new ArrayList[g.getSize()];
	}
	
	@SuppressWarnings("unchecked")
	public String traceRoute(String sourceId, String destId, int minBandwidth, int lambda) {
        
        Host sourceHost = graph.getHost(sourceId);
        Host destHost = graph.getHost(destId);
        if (sourceHost == null || destHost == null) return "Some error occurred in trace_route.";
        if (sourceId.equals(destId)) return "Optimal route " + sourceId + " -> " + destId + ": " + sourceId + " (Latency = 0ms)";

        
        bestStates = (ArrayList<State>[]) new ArrayList[graph.getSize()];

        PriorityQueue<State> pq = new PriorityQueue<>();
        State startState = new State(sourceId, 0, 0, null); 
        pq.add(startState);
        
        // Use Index
        int srcIdx = sourceHost.getIndex();
        bestStates[srcIdx] = new ArrayList<>();
        bestStates[srcIdx].add(startState);
        
        while (!pq.isEmpty()) {
            State curr = pq.poll();
            if(!curr.isActive) continue;
            
            if (curr.currentHostId.equals(destId)) {
                return formatSuccessOutput(sourceId, destId, curr);
            }
            
            Host currentHost = graph.getHost(curr.currentHostId);
            
            for(Tunnel tunnel : currentHost.getConnections()) {
                if (tunnel.isSealed()) continue;
                if (tunnel.getBandwidth() < minBandwidth) continue;
                if (currentHost.getClearance() < tunnel.getFirewallLevel()) continue;
                
                Host neighborHost = tunnel.getDestination();
                
                int segmentLatency = tunnel.getLatency() + (curr.hops * lambda); 
                int newTotalCost = curr.totalCost + segmentLatency;
                int newHops = curr.hops + 1;
                
                State nextState = new State(neighborHost.getId(), newTotalCost, newHops, curr);
                
                // Pass neighbor INDEX
                if (processState(nextState, neighborHost.getIndex(), pq)) {
                    pq.add(nextState);
                }
            }
        }
        return "No route found from " + sourceId + " to " + destId;
    }
	
    private boolean processState(State newState, int hostIndex, PriorityQueue<State> pq) {
        // OPTIMIZATION: Direct array access
        ArrayList<State> existingStates = bestStates[hostIndex];
        
        if (existingStates == null) {
            existingStates = new ArrayList<>();
            existingStates.add(newState);
            bestStates[hostIndex] = existingStates;
            return true;
        }
        
        
        Iterator<State> it = existingStates.iterator();
        while (it.hasNext()) {
            State existing = it.next();
            
             if (existing.totalCost <= newState.totalCost && existing.hops <= newState.hops) {
                if (existing.totalCost < newState.totalCost || existing.hops < newState.hops) return false; 
                if (comparePaths(existing, newState) <= 0) return false;
            }
            if (newState.totalCost <= existing.totalCost && newState.hops <= existing.hops) {
                 boolean removeExisting = false;
                 if (newState.totalCost < existing.totalCost || newState.hops < existing.hops) removeExisting = true;
                 else if (comparePaths(newState, existing) < 0) removeExisting = true;
                 
                 if (removeExisting) {
                     existing.isActive = false; 
                     it.remove(); 
                 }
            }
        }
        existingStates.add(newState);
        return true;
    }
    
    
    private int comparePaths(State s1, State s2) {
        
        buffer1.clear(); buffer2.clear();
        State curr = s1; while(curr != null) { buffer1.add(curr.currentHostId); curr = curr.parent; }
        curr = s2; while(curr != null) { buffer2.add(curr.currentHostId); curr = curr.parent; }
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
    
    private String formatSuccessOutput(String sourceId, String destId, State finalState) {
        
         StringBuilder sb = new StringBuilder();
        sb.append("Optimal route ");
        sb.append(sourceId).append(" -> ").append(destId).append(": ");
        LinkedList<String> path = finalState.getPath();
        for (int i = 0; i < path.size(); i++) {
            sb.append(path.get(i));
            if (i < path.size() - 1) sb.append(" -> ");
        }
        sb.append(" (Latency = ").append(finalState.totalCost).append("ms)");
        return sb.toString();
    }
}