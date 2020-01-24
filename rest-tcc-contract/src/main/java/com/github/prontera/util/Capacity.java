package com.github.prontera.util;

/**
 * @author Zhao Junjian
 * @date 2019/06/20
 */
public final class Capacity {

    private Capacity() {
    }

    /**
     * @throws IllegalArgumentException throws if less than 0 or larger than the largest power of
     *                                  two that can be represented as an {@code int}
     */
    public static int toMapExpectedSize(int expectedSize) {
        if (expectedSize >= 0 && expectedSize < 1 << Integer.SIZE - 2) {
            return (int) (expectedSize / 0.75F + 1.0F);
        }
        throw new IllegalArgumentException("expectedSize '" + expectedSize + "' is invalid.");
    }

}
