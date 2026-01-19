package courseProject;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class DoctorTest {

	private Doctor doctor;
    private String name;
    private long nationalId;
    private int diplomaId;
    private int maxPatientPerDay;
    
    @Before
    public void setUp() {
        // Mock data
        name = "Dr. House";
        nationalId = 123456789L;
        diplomaId = 12345;
        maxPatientPerDay = 20;

        // Initialize Doctor
        doctor = new Doctor(name, nationalId, diplomaId, maxPatientPerDay);
    }

    @Test
    public void testGetSchedule() {
        Schedule schedule = doctor.getSchedule();
        assertNotNull(schedule);
        assertEquals(maxPatientPerDay, schedule.getMaxPatientPerDay());
        assertEquals(doctor, schedule.getDoctor());
    }

    @Test
    public void testToString() {
        String expectedString = doctor.getName();
        assertEquals(expectedString, doctor.toString());
    }

    @Test
    public void testGetDiplomaId() {
        assertEquals(diplomaId, doctor.getDiploma_id());
    }

}
