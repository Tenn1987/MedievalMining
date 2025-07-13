package com.brandon.globaleconomy.economy.market;

import org.bukkit.Material;

public class MarketItem {
    private final Material material;
    private final double minPrice;
    private final double maxPrice;
    private double currentPrice;
    private double sellPrice;

    public MarketItem(Material material, double currentPrice, double minPrice, double maxPrice) {
        this.material = material;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        setCurrentPrice(currentPrice);
    }

    public Material getMaterial() {
        return material;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
        this.sellPrice = Math.max(minPrice, currentPrice * 0.75);  // Sell price tracks 75% of market
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
