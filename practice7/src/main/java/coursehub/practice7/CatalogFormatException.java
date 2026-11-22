package coursehub.practice7;

import java.io.IOException;

public class CatalogFormatException extends IOException {

    private static final long serialVersionUID = 1L;

    public CatalogFormatException(String message) {
        super(message);
    }

    public CatalogFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}
