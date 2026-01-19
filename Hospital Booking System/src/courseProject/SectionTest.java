package courseProject;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class SectionTest {

	private Section section;
    private Doctor doctor1;
    private Doctor doctor2;
    private int sectionId;
    private String sectionName;

    @Before
    public void setUp() {
        // Mock data
        sectionId = 1;
        sectionName = "Cardiology";
        section = new Section(sectionId, sectionName);

        // Create doctors with different diploma IDs
        doctor1 = new Doctor("Dr. House", 123456789L, 1111, 10);
        doctor2 = new Doctor("Dr. Home", 987654321L, 2222, 15);
    }

    @Test
    public void testAddDoctorSuccess() throws DuplicateInfoException {
        section.addDoctor(doctor1);  // Add first doctor
        section.addDoctor(doctor2);  // Add second doctor
        
        Doctor foundDoctor1 = section.getDoctor(1111);
        Doctor foundDoctor2 = section.getDoctor(2222);

        assertNotNull(foundDoctor1);  // Doctor 1 should be found
        assertNotNull(foundDoctor2);  // Doctor 2 should be found
        assertEquals(doctor1, foundDoctor1);
        assertEquals(doctor2, foundDoctor2);
    }

    @Test(expected = DuplicateInfoException.class)
    public void testAddDoctorDuplicate() throws DuplicateInfoException {
        section.addDoctor(doctor1);  // Add first doctor

        // Attempt to add the same doctor again, should throw DuplicateInfoException
        section.addDoctor(doctor1);
    }

    @Test
    public void testGetDoctorFound() throws DuplicateInfoException {
        section.addDoctor(doctor1);  // Add first doctor
        Doctor foundDoctor = section.getDoctor(1111);
        
        assertNotNull(foundDoctor);  // Doctor should be found
        assertEquals(doctor1, foundDoctor);
    }

    @Test
    public void testGetDoctorNotFound() {
        Doctor foundDoctor = section.getDoctor(9999);  // No doctor with this diploma ID
        assertNull(foundDoctor);  // Should return null
    }

    @Test
    public void testGetName() {
        assertEquals(sectionName, section.getName());
    }

    @Test
    public void testSetName() {
        String newName = "Neurology";
        section.setName(newName);
        assertEquals(newName, section.getName());
    }

    @Test
    public void testGetId() {
        assertEquals(sectionId, section.getId());
    }

}
