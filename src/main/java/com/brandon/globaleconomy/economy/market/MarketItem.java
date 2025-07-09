package com.brandon.globaleconomy.economy.market;

import org.bukkit.Material;

public class MarketItem {
    private final Material material;
    private final double minPrice;
    private final double maxPrice;
    private double currentPrice;

    public MarketItem(Material material, double currentPrice, double minPrice, double maxPrice) {
        this.material = material;
        this.currentPrice = currentPrice;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }

    public Material getMaterial() {
        return material;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public double getMinPrice() {
        return minPrice;
    }

    public double getMaxPrice() {
        return maxPrice;
    }
}