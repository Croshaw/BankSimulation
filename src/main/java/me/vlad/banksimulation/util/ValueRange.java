package me.vlad.banksimulation.util;

import java.util.Random;

public class ValueRange<T extends Number> {
    private final T min;
    private final T max;
    public ValueRange(T min, T max) {
        this.min = min;
        this.max = max;
    }
    public T getMin() {
        return min;
    }
    public T getMax() {
        return max;
    }
    public T getRandomValueFromRange(Random random) {
        if (min instanceof Integer) {
            return (T) (Number) (min.intValue() + random.nextInt(max.intValue() - min.intValue() + 1));
        } else if (min instanceof Long) {
            return (T) (Number) (min.longValue() + (long) (random.nextDouble() * (max.longValue() - min.longValue())));
        } else if (min instanceof Double) {
            return (T) (Number) (min.doubleValue() + random.nextDouble() * (max.doubleValue() - min.doubleValue()));
        } else {
            throw new UnsupportedOperationException("Type not supported");
        }
    }
}
