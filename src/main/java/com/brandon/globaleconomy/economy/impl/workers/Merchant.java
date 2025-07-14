package com.brandon.globaleconomy.economy.impl.workers;

import com.brandon.globaleconomy.city.City;
import com.brandon.globaleconomy.core.PluginCore;
import com.brandon.globaleconomy.economy.market.MarketAPI;
import com.brandon.globaleconomy.economy.market.MarketItem;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Merchant extends Worker {
    private long lastWorkTime = 0;
    private static final long MERCHANT_COOLDOWN_MS = 20000; // 20 seconds

    public Merchant(City city, String name, UUID npcId) {
        super(city, name, WorkerRole.MERCHANT, npcId);
    }

    @Override
    public void performWork(City city) {
        if (System.currentTimeMillis() - lastWorkTime < MERCHANT_COOLDOWN_MS) return;
        lastWorkTime = System.currentTimeMillis();

        Bukkit.getLogger().info("[Merchant] " + getName() + " performWork started for city: " + city.getName());

        Map<String, Integer> stock = city.getResources();
        if (stock == null || stock.isEmpty()) {
            Bukkit.getLogger().warning("[Merchant] " + getName() + " found no resources to sell.");
            return;
        }

        List<String> tradable = stock.keySet().stream()
                .filter(k -> {
                    Material mat = Material.matchMaterial(k);
                    boolean valid = mat != null && MarketAPI.getInstance().getItem(mat) != null;
                    if (!valid) {
                        Bukkit.getLogger().info("[Merchant] Skipped invalid or unregistered item: " + k);
                    }
                    return valid;
                })
                .filter(k -> stock.get(k) > 2)
                .toList();

        if (tradable.isEmpty()) {
            Bukkit.getLogger().info("[Merchant] " + getName() + " found no tradable items with quantity > 2.");
            return;
        }

        Collections.shuffle(tradable);
        int itemsToSell = 1 + new Random().nextInt(3);
        List<String> batch = tradable.subList(0, Math.min(itemsToSell, tradable.size()));

        Bukkit.getLogger().info("[Merchant] " + getName() + " selected items: " + batch);

        new BukkitRunnable() {
            @Override
            public void run() {
                double totalEarnings = 0;

                for (String itemName : batch) {
                    Material mat = Material.matchMaterial(itemName);
                    if (mat == null) {
                        Bukkit.getLogger().warning("[Merchant] Invalid material: " + itemName);
                        continue;
                    }

                    int quantity = Math.min(stock.get(itemName), 4);
                    if (quantity <= 0) continue;

                    MarketItem item = MarketAPI.getInstance().getItem(mat);
                    if (item == null) {
                        Bukkit.getLogger().warning("[Merchant] No MarketItem found for " + mat);
                        continue;
                    }

                    double revenue = quantity * item.getCurrentPrice();
                    city.removeItem(mat, quantity);
                    totalEarnings += revenue;

                    Bukkit.getLogger().info("[Merchant] " + getName() + " sold " + quantity + "x " + mat.name() + " for " + revenue);
                }

                if (totalEarnings > 0) {
                    city.depositToTreasury(city.getEffectiveCurrency(null), totalEarnings);
                    Bukkit.getLogger().info("[Merchant] " + getName() + " deposited " + totalEarnings + " to treasury.");
                }

                NPC npc = CitizensAPI.getNPCRegistry().getByUniqueId(npcId);
                if (npc != null && npc.isSpawned()) {
                    Location target = city.getLocation().clone().add(2 - new Random().nextInt(5), 0, 2 - new Random().nextInt(5));
                    npc.getNavigator().setTarget(target);
                    if (npc.getEntity() instanceof Player playerEntity) {
                        playerEntity.swingMainHand();
                    }
                    npc.setName(getName() + " §e(Trading)");
                }

                markCooldown();
            }
        }.runTaskLater(PluginCore.getInstance(), 40L); // Delay to simulate activity
    }
}
