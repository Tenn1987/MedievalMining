package com.brandon.globaleconomy.economy.impl.workers;

import com.brandon.globaleconomy.city.City;
import com.brandon.globaleconomy.core.PluginCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class Woodsman extends Worker {
    private long lastWorkTime = 0;
    private static final long COOLDOWN_MS = 15000; // 15 seconds

    public Woodsman(City city, String name, UUID npcId) {
        super(city, name, WorkerRole.WOODSMAN, npcId);
    }

    @Override
    public void performWork(City city) {
        if (System.currentTimeMillis() - lastWorkTime < COOLDOWN_MS) return;
        lastWorkTime = System.currentTimeMillis();

        List<String> LOGS = List.of(
                "OAK_LOG", "BIRCH_LOG", "SPRUCE_LOG", "JUNGLE_LOG",
                "ACACIA_LOG", "DARK_OAK_LOG", "CHERRY_LOG", "MANGROVE_LOG"
        );

        Map<String, Integer> scanned = city.getResources();

        List<String> available = LOGS.stream()
                .filter(scanned::containsKey)
                .filter(k -> scanned.get(k) > 0)
                .toList();

        if (available.isEmpty()) return;

        String chosenLog = available.get(new Random().nextInt(available.size()));
        int amount = 3 + new Random().nextInt(3); // 3–5 logs

        // Delayed execution
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!city.getProductionManager().produce(Woodsman.this, chosenLog, amount)) {
                    Bukkit.getLogger().info(name + " was blocked from chopping " + amount + "x " + chosenLog);
                    return;
                }

                Material material = Material.valueOf(chosenLog);
                city.addItem(material, amount);

                Location chestLoc = city.getChestLocation();
                if (chestLoc != null && chestLoc.getBlock().getState() instanceof Chest chest) {
                    chest.getBlockInventory().addItem(new ItemStack(material, amount));
                }

                Bukkit.getLogger().info(name + " chopped " + amount + "x " + chosenLog);
                markCooldown();
            }
        }.runTaskLater(PluginCore.getInstance()
                , 60L); // Delay 3 seconds
    }
}
