package coursehub.practice6.numbers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Task04Test {
    @Test
    @DisplayName("Распаковка null даёт NullPointerException")
    void unboxNullThrows() {
        assertThrows(NullPointerException.class, () -> IntegerPolicy.unbox(null));
    }

    @Test
    @DisplayName("Кэш Integer делает 127 одной ссылкой")
    void smallValueSharesReference() {
        assertTrue(IntegerPolicy.sameReference(127, 127));
    }

    @Test
    @DisplayName("128 сравниваются по ссылке как разные")
    void cacheBoundaryIsNotShared() {
        assertFalse(IntegerPolicy.sameReference(128, 128));
    }

    @Test
    @DisplayName("1000 сравниваются по ссылке как разные")
    void largeValueIsNotShared() {
        assertFalse(IntegerPolicy.sameReference(1000, 1000));
    }

    @Test
    @DisplayName("Равные значения сравнивают через equals")
    void equalValuesUseEquals() {
        assertFalse(IntegerPolicy.sameReference(1000, 1000));
        assertTrue(IntegerPolicy.sameValue(1000, 1000));
    }

    @Test
    @DisplayName("add с null слева сообщает об ошибке")
    void addNullLeftThrows() {
        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> IntegerPolicy.add(null, 1)
        );
        assertTrue(error.getMessage().contains("left"));
    }
}
