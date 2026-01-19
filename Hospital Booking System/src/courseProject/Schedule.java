package courseProject;

import java.io.Serializable;
import java.util.Date;
import java.util.Calendar;
import java.util.LinkedList;

public class Schedule implements Serializable{
	
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 8L;
	
	private LinkedList<Rendezvous> sessions;
	private Doctor doctor;
	private int maxPatientPerDay;
	
	public Schedule(int maxPatientPerDay, Doctor doctor) {
		
		this.maxPatientPerDay = maxPatientPerDay;
		this.doctor = doctor;
		sessions = new LinkedList<>();
		
	}
	
	public boolean addRendezvous(Patient p, Date desired)
	{
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		int totalRend = 0;
		
		cal1.setTime(desired);
		
		for(Rendezvous rend : sessions)
		{
			cal2.setTime(rend.getDateTime());
			
			if(cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
		               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)) {
				totalRend++;
			}
		}
		
		if(totalRend < maxPatientPerDay)
		{
			sessions.add(new Rendezvous(desired, getDoctor(), p));
			System.out.println(doctor.getName() +"'s total rendezvous that day (after this one): " + (totalRend+1) + "/" +  maxPatientPerDay + "\n");
			return true;
		}
		else {
			System.out.println(doctor.getName() +" is full that day: " + totalRend + "/" +  maxPatientPerDay + "\n");
			return false;
		}
		
		
	}
	public Doctor getDoctor() {
		return doctor;
	}
	public void setDoctor(Doctor doctor) {
		this.doctor = doctor;
	}
	public int getMaxPatientPerDay() {
		return maxPatientPerDay;
	}
	public void setMaxPatientPerDay(int maxPatientPerDay) {
		this.maxPatientPerDay = maxPatientPerDay;
	}
	public LinkedList<Rendezvous> getSessions() {
		return sessions;
	}

}
