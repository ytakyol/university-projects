import java.util.ArrayList;

public class Host {
	
	String id;
	int clearance;
	
    
    int index; 
    
    int visitedToken = -1;
    boolean active = true; 
	
	ArrayList<Tunnel> adjList;
	HashMap<String, Tunnel> tunnelMap;
	
	Host(String id, int clear){
		this.id = id;
		clearance = clear;
		adjList = new ArrayList<>();
		tunnelMap = new HashMap<>();
        
	}

    public void setIndex(int i) {
        this.index = i;
    }
    
    public int getIndex() {
        return index;
    }
	
    
    public void addTunnel(Tunnel tunnel) {
	    adjList.add(tunnel);
	    tunnelMap.put(tunnel.getDestination().getId(), tunnel);
	}
	
	public ArrayList<Tunnel> getConnections(){
		return adjList;
	}
    
    public void removeConnectionTo(String neighborId) {
        for (int i = 0; i < adjList.size(); i++) {
            if (adjList.get(i).getDestination().id.equals(neighborId)) {
            	adjList.remove(i);
            	tunnelMap.remove(neighborId);
                return;
            }
        }
    }
	
	public Tunnel getTunnelTo(String id){
	    return tunnelMap.get(id);
	}
	
	public boolean hasConnectionTo(String id){
	    return tunnelMap.get(id) != null;
	}
	
	public String getId() { return id; }
	public int getClearance() { return clearance; }
	public boolean isActive() { return active; }
	public void setActive(boolean status) { this.active = status; }
}