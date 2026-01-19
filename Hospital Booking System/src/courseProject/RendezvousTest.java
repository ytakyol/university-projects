package courseProject;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

public class RendezvousTest {

	private Rendezvous rendezvous;
    private Date dateTime;
    private Doctor doctor;
    private Patient patient;

    @Before
    public void setUp() {
        
        dateTime = new Date();
        doctor = new Doctor("Walter White", 111L, 001, 10);
        patient = new Patient("Patient 0", 112L);
        rendezvous = new Rendezvous(dateTime, doctor, patient);
    }

    @Test
    public void testGetDateTime() {
        assertEquals(dateTime, rendezvous.getDateTime());
    }

    @Test
    public void testSetDateTime() {
        Date newDateTime = new Date();
        rendezvous.setDateTime(newDateTime);
        assertEquals(newDateTime, rendezvous.getDateTime());
    }

    @Test
    public void testGetDoctor() {
        assertEquals(doctor, rendezvous.getDoctor());
    }

    @Test
    public void testSetDoctor() {
        Doctor newDoctor = new Doctor("Walter White", 112L, 002, 10); // Mock new Doctor
        rendezvous.setDoctor(newDoctor);
        assertEquals(newDoctor, rendezvous.getDoctor());
    }

    @Test
    public void testGetPatient() {
        assertEquals(patient, rendezvous.getPatient());
    }

    @Test
    public void testSetPatient() {
        Patient newPatient = new Patient("Patient 1", 113L); // Mock new Patient
        rendezvous.setPatient(newPatient);
        assertEquals(newPatient, rendezvous.getPatient());
    }

}
