package com.brandon.globaleconomy.economy.currencies;

import java.util.HashMap;
import java.util.Map;

public class MetalPool {
    private final Map<String, Double> supplies = new HashMap<>();
    private final Map<String, Double> basePrices = new HashMap<>(); // price per unit

    public void addMetal(String metal, double amount) {
        metal = metal.toLowerCase();
        supplies.put(metal, supplies.getOrDefault(metal, 0.0) + amount);
    }

    public void removeMetal(String metal, double amount) {
        metal = metal.toLowerCase();
        supplies.put(metal, Math.max(0.0, supplies.getOrDefault(metal, 0.0) - amount));
    }

    public double getSupply(String metal) {
        return supplies.getOrDefault(metal.toLowerCase(), 0.0);
    }

    public Map<String, Double> getAllSupplies() {
        return new HashMap<>(supplies);
    }

    public void setBasePrice(String metal, double price) {
        basePrices.put(metal.toLowerCase(), price);
    }

    public double getValueOf(String metal) {
        double basePrice = basePrices.getOrDefault(metal.toLowerCase(), 1.0);
        double supply = supplies.getOrDefault(metal.toLowerCase(), 100.0); // avoid divide-by-zero

        // Simulate rising value as supply drops
        return basePrice * (100.0 / Math.max(1.0, supply));
    }

}
