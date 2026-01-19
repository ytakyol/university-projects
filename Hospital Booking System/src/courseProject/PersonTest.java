package courseProject;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class PersonTest {

	private Person person;

    @Before
    public void setUp() {
        // Initialize a new Person object before each test
        person = new Person("Walter White", 12312432L);
    }

    // Test constructor and getters
    @Test
    public void testPersonConstructorAndGetters() {
        assertEquals("Walter White", person.getName());
        assertEquals(12312432L, person.getNational_id());
    }

    // Test setters
    @Test
    public void testSetName() {
        person.setName("Jane Doe");
        assertEquals("Jane Doe", person.getName());
    }
    
    @Test
    public void testToString() {
    	String beklenen = "Person [name=" + "Walter White" + ", national_id=" + 12312432L + "]";
    	String sonuc = person.toString();
    	assertEquals(beklenen,sonuc);
    }
}
