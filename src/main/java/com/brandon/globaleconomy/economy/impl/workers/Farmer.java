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
import org.bukkit.World;
import org.bukkit.entity.Player;
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

    private static final Random RANDOM = new Random();
    private long lastWorkTime = 0;
    private static final long COOLDOWN_MS = 15000;
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

        Material seed = getSeedFromCrop(crop);
        List<Location> fertilePlots = city.getFertilePlots(crop);
        if (fertilePlots.isEmpty()) return;

        Location targetPlot = fertilePlots.get(RANDOM.nextInt(fertilePlots.size()));

        NPC npc = CitizensAPI.getNPCRegistry().getByUniqueId(npcId);
        if (npc == null || !npc.isSpawned()) return;

        npc.getNavigator().setTarget(targetPlot);

        new BukkitRunnable() {
            @Override
            public void run() {
                double multiplier = getProductivityMultiplier(targetPlot);
                int cropHarvested = (int) Math.round((1 + RANDOM.nextInt(2)) * multiplier);
                int seedsHarvested = RANDOM.nextInt(4);
                int seedsKept = Math.min(seedsHarvested, 2);
                addToInventory(seed, seedsKept);

                Location soil = targetPlot.clone().subtract(0, 1, 0);

                // Optional: Require hoe from city inventory
                /*
                if (!city.hasItem(Material.STONE_HOE)) {
                    Bukkit.getLogger().info("[Farmer] " + name + " needs a hoe to till soil.");
                    return;
                } else {
                    city.removeItem(Material.STONE_HOE, 1);
                }
                */

                // Hoe the ground if not already farmland
                if (soil.getBlock().getType() != Material.FARMLAND) {
                    boolean hoed = tillSoil(soil);
                    if (!hoed) return;
                }

                // Replant if seed is available
                if (personalInventory.getOrDefault(seed, 0) > 0) {
                    targetPlot.getBlock().setType(crop);
                    personalInventory.put(seed, personalInventory.get(seed) - 1);
                }

                // Return to town center
                npc.getNavigator().setTarget(city.getLocation());

                // Add harvested crop to city inventory
                city.addItem(crop, cropHarvested);

                MarketItem item = MarketAPI.getInstance().getItem(crop);
                if (item != null) {
                    double price = item.getCurrentPrice();
                    double earnings = price * cropHarvested;
                    city.depositToTreasury(city.getEffectiveCurrency(null), earnings);
                    Bukkit.getLogger().info("[Farmer] " + name + " harvested " + cropHarvested + "x " + crop + " and earned " + earnings);
                }

                animateFarming(city);
            }
        }.runTaskLater(PluginCore.getInstance(), 60L);
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

    private double getProductivityMultiplier(Location loc) {
        Material soil = loc.clone().subtract(0, 1, 0).getBlock().getType();
        if (soil == Material.FARMLAND) return 1.2;
        if (soil == Material.DIRT) return 0.9;
        return 1.0;
    }

    private boolean tillSoil(Location loc) {
        Material ground = loc.getBlock().getType();
        Material above = loc.clone().add(0, 1, 0).getBlock().getType();

        if ((ground == Material.DIRT || ground == Material.GRASS_BLOCK || ground == Material.COARSE_DIRT)
                && above == Material.AIR) {
            loc.getBlock().setType(Material.FARMLAND);
            Bukkit.getLogger().info("[Farmer] Tilled soil at " + loc);
            return true;
        }
        return false;
    }

    private void animateFarming(City city) {
        NPC npc = CitizensAPI.getNPCRegistry().getByUniqueId(npcId);
        if (npc != null && npc.isSpawned()) {
            Location target = city.getLocation().clone().add(2 - RANDOM.nextInt(5), 0, 2 - RANDOM.nextInt(5));
            npc.getNavigator().setTarget(target);
            if (npc.getEntity() instanceof Player playerEntity) {
                playerEntity.swingMainHand();
            }
        }
    }
}
