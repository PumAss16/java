package coursehub.practice6.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Task15Test {

    private static final ZoneId BERLIN = ZoneId.of("Europe/Berlin");
    private static final Instant S1 = Instant.parse("2024-03-30T09:00:00Z");
    private static final Instant S2 = Instant.parse("2024-03-31T09:00:00Z");
    private static final Instant S3 = Instant.parse("2024-04-01T09:00:00Z");
    private static final Instant S4 = Instant.parse("2024-04-02T09:00:00Z");
    private static final List<Instant> SESSIONS = List.of(S1, S2, S3, S4);
    private static final Instant DEADLINE = Instant.parse("2024-03-01T00:00:00Z");
    private static final BigDecimal TEN_PERCENT = new BigDecimal("10");

    @Test
    @DisplayName("Серия вебинаров вокруг перехода на летнее время")
    void handlesDaylightSavingChange() {
        WebinarSeriesPlan plan = plan(SESSIONS, Duration.ofMinutes(90));
        assertEquals(4, plan.lessonCount());
        assertEquals(S4.plus(Duration.ofMinutes(90)), plan.lastSessionEnd());
        assertEquals(List.of(S2, S3, S4), plan.sessionsAfter(S1));
        assertEquals(LocalDate.of(2024, 3, 30), plan.localDateOf(S1));
        assertEquals(LocalDate.of(2024, 3, 31), plan.localDateOf(S2));
        assertEquals(ZoneOffset.ofHours(1), S1.atZone(BERLIN).getOffset());
        assertEquals(ZoneOffset.ofHours(2), S2.atZone(BERLIN).getOffset());
    }

    @Test
    @DisplayName("Високосный день в зоне организатора")
    void handlesLeapDay() {
        Instant leapDay = Instant.parse("2024-02-29T10:00:00Z");
        WebinarSeriesPlan plan = plan(List.of(leapDay), Duration.ofMinutes(60));
        assertEquals(LocalDate.of(2024, 2, 29), plan.localDateOf(leapDay));
        assertEquals(leapDay.plus(Duration.ofMinutes(60)), plan.lastSessionEnd());
    }

    @Test
    @DisplayName("Скидка действует только до дедлайна")
    void earlyBirdIsStrictlyBeforeDeadline() {
        EarlyBirdPolicy policy = new EarlyBirdPolicy(DEADLINE, TEN_PERCENT);
        Money base = Money.of("100.00", "EUR");
        assertTrue(policy.isActive(DEADLINE.minusNanos(1)));
        assertFalse(policy.isActive(DEADLINE));
        assertEquals(Money.of("90.00", "EUR"),
                policy.priceAt(base, DEADLINE.minusNanos(1), RoundingMode.HALF_UP));
        assertEquals(base, policy.priceAt(base, DEADLINE, RoundingMode.HALF_UP));
    }

    @Test
    @DisplayName("Зона зрителя меняет только отображение")
    void viewerZoneChangesOnlyDisplay() {
        PricingService service = new PricingService();
        CourseOffering offering = offering();
        Instant moment = Instant.parse("2024-02-01T10:00:00Z");
        Map<ZoneId, String> displays = Map.of(
                ZoneId.of("UTC"), "30.03.2024 09:00 UTC",
                BERLIN, "30.03.2024 10:00 CET",
                ZoneId.of("Asia/Tokyo"), "30.03.2024 18:00 JST");
        for (Map.Entry<ZoneId, String> entry : displays.entrySet()) {
            assertEquals(Money.of("90.00", "EUR"), service.priceAt(offering, moment, RoundingMode.HALF_UP));
            assertEquals(entry.getValue(), service.displaySession(offering.plan(), S1, entry.getKey()));
        }
    }

    @Test
    @DisplayName("Валюта покупки должна совпадать с ценой")
    void rejectsPurchaseCurrencyMismatch() {
        CourseOffering offering = offering();
        Instant purchasedAt = Instant.parse("2024-02-01T10:00:00Z");
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> new Purchase(offering, Money.of("90.00", "USD"), purchasedAt));
        assertTrue(error.getMessage().contains("USD"));
        Purchase purchase = new Purchase(offering, Money.of("90.00", "EUR"), purchasedAt);
        assertEquals(Money.of("90.00", "EUR"), purchase.paid());
    }

    @Test
    @DisplayName("Возврат считается по оставшимся занятиям")
    void refundsRemainingSessions() {
        RefundService service = new RefundService();
        CourseOffering small = offering("99.99", List.of(S1, S2, S3));
        Purchase smallPurchase = new Purchase(small, Money.of("99.99", "EUR"), S1.minus(Duration.ofDays(5)));
        assertEquals(Money.of("66.66", "EUR"),
                service.refund(smallPurchase, S1.plus(Duration.ofMinutes(5)), RoundingMode.HALF_UP));
        assertEquals(Money.of("33.33", "EUR"),
                service.refund(smallPurchase, S2.plus(Duration.ofMinutes(5)), RoundingMode.HALF_UP));

        CourseOffering full = offering();
        Purchase fullPurchase = new Purchase(full, Money.of("100.00", "EUR"), S1.minus(Duration.ofDays(5)));
        assertEquals(Money.of("100.00", "EUR"),
                service.refund(fullPurchase, S1.minus(Duration.ofMinutes(5)), RoundingMode.HALF_UP));
        assertEquals(Money.of("75.00", "EUR"),
                service.refund(fullPurchase, S1, RoundingMode.HALF_UP));
        assertEquals(Money.of("75.00", "EUR"),
                service.refund(fullPurchase, S1.plus(Duration.ofMinutes(5)), RoundingMode.HALF_UP));
        assertEquals(Money.of("0.00", "EUR"),
                service.refund(fullPurchase, S4.plus(Duration.ofMinutes(5)), RoundingMode.HALF_UP));
    }

    private static WebinarSeriesPlan plan(List<Instant> sessions, Duration duration) {
        return new WebinarSeriesPlan(BERLIN, sessions, duration);
    }

    private static CourseOffering offering() {
        return offering("100.00", SESSIONS);
    }

    private static CourseOffering offering(String price, List<Instant> sessions) {
        return new CourseOffering("JAVA-1", CourseTitle.of("Java"), plan(sessions, Duration.ofMinutes(60)),
                Money.of(price, "EUR"), DEADLINE, TEN_PERCENT);
    }
}
