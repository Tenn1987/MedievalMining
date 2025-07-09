package com.brandon.globaleconomy.economy.market;

import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MarketAPI {
    private static MarketAPI instance;
    private final Map<Material, MarketItem> itemMap = new HashMap<>();
    private final Random random = new Random();

    private MarketAPI() {
        registerDefaults();
    }

    public static MarketAPI getInstance() {
        if (instance == null) {
            instance = new MarketAPI();
        }
        return instance;
    }

    private void registerDefaults() {
        registerItem(new MarketItem(Material.WHEAT, 0.50, 0.35, 100.00));
        registerItem(new MarketItem(Material.CARROT, 0.45, 0.30, 99.95));
        registerItem(new MarketItem(Material.POTATO, 0.40, 0.25, 99.90));
        registerItem(new MarketItem(Material.BEETROOT, 0.35, 0.20, 99.80));
        registerItem(new MarketItem(Material.PUMPKIN, 0.60, 0.40, 100.20));
        registerItem(new MarketItem(Material.MELON_SLICE, 0.40, 0.25, 100.90));
        registerItem(new MarketItem(Material.APPLE, 0.55, 0.30, 100.00));
        registerItem(new MarketItem(Material.BREAD, 0.60, 0.45, 100.10));
        registerItem(new MarketItem(Material.HAY_BLOCK, 2.00, 1.50, 400.00));
        registerItem(new MarketItem(Material.SUGAR, 0.25, 0.15, 100.60));
        registerItem(new MarketItem(Material.EGG, 0.30, 0.20, 100.60));
        registerItem(new MarketItem(Material.COOKED_BEEF, 1.20, 0.80, 200.50));
        registerItem(new MarketItem(Material.COOKED_CHICKEN, 1.00, 0.70, 200.20));
        registerItem(new MarketItem(Material.COOKED_PORKCHOP, 1.10, 0.75, 200.30));
        registerItem(new MarketItem(Material.COOKED_MUTTON, 0.95, 0.65, 200.00));
        registerItem(new MarketItem(Material.COOKED_COD, 0.85, 0.55, 100.80));
        registerItem(new MarketItem(Material.COOKED_SALMON, 1.00, 0.70, 200.20));
        registerItem(new MarketItem(Material.PORKCHOP, 0.55, 0.40, 100.10));
        registerItem(new MarketItem(Material.BEEF, 0.65, 0.45, 100.30));
        registerItem(new MarketItem(Material.MUTTON, 0.50, 0.35, 100.00));
        registerItem(new MarketItem(Material.CHICKEN, 0.50, 0.35, 100.00));
        registerItem(new MarketItem(Material.MILK_BUCKET, 1.50, 1.00, 300.00));
        registerItem(new MarketItem(Material.OAK_LOG, 0.40, 0.30, 100.90));
        registerItem(new MarketItem(Material.SPRUCE_LOG, 0.40, 0.30, 100.90));
        registerItem(new MarketItem(Material.BIRCH_LOG, 0.40, 0.30, 100.90));
        registerItem(new MarketItem(Material.JUNGLE_LOG, 0.40, 0.30, 100.90));
        registerItem(new MarketItem(Material.ACACIA_LOG, 0.40, 0.30, 100.90));
        registerItem(new MarketItem(Material.DARK_OAK_LOG, 0.40, 0.30, 100.90));
        registerItem(new MarketItem(Material.OAK_PLANKS, 0.30, 0.20, 100.60));
        registerItem(new MarketItem(Material.SPRUCE_PLANKS, 0.30, 0.20, 100.60));
        registerItem(new MarketItem(Material.BIRCH_PLANKS, 0.30, 0.20, 100.60));
        registerItem(new MarketItem(Material.JUNGLE_PLANKS, 0.30, 0.20, 100.60));
        registerItem(new MarketItem(Material.ACACIA_PLANKS, 0.30, 0.20, 100.60));
        registerItem(new MarketItem(Material.DARK_OAK_PLANKS, 0.30, 0.20, 100.60));
        registerItem(new MarketItem(Material.MANGROVE_PLANKS, 0.30, 0.20, 100.60));
        registerItem(new MarketItem(Material.CHERRY_PLANKS, 0.30, 0.20, 100.60));
        registerItem(new MarketItem(Material.BAMBOO_PLANKS, 0.30, 0.20, 100.60));
        registerItem(new MarketItem(Material.COBBLESTONE, 0.25, 0.15, 100.60));
        registerItem(new MarketItem(Material.STONE, 0.30, 0.20, 100.70));
        registerItem(new MarketItem(Material.SANDSTONE, 0.35, 0.25, 100.80));
        registerItem(new MarketItem(Material.DEEPSLATE, 0.30, 0.20, 100.70));
        registerItem(new MarketItem(Material.GRANITE, 0.30, 0.20, 100.70));
        registerItem(new MarketItem(Material.DIORITE, 0.30, 0.20, 100.70));
        registerItem(new MarketItem(Material.ANDESITE, 0.30, 0.20, 100.70));
        registerItem(new MarketItem(Material.SAND, 0.20, 0.10, 100.50));
        registerItem(new MarketItem(Material.GRAVEL, 0.20, 0.10, 100.50));
        registerItem(new MarketItem(Material.CLAY, 0.25, 0.15, 100.60));
        registerItem(new MarketItem(Material.IRON_NUGGET, 0.10, 0.05, 100.25));
        registerItem(new MarketItem(Material.GOLD_NUGGET, 0.15, 0.08, 100.30));
        registerItem(new MarketItem(Material.IRON_INGOT, 0.90, 0.60, 1000.80));
        registerItem(new MarketItem(Material.GOLD_INGOT, 1.20, 0.85, 2000.20));
        registerItem(new MarketItem(Material.COPPER_INGOT, 0.70, 0.40, 1000.50));
        registerItem(new MarketItem(Material.IRON_BLOCK, 8.00, 6.00, 12000.00));
        registerItem(new MarketItem(Material.GOLD_BLOCK, 10.00, 7.50, 15000.00));
        registerItem(new MarketItem(Material.COPPER_BLOCK, 6.50, 4.50, 10000.00));
        registerItem(new MarketItem(Material.COAL, 0.70, 0.50, 1000.60));
        registerItem(new MarketItem(Material.REDSTONE, 0.60, 0.40, 1000.40));
        registerItem(new MarketItem(Material.LAPIS_LAZULI, 0.65, 0.45, 1000.50));
        registerItem(new MarketItem(Material.DIAMOND, 5.00, 4.00, 8000.00));
        registerItem(new MarketItem(Material.EMERALD, 4.00, 3.00, 7000.00));
    }


    public void registerItem(MarketItem item) {
        itemMap.put(item.getMaterial(), item);
    }

    public MarketItem getItem(Material material) {
        return itemMap.get(material);
    }

    public void updateMarket() {
        for (MarketItem item : itemMap.values()) {
            double currentPrice = item.getCurrentPrice();
            double volatility = 0.05 + random.nextDouble() * 0.05; // 5%–10% swing
            boolean increase = random.nextBoolean();
            double change = currentPrice * volatility;
            double newPrice = increase ? currentPrice + change : currentPrice - change;
            newPrice = Math.max(item.getMinPrice(), Math.min(item.getMaxPrice(), newPrice));
            item.setCurrentPrice(newPrice);
        }
    }

    public Map<Material, MarketItem> getAllItems() {
        return itemMap;
    }
}
