package scanner;

public class LexicalException extends Exception {
	
	// Costruttori
	public LexicalException(String message) {
		super(message);
	}

	public LexicalException(String message, Throwable cause) {
		super(message, cause);
	}

	public String toString() {
		return "LexicalException: " + getMessage();
	}

}
