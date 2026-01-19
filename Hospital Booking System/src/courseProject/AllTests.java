package courseProject;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ CRSTest.class, DoctorTest.class, HospitalTest.class, PersonTest.class, RendezvousTest.class,
		ScheduleTest.class, SectionTest.class })
public class AllTests {

}
