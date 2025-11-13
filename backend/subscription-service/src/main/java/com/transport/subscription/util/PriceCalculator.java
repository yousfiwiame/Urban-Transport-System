package com.transport.subscription.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PriceCalculator {

    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    public static BigDecimal calculateTotal(BigDecimal price, int quantity) {
        if (price == null || quantity <= 0) {
            return BigDecimal.ZERO;
        }
        return price.multiply(BigDecimal.valueOf(quantity))
                .setScale(SCALE, ROUNDING_MODE);
    }

    public static BigDecimal add(BigDecimal amount1, BigDecimal amount2) {
        if (amount1 == null) amount1 = BigDecimal.ZERO;
        if (amount2 == null) amount2 = BigDecimal.ZERO;
        return amount1.add(amount2).setScale(SCALE, ROUNDING_MODE);
    }

    public static BigDecimal subtract(BigDecimal amount1, BigDecimal amount2) {
        if (amount1 == null) amount1 = BigDecimal.ZERO;
        if (amount2 == null) amount2 = BigDecimal.ZERO;
        return amount1.subtract(amount2).setScale(SCALE, ROUNDING_MODE);
    }

    public static boolean isPositive(BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    }
}

