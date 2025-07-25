
package com.brandon.globaleconomy.economy.currencies;

public class NationalCurrency {
    private final String name;
    private final boolean metalBacked;
    private final String backingMaterial;
    private final double backingRatio;
    private double supply;

    public NationalCurrency(String name, boolean metalBacked, String backingMaterial, double backingRatio) {
        this.name = name;
        this.metalBacked = metalBacked;
        this.backingMaterial = backingMaterial;
        this.backingRatio = backingRatio;
        this.supply = 0.0;
    }

    public double getValue(MetalPool metalPool) {
        if (metalBacked && backingMaterial != null && backingRatio > 0) {
            double metalPrice = metalPool.getValueOf(backingMaterial);
            return metalPrice * backingRatio;
        }
        return 1.0; // Default fiat value
    }


    public String getName() {
        return name;
    }

    public boolean isMetalBacked() {
        return metalBacked;
    }

    public String getBackingMaterial() {
        return backingMaterial;
    }

    public double getBackingRatio() {
        return backingRatio;
    }

    public double getSupply() {
        return supply;
    }

    public void setSupply(double supply) {
        this.supply = supply;
    }

    public void adjustValueFromBacking(int amount) {}
}
