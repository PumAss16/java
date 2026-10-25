package coursehub.practice7;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FilterReader;
import java.io.FilterWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.List;
import java.util.Objects;

public final class Task3ResourcePipeline {

    private Task3ResourcePipeline() {
    }

    @FunctionalInterface
    public interface LineMapper {
        String map(String line) throws IOException;
    }

    public static void transform(Reader source, Writer target, LineMapper mapper,
                                 List<String> closeLog) throws IOException {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(target, "target");
        Objects.requireNonNull(mapper, "mapper");
        Objects.requireNonNull(closeLog, "closeLog");

        try (TrackingReader reader = new TrackingReader(source, closeLog);
             BufferedReader bufferedReader = new BufferedReader(reader);
             TrackingWriter writer = new TrackingWriter(target, closeLog);
             BufferedWriter bufferedWriter = new BufferedWriter(writer)) {

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                bufferedWriter.write(mapper.map(line));
                bufferedWriter.newLine();
            }
            bufferedWriter.flush();
        }
    }

    static final class TrackingReader extends FilterReader {

        private final List<String> closeLog;
        private boolean closed;

        TrackingReader(Reader in, List<String> closeLog) {
            super(in);
            this.closeLog = closeLog;
        }

        @Override
        public void close() throws IOException {
            if (!closed) {
                closed = true;
                closeLog.add("reader");
            }
            super.close();
        }
    }

    static final class TrackingWriter extends FilterWriter {

        private final List<String> closeLog;
        private boolean closed;

        TrackingWriter(Writer out, List<String> closeLog) {
            super(out);
            this.closeLog = closeLog;
        }

        @Override
        public void close() throws IOException {
            if (!closed) {
                closed = true;
                closeLog.add("writer");
            }
            super.close();
        }
    }
}
