import java.util.ArrayList;
import java.util.LinkedList;

public class State implements Comparable<State> {
    String currentHostId;
    int totalCost;
    int hops;
    State parent;
    boolean isActive = true;

    
    private static final ArrayList<String> buffer1 = new ArrayList<>();
    private static final ArrayList<String> buffer2 = new ArrayList<>();

    public State(String id, int cost, int hops, State parent) {
        this.currentHostId = id;
        this.totalCost = cost;
        this.hops = hops;
        this.parent = parent;
    }
    
    @Override
    public int compareTo(State other) {
        // 1. Compare Total Latency
        int costCompare = Integer.compare(this.totalCost, other.totalCost);
        if (costCompare != 0) return costCompare;

        // 2. Compare Hop Count
        int hopsCompare = Integer.compare(this.hops, other.hops);
        if (hopsCompare != 0) return hopsCompare;

        // 3. Compare Full Path Lexicographically (Optimized)
        // We use the static buffers instead of creating new lists
        return comparePaths(this, other);
    }
    
    // Helper to compare paths without allocation
    private int comparePaths(State s1, State s2) {
        buffer1.clear();
        buffer2.clear();
        
        // Fill buffers by traversing up (Order: Dest -> Source)
        State curr = s1;
        while(curr != null) { 
            buffer1.add(curr.currentHostId); 
            curr = curr.parent; 
        }
        
        curr = s2;
        while(curr != null) { 
            buffer2.add(curr.currentHostId); 
            curr = curr.parent; 
        }
        
        int size1 = buffer1.size();
        int size2 = buffer2.size();
        int minSize = Math.min(size1, size2);
        
        // Compare from the "end" of the buffers (which corresponds to Source -> Dest order)
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
        State curr = this;
        
        while (curr != null) {
            // addFirst is O(1) for LinkedList
            path.addFirst(curr.currentHostId);
            curr = curr.parent;
        }
        
        return path;
    }
}