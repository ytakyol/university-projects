package courseProject;

import java.io.Serializable;

public class Person implements Serializable{

	@SuppressWarnings("unused")
	private static final long serialVersionUID = 6L;
	
	private String name;
	private final long national_id;
	
	public Person(String name, long national_id) {
		this.name = name;
		this.national_id = national_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getNational_id() {
		return national_id;
	}

	@Override
	public String toString() {
		return "Person [name=" + getName() + ", national_id=" + getNational_id() + "]";
	}
	
}
