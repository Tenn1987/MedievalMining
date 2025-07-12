package com.brandon.globaleconomy.economy.impl.workers;

import com.brandon.globaleconomy.city.City;
import com.brandon.globaleconomy.economy.market.MarketAPI;
import com.brandon.globaleconomy.economy.market.MarketItem;
import com.brandon.globaleconomy.core.PluginCore;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Digger extends Worker {
    private static final List<String> DIGGABLES = List.of("CLAY", "MUD", "DRIPSTONE_BLOCK", "GRAVEL", "SAND");
    private static final long COOLDOWN_MS = 8000; // 8 seconds
    private long lastWorkTime = 0;

    public Digger(City city, String name, UUID npcId) {
        super(city, name, WorkerRole.DIGGER, npcId);
    }

    @Override
    public void performWork(City city) {
        if (System.currentTimeMillis() - lastWorkTime < COOLDOWN_MS) return;
        lastWorkTime = System.currentTimeMillis();

        String resource = getBestDiggable(city);
        if (resource == null) return;

        int amount = 1 + new Random().nextInt(3); // 1–3 gathered
        int kept = 1;
        int sold = Math.max(0, amount - kept);

        new BukkitRunnable() {
            @Override
            public void run() {
                boolean success = city.getProductionManager().produce(Digger.this, resource, sold);
                if (!success) {
                    Bukkit.getLogger().info(name + " was blocked from producing " + sold + "x " + resource);
                    return;
                }

                // Add to city inventory
                Material mat = resource.equals("CLAY") ? Material.CLAY_BALL : Material.getMaterial(resource);
                int yield = resource.equals("CLAY") ? sold * 4 : sold;
                if (mat != null && yield > 0) {
                    city.addItem(mat, yield);
                }

                // Attempt to store in chest
                if (mat != null && city.getChestLocation() != null) {
                    var state = city.getChestLocation().getBlock().getState();
                    if (state instanceof Chest chest) {
                        chest.getInventory().addItem(new ItemStack(mat, yield));
                    }
                }

                // Treasury income
                MarketItem item = MarketAPI.getInstance().getItem(mat);
                if (item != null) {
                    double price = item.getCurrentPrice();
                    double earnings = price * yield;
                    city.depositToTreasury(city.getEffectiveCurrency(null), earnings);
                    Bukkit.getLogger().info(name + " dug " + yield + "x " + mat + " and earned " + earnings);
                }

                markCooldown();
            }
        }.runTaskLater(PluginCore.getInstance(), 60L); // 3-second delay
    }

    private String getBestDiggable(City city) {
        Map<String, Integer> scanned = city.getResources();
        Map<String, Integer> available = new HashMap<>();

        int total = 0;
        for (String res : DIGGABLES) {
            int count = scanned.getOrDefault(res, 0);
            if (count > 0) {
                available.put(res, count);
                total += count;
            }
        }

        if (available.isEmpty()) return null;

        int roll = new Random().nextInt(total);
        int cumulative = 0;
        for (Map.Entry<String, Integer> entry : available.entrySet()) {
            cumulative += entry.getValue();
            if (roll < cumulative) {
                return entry.getKey();
            }
        }

        return null; // Fallback, though it shouldn't happen
    }


}
