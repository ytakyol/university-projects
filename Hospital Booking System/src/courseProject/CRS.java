package courseProject;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

public class CRS {

	private HashMap<Long, Patient> patients;
	private HashMap<Integer, Hospital> hospitals;
	private LinkedList<Rendezvous> rendezvous;

	public HashMap<Long, Patient> getPatients() {
		return patients;
	}

	public void setPatients(HashMap<Long, Patient> patients) {
		this.patients = patients;
	}

	public HashMap<Integer, Hospital> getHospitals() {
		return hospitals;
	}

	public void setHospitals(HashMap<Integer, Hospital> hospitals) {
		this.hospitals = hospitals;
	}

	public LinkedList<Rendezvous> getRendezvous() {
		return rendezvous;
	}

	public void setRendezvous(LinkedList<Rendezvous> rendezvous) {
		this.rendezvous = rendezvous;
	}

	public CRS(HashMap<Long, Patient> patients, HashMap<Integer, Hospital> hospitals,
			LinkedList<Rendezvous> rendezvous) {

		this.patients = patients;
		this.hospitals = hospitals;
		this.rendezvous = rendezvous;

	}

	public synchronized boolean makeRendezvous(long patientID, int hospitalID, int sectionID, int diplomaID, Date desiredDate)
			throws IDException {
		boolean made = false;

		try {
			//System.out.println("Making Rendezvous...");
			Patient patient = patients.get(patientID);
			//System.out.println("Found Patient");
			Hospital hospital = hospitals.get(hospitalID);
			//System.out.println("Found Hospital");
			Section section = hospital.getSection(sectionID);
			//System.out.println("Found Section");
			Doctor doctor = section.getDoctor(diplomaID);
			//System.out.println("Found Doctor");
			Schedule schedule = doctor.getSchedule();
			//System.out.println("Found Doctor's Schedule");

			if (schedule.addRendezvous(patient, desiredDate)) {
				made = true;
				rendezvous.add(schedule.getSessions().getLast());

				System.out.println("Rendezvous has been succesfully made. Details:" + "\nHospital: "
						+ hospital.getName() + "\nSection: " + section.getName() + "\n"
						+ schedule.getSessions().getLast().toString());
			}
		} catch (Exception e) {
			if(!Main.guiMode) {
				throw new IDException("An id can not be found.");
			}
		}
		return made;
	}

	public synchronized void saveTablesToDisk(String fullPath) {
		try (FileOutputStream fileOut = new FileOutputStream(fullPath);
				ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)) {

			
			objectOut.writeObject(patients);
			objectOut.writeObject(hospitals);
			objectOut.writeObject(rendezvous);

			System.out.println("HashMaps have been saved to " + fullPath + "\n");
		} catch (IOException e) {
			e.printStackTrace(); 
		}
	}

	@SuppressWarnings("unchecked")
	public synchronized void loadTablesToDisk(String fullPath) {
		try (FileInputStream fileIn = new FileInputStream(fullPath);
				ObjectInputStream objectIn = new ObjectInputStream(fileIn)) {

			patients = (HashMap<Long, Patient>) objectIn.readObject();
			hospitals = (HashMap<Integer, Hospital>) objectIn.readObject();
			rendezvous = (LinkedList<Rendezvous>) objectIn.readObject();

			System.out.println("HashMaps have been loaded from " + fullPath + "\n");

		} catch (FileNotFoundException e) {
			System.out.println("File not found: " + fullPath);
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace(); 
		}
	}

}
