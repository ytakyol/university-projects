package courseProject;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class HospitalTest {

	 	private Hospital hospital;
	    private Section section1;
	    private Section section2;
	    private int hospitalId;
	    private String hospitalName;

	    @Before
	    public void setUp() {
	        // Mock data
	        hospitalId = 101;
	        hospitalName = "İstanbul Şehir Hastanesi";
	        hospital = new Hospital(hospitalId, hospitalName);

	        // Create sections with different IDs
	        section1 = new Section(1, "Cardiology");
	        section2 = new Section(2, "Neurology");
	    }

	    @Test
	    public void testAddSectionSuccess() throws DuplicateInfoException {
	        hospital.addSection(section1);  // Add first section
	        hospital.addSection(section2);  // Add second section

	        Section foundSection1 = hospital.getSection(1);
	        Section foundSection2 = hospital.getSection(2);

	        assertNotNull(foundSection1);  // Section 1 should be found
	        assertNotNull(foundSection2);  // Section 2 should be found
	        assertEquals(section1, foundSection1);
	        assertEquals(section2, foundSection2);
	    }

	    @Test(expected = DuplicateInfoException.class)
	    public void testAddSectionDuplicate() throws DuplicateInfoException {
	        hospital.addSection(section1);  // Add first section

	        // Attempt to add the same section again (by name), should throw DuplicateInfoException
	        hospital.addSection(section1);
	    }

	    @Test
	    public void testGetSectionByIdFound() throws DuplicateInfoException {
	        hospital.addSection(section1);  // Add section
	        Section foundSection = hospital.getSection(1);

	        assertNotNull(foundSection);  // Section should be found
	        assertEquals(section1, foundSection);
	    }

	    @Test
	    public void testGetSectionByIdNotFound() {
	        Section foundSection = hospital.getSection(999);  // No section with this ID
	        assertNull(foundSection);  // Should return null
	    }

	    @Test
	    public void testGetName() {
	        assertEquals(hospitalName, hospital.getName());
	    }

	    @Test
	    public void testSetName() {
	        String newName = "City Hospital";
	        hospital.setName(newName);
	        assertEquals(newName, hospital.getName());
	    }

	    @Test
	    public void testGetId() {
	        assertEquals(hospitalId, hospital.getId());
	    }

}
