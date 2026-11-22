package collections;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Task04Test {

    @Test
    @DisplayName("Очередь выдаёт элементы в порядке добавления")
    void pollReturnsElementsInFifoOrder() {
        Waitlist waitlist = new Waitlist();
        CourseId a = CourseId.of("A");
        CourseId b = CourseId.of("B");
        CourseId c = CourseId.of("C");
        waitlist.add(a);
        waitlist.add(b);
        waitlist.add(c);

        assertAll(
                () -> assertEquals(a, waitlist.poll()),
                () -> assertEquals(b, waitlist.poll()),
                () -> assertEquals(c, waitlist.poll())
        );
    }

    @Test
    @DisplayName("peek возвращает первый элемент и не извлекает его")
    void peekDoesNotRemoveElement() {
        Waitlist waitlist = new Waitlist();
        CourseId a = CourseId.of("A");
        CourseId b = CourseId.of("B");
        waitlist.add(a);
        waitlist.add(b);

        assertAll(
                () -> assertEquals(a, waitlist.peek()),
                () -> assertEquals(2, waitlist.size()),
                () -> assertEquals(a, waitlist.poll()),
                () -> assertEquals(b, waitlist.peek())
        );
    }

    @Test
    @DisplayName("poll и peek на пустой очереди возвращают null")
    void pollAndPeekReturnNullOnEmptyQueue() {
        Waitlist waitlist = new Waitlist();

        assertAll(
                () -> assertNull(waitlist.poll()),
                () -> assertNull(waitlist.peek())
        );
    }

    @Test
    @DisplayName("removeNext и element на пустой очереди бросают NoSuchElementException")
    void removeNextAndElementThrowOnEmptyQueue() {
        Waitlist waitlist = new Waitlist();

        assertAll(
                () -> assertThrows(NoSuchElementException.class, waitlist::removeNext),
                () -> assertThrows(NoSuchElementException.class, waitlist::element)
        );
    }

    @Test
    @DisplayName("add и offer на неограниченной очереди возвращают true")
    void addAndOfferReturnTrue() {
        Waitlist waitlist = new Waitlist();

        assertAll(
                () -> assertTrue(waitlist.add(CourseId.of("A"))),
                () -> assertTrue(waitlist.offer(CourseId.of("B"))),
                () -> assertEquals(2, waitlist.size())
        );
    }

    @Test
    @DisplayName("После извлечения всех элементов очередь пуста")
    void queueIsEmptyAfterAllElementsPolled() {
        Waitlist waitlist = new Waitlist();
        waitlist.offer(CourseId.of("A"));
        waitlist.offer(CourseId.of("B"));
        List<CourseId> polled = new ArrayList<>();
        while (!waitlist.isEmpty()) {
            polled.add(waitlist.poll());
        }

        assertAll(
                () -> assertEquals(List.of(CourseId.of("A"), CourseId.of("B")), polled),
                () -> assertEquals(0, waitlist.size()),
                () -> assertTrue(waitlist.isEmpty())
        );
    }
}
