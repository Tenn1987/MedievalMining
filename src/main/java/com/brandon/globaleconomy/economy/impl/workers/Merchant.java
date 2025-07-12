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

        Map<String, Integer> stock = city.getResources();
        if (stock.isEmpty()) return;

        List<String> tradable = stock.keySet().stream()
                .filter(k -> MarketAPI.getInstance().getItem(Material.matchMaterial(k)) != null)
                .filter(k -> stock.get(k) > 2) // Avoid selling all of something
                .toList();

        if (tradable.isEmpty()) return;

        // Random selection of 1–3 items to sell
        Collections.shuffle(tradable);
        int itemsToSell = 1 + new Random().nextInt(3);
        List<String> batch = tradable.subList(0, Math.min(itemsToSell, tradable.size()));

        new BukkitRunnable() {
            @Override
            public void run() {
                double totalEarnings = 0;

                for (String itemName : batch) {
                    Material mat = Material.matchMaterial(itemName);
                    if (mat == null) continue;

                    int quantity = Math.min(stock.get(itemName), 4); // Sell up to 4
                    if (quantity <= 0) continue;

                    MarketItem item = MarketAPI.getInstance().getItem(mat);
                    if (item == null) continue;

                    double revenue = quantity * item.getCurrentPrice();
                    city.removeItem(mat, quantity);
                    totalEarnings += revenue;

                    Bukkit.getLogger().info(name + " sold " + quantity + "x " + mat + " for " + revenue);
                }

                if (totalEarnings > 0) {
                    city.depositToTreasury(city.getEffectiveCurrency(null), totalEarnings);
                }

                // Visual feedback
                NPC npc = CitizensAPI.getNPCRegistry().getByUniqueId(npcId);
                if (npc != null && npc.isSpawned()) {
                    Location target = city.getLocation().clone().add(2 - new Random().nextInt(5), 0, 2 - new Random().nextInt(5));
                    npc.getNavigator().setTarget(target);
                    if (npc.getEntity() instanceof Player playerEntity) {
                        playerEntity.swingMainHand();
                    }
                }

                markCooldown();
            }
        }.runTaskLater(PluginCore.getInstance(), 40L); // 2-second delay before transaction
    }
}
