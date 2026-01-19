package courseProject;

import java.io.Serializable;
import java.util.LinkedList;

public class Hospital implements Serializable {

	@SuppressWarnings("unused")
	private static final long serialVersionUID = 3L;

	public LinkedList<Section> getSections() {
		return sections;
	}

	public void setSections(LinkedList<Section> sections) {
		this.sections = sections;
	}

	private final int id;
	private String name;
	private LinkedList<Section> sections;

	public Hospital(int id, String name) {

		this.id = id;
		this.name = name;
		sections = new LinkedList<>();
	}

	public Section getSection(int id) {
		Section found = null;

		for (Section section : sections) {
			if (section.getId() == id) {
				found = section;
			}
		}

		return found;
	}

	private Section getSection(String name) {
		Section found = null;

		for (Section section : sections) {
			if (section.getName() == name) {
				found = section;
			}
		}

		return found;
	}

	public void addSection(Section section) throws DuplicateInfoException {

		if (getSection(section.getName()) == null) {
			sections.add(section);
		} else {
			if(!Main.guiMode) {
				throw new DuplicateInfoException("Section already exists in the section " + name);
			}
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		return getName();
	}
}
