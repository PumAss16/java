package coursehub;

/** Цена курса: неверный аргумент — unchecked ошибка программиста (задание 7). */
public class PriceCalculator {

    public long calculatePrice(int hours, int discountPercent) {
        Course.checkHours(hours);
        if (discountPercent < 0 || discountPercent > 100) {
            throw new ValidationException("discount must be in [0, 100], got " + discountPercent);
        }
        return (long) hours * 100_000 * (100 - discountPercent) / 100;
    }
}
