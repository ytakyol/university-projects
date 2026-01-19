package courseProject;


public class Doctor extends Person {
	
	
	private static final long serialVersionUID = 1L;
	private final int diploma_id;
	private Schedule schedule;
	
	public Doctor(String name, long national_id, int diploma, int maxPatientPerDay) {
		super(name, national_id);
		diploma_id = diploma;
		schedule = new Schedule(maxPatientPerDay, this);
	}
	
	public Schedule getSchedule()
	{
		return schedule;
	}

	@Override
	public String toString() {
		return getName();
	}

	public int getDiploma_id() {
		return diploma_id;
	}
	
	
}
