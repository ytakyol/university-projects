package courseProject;

import java.awt.*;
import java.awt.event.*;
import java.util.Date;
import java.util.HashMap;

import javax.swing.*;

@SuppressWarnings("serial")
public class GUI extends JFrame implements ActionListener {

	/*
	 * -Main Menu - Randevu al - Hastane seçin: (radio) - Bölüm seçin: (radio) -
	 * Doktor seçin: (radio) - Hasta seçin: (radio) - Date seçin: (text box) -
	 * KAYDET (buton) - MAIN MENU(buton) - Randevuları Görüntüle (Pop up ile ekrana
	 * tüm randevuları yazdırır.) - Randevuları Kaydet - Randevuları Yükle
	 * 
	 */

	private final static int panoX = 1000, panoY = 500;
	private final static int color1 = 220, color2 = 220, color3 = 220;

	JPanel mainMenu = new JPanel();
	JPanel randevuAl = new JPanel();
	JPanel seeRendezvous = new JPanel();
	JTable table;
	CRS crs;

	JComboBox<Hospital> hospital;
	JComboBox<Section> section;
	JComboBox<Doctor> doctor;

	JTextField hastaAdi;
	JTextField hastaTC;
	JTextField tarih;

	public GUI(CRS crs) {

		// Initialize the main this
		this.setTitle("Clinic Rezervation System");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(panoX, panoY);
		this.getContentPane().setBackground(new Color(color1, color2, color3));

		this.crs = crs;

		this.setLayout(null);
		setLocationRelativeTo(null);
		

		createMainMenu();
		createRandevuAl();
		createSeeRendezvous();
		
		loadMainMenu();
		
		setAlwaysOnTop(true);
		Timer timer = new Timer(1000, e -> setAlwaysOnTop(false)); // Disable always on top after 2 seconds
        timer.setRepeats(false);
        timer.start();
        
		this.setVisible(true);
		
	}

	public JButton createButton(String buttonName, int x, int y, int width, int height, JPanel panel1) {
		JButton button = new JButton(buttonName);
		button.setBounds(x, y, width, height);
		button.addActionListener(this);
		panel1.add(button);
		button.setFocusable(false);
		return button;
	}

	public JLabel createLabel(String text, int locX, int locY, int sizeX, int sizeY, JPanel panel) {
		JLabel etiket = new JLabel(text);
		etiket.setVisible(false);
		etiket.setBounds(locX, locY, sizeX, sizeY);
		panel.add(etiket);
		etiket.setVisible(true);
		return etiket;
	}

	public <T> JComboBox<T> createComboBoxes(T[] liste, int locX, int locY, int sizeX, int sizeY, JPanel panel,
			String name) {

		JComboBox<T> comboBox = new JComboBox<T>(liste);
		comboBox.setVisible(false);
		comboBox.setBounds(locX, locY, sizeX, sizeY);
		comboBox.setActionCommand(name);
		panel.add(comboBox);
		comboBox.setVisible(true);
		return comboBox;

	}

	public JTextField createTextField(String text, int locX, int locY, int sizeX, int sizeY, JPanel panel) {
		JTextField textField = new JTextField(text);

		// Set the placeholder color
		textField.setForeground(Color.GRAY);

		// Add FocusListener to handle the placeholder text
		textField.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				// When the field gains focus, clear the placeholder if it's still there
				if (textField.getText().equals(text)) {
					textField.setText("");
					textField.setForeground(Color.BLACK); // Change text color to black when typing
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				// When the field loses focus, restore the placeholder if it's empty
				if (textField.getText().isEmpty()) {
					textField.setText(text);
					textField.setForeground(Color.GRAY); // Set the placeholder text color
				}
			}
		});

		textField.setVisible(false);
		textField.setBounds(locX, locY, sizeX, sizeY);
		panel.add(textField);
		textField.setVisible(true);
		return textField;
	}

	public Hospital[] getHospitalArray() {
		return crs.getHospitals().values().toArray(new Hospital[0]);
	}

	public Section[] getSectionArray(Hospital hospital) {
		return hospital.getSections().toArray(new Section[1]);
	}

	public Doctor[] getDoctorArray(Section section) {
		return section.getDoctors().toArray(new Doctor[0]);
	}

	public void createMainMenu() {
		mainMenu.removeAll();
		mainMenu.setVisible(false);
		mainMenu.setBounds(0, 0, panoX, panoY);
		mainMenu.setBackground(new Color(color1, color2, color3));

		mainMenu.setLayout(null);

		createButton("Make a Rendezvous", 400, 75, 200, 50, mainMenu);
		createButton("See Rendezvous", 400, 150, 200, 50, mainMenu);
		createButton("Save Rendezvous", 400, 225, 200, 50, mainMenu);
		createButton("Load Rendezvous", 400, 300, 200, 50, mainMenu);

		mainMenu.setVisible(true);
	}
	public void loadMainMenu() {
		this.getContentPane().removeAll();
		add(mainMenu);    // Add the new panel
        revalidate();   // Revalidate to update the container
        repaint(); 
	}

	public void createRandevuAl() {
		randevuAl.removeAll();
		randevuAl.setVisible(false);
		randevuAl.setBounds(0, 0, panoX, panoY);
		randevuAl.setBackground(new Color(color1, color2, color3));

		randevuAl.setLayout(null);

		createLabel("Choose Hospital", 350, 50, 100, 30, randevuAl);
		createLabel("Choose Section", 350, 80, 100, 30, randevuAl);
		createLabel("Choose Doctor", 350, 110, 100, 30, randevuAl);

		hospital = createComboBoxes(getHospitalArray(), 450, 50, 250, 30, randevuAl, "hospitalComboBox");
		section = createComboBoxes(getSectionArray((Hospital) hospital.getSelectedItem()), 450, 80, 250, 30, randevuAl,
				"sectionComboBox");
		doctor = createComboBoxes(getDoctorArray((Section) section.getSelectedItem()), 450, 110, 250, 30, randevuAl,
				"doctorComboBox");

		hospital.addActionListener(this);
		section.addActionListener(this);
		doctor.addActionListener(this);

		createLabel("Patient Name", 350, 140, 100, 30, randevuAl);
		createLabel("Patient ID", 350, 170, 100, 30, randevuAl);
		createLabel("Choose Date", 350, 200, 100, 30, randevuAl);
		createLabel("Ex: Jan 30 2025 12:00:00", 710, 200, 210, 30, randevuAl);

		hastaAdi = createTextField("Patient Name", 450, 140, 250, 30, randevuAl);
		hastaTC = createTextField("Patient ID", 450, 170, 250, 30, randevuAl);
		tarih = createTextField("MMM dd yyyy hh:mm:ss", 450, 200, 250, 30, randevuAl);

		createButton("Make Rendezvous", 400, 275, 150, 50, randevuAl);
		createButton("Main Menu", 400, 350, 150, 50, randevuAl);

		randevuAl.setVisible(true);
	}
	
	public void loadRandevuAl() {
		this.getContentPane().removeAll();
		add(randevuAl);    // Add the new panel
        revalidate();   // Revalidate to update the container
        repaint(); 
	}
	
	public void createSeeRendezvous() {
		seeRendezvous.removeAll();
		seeRendezvous.setVisible(false);
		seeRendezvous.setBounds(0, 0, panoX, panoY);
		seeRendezvous.setBackground(new Color(color1, color2, color3));
		seeRendezvous.setLayout(null);
		
		String[] columnNames = { "Doctor", "Patient Name", "Patient ID", "Date"};
		Object[][] data = new Object[crs.getRendezvous().size()][4];
		
		int i = 0;
		for(Rendezvous rendezvous:crs.getRendezvous()) {
			data[i][0] = rendezvous.getDoctor();
			data[i][1] = rendezvous.getPatient().getName();
			data[i][2] = rendezvous.getPatient().getNational_id();
			data[i][3] = rendezvous.getDateTime();
			i++;
		}
		
		table = new JTable(data, columnNames);
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBounds(100, 10, 800, 350);
		createButton("Main Menu", 450, 400, 150, 30, seeRendezvous);
		seeRendezvous.add(scrollPane);
		
		seeRendezvous.setVisible(true);
	}
	
	public void loadSeeRendezvous() {
		this.getContentPane().removeAll();
		add(seeRendezvous);    // Add the new panel
        revalidate();   // Revalidate to update the container
        repaint();
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand(); // Get the action command (button name)

		switch (command) {
		case "Make a Rendezvous":
	
			loadRandevuAl();
			break;

		case "See Rendezvous":
			createSeeRendezvous();
			loadSeeRendezvous();

			break;

		case "Save Rendezvous":

			crs.saveTablesToDisk(Main.saveNameForGUI);
			JOptionPane.showMessageDialog(new JFrame(), "Rendezvous Has Been Saved!");

			break;

		case "Load Rendezvous":

			crs.loadTablesToDisk(Main.saveNameForGUI);
			JOptionPane.showMessageDialog(new JFrame(), "Rendezvous Has Been Loaded!");

			break;
		case "hospitalComboBox":

			section.setModel(new DefaultComboBoxModel<>(getSectionArray((Hospital) hospital.getSelectedItem())));
			section.setSelectedIndex(0);

			break;
		case "sectionComboBox":

			doctor.setModel(new DefaultComboBoxModel<>(getDoctorArray((Section) section.getSelectedItem())));
			doctor.setSelectedIndex(0);

			break;
		case "doctorComboBox":

			break;
		case "Make Rendezvous":
			
			long id = Long.parseLong(hastaTC.getText());
			if(crs.getPatients().get(id) == null)
			{
				HashMap<Long, Patient> temp = crs.getPatients();
				temp.put(id, new Patient(hastaAdi.getText(), id));
				crs.setPatients(temp);
			}
			

			Hospital temp1 = (Hospital) hospital.getSelectedItem();
			Section temp2 = (Section) section.getSelectedItem();
			Doctor temp3 = (Doctor) doctor.getSelectedItem();
			

			@SuppressWarnings("deprecation") Date desiredDate = new Date(tarih.getText());
			
			
			boolean bos = true;
			
			/////////
			for(Rendezvous rendezvous:crs.getRendezvous()) {
				if(rendezvous.getDateTime().equals(desiredDate) && rendezvous.getDoctor().getDiploma_id() == temp3.getDiploma_id()) {
					JOptionPane.showMessageDialog(new JFrame(), "O dakikada bu doktorun bir randevusu bulunmaktadır.");
					bos = false;
					break;
				}
			}
			/////////
			
			if (bos && crs.makeRendezvous(id, temp1.getId(), temp2.getId(), temp3.getDiploma_id(), desiredDate))
				JOptionPane.showMessageDialog(new JFrame(), "Rendezvous Has Been Saved!");
			else
				JOptionPane.showMessageDialog(new JFrame(), "Can't Make Rendezvous Because Of Crowdness");

			break;
		case "Main Menu":
			loadMainMenu();
			break;
		default:
			System.out.println("Unknown action: " + command);
			break;
		}
	}
}
