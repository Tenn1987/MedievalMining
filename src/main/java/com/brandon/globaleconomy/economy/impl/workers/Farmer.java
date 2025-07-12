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
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Farmer extends Worker {
    private static final List<Material> CROPS = List.of(
            Material.WHEAT,
            Material.CARROTS,
            Material.POTATOES,
            Material.BEETROOTS,
            Material.PUMPKIN,
            Material.MELON
    );

    private long lastWorkTime = 0;
    private static final long COOLDOWN_MS = 15000; // 15 seconds
    private final Map<Material, Integer> personalInventory = new HashMap<>();

    public Farmer(City city, String name, UUID npcId) {
        super(city, name, WorkerRole.FARMER, npcId);
    }

    @Override
    public void performWork(City city) {
        if (System.currentTimeMillis() - lastWorkTime < COOLDOWN_MS) return;
        lastWorkTime = System.currentTimeMillis();

        Material crop = getBestCrop(city);
        if (crop == null) return;

        new BukkitRunnable() {
            @Override
            public void run() {
                // Simulate harvesting
                int cropHarvested = 1 + new Random().nextInt(2); // 1–2 crop
                int seedsHarvested = new Random().nextInt(4);    // 0–3 seeds

                int seedsKept = Math.min(seedsHarvested, 2);     // Keep some seeds
                int cropSold = cropHarvested;

                // Update personal inventory
                addToInventory(getSeedFromCrop(crop), seedsKept);

                // Add to city inventory and sell
                city.addItem(crop, cropSold);

                MarketItem item = MarketAPI.getInstance().getItem(crop);
                if (item != null) {
                    double price = item.getCurrentPrice();
                    double earnings = price * cropSold;
                    city.depositToTreasury(city.getEffectiveCurrency(null), earnings);
                    Bukkit.getLogger().info(name + " harvested " + cropSold + "x " + crop + " and earned " + earnings);
                }

                // Simulate hoe/plant visuals
                animateFarming();

                markCooldown();
            }
        }.runTaskLater(PluginCore.getInstance(), 60L); // 3 seconds delay
    }

    private Material getBestCrop(City city) {
        Map<String, Integer> scanned = city.getResources();
        return CROPS.stream()
                .filter(mat -> scanned.getOrDefault(mat.name(), 0) > 0)
                .findFirst()
                .orElse(null);
    }

    private Material getSeedFromCrop(Material crop) {
        return switch (crop) {
            case WHEAT -> Material.WHEAT_SEEDS;
            case BEETROOTS -> Material.BEETROOT_SEEDS;
            case PUMPKIN -> Material.PUMPKIN_SEEDS;
            case MELON -> Material.MELON_SEEDS;
            case CARROTS -> Material.CARROT;
            case POTATOES -> Material.POTATO;
            default -> null;
        };
    }

    private void addToInventory(Material item, int amount) {
        if (item == null || amount <= 0) return;
        personalInventory.put(item, personalInventory.getOrDefault(item, 0) + amount);
    }

    public Map<Material, Integer> getPersonalInventory() {
        return personalInventory;
    }

    private void animateFarming() {
        NPC npc = CitizensAPI.getNPCRegistry().getByUniqueId(npcId);
        if (npc != null && npc.isSpawned()) {
            Location target = city.getLocation().clone().add(2 - new Random().nextInt(5), 0, 2 - new Random().nextInt(5));
            npc.getNavigator().setTarget(target); // Walk to random spot near town center
            if (npc.getEntity() instanceof Player playerEntity) {
                playerEntity.swingMainHand();
            }
        }
    }
}
