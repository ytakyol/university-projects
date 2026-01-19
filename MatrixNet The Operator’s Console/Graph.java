import java.util.ArrayList;
import java.util.LinkedList;

public class Graph {

	HashMap<String, Host> hosts;
    ArrayList<Host> hostList;
    
    
    int globalVisitedToken = 0;

    Graph() {
        hosts = new HashMap<>();
        hostList = new ArrayList<>();
    }

    public boolean addHost(String id, Host host) {
        if (!doExist(id)) {
            
            host.setIndex(hostList.size()); 
            hosts.put(id, host);
            hostList.add(host);
            return true;
        }
        return false;
    }

    
	public Host getHost(String id) { return hosts.get(id); }
    public ArrayList<Host> getAllHosts() { return hostList; }
    public int getSize() { return hosts.size(); }
    public boolean doExist(String id) { return hosts.get(id) != null; }

    public boolean removeHost(String id) {
        Host target = hosts.get(id);
        if (target == null) return false;
        for (Tunnel t : target.getConnections()) {
            t.getDestination().removeConnectionTo(id);
        }
        hosts.remove(id);
        hostList.remove(target);
        
        return true;
    }
    
    public boolean addEdge(String id1, String id2, int lat, int bw, int firewall) {
		Host h1 = hosts.get(id1);
		Host h2 = hosts.get(id2);
		if (h1 == null || h2 == null || h1.hasConnectionTo(id2) || id1.equals(id2)) return false;
		h1.addTunnel(new Tunnel(h2, lat, bw, firewall));
		h2.addTunnel(new Tunnel(h1, lat, bw, firewall));
		return true;
	}
    
    public Tunnel getTunnel(String id1, String id2) {
		Host h1 = hosts.get(id1);
		return (h1 == null) ? null : h1.getTunnelTo(id2);
	}

    // OPTIMIZATION: O(1) Reset
	private void resetVisited() {
        globalVisitedToken++;
    }
    
    // Helper to check visited status
    private boolean isVisited(Host h) {
        return h.visitedToken == globalVisitedToken;
    }
    
    private void markVisited(Host h) {
        h.visitedToken = globalVisitedToken;
    }
	
	public int countConnectedComponents() {
        ArrayList<Host> allHosts = getAllHosts();
        if (allHosts.size() <= 1) return 1;

        resetVisited(); // O(1)
        int componentCount = 0;

        for (int i = 0; i < allHosts.size(); i++) {
            Host startNode = allHosts.get(i);
            
            if (!isVisited(startNode) && startNode.isActive()) {
                componentCount++;
                bfs(startNode);
            }
        }
        return componentCount;
    }

    private void bfs(Host start) {
        LinkedList<Host> queue = new LinkedList<>();
        markVisited(start);
        queue.add(start);
        
        while (!queue.isEmpty()) {
            Host current = queue.poll();
            ArrayList<Tunnel> neighbors = current.getConnections();
            for (int i = 0; i < neighbors.size(); i++) {
                Tunnel t = neighbors.get(i);
                Host neighbor = t.getDestination();
                if (!t.isSealed() && !isVisited(neighbor) && neighbor.isActive()) {
                    markVisited(neighbor);
                    queue.add(neighbor);
                }
            }
        }
    }
    
    
    public boolean isBridge(String id1, String id2) {
        Host u = getHost(id1);
        Host v = getHost(id2);
        if(u == null || v == null) return false;
        
        // Localized BFS
        resetVisited();
        LinkedList<Host> q = new LinkedList<>();
        
        markVisited(u);
        q.add(u);
        
        while(!q.isEmpty()){
            Host curr = q.poll();
            if(curr == v) return false; // Still connected -> Not a bridge
            
            for(Tunnel t : curr.getConnections()){
             
                if(!t.isSealed()){
                    Host n = t.getDestination();
                    if(!isVisited(n) && n.isActive()){
                        markVisited(n);
                        q.add(n);
                    }
                }
            }
        }
        return true; // Connection lost -> It is a bridge
    }

    // Cycle detection needs to use the token system too
    public boolean containsCycle() {
        resetVisited();
        for (Host h : getAllHosts()) {
            if (!isVisited(h)) {
                if (dfsCycle(h, null)) return true;
            }
        }
        return false;
    }

    private boolean dfsCycle(Host startNode, Host dummyParent) {
        LinkedList<Host> stack = new LinkedList<>();
        LinkedList<Host> parentStack = new LinkedList<>();

        stack.push(startNode);
        parentStack.push(dummyParent);

        while (!stack.isEmpty()) {
            Host current = stack.pop();
            Host parent = parentStack.pop();

            if (isVisited(current)) continue;
            markVisited(current);

            for (Tunnel t : current.getConnections()) {
                Host neighbor = t.getDestination();
                if (t.isSealed()) continue; 

                if (!isVisited(neighbor)) {
                    stack.push(neighbor);
                    parentStack.push(current);
                } 
                else if (neighbor != parent) {
                    return true;
                }
            }
        }
        return false;
    }
}