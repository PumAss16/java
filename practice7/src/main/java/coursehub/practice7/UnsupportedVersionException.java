package coursehub.practice7;

import java.io.IOException;

public final class UnsupportedVersionException extends IOException {

    private static final long serialVersionUID = 1L;

    private final int version;

    public UnsupportedVersionException(int version, String supported) {
        super("unsupported format version " + version + "; supported: " + supported);
        this.version = version;
    }

    public int version() {
        return version;
    }
}
