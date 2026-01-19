public class Tunnel {
	
	Host neighbor;
	
	int latency;	//PN
	int bandwidth;	//PN
	int firewall;	//1-5
	boolean isSealed;
	
	public Tunnel(Host neighbor, int latency, int bandwidth, int firewall) {
		super();
		this.neighbor = neighbor;
		this.latency = latency;
		this.bandwidth = bandwidth;
		this.firewall = firewall;
		isSealed = false;
	}	
	
	public Host getDestination() {
		
		return neighbor;
	}

    public int getLatency() {
        return latency;
    }

    public int getBandwidth() {
        return bandwidth;
    }

    public int getFirewallLevel() {
        return firewall;
    }

    public boolean isSealed() {
        return isSealed;
    }

    public void setSealed(boolean sealed) {
        this.isSealed = sealed;
    }
}
