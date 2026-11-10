package coursehub;

public class Task07PriceCalculator {

    public long calculatePrice(int hours, int discountPercent) {
        Task00Course.checkHours(hours);
        if (discountPercent < 0 || discountPercent > 100) {
            throw new Task02ValidationException("discount must be in [0, 100], got " + discountPercent);
        }
        return (long) hours * 100_000 * (100 - discountPercent) / 100;
    }
}
