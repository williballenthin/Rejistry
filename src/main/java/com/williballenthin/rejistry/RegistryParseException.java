package com.williballenthin.rejistry;

/**
 * RegistryParseException is thrown when the parsing code encountered
 * an unexpected value or structure.
 */
public class RegistryParseException extends Exception {
    private static final long serialVersionUID = 1953257223042282068L;

    public RegistryParseException() {    }

    public RegistryParseException(String message) {
        super(message);
    }

    public RegistryParseException(Throwable cause) {
        super(cause);
    }

    public RegistryParseException(String message, Throwable cause) {
        super(message, cause);
    }

}
