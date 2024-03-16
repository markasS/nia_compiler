package lt.artsysoft.compiler.exceptions;

public class NoSuchLexemException extends Exception {
	
	/**
	 * The UID for this exception that needed to be generated for whatever reason
	 */
	private static final long serialVersionUID = -6302839879917288027L;

	public NoSuchLexemException() {
		super();
	}
	
	public NoSuchLexemException(String source) {
		super(source);
	}

}
