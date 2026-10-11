package coursehub;

import java.io.IOException;

/** Абстрактный источник записей импорта (задание 14). */
public interface RecordReader extends AutoCloseable {

    /** @return следующая запись или null в конце потока */
    String readRecord() throws IOException;

    @Override
    void close() throws IOException;
}
