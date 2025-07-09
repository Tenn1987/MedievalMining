package com.brandon.globaleconomy.economy.market;

import org.bukkit.Material;

public class MarketDataLoader {

    public static void loadDefaults(MarketAPI marketAPI) {
        registerItem(marketAPI, Material.WHEAT, 0.50, 0.35, 100.00);
        registerItem(marketAPI, Material.CARROT, 0.45, 0.30, 99.95);
        registerItem(marketAPI, Material.POTATO, 0.40, 0.25, 99.90);
        registerItem(marketAPI, Material.BEETROOT, 0.35, 0.20, 99.80);
        registerItem(marketAPI, Material.PUMPKIN, 0.60, 0.40, 100.20);
        registerItem(marketAPI, Material.MELON_SLICE, 0.40, 0.25, 100.90);
        registerItem(marketAPI, Material.APPLE,  0.55, 0.30, 100.00);
        registerItem(marketAPI, Material.BREAD,  0.60, 0.45, 100.10);
        registerItem(marketAPI, Material.HAY_BLOCK,2.00, 1.50, 400.00);
        registerItem(marketAPI, Material.SUGAR,   0.25, 0.15, 100.60);
        registerItem(marketAPI, Material.EGG,     0.30, 0.20, 100.60);
        registerItem(marketAPI, Material.COOKED_BEEF,  1.20, 0.80, 200.50);
        registerItem(marketAPI, Material.COOKED_CHICKEN,1.00, 0.70, 200.20);
        registerItem(marketAPI, Material.COOKED_PORKCHOP,1.10, 0.75, 200.30);
        registerItem(marketAPI, Material.COOKED_MUTTON,0.95, 0.65, 200.00);
        registerItem(marketAPI, Material.COOKED_COD,   0.85, 0.55, 100.80);
        registerItem(marketAPI, Material.COOKED_SALMON,1.00, 0.70, 200.20);
        registerItem(marketAPI, Material.PORKCHOP, 0.55, 0.40, 100.10);
        registerItem(marketAPI, Material.BEEF,     0.65, 0.45, 100.30);
        registerItem(marketAPI, Material.MUTTON,   0.50, 0.35, 100.00);
        registerItem(marketAPI, Material.CHICKEN,  0.50, 0.35, 100.00);
        registerItem(marketAPI, Material.MILK_BUCKET,1.50, 1.00, 300.00);
        registerItem(marketAPI, Material.OAK_LOG,  0.40, 0.30, 100.90);
        registerItem(marketAPI, Material.SPRUCE_LOG,0.40, 0.30, 100.90);
        registerItem(marketAPI, Material.BIRCH_LOG, 0.40, 0.30, 100.90);
        registerItem(marketAPI, Material.JUNGLE_LOG,0.40, 0.30, 100.90);
        registerItem(marketAPI, Material.ACACIA_LOG,0.40, 0.30, 100.90);
        registerItem(marketAPI, Material.DARK_OAK_LOG,0.40, 0.30, 100.90);
        registerItem(marketAPI, Material.OAK_PLANKS, 0.30, 0.20, 100.60);
        registerItem(marketAPI, Material.SPRUCE_PLANKS, 0.30, 0.20, 100.60);
        registerItem(marketAPI, Material.BIRCH_PLANKS, 0.30, 0.20, 100.60);
        registerItem(marketAPI, Material.JUNGLE_PLANKS, 0.30, 0.20, 100.60);
        registerItem(marketAPI, Material.ACACIA_PLANKS, 0.30, 0.20, 100.60);
        registerItem(marketAPI, Material.DARK_OAK_PLANKS, 0.30, 0.20, 100.60);
        registerItem(marketAPI, Material.MANGROVE_PLANKS, 0.30, 0.20, 100.60);
        registerItem(marketAPI, Material.CHERRY_PLANKS, 0.30, 0.20, 100.60);
        registerItem(marketAPI, Material.BAMBOO_PLANKS, 0.30, 0.20, 100.60);
        registerItem(marketAPI, Material.COBBLESTONE, 0.25, 0.15, 100.60);
        registerItem(marketAPI, Material.STONE,       0.30, 0.20, 100.70);
        registerItem(marketAPI, Material.SANDSTONE,   0.35, 0.25, 100.80);
        registerItem(marketAPI, Material.DEEPSLATE,   0.30, 0.20, 100.70);
        registerItem(marketAPI, Material.GRANITE,     0.30, 0.20, 100.70);
        registerItem(marketAPI, Material.DIORITE,     0.30, 0.20, 100.70);
        registerItem(marketAPI, Material.ANDESITE,    0.30, 0.20, 100.70);
        registerItem(marketAPI, Material.SAND,        0.20, 0.10, 100.50);
        registerItem(marketAPI, Material.GRAVEL,      0.20, 0.10, 100.50);
        registerItem(marketAPI, Material.CLAY,        0.25, 0.15, 100.60);
// Nuggets
        registerItem(marketAPI, Material.IRON_NUGGET, 0.10, 0.05, 100.25);
        registerItem(marketAPI, Material.GOLD_NUGGET, 0.15, 0.08, 100.30);

// Ingots
        registerItem(marketAPI, Material.IRON_INGOT, 0.90, 0.60, 1000.80);
        registerItem(marketAPI, Material.GOLD_INGOT, 1.20, 0.85, 2000.20);
        registerItem(marketAPI, Material.COPPER_INGOT, 0.70, 0.40, 1000.50);

// Blocks (Fe, Au, Cu)
        registerItem(marketAPI, Material.IRON_BLOCK, 8.00, 6.00, 12000.00);
        registerItem(marketAPI, Material.GOLD_BLOCK, 10.00, 7.50, 15000.00);
        registerItem(marketAPI, Material.COPPER_BLOCK, 6.50, 4.50, 10000.00);
        registerItem(marketAPI, Material.COAL,             0.70, 0.50, 1000.60);
        registerItem(marketAPI, Material.REDSTONE,         0.60, 0.40, 1000.40);
        registerItem(marketAPI, Material.LAPIS_LAZULI,     0.65, 0.45, 1000.50);
        registerItem(marketAPI, Material.DIAMOND,          5.00, 4.00, 8000.00);
        registerItem(marketAPI, Material.EMERALD,          4.00, 3.00, 7000.00);
    }

    private static void registerItem(MarketAPI api, Material mat, double basePrice, double minPrice, double maxPrice) {
        api.registerItem(new MarketItem(mat, basePrice, minPrice, maxPrice));
    }
}
