package coursehub.practice7;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import java.util.function.Consumer;
import java.util.zip.CRC32;

public final class Task9VersionedCatalog {

    static final int MAGIC = 0x43415456;
    static final int VERSION_1 = 1;
    public static final int CURRENT_VERSION = 2;

    private static final int MAX_ENTRIES = 1_000_000;
    private static final int MAX_PAYLOAD = 64 * 1024 * 1024;

    private Task9VersionedCatalog() {
    }

    public record LoadedCatalog(List<Course> courses, int schemaVersion) {

        public LoadedCatalog {
            courses = List.copyOf(courses);
            if (schemaVersion < 1) {
                throw new IllegalArgumentException("schemaVersion must be >= 1");
            }
        }
    }

    public static void write(Path file, List<Course> courses) throws IOException {
        Objects.requireNonNull(file, "file");
        List<Course> snapshot = List.copyOf(courses);
        if (snapshot.size() > MAX_ENTRIES) {
            throw new IllegalArgumentException("too many entries: " + snapshot.size());
        }

        byte[] payload = encodePayload(snapshot);
        if (payload.length > MAX_PAYLOAD) {
            throw new IllegalArgumentException("payload is too large: " + payload.length);
        }
        long checksum = checksum(payload);

        try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(
                Files.newOutputStream(file,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING,
                        StandardOpenOption.WRITE)))) {

            out.writeInt(MAGIC);
            out.writeInt(CURRENT_VERSION);
            out.writeInt(payload.length);
            out.write(payload);
            out.writeLong(checksum);
            out.flush();
        }
    }

    public static LoadedCatalog read(Path file) throws IOException {
        Objects.requireNonNull(file, "file");

        try (DataInputStream in = new DataInputStream(new BufferedInputStream(
                Files.newInputStream(file)))) {

            int magic = in.readInt();
            if (magic != MAGIC) {
                throw new CatalogFormatException(
                        String.format("bad magic: 0x%08X, expected 0x%08X", magic, MAGIC));
            }

            int version = in.readInt();
            return switch (version) {
                case VERSION_1 -> new LoadedCatalog(readV1Body(in), VERSION_1);
                case CURRENT_VERSION -> new LoadedCatalog(readV2Body(in), CURRENT_VERSION);
                default -> throw new UnsupportedVersionException(
                        version, VERSION_1 + ", " + CURRENT_VERSION);
            };
        } catch (EOFException e) {
            throw new TruncatedCatalogException("truncated versioned catalog: " + file, e);
        }
    }

    public static LoadedCatalog migrateV1ToV2(Path source, Path target) throws IOException {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(target, "target");

        LoadedCatalog loaded = read(source);
        if (loaded.schemaVersion() != VERSION_1) {
            throw new IllegalArgumentException(
                    "source must be version 1, got " + loaded.schemaVersion());
        }
        write(target, loaded.courses());
        return read(target);
    }

    public static LoadedCatalog readAndPublish(Path file, Consumer<List<Course>> publisher)
            throws IOException {
        Objects.requireNonNull(publisher, "publisher");
        LoadedCatalog catalog = read(file);
        publisher.accept(catalog.courses());
        return catalog;
    }

    private static byte[] encodePayload(List<Course> courses) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (DataOutputStream payload = new DataOutputStream(bytes)) {
            payload.writeInt(courses.size());
            for (Course course : courses) {
                payload.writeUTF(course.code());
                payload.writeUTF(course.title());
                payload.writeInt(course.level());
            }
        }
        return bytes.toByteArray();
    }

    private static List<Course> readV1Body(DataInputStream in) throws IOException {
        int count = checkedCount(in.readInt());
        List<Course> courses = new ArrayList<>(Math.min(count, 1024));
        for (int index = 0; index < count; index++) {
            String code = in.readUTF();
            String title = in.readUTF();
            courses.add(course(code, title, 1, index));
        }
        return courses;
    }

    private static List<Course> readV2Body(DataInputStream in) throws IOException {
        int payloadLength = in.readInt();
        if (payloadLength < Integer.BYTES || payloadLength > MAX_PAYLOAD) {
            throw new CatalogFormatException("invalid payload length: " + payloadLength);
        }

        byte[] payload = new byte[payloadLength];
        in.readFully(payload);
        long expectedChecksum = in.readLong();
        long actualChecksum = checksum(payload);
        if (expectedChecksum != actualChecksum) {
            throw new CatalogFormatException(String.format(
                    "checksum mismatch: expected 0x%08X, actual 0x%08X",
                    expectedChecksum, actualChecksum));
        }
        return decodePayload(payload);
    }

    private static List<Course> decodePayload(byte[] payload) throws IOException {
        try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(payload))) {
            int count = checkedCount(in.readInt());
            List<Course> courses = new ArrayList<>(Math.min(count, 1024));
            for (int index = 0; index < count; index++) {
                String code = in.readUTF();
                String title = in.readUTF();
                int level = in.readInt();
                courses.add(course(code, title, level, index));
            }
            if (in.read() != -1) {
                throw new CatalogFormatException("trailing bytes in payload");
            }
            return List.copyOf(courses);
        } catch (EOFException e) {
            throw new CatalogFormatException("corrupt version 2 payload", e);
        }
    }

    private static int checkedCount(int count) throws CatalogFormatException {
        if (count < 0 || count > MAX_ENTRIES) {
            throw new CatalogFormatException("invalid entry count: " + count);
        }
        return count;
    }

    private static Course course(String code, String title, int level, int index)
            throws CatalogFormatException {
        try {
            return new Course(code, title, level);
        } catch (IllegalArgumentException e) {
            throw new CatalogFormatException("entry " + index + ": " + e.getMessage(), e);
        }
    }

    private static long checksum(byte[] payload) {
        CRC32 crc = new CRC32();
        crc.update(payload, 0, payload.length);
        return crc.getValue();
    }
}
