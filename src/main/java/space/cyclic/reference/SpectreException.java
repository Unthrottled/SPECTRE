package space.cyclic.reference;

public class SpectreException extends Exception {
    public SpectreException() {
    }

    public SpectreException(String message) {
        super(message);
    }

    public SpectreException(String message, Throwable cause) {
        super(message, cause);
    }

    public SpectreException(Throwable cause) {
        super(cause);
    }

    public SpectreException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
