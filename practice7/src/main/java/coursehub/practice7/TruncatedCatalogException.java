package coursehub.practice7;

import java.io.IOException;

public final class TruncatedCatalogException extends IOException {

    private static final long serialVersionUID = 1L;

    public TruncatedCatalogException(String message, Throwable cause) {
        super(message, cause);
    }
}
