package com.williballenthin.rejistry;

/**
 * RegistryParseException is thrown when the parsing code encountered
 *   an unexpected value or structure.
 */
public class RegistryParseException extends Exception {
	private static final long serialVersionUID = 1953257223042282068L;

	public RegistryParseException() {
		// TODO Auto-generated constructor stub
	}

	public RegistryParseException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public RegistryParseException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public RegistryParseException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
