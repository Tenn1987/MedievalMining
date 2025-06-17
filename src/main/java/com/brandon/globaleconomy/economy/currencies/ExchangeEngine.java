package com.brandon.globaleconomy.economy.currencies;

public class ExchangeEngine {

    /**
     * Computes the exchange rate from "fromCurrency" to "toCurrency" based on current metal pools.
     * Returns 1.0 if no meaningful rate is found (e.g., fiat to fiat).
     */
    public double getExchangeRate(NationalCurrency from, NationalCurrency to, MetalPool metalPool) {
        // Both metal-backed
        if (from.isMetalBacked() && to.isMetalBacked()) {
            double fromMetalPerUnit = from.getBackingRatio();
            double toMetalPerUnit = to.getBackingRatio();

            // If same metal, ratio is simple
            if (from.getBackingMaterial().equalsIgnoreCase(to.getBackingMaterial())) {
                return fromMetalPerUnit / toMetalPerUnit;
            } else {
                // Different metals, use market value logic
                double fromMetalValue = getMarketValue(from.getBackingMaterial(), metalPool);
                double toMetalValue = getMarketValue(to.getBackingMaterial(), metalPool);
                return (fromMetalValue * fromMetalPerUnit) / (toMetalValue * toMetalPerUnit);
            }
        }

        // One or both fiat: implement floating rate rules or just default to 1.0
        return 1.0;
    }

    /**
     * Gets the relative value of a metal based on world supply.
     * For example, scarcer metals are more valuable.
     * (You can make this more sophisticated: use price history, supply/demand, etc.)
     */
    private double getMarketValue(String metal, MetalPool metalPool) {
        double supply = metalPool.getSupply(metal);
        if (supply <= 0.0) return 1_000_000.0; // Arbitrary high value for zero supply
        return 1.0 / supply;
    }
}
