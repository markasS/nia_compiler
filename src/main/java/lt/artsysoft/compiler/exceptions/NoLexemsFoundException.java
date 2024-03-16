package lt.artsysoft.compiler.exceptions;

public class NoLexemsFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5637300041853460996L;

	public NoLexemsFoundException() {
		super();
	}

	public NoLexemsFoundException(String source) {
		super("No lexems for the type " + source + " found");
	}

}
