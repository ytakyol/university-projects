package courseProject;

import java.io.Serializable;
import java.util.LinkedList;

public class Section implements Serializable{

	@SuppressWarnings("unused")
	private static final long serialVersionUID = 9L;
	
	private final int id;
	private String name;
	private LinkedList<Doctor> doctors;
	
	public Section(int id, String name) {
		
		this.id = id;
		this.name = name;
		doctors = new LinkedList<>();
	}
	
	public void listDoctors()
	{
		System.out.println("All of the doctors for section "+ name + ":\n");
		for(Doctor doctor : doctors) {
			System.out.println(doctor);
			System.out.println("\n");
		}
	}
	
	public Doctor getDoctor(int diploma_id) {
		Doctor found = null;
		
		for(Doctor doctor : doctors) {
			if(doctor.getDiploma_id() == diploma_id) {
				found = doctor;
			}
		}
		
		return found;
	}
	
	public void addDoctor(Doctor doctor) throws DuplicateInfoException {
		
		if(getDoctor(doctor.getDiploma_id()) == null) {
			doctors.add(doctor);
		}
		else {
			if(!Main.guiMode) {
				throw new DuplicateInfoException("Doctor already exists in the section "+name);
			}
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public LinkedList<Doctor> getDoctors() {
		return doctors;
	}

	public void setDoctors(LinkedList<Doctor> doctors) {
		this.doctors = doctors;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
}
