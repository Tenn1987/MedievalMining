package com.brandon.globaleconomy.economy.currencies;

public class NationalCurrency {
    private final String name;
    private final boolean metalBacked;
    private final String backingMaterial; // "gold", "silver", etc., or null for fiat
    private final double backingRatio;    // e.g., 1.0 means 1 unit = 1 gram
    private double supply;                // Total currency in existence

    public NationalCurrency(String name, boolean metalBacked, String backingMaterial, double backingRatio) {
        this.name = name;
        this.metalBacked = metalBacked;
        this.backingMaterial = backingMaterial;
        this.backingRatio = backingRatio;
        this.supply = 0.0;
    }

    public String getName() { return name; }
    public boolean isMetalBacked() { return metalBacked; }
    public String getBackingMaterial() { return backingMaterial; }
    public double getBackingRatio() { return backingRatio; }
    public double getSupply() { return supply; }

    public void mint(double amount) { supply += amount; }
    public void burn(double amount) { supply = Math.max(0, supply - amount); }
}
