import java.util.ArrayList;

public class Platform {
	// HashMaps
	HashMap<String, SkillProfile> services;
	HashMap<String, Freelancer> freelancers;
	HashMap<String, Customer> customers;
	HashMap<String, PriorityQueue> servicePQs;
	HashMap<String, Boolean> blacklists;
	// Lists
	ArrayList<ServiceChangeRequest> scrThisMonth;
	// services
	String[] serviceTypes;

	Platform() {
		services = new HashMap<>();
		freelancers = new HashMap<>();
		customers = new HashMap<>();
		servicePQs = new HashMap<>();
		blacklists = new HashMap<>();

		scrThisMonth = new ArrayList<>();

		serviceTypes = new String[] { "paint", "web_dev", "graphic_design", "data_entry", "tutoring", "cleaning",
				"writing", "photography", "plumbing", "electrical" };

		createServices();
		createPriorityQueues();
	}

	void createServices() {
		// T C R E A
		// T R E C A
		SkillProfile paintSP = new SkillProfile(70, 60, 50, 85, 90);
		paintSP.setPriors(1, 0, 0, 1, 2);
		services.put("paint", paintSP);

		SkillProfile web_devSP = new SkillProfile(95, 75, 85, 80, 90);
		web_devSP.setPriors(2, 0, 1, 0, 1);
		services.put("web_dev", web_devSP);

		SkillProfile graphic_designSP = new SkillProfile(75, 85, 95, 70, 85);
		graphic_designSP.setPriors(0, 1, 2, 0, 1);
		services.put("graphic_design", graphic_designSP);

		SkillProfile data_entrySP = new SkillProfile(50, 50, 30, 95, 95);
		data_entrySP.setPriors(1, 0, 0, 2, 1);
		services.put("data_entry", data_entrySP);

		SkillProfile tutoringSP = new SkillProfile(80, 95, 70, 90, 75);
		tutoringSP.setPriors(1, 2, 0, 1, 0);
		services.put("tutoring", tutoringSP);

		SkillProfile cleaningSP = new SkillProfile(40, 60, 40, 90, 85);
		cleaningSP.setPriors(0, 1, 0, 2, 1);
		services.put("cleaning", cleaningSP);

		SkillProfile writingSP = new SkillProfile(70, 85, 90, 80, 95);
		writingSP.setPriors(0, 1, 1, 0, 2);
		services.put("writing", writingSP);

		SkillProfile photographySP = new SkillProfile(85, 80, 90, 75, 90);
		photographySP.setPriors(1, 0, 2, 0, 1);
		services.put("photography", photographySP);

		SkillProfile plumbingSP = new SkillProfile(85, 65, 60, 90, 85);
		plumbingSP.setPriors(1, 0, 0, 2, 1);
		services.put("plumbing", plumbingSP);

		SkillProfile electricalSP = new SkillProfile(90, 65, 70, 95, 95);
		electricalSP.setPriors(1, 0, 0, 2, 1);
		services.put("electrical", electricalSP);
	}

	void createPriorityQueues() {
		for (String service : serviceTypes) {
			servicePQs.put(service, new PriorityQueue());
		}
	}

	boolean doFExist(String id) {
		if (freelancers.get(id) == null) {
			return false;
		}
		return true;
	}

	boolean doCExist(String id) {
		if (customers.get(id) == null) {
			return false;
		}
		return true;
	}

	boolean isAService(String string) {
		boolean result = false;

		for (String service : serviceTypes) {
			if (service.compareTo(string) == 0)
				result = true;
		}

		return result;
	}

	boolean withInRange(int number) {
		if (0 <= number && number <= 100)
			return true;
		else
			return false;
	}

	boolean inBlacklist(String id) {
		Boolean val = blacklists.get(id);
		// Check if not null and true
		if (val != null && val) {
			return true;
		}
		return false;
	}

	boolean isUniqueID(String id) {
		if (freelancers.get(id) == null && customers.get(id) == null) {
			return true;
		} else {
			return false;
		}
	}

	public String registerCustomer(String customerID) {
		String output;

		if (isUniqueID(customerID)) {
			Customer customer = new Customer(customerID);
			customers.put(customerID, customer);
			output = "registered customer " + customerID;
		} else {
			output = "Some error occurred in register_customer.";
		}

		return output;
	}

	public String registerFreelancer(String freelancerID, String serviceName, int basePrice, int t, int c, int r, int e,
			int a) {

		String output;

		if (isUniqueID(freelancerID) && isAService(serviceName) && basePrice > 0 && withInRange(t) && withInRange(c)
				&& withInRange(r) && withInRange(e) && withInRange(a)) {
			SkillProfile skills = new SkillProfile(t, c, r, e, a);
			Freelancer freelancer = new Freelancer(skills, freelancerID, serviceName, services.get(serviceName),
					basePrice);

			freelancers.put(freelancerID, freelancer);
			servicePQs.get(serviceName).add(freelancer);

			output = "registered freelancer " + freelancerID;
		} else {
			output = "Some error occurred in register_freelancer.";
		}

		return output;
	}

	public String requestJob(String customerID, String serviceName, int topK) {

		if (!(isAService(serviceName) && doCExist(customerID))) {
			return "Some error occurred in request_job.";
		}

		PriorityQueue pq = servicePQs.get(serviceName);

		if (pq.isEmpty()) {
			return "no freelancers available";
		}

		ArrayList<Freelancer> topCandidates = new ArrayList<>();
		ArrayList<Freelancer> polledFreelancers = new ArrayList<>();
		Customer customer = customers.get(customerID);
		String result = "";

		while (topCandidates.size() < topK && !pq.isEmpty()) {

			Freelancer best = pq.poll();
			polledFreelancers.add(best);

			// TODO 2nd and 3rd checks will be deleted
			if (!customer.inBlackList(best.freelancerID)) {
				topCandidates.add(best);
			}
		}

		if (topCandidates.isEmpty()) {
			return "no freelancers available";
		}

		Freelancer topCandidate = topCandidates.get(0);

		result += "available freelancers for " + serviceName + " (top " + topCandidates.size() + "):" + "\n";
		for (Freelancer f : topCandidates) {
			String s = "";
			s += String.format("%s - composite: %d, price: %d, rating: %.1f\n", f.freelancerID, f.compositeScore,
					f.price, f.averageRating);

			result += s;
		}
		result += "auto-employed best freelancer: " + topCandidate.freelancerID + " for customer " + customerID;
		topCandidate.employer = customer;
		customer.totalEmployment++;

		for (Freelancer f : polledFreelancers) {
			if (f != topCandidate) {
				pq.add(f);
			}
		}

		return result;
	}

	public String employFreelancer(String customerID, String freelancerID) {

		String output;

		if (doFExist(freelancerID) && doCExist(customerID)) {
			Customer customer = customers.get(customerID);
			Freelancer freelancer = freelancers.get(freelancerID);

			if (!customer.inBlackList(freelancerID) && !freelancer.isEmployed() && !inBlacklist(freelancerID)) {

				freelancer.employer = customer;
				customer.totalEmployment++;
				servicePQs.get(freelancer.serviceType).remove(freelancer);

				output = customerID + " employed " + freelancerID + " for " + freelancer.serviceType;
			} else {
				output = "Some error occurred in employ.";
			}

		} else {
			output = "Some error occurred in employ.";
		}

		return output;
	}

	public String completeAndRate(String freelancerID, int rating) {

		if (!doFExist(freelancerID) || !(0 <= rating && rating <= 5)) {
			return "Some error occurred in complete_and_rate.";
		}

		Freelancer freelancer = freelancers.get(freelancerID);

		if (!freelancer.isEmployed()) {
			return "Some error occurred in complete_and_rate.";
		}

		Customer customer = freelancer.employer;

		String output = String.format("%s completed job for %s with rating %d", freelancerID, customer.customerID,
				rating);

		customer.finishEmployment(freelancer.price);
		freelancer.completeJob(rating);
		servicePQs.get(freelancer.serviceType).add(freelancer);

		return output;
	}

	public String cancelByFreelancer(String freelancerID) {

		if (!doFExist(freelancerID)) {
			return "Some error occurred in cancel_by_freelancer.";
		}

		Freelancer freelancer = freelancers.get(freelancerID);

		if (!freelancer.isEmployed()) {
			return "Some error occurred in cancel_by_freelancer.";
		}

		String output = String.format("cancelled by freelancer: %s cancelled %s", freelancerID,
				freelancer.employer.customerID);

		freelancer.cancelEmployement();

		if (freelancer.cancelledThisMonth == 5) {
			blacklists.put(freelancerID, true);
			output += "\n" + "platform banned freelancer: " + freelancerID;
		} else {
			servicePQs.get(freelancer.serviceType).add(freelancer);
		}

		return output;
	}

	public String cancelByCustomer(String customerID, String freelancerID) {

		if (!doCExist(customerID) || !doFExist(freelancerID)) {
			return "Some error occurred in cancel_by_customer.";
		}

		Customer customer = customers.get(customerID);
		Freelancer freelancer = freelancers.get(freelancerID);

		if (freelancer.employer != customer) {
			return "Some error occurred in cancel_by_customer.";
		}

		freelancer.employer = null;
		servicePQs.get(freelancer.serviceType).add(freelancer);

		customer.totalCancelledJobs++;

		return String.format("cancelled by customer: %s cancelled %s", customerID, freelancerID);

	}

	public String blacklist(String customerID, String freelancerID) {
		
		if(!doCExist(customerID) || !doFExist(freelancerID))
			return "Some error occurred in blacklist.";
	
		Customer customer = customers.get(customerID);
		
		if(customer.inBlackList(freelancerID))
			return "Some error occurred in blacklist.";
		
		customer.addToBlackList(freelancerID);
		
		return String.format("%s blacklisted %s", customerID, freelancerID);
	}

	public String unblacklist(String customerID, String freelancerID) {
		
		if(!doCExist(customerID) || !doFExist(freelancerID))
			return "Some error occurred in unblacklist.";
		
		Customer customer = customers.get(customerID);
		
		if(!customer.inBlackList(freelancerID))
			return "Some error occurred in unblacklist.";
		
		customer.removeFromBlackList(freelancerID);
		
		return String.format("%s unblacklisted %s", customerID, freelancerID);
	}

	public String changeService(String freelancerID, String newService, int newPrice) {

		if (!doFExist(freelancerID) || !isAService(newService) || newPrice <= 0) {
			return "Some error occurred in change_service.";
		}

		ServiceChangeRequest scr = new ServiceChangeRequest(freelancerID, newService, newPrice);
		scrThisMonth.add(scr);

		return String.format("service change for %s queued from %s to %s", freelancerID,
				freelancers.get(freelancerID).serviceType, newService);
	}

	public String simulateMonth() {
		
		// loyalty updates
		for(Customer customer : customers.values()) {
			customer.updateLoyaltyTier();
		}
		
		//Service Changes
		for(ServiceChangeRequest scr: scrThisMonth) {
			
			Freelancer freelancer = freelancers.get(scr.freelancerid);
			String service = scr.service;
			SkillProfile ssp = services.get(service);
			int price = scr.price;
			
			servicePQs.get(freelancer.serviceType).remove(freelancer);
			
			freelancer.setService(service, ssp);
			freelancer.price = price;
			
			freelancer.updateCompositeScore();
			
			if(!inBlacklist(scr.freelancerid))
				servicePQs.get(freelancer.serviceType).add(freelancer);
		}
		
		scrThisMonth.clear();
		
		//Freelancer updates
		for(Freelancer freelancer : freelancers.values()) {
			
			//burnouts
			if(freelancer.burnout) {
				if(freelancer.completedThisMonth <= 2) {
					freelancer.burnout = false;
				}
			}
			else {
				if(freelancer.completedThisMonth >= 5) {
					freelancer.burnout = true;
				}
			}
			
			//reset monthly encounters
			freelancer.resetMonthlyCounters();
			
			//update compositeScore 
			freelancer.updateCompositeScore();
			servicePQs.get(freelancer.serviceType).updateFreelancer(freelancer);
		}

		return "month complete";
	}

	public String queryFreelancer(String freelancerID) {

		if (!doFExist(freelancerID)) {
			return "Some error occurred in query_freelancer.";
		}

		Freelancer freelancer = freelancers.get(freelancerID);

		String service = freelancer.serviceType;
		int price = freelancer.price;
		double rating = freelancer.averageRating;
		int completed = freelancer.completedTotal;
		int cancelled = freelancer.cancelledTotal;
		int[] skills = freelancer.skills.skills;
		String avaliable;
		String burnout;

		if (freelancer.isEmployed() || inBlacklist(freelancerID))
			avaliable = "no";
		else
			avaliable = "yes";

		if (freelancer.burnout)
			burnout = "yes";
		else
			burnout = "no";

		String output = String.format(
				"%s: %s, price: %d, rating: %.1f, completed: %d, cancelled: %d, skills: (%d,%d,%d,%d,%d), available: %s, burnout: %s",
				freelancerID, service, price, rating, completed, cancelled, skills[0], skills[1], skills[2], skills[3],
				skills[4], avaliable, burnout);

		return output;
	}

	public String queryCustomer(String customerID) {
		
		if(!doCExist(customerID))
			return "Some error occurred in query_customer.";
		
		Customer customer = customers.get(customerID);
		
		int spent = customer.totalSpent;
		String tier = customer.loyaltyTier;
		int blCount = customer.blackListCount();
		int te = customer.totalEmployment;
		
		String output = String.format("%s: total spent: $%d, loyalty tier: %s, blacklisted freelancer count: %d, total employment count: %d", customerID, spent, tier, blCount, te);
		
		return output;
	}

	public String updateSkill(String freelancerID, int t, int c, int r, int e, int a) {
		
		if(!(doFExist(freelancerID) && withInRange(t) && withInRange(c) && withInRange(r) && withInRange(e) && withInRange(a))) {
			return "Some error occurred in update_skill.";
		}
		
		Freelancer freelancer = freelancers.get(freelancerID);
		
		freelancer.setSkills(t, c, r, e, a);
		
		freelancer.updateCompositeScore();
		
		servicePQs.get(freelancer.serviceType).updateFreelancer(freelancer);
		
		return String.format("updated skills of %s for %s", freelancerID, freelancer.serviceType);
	}

}
