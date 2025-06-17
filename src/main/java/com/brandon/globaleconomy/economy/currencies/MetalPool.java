package com.brandon.globaleconomy.economy.currencies;

import java.util.HashMap;
import java.util.Map;

public class MetalPool {
    private final Map<String, Double> supplies = new HashMap<>();

    public void addMetal(String metal, double amount) {
        supplies.put(metal.toLowerCase(), supplies.getOrDefault(metal.toLowerCase(), 0.0) + amount);
    }

    public void removeMetal(String metal, double amount) {
        supplies.put(metal.toLowerCase(), Math.max(0.0, supplies.getOrDefault(metal.toLowerCase(), 0.0) - amount));
    }

    public double getSupply(String metal) {
        return supplies.getOrDefault(metal.toLowerCase(), 0.0);
    }

    public Map<String, Double> getAllSupplies() {
        return new HashMap<>(supplies);
    }
}
