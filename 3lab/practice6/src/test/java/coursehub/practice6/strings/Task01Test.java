package coursehub.practice6.strings;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Objects;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Task01Test {

    @Test
    @DisplayName("Одинаковые литералы дают одну и ту же ссылку")
    void literalsShareSameReference() {
        assertTrue(StringIdentityDemo.sameReference("JAVA", "JAVA"));
        assertTrue(StringIdentityDemo.sameValue("JAVA", "JAVA"));
    }

    @Test
    @DisplayName("copy создаёт новый объект с тем же текстом")
    void copyCreatesNewReferenceWithSameValue() {
        String copied = StringIdentityDemo.copy("JAVA");
        assertNotSame(copied, "JAVA");
        assertFalse(StringIdentityDemo.sameReference(copied, "JAVA"));
        assertTrue(StringIdentityDemo.sameValue(copied, "JAVA"));
    }

    @Test
    @DisplayName("Конкатенация литералов считается на этапе компиляции")
    void literalConcatenationIsCompileTimeConstant() {
        String joined = "JA" + "VA";
        assertTrue(StringIdentityDemo.sameReference(joined, "JAVA"));
        assertEquals("JAVA", joined);
    }

    @Test
    @DisplayName("Строка из рантайма не та же ссылка, но равное значение")
    void runtimeBuiltStringIsNotSameReference() {
        String built = StringIdentityDemo.builtAtRuntime("VA");
        assertNotSame(built, "JAVA");
        assertFalse(StringIdentityDemo.sameReference(built, "JAVA"));
        assertTrue(StringIdentityDemo.sameValue(built, "JAVA"));
        assertTrue(StringIdentityDemo.sameReference(StringIdentityDemo.interned(built), "JAVA"));
    }

    @Test
    @DisplayName("Сравнивай текст через equals или Objects.equals, а не по ссылке из пула")
    void compareTextByValueNotByPoolReference() {
        String built = new StringBuilder("JA").append("VA").toString();
        assertNotSame(built, "JAVA");
        assertTrue(built.equals("JAVA"));
        assertTrue(Objects.equals(built, "JAVA"));
        assertEquals("JAVA", built);
    }
}
