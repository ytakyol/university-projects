import java.math.BigDecimal;
import java.math.RoundingMode;

public class Manager {
	
	Graph graph;
	
	Manager(){
		graph = new Graph();
	}
	

	public String spawnHost(String hostId, int clearance) {
		// TODO Auto-generated method stub
		
		if (!hostId.matches("^[a-zA-Z0-9_]+$")) {
	        return "Some error occurred in spawn_host.";
	    }
		
		Host host = new Host(hostId, clearance);
		if(graph.addHost(hostId, host)) {
			return String.format("Spawned host %s with clearance level %d.", hostId, clearance);
		}
		return String.format("Some error occurred in spawn_host.");
	}

	public String linkBackdoor(String hostId1, String hostId2, int latency, int bandwidth, int firewall) {
		// TODO Auto-generated method stub
		
		if(graph.addEdge(hostId1, hostId2, latency, bandwidth, firewall))
			return String.format("Linked %s <-> %s with latency %dms, bandwidth %dMbps, firewall %d.", hostId1, hostId2, latency, bandwidth, firewall);
		
		return "Some error occurred in link_backdoor.";
	}

	public String sealBackdoor(String hostId1, String hostId2) {
		// TODO Auto-generated method stub
		
		Tunnel tunnel1 = graph.getTunnel(hostId1, hostId2);
		Tunnel tunnel2 = graph.getTunnel(hostId2, hostId1);
		
		if(tunnel1 == null || tunnel2 == null) {
			return "Some error occurred in seal_backdoor.";
		}
		
		if(tunnel1.isSealed) {
			tunnel1.setSealed(false);
			tunnel2.setSealed(false);
			return String.format("Backdoor %s <-> %s unsealed.", hostId1, hostId2);
		}
		else {
			tunnel1.setSealed(true);
			tunnel2.setSealed(true);
			return String.format("Backdoor %s <-> %s sealed.", hostId1, hostId2);
		}
		
	}

	public String traceRoute(String sourceId, String destId, int minBandwidth, int lambda) {
		// TODO Auto-generated method stub

		if(lambda == 0) {		
//			Dijkstra dijkstra = new Dijkstra(graph);
//			return dijkstra.traceRoute(sourceId, destId, minBandwidth);
			RouteFinder routeFinder = new RouteFinder(graph);
			return routeFinder.traceRoute(sourceId, destId, minBandwidth, lambda);
		}
		else {
			RouteFinder routeFinder = new RouteFinder(graph);
			return routeFinder.traceRoute(sourceId, destId, minBandwidth, lambda);
		}
		
	}

	public String scanConnectivity() {
		int components = graph.countConnectedComponents();

		if (components <= 1) {
		    // Log success: "Network is fully connected."
			return "Network is fully connected.";
		} else {
		    // Log failure: "Network has " + components + " disconnected components."
			return String.format("Network has %d disconnected components.", components);
		}
	}

	public String simulateBreach(String hostId1, String hostId2) {
	    Tunnel t1 = graph.getTunnel(hostId1, hostId2);
	    Tunnel t2 = graph.getTunnel(hostId2, hostId1);

	    if (t1 == null || t2 == null || t1.isSealed()) {
	        return "Some error occurred in simulate_breach.";
	    }

	    // 1. Get baseline (Required to print the number of components)
	    int baseline = graph.countConnectedComponents();

	    // 2. Temporarily Seal
	    t1.setSealed(true);
	    t2.setSealed(true);

	    // 3. Optimized Check: Is it a bridge?
	    // We do NOT run a full scan. We check if connectivity is lost.
	    boolean isBridge = graph.isBridge(hostId1, hostId2);

	    // 4. Restore
	    t1.setSealed(false);
	    t2.setSealed(false);

	    // 5. Output
	    if (isBridge) {
	        // If it was a bridge, components increased by exactly 1
	        return String.format("Backdoor %s <-> %s IS a bridge.\nFailure results in %d disconnected components.", hostId1, hostId2, baseline + 1);
	    } else {
	        return String.format("Backdoor %s <-> %s is NOT a bridge. Network remains the same.", hostId1, hostId2);
	    }
	}

	public String simulateBreach(String hostId) {
		// TODO Auto-generated method stub
		Host host = graph.getHost(hostId);
	    if (host == null) {
	        return "Some error occurred in simulate_breach.";
	    }
	    
	    int baseline = graph.countConnectedComponents();
	    
	    host.setActive(false);

	    // 3. Scan network
	    int components = graph.countConnectedComponents();

	    // 4. Restore the host (CRITICAL)
	    host.setActive(true);
	    
	    if (components > baseline) {
	        return "Host "+hostId+" IS an articulation point.\nFailure results in "+components+" disconnected components.";
	    } else {
	        return "Host "+hostId+" is NOT an articulation point. Network remains the same.";
	    }

	}

	public String oracleReport() {
		// TODO Auto-generated method stub
		int totalHosts = 0;
	    int totalUnsealed = 0;
	    double totalBandwidth = 0;
	    double totalClearance = 0;
	    
	    // You need a way to iterate unique edges. 
	    // Since edges are bidirectional (A->B and B->A), we can sum all and divide by 2.
	    
	    for (Host h : graph.hosts.values()) {
	        totalHosts++;
	        totalClearance += h.getClearance();
	        
	        for (Tunnel t : h.getConnections()) {
	            if (!t.isSealed()) {
	                totalUnsealed++;
	                totalBandwidth += t.getBandwidth();
	            }
	        }
	    }

	    // Adjust for bidirectional duplication
	    int uniqueBackdoors = totalUnsealed / 2;
	    double avgBandwidth = (uniqueBackdoors == 0) ? 0 : (totalBandwidth / 2.0) / uniqueBackdoors;
	    double avgClearance = (totalHosts == 0) ? 0 : totalClearance / totalHosts;

	    // 2. Connectivity & Cycles
	    int components = graph.countConnectedComponents();
	    boolean hasCycle = graph.containsCycle();
	    
	    // 3. Rounding 
	    BigDecimal bwBD = new BigDecimal(avgBandwidth).setScale(1, RoundingMode.HALF_UP);
	    BigDecimal clBD = new BigDecimal(avgClearance).setScale(1, RoundingMode.HALF_UP);

	    // 4. Construct Output
	    StringBuilder sb = new StringBuilder();
	    sb.append("--- Resistance Network Report ---\n");
	    sb.append("Total Hosts: ").append(totalHosts).append("\n");
	    sb.append("Total Unsealed Backdoors: ").append(uniqueBackdoors).append("\n");
	    sb.append("Network Connectivity: ").append(components == 1 ? "Connected" : "Disconnected").append("\n");
	    sb.append("Connected Components: ").append(components).append("\n");
	    sb.append("Contains Cycles: ").append(hasCycle ? "Yes" : "No").append("\n");
	    sb.append("Average Bandwidth: ").append(bwBD).append("Mbps\n");
	    sb.append("Average Clearance Level: ").append(clBD);
	    
	    return sb.toString();
	}

}
