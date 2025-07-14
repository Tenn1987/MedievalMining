package com.brandon.globaleconomy.economy.impl.workers;

import com.brandon.globaleconomy.city.City;
import com.brandon.globaleconomy.core.PluginCore;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class Woodsman extends Worker {
    private static final long COOLDOWN_MS = 15000; // 15 seconds
    private static final Random RANDOM = new Random();
    private long lastWorkTime = 0;

    private static final List<Material> LOG_TYPES = List.of(
            Material.OAK_LOG, Material.BIRCH_LOG, Material.SPRUCE_LOG,
            Material.JUNGLE_LOG, Material.ACACIA_LOG, Material.DARK_OAK_LOG,
            Material.CHERRY_LOG, Material.MANGROVE_LOG
    );

    public Woodsman(City city, String name, UUID npcId) {
        super(city, name, WorkerRole.WOODSMAN, npcId);
    }

    @Override
    public void performWork(City city) {
        if (System.currentTimeMillis() - lastWorkTime < COOLDOWN_MS) return;
        lastWorkTime = System.currentTimeMillis();

        Map<String, Integer> resources = city.getResources();

        List<Material> availableLogs = LOG_TYPES.stream()
                .filter(mat -> resources.getOrDefault(mat.name(), 0) > 0)
                .collect(Collectors.toList());

        if (availableLogs.isEmpty()) return;

        Material selectedLog = availableLogs.get(RANDOM.nextInt(availableLogs.size()));
        int amount = 3 + RANDOM.nextInt(3); // 3–5 logs

        Location loggingSite = city.getLocation().clone().add(10, 0, 10); // Arbitrary forest edge location
        NPC npc = CitizensAPI.getNPCRegistry().getByUniqueId(npcId);
        if (npc == null || !npc.isSpawned()) return;

        // Step 1: Go to forest
        npc.getNavigator().setTarget(loggingSite);

        // Step 2: After 3 seconds, simulate harvesting and return to town
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!city.getProductionManager().produce(Woodsman.this, selectedLog.name(), amount)) {
                    Bukkit.getLogger().info("[Woodsman] " + name + " was blocked from chopping " + amount + "x " + selectedLog.name());
                    return;
                }

                city.addItem(selectedLog, amount);

                // Step 3: Return to town
                npc.getNavigator().setTarget(city.getLocation());

                // Step 4: Try depositing to city chest
                Location chestLoc = city.getChestLocation();
                if (chestLoc != null && chestLoc.getBlock().getState() instanceof Chest chest) {
                    chest.getBlockInventory().addItem(new ItemStack(selectedLog, amount));
                }

                Bukkit.getLogger().info("[Woodsman] " + name + " chopped " + amount + "x " + selectedLog.name());
                markCooldown();
            }
        }.runTaskLater(PluginCore.getInstance(), 60L); // Delay for realism
    }
}
