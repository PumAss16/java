package coursehub;

import java.io.IOException;

public interface Task14RecordReader extends AutoCloseable {

    String readRecord() throws IOException;

    @Override
    void close() throws IOException;
}
