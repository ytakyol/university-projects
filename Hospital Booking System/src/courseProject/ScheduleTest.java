package courseProject;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

public class ScheduleTest {

	private Schedule schedule;
    private Doctor doctor;
    private Patient patient;
    private int maxPatientPerDay;

    @Before
    public void setUp() {
        // Mock data
    	maxPatientPerDay = 10;
        doctor = new Doctor("Dr. House", 123456789L, 12345, maxPatientPerDay);
        patient = new Patient("Patient 0", 987654321L);  
        

        // Initialize Schedule
        schedule = doctor.getSchedule();
    }

    @Test
    public void testAddRendezvousSuccess() {
        Date desiredDate = new Date();  // Current date
        boolean added = schedule.addRendezvous(patient, desiredDate);
        
        assertTrue(added);  // Assert that the rendezvous was successfully added
        LinkedList<Rendezvous> sessions = schedule.getSessions();
        assertEquals(1, sessions.size());  // Verify session count
        assertEquals(desiredDate, sessions.get(0).getDateTime());  // Verify date of added session
        assertEquals(patient, sessions.get(0).getPatient());  // Verify patient
        assertEquals(doctor, sessions.get(0).getDoctor());  // Verify doctor
    }

    @Test
    public void testAddRendezvousExceedMax() {
        // Add maxPatientPerDay rendezvous for the same day
        Date desiredDate = new Date();  // Current date
        
        for (int i = 0; i < maxPatientPerDay; i++) {
            assertTrue(schedule.addRendezvous(new Patient("Patient " + i, i), desiredDate));
        }

        // Now attempt to add one more for the same day, which should fail
        boolean added = schedule.addRendezvous(patient, desiredDate);
        assertFalse(added);
    }

    @Test
    public void testAddRendezvousDifferentDays() {
        // Add one rendezvous for today
        Date today = new Date();
        boolean addedToday = schedule.addRendezvous(patient, today);
        assertTrue(addedToday);

        // Add another rendezvous for tomorrow, should succeed since it's a different day
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.add(Calendar.DAY_OF_YEAR, 1);  // Set to tomorrow's date
        Date tomorrow = calendar.getTime();
        boolean addedTomorrow = schedule.addRendezvous(patient, tomorrow);
        
        assertTrue(addedTomorrow);
        assertEquals(2, schedule.getSessions().size());  // There should be 2 sessions total
    }

    @Test
    public void testGetDoctor() {
        assertEquals(doctor, schedule.getDoctor());
    }

    @Test
    public void testSetDoctor() {
        Doctor newDoctor = new Doctor("Dr. Jane Doe", 234567890L, 54321, 15);
        schedule.setDoctor(newDoctor);
        assertEquals(newDoctor, schedule.getDoctor());
    }

    @Test
    public void testGetMaxPatientPerDay() {
        assertEquals(maxPatientPerDay, schedule.getMaxPatientPerDay());
    }

    @Test
    public void testSetMaxPatientPerDay() {
        int newMax = 10;
        schedule.setMaxPatientPerDay(newMax);
        assertEquals(newMax, schedule.getMaxPatientPerDay());
    }

    @Test
    public void testGetSessions() {
        assertNotNull(schedule.getSessions());
        assertEquals(0, schedule.getSessions().size());  // Initially should be empty
    }

}
