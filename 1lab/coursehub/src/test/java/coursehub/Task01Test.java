package coursehub;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Задание 1. Stack trace")
class Task01Test {

    record Rec(String name, double avg) {
    }

    static class Repo {
        private final Map<String, Rec> data = new HashMap<>();

        void save(String id, Rec rec) {
            data.put(id, rec);
        }

        Rec find(String id) {
            return data.get(id);
        }
    }

    static class Report {
        private final Repo repo;

        Report(Repo repo) {
            this.repo = repo;
        }

        String render(String id) {
            return format(repo.find(id));
        }

        String format(Rec rec) {
            return name(rec) + " avg=" + rec.avg();
        }

        String name(Rec rec) {
            return rec.name().toUpperCase();
        }
    }

    static class FixedRepo {
        private final Map<String, Rec> data = new HashMap<>();

        void save(String id, Rec rec) {
            data.put(id, rec);
        }

        Optional<Rec> find(String id) {
            return Optional.ofNullable(data.get(id));
        }
    }

    static class FixedReport {
        private final FixedRepo repo;

        FixedReport(FixedRepo repo) {
            this.repo = repo;
        }

        String render(String id) {
            Rec rec = repo.find(id).orElseThrow(() -> new NoSuchElementException("student not found: " + id));
            return rec.name().toUpperCase() + " avg=" + rec.avg();
        }
    }

    private NullPointerException reproduce(Report report) {
        try {
            report.render("S-404");
            throw new AssertionError("ожидали NullPointerException");
        } catch (NullPointerException e) {
            return e;
        }
    }

    @Test
    @DisplayName("Тип, сообщение и путь вызовов: name -> format -> render -> вызывающий код")
    void readsTheStackTrace() {
        Repo repo = new Repo();
        repo.save("S-1", new Rec("anna", 4.8));

        NullPointerException e = reproduce(new Report(repo));
        List<String> frames = Arrays.stream(e.getStackTrace())
                .limit(4)
                .map(StackTraceElement::getMethodName)
                .toList();

        assertAll(
                () -> assertEquals("java.lang.NullPointerException", e.getClass().getName()),
                () -> assertTrue(e.getMessage().contains("rec"), "сообщение: " + e.getMessage()),
                () -> assertEquals(List.of("name", "format", "render", "reproduce"), frames),
                () -> assertEquals(Report.class.getName(), e.getStackTrace()[0].getClassName()),
                () -> assertTrue(e.getStackTrace()[0].getLineNumber() > 0));
    }

    @Test
    @DisplayName("Причина — контракт репозитория: null ломает любого потребителя")
    void causeIsTheRepositoryContract() {
        Repo repo = new Repo();

        Rec rec = repo.find("S-404");

        assertAll(
                () -> assertNull(rec),
                () -> assertThrows(NullPointerException.class, () -> rec.name().length()));
    }

    @Test
    @DisplayName("Исправление причины: Optional и осмысленный отказ вместо NPE")
    void fixesTheCause() {
        FixedRepo repo = new FixedRepo();
        repo.save("S-1", new Rec("anna", 4.8));
        FixedReport report = new FixedReport(repo);

        NoSuchElementException e = assertThrows(NoSuchElementException.class, () -> report.render("S-404"));

        assertAll(
                () -> assertEquals("ANNA avg=4.8", report.render("S-1")),
                () -> assertEquals("student not found: S-404", e.getMessage()));
    }
}
