package courseProject;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class Rendezvous implements Serializable {
	
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 7L;
	
	private Date dateTime;
	private Doctor doctor;
	private Patient patient;
	public Rendezvous(Date dateTime, Doctor doctor, Patient patient) {
		
		this.dateTime = dateTime;
		this.doctor = doctor;
		this.patient = patient;
	}
	public Date getDateTime() {
		return dateTime;
	}
	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}
	public Doctor getDoctor() {
		return doctor;
	}
	public void setDoctor(Doctor doctor) {
		this.doctor = doctor;
	}
	public Patient getPatient() {
		return patient;
	}
	public void setPatient(Patient patient) {
		this.patient = patient;
	}
	
	@Override
	public String toString()
	{
		String text = "";
		
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(dateTime);
		
		text += "Doctor = " + doctor.getName() + "\n";
		text += "Patient = " + patient.getName() + "\n";
		text += "Date = " + cal1.get(Calendar.DAY_OF_MONTH) + "/" + cal1.get(Calendar.MONTH+1) + "/" + cal1.get(Calendar.YEAR) +" "+cal1.get(Calendar.HOUR_OF_DAY)+":"+cal1.get(Calendar.MINUTE) +"\n";
		
		//text += "\n";
		
		return text;
	}
	

}
