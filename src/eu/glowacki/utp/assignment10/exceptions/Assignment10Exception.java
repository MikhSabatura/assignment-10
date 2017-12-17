package eu.glowacki.utp.assignment10.exceptions;

public final class Assignment10Exception extends RuntimeException {

    public Assignment10Exception(Exception e) {
        super(e);
    }

    public Assignment10Exception(String message, Exception cause) {
        super(message, cause);
    }

}
