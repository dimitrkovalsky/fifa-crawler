package com.liberty.common;

/**
 * @author Dmytro_Kovalskyi.
 * @since 15.11.2016.
 */
public enum PriceStrategy {
    MANUAL("manual");

    private PriceStrategy(String strategy) {
        this.strategy = strategy;
    }

    private final String strategy;
}
