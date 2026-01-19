
public class SkillProfile {
	
	int[] skills;
	int[] priors;

	public SkillProfile(int t, int c, int r, int e, int a) {

		skills = new int[] { t, c, r, e, a };
	}

	public double skillScore(SkillProfile s) {
		double up = 0;
		double down = 0;
		for (int i = 0; i < 5; i++) {
			up += s.skills[i]*skills[i];
		}
		for (int i = 0; i < 5; i++) {
			down += s.skills[i];
		}
		return up/(100*down);
	}

	public void skillGain(SkillProfile s) {
		for (int i = 0; i < 5; i++) {
			skills[i] = Math.min(100, skills[i] + s.priors[i]) ;
		}
	}

	public void skillLose() {
		for (int i = 0; i < 5; i++) {
			skills[i] = Math.max(skills[i] - 3, 0) ;
		}
	}
	
	public void setPriors(int t, int c, int r, int e, int a) {
		priors = new int[] { t, c, r, e, a };
	}
}
