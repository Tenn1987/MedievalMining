package com.brandon.globaleconomy.economy.currencies;

import org.bukkit.Material;

public class Currency {
    private final String code;
    private final Material backing;
    private double value;
    private final double stability;

    public Currency(String code, Material backing, double initialValue, double stability) {
        this.code = code.toUpperCase();
        this.backing = backing;
        this.value = initialValue;
        this.stability = stability;
    }

    public String getCode() {
        return code;
    }

    public Material getBacking() {
        return backing;
    }

    public double getValue() {
        return value;
    }

    public double getStability() {
        return stability;
    }

    public void adjustValueFromBacking(int backingAmount) {
        if (backingAmount <= 0) return;
        double newValue = (backingAmount * 0.01); // Example: 1 value per 100 backing
        value = (value * stability + newValue) / (stability + 1);
    }

    public void setValue(double value) {
        this.value = value;
    }
}
