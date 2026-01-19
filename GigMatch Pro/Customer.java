
public class Customer {

	String customerID;

	int totalSpent;
	int totalEmployment;
	int totalCancelledJobs;

	int loyaltyPoints; // loyaltyPoints = max(0, totalSpent - (cancelledJobsTotal × 250))
	String loyaltyTier;
	double subsidy;

	HashMap<String, Boolean> blacklist;

	public Customer(String customerID) {
		this.customerID = customerID;
		blacklist = new HashMap<>();
		updateLoyaltyTier();
	}

	public void updateLoyaltyPoints() {
		loyaltyPoints = Math.max(0, totalSpent - (totalCancelledJobs * 250));
	}

	public void updateLoyaltyTier() {
		updateLoyaltyPoints();
		if (0 <= loyaltyPoints && loyaltyPoints <= 499) {
			loyaltyTier = "BRONZE";
			subsidy = 0.0;
		} else if (500 <= loyaltyPoints && loyaltyPoints <= 1999) {
			loyaltyTier = "SILVER";
			subsidy = 0.05;
		} else if (2000 <= loyaltyPoints && loyaltyPoints <= 4999) {
			loyaltyTier = "GOLD";
			subsidy = 0.1;
		} else if (5000 <= loyaltyPoints) {
			loyaltyTier = "PLATINUM";
			subsidy = 0.15;
		}
	}

	public void finishEmployment(int freelancerPrice) {
		totalSpent += (int) (freelancerPrice * (1.0-subsidy));
	}

	public int calcPayment(int money) {
		return (int) (money * (1.0 - subsidy));
	}

	public void addToBlackList(String id) {
		blacklist.put(id, true);
	}

	public boolean removeFromBlackList(String id) {
		return blacklist.remove(id);
	}

	public boolean inBlackList(String id) {
		Boolean val = blacklist.get(id);
		// Check if not null and true
		if (val != null && val) {
			return true;
		}
		return false;
	}
	
	public int blackListCount() {
		return blacklist.size();
	}
}
