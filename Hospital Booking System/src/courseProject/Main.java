package courseProject;

import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;
import java.util.LinkedList;

public class Main {
	
	
	
	public static boolean guiMode = false;
	public static String saveNameForGUI = "save1.ser";
	public static String saveNameForConsole = "save2.ser";
	
	public static void main(String[] args)
	{
		Scanner scanner = new Scanner(System.in); 
    
        System.out.print("Do you want to enter gui mode? (y/n): ");
        String name = scanner.nextLine(); 
        
        scanner.close();
        
        if(name.equals("y"))
        {
        	guiMode = true;
        }
        else if (name.equals("n")) {
        	guiMode = false;
        }
        else
        {
        	System.out.println("please enter a valid statement.");
        	System.exit(0);
        }
        
        ///////////////////////////////////////////////////////////////////////////
        
        Hospital hospital1 = new Hospital(1, "İstanbul Hospital");
    	
    	Section hospital1Section1 = new Section(1, "Dermatology");
    	Section hospital1Section2 = new Section(2, "Cardiology");
    	Section hospital1Section3 = new Section(3, "Oncology");
    	
    	Doctor doctor1 = new Doctor("Dr. Ahmet", 11111111111L, 287410, 5);
    	Doctor doctor2 = new Doctor("Dr. Mehmet", 11111111112L, 287411, 6);
    	Doctor doctor3 = new Doctor("Dr. Ali", 11111111113L, 287412, 5);
    	Doctor doctor4 = new Doctor("Dr. Mahmut", 11111111114L, 287413, 10);
    	Doctor doctor9 = new Doctor("Dr. Buse", 11111111115L, 287411, 6);
    	Doctor doctor10 = new Doctor("Dr. Fatma", 11111111116L, 287412, 5);
    	Doctor doctor11 = new Doctor("Dr. Murat", 11111111117L, 287413, 10);
    	
    	hospital1Section1.addDoctor(doctor1);
    	hospital1Section1.addDoctor(doctor2);
    	
    	hospital1Section2.addDoctor(doctor3);
    	hospital1Section2.addDoctor(doctor4);
    	hospital1Section2.addDoctor(doctor9);
    	
    	hospital1Section3.addDoctor(doctor10);
    	hospital1Section3.addDoctor(doctor11);
    	
    	hospital1.addSection(hospital1Section1);
    	hospital1.addSection(hospital1Section2);
    	hospital1.addSection(hospital1Section3);
    	
    	///
    	Hospital hospital2 = new Hospital(2, "Ankara Hospital");
    	
    	Section hospital2Section1 = new Section(1, "Ortopedy");
    	Section hospital2Section2 = new Section(2, "Orthodenty");
    	
    	Doctor doctor5 = new Doctor("Dr. Ayşe", 11111111118L, 287414, 7);
    	Doctor doctor6 = new Doctor("Dr. Emine", 11111111119L, 287415, 5);
    	Doctor doctor7 = new Doctor("Dr. İrem", 111111111120L, 287416, 15);
    	Doctor doctor8 = new Doctor("Dr. Bengi", 11111111121L, 287417, 11);
    	
    	hospital2Section1.addDoctor(doctor5);
    	hospital2Section1.addDoctor(doctor6);
    	hospital2Section2.addDoctor(doctor7);
    	hospital2Section2.addDoctor(doctor8);
    	
    	hospital2.addSection(hospital2Section1);
    	hospital2.addSection(hospital2Section2);
    	///
    	
    	Patient patient1 = new Patient("Peter Grifin", 12111111110L);
    	Patient patient2 = new Patient("Meghan Grifin", 12111111111L);
    	Patient patient3 = new Patient("Brian Grifin", 12111111112L);
    	Patient patient4 = new Patient("Stewe Grifin", 12111111113L);
    	///
    	
    	HashMap<Long, Patient> patients = new HashMap<>();
    	HashMap<Integer, Hospital> hospitals=new HashMap<>();
    	LinkedList<Rendezvous> rendezvous = new LinkedList<>();
    	
    	patients.put(12111111110L,patient1);
    	patients.put(12111111111L,patient2);
    	patients.put(12111111112L,patient3);
    	patients.put(12111111113L,patient4);
    	
    	hospitals.put(1, hospital1);
    	hospitals.put(2, hospital2);
    	
    	CRS crs1 = new CRS(patients, hospitals, rendezvous);
    	///////////////////////////////////////////////////////////////////////////////////
        
        if (guiMode) {
        	
        	new GUI(crs1);
        	
        }
        else {
        	crs1.loadTablesToDisk(saveNameForConsole);
        	
			@SuppressWarnings("deprecation")
			Date date1 = new Date("Jan 30 2025 15:00:00");
        	crs1.makeRendezvous(12111111110L, 1, 1, 287411, date1);
        	
        	Date date2 = new Date();
        	crs1.makeRendezvous(12111111112L, 2, 2, 287416, date2);
        	
        	crs1.saveTablesToDisk("save1.ser");
        }
	}

}
