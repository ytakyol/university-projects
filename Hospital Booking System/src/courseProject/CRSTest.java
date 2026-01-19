package courseProject;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import org.junit.Before;
import org.junit.Test;

public class CRSTest {

	private CRS crs;
    private HashMap<Long, Patient> patients;
    private HashMap<Integer, Hospital> hospitals;
    private LinkedList<Rendezvous> rendezvous;

    @Before
    public void setUp() {
        // Initialize the HashMaps and LinkedList for the test
        patients = new HashMap<>();
        hospitals = new HashMap<>();
        rendezvous = new LinkedList<>();

        // Adding sample data
        Patient patient = new Patient("John Smith", 987654321L);
        Hospital hospital = new Hospital(1, "City Hospital");
        Section section = new Section(1, "Cardiology");
        Doctor doctor = new Doctor("Dr. Jones", 123456789L, 456, 10);
        section.addDoctor(doctor);
        hospital.addSection(section);

        patients.put(987654321L, patient);
        hospitals.put(1, hospital);

        // Create the CRS instance
        crs = new CRS(patients, hospitals, rendezvous);
    }

    @Test
    public void testMakeRendezvous() throws IDException {
        Date desiredDate = new Date();

        // Make a rendezvous
        boolean result = crs.makeRendezvous(987654321L, 1, 1, 456, desiredDate);

        // Verify that the rendezvous was successfully made
        assertTrue(result);
        assertEquals(1, rendezvous.size());
    }

    @Test
    public void testMakeRendezvousWithInvalidID() {
        Date desiredDate = new Date();

        // Test with an invalid patient ID
        try {
            crs.makeRendezvous(111111111L, 1, 1, 456, desiredDate);
            fail("Expected an IDException to be thrown");
        } catch (IDException e) {
            assertEquals("An id can not be found.", e.getMessage());
        }
    }

    @Test
    public void testSaveAndLoadTablesToDisk() {
        String filePath = "crs_test_output.ser";
        
        Date desiredDate = new Date();

        // Make a rendezvous
        crs.makeRendezvous(987654321L, 1, 1, 456, desiredDate);
        
        // Save data to disk
        crs.saveTablesToDisk(filePath);

        // Create a new CRS instance and load data from the saved file
        CRS loadedCRS = new CRS(new HashMap<>(), new HashMap<>(), new LinkedList<>());
        loadedCRS.loadTablesToDisk(filePath);

        // Assert that the loaded data is the same as the saved data
        assertEquals(patients.get(987654321L).getNational_id(), loadedCRS.getPatients().get(987654321L).getNational_id());
        assertEquals(hospitals.get(1).getId(), loadedCRS.getHospitals().get(1).getId());
        
        assertEquals(rendezvous.get(0).getDateTime(), loadedCRS.getRendezvous().get(0).getDateTime());
        assertEquals(rendezvous.get(0).getDoctor().getDiploma_id(), loadedCRS.getRendezvous().get(0).getDoctor().getDiploma_id());
        assertEquals(rendezvous.get(0).getPatient().getNational_id(), loadedCRS.getRendezvous().get(0).getPatient().getNational_id());

        // Cleanup test file
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
    }

}
