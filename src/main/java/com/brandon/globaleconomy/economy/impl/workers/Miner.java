package com.brandon.globaleconomy.economy.impl.workers;

import com.brandon.globaleconomy.city.City;
import com.brandon.globaleconomy.core.PluginCore;
import net.citizensnpcs.api.npc.NPC;
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

public class Miner extends Worker {
    private long lastWorkTime = 0;
    private static final long COOLDOWN_MS = 15000; // 15 seconds

    public Miner(City city, String name, UUID npcId) {
        super(city, name, WorkerRole.MINER, npcId);
    }

    @Override
    public void performWork(City city) {
        if (System.currentTimeMillis() - lastWorkTime < COOLDOWN_MS) return;
        lastWorkTime = System.currentTimeMillis();

        List<String> ORES = List.of(
                "COAL_ORE", "IRON_ORE", "COPPER_ORE", "GOLD_ORE",
                "LAPIS_ORE", "REDSTONE_ORE", "DIAMOND_ORE",
                "STONE", "DIORITE", "GRANITE", "SANDSTONE"
        );

        Map<String, Integer> scanned = city.getResources();

        List<String> available = ORES.stream()
                .filter(scanned::containsKey)
                .filter(k -> scanned.get(k) > 0)
                .toList();

        if (available.isEmpty()) return;

        String chosenOre = available.get(new Random().nextInt(available.size()));
        int amount = 3 + new Random().nextInt(3); // 3–5

        // Animate movement (optional)
        NPC npc = getNpc();
        if (npc != null && npc.isSpawned()) {
            Location target = city.getLocation().clone().add(new Random().nextInt(5) - 2, 0, new Random().nextInt(5) - 2);
            npc.getNavigator().setTarget(target);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!city.getProductionManager().produce(Miner.this, chosenOre, amount)) {
                    Bukkit.getLogger().info(name + " was blocked from mining " + amount + "x " + chosenOre);
                    return;
                }

                Material drop = switch (chosenOre) {
                    case "STONE" -> Material.COBBLESTONE;
                    case "COAL_ORE" -> Material.COAL;
                    case "IRON_ORE" -> Material.RAW_IRON;
                    case "COPPER_ORE" -> Material.RAW_COPPER;
                    case "GOLD_ORE" -> Material.RAW_GOLD;
                    case "LAPIS_ORE" -> Material.LAPIS_LAZULI;
                    case "REDSTONE_ORE" -> Material.REDSTONE;
                    case "DIAMOND_ORE" -> Material.DIAMOND;
                    default -> Material.valueOf(chosenOre); // fallback
                };

                int adjustedAmount = switch (chosenOre) {
                    case "LAPIS_ORE" -> amount * (4 + new Random().nextInt(6));     // 4–9
                    case "REDSTONE_ORE" -> amount * (4 + new Random().nextInt(2));  // 4–5
                    default -> amount;
                };

                city.addItem(drop, adjustedAmount);

                Location chestLoc = city.getChestLocation();
                if (chestLoc != null && chestLoc.getBlock().getState() instanceof Chest chest) {
                    chest.getBlockInventory().addItem(new ItemStack(drop, adjustedAmount));
                }

                Bukkit.getLogger().info(name + " mined " + adjustedAmount + "x " + drop);
                markCooldown();
            }
        }.runTaskLater(PluginCore.getInstance(), 60L); // 3 seconds
    }
}
