package courseProject;

public class DuplicateInfoException extends RuntimeException{
	
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 2L;

	public DuplicateInfoException(String message) {
		super(message);
	}

}
