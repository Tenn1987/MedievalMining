package com.brandon.globaleconomy.economy.market;

import org.bukkit.Material;

public class MarketItem {
    private final Material material;
    private final double minPrice;
    private final double maxPrice;
    private double currentPrice;
    private double sellPrice; // ← Add this field

    public MarketItem(Material material, double currentPrice, double minPrice, double maxPrice) {
        this.material = material;
        this.currentPrice = currentPrice;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.sellPrice = currentPrice * 0.75; // ← Default sell price is 75% of currentPrice
    }

    public Material getMaterial() {
        return material;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
        this.sellPrice = currentPrice * 0.75; // Keep sell price updated dynamically
    }

    public double getMinPrice() {
        return minPrice;
    }

    public double getMaxPrice() {
        return maxPrice;
    }

    public double getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(double sellPrice) {
        this.sellPrice = sellPrice;
    }
}
