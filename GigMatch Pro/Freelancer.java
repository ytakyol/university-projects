
public class Freelancer {

	public static double w_s = 0.55, w_r = 0.25, w_l = 0.20;

	SkillProfile skills;
	int compositeScore;

	String freelancerID;
	String serviceType;
	SkillProfile serviceSkill;
	int price;

	int cancelledTotal;
	int completedTotal;
	
	int cancelledThisMonth;
	int completedThisMonth;

	Customer employer;
	boolean burnout;

	double averageRating; // 5 at first

	public Freelancer(SkillProfile skills, String freelancerID, String serviceType, SkillProfile serviceSkill, int price) {

		this.skills = skills;
		this.freelancerID = freelancerID;
		this.serviceType = serviceType;
		this.price = price;
		this.serviceSkill = serviceSkill;
		
		employer = null;
		burnout = false;

		averageRating = 5;
		
		updateCompositeScore();
	}

	public boolean isPriorTo(Freelancer rival) {

		if (this.compositeScore > rival.compositeScore) {
			return true;
		} else if (this.compositeScore < rival.compositeScore) {
			return false;
		} else {

			int result = this.freelancerID.compareTo(rival.freelancerID);

			if (result < 0) {
				return true;
			} else if (result > 0) {
				return false;
			} else {
				System.out.println("a and b are equal");
				return false;
			}
		}
	}

	public void setSkills(SkillProfile skills) {

		this.skills = skills;
		updateCompositeScore();
	}

	public void setSkills(int t, int c, int r, int e, int a) {

		this.skills = new SkillProfile(t, c, r, e, a);
		updateCompositeScore();
	}

	public int updateCompositeScore() {
		double calc;
		double burnoutPenalty;
		if(burnout) {
			burnoutPenalty = 0.45;
		}
		else {
			burnoutPenalty = 0;
		}
		
		calc = 10000*(w_s*getSkillScore()+w_r*getRatingScore()+w_l*getReliabilityScore()-burnoutPenalty);
		
		compositeScore = (int) calc;
		
		return compositeScore;
	}
	
	public double getReliabilityScore() {
		if(completedTotal+cancelledTotal == 0) {
			return 1;
		}
		else {
			return (1.0-(cancelledTotal/(double)(completedTotal+cancelledTotal)));
		}
	}
	
	public double getRatingScore() {
		return (averageRating/5.0);
	}
	
	public double getSkillScore() {
		return skills.skillScore(serviceSkill);
	}
	
	public void setService(String service, SkillProfile serviceSkill) {
		this.serviceType = service;
		this.serviceSkill = serviceSkill;
	}
	
	public void addReview(int star) {
		
		int total = completedTotal + cancelledTotal;
		
		averageRating = (averageRating*total + star)/(double)(total+1);
		
	}
	
	public void cancelEmployement() {
		cancelledThisMonth++;
		cancelledTotal++;
		addReview(0);
		employer = null;
		skills.skillLose();
		updateCompositeScore();
	}
	
	public void completeJob(int rating) {
		completedTotal++;
		completedThisMonth++;
		addReview(rating);
		employer = null;
		if(rating == 4 || rating == 5) {
			skills.skillGain(serviceSkill);
		}
		updateCompositeScore();
	}
	
	public boolean isEmployed() {
		if(employer != null) {
			return true;
		}
		return false;
	}
	
	public void resetMonthlyCounters() {
		cancelledThisMonth = 0;
		completedThisMonth = 0;
	}
}
