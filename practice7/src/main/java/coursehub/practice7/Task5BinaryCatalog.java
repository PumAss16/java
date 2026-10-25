package coursehub.practice7;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Task5BinaryCatalog {

    static final int MAGIC = 0x43415431;
    static final int VERSION = 1;
    private static final int MAX_ENTRIES = 1_000_000;

    private Task5BinaryCatalog() {
    }

    public static void write(Path file, List<Course> courses) throws IOException {
        Objects.requireNonNull(file, "file");
        List<Course> snapshot = List.copyOf(courses);
        if (snapshot.size() > MAX_ENTRIES) {
            throw new IllegalArgumentException("too many entries: " + snapshot.size());
        }

        try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(
                Files.newOutputStream(file,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING,
                        StandardOpenOption.WRITE)))) {

            out.writeInt(MAGIC);
            out.writeInt(VERSION);
            out.writeInt(snapshot.size());
            for (Course course : snapshot) {
                out.writeUTF(course.code());
                out.writeUTF(course.title());
            }
            out.flush();
        }
    }

    public static List<Course> read(Path file) throws IOException {
        Objects.requireNonNull(file, "file");

        try (DataInputStream in = new DataInputStream(new BufferedInputStream(
                Files.newInputStream(file)))) {

            int magic = in.readInt();
            if (magic != MAGIC) {
                throw new CatalogFormatException(
                        String.format("bad magic: 0x%08X, expected 0x%08X", magic, MAGIC));
            }

            int version = in.readInt();
            if (version != VERSION) {
                throw new UnsupportedVersionException(version, Integer.toString(VERSION));
            }

            int count = in.readInt();
            if (count < 0 || count > MAX_ENTRIES) {
                throw new CatalogFormatException("invalid entry count: " + count);
            }

            List<Course> courses = new ArrayList<>(Math.min(count, 1024));
            for (int index = 0; index < count; index++) {
                String code = in.readUTF();
                String title = in.readUTF();
                try {
                    courses.add(new Course(code, title));
                } catch (IllegalArgumentException e) {
                    throw new CatalogFormatException("entry " + index + ": " + e.getMessage(), e);
                }
            }
            return List.copyOf(courses);
        } catch (EOFException e) {
            throw new TruncatedCatalogException("truncated binary catalog: " + file, e);
        }
    }
}
