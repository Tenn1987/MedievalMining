package com.brandon.globaleconomy.economy.impl.workers;

import com.brandon.globaleconomy.city.City;
import com.brandon.globaleconomy.core.PluginCore;
import com.brandon.globaleconomy.economy.market.MarketAPI;
import com.brandon.globaleconomy.economy.market.MarketItem;
import com.brandon.globaleconomy.diplomacy.DiplomaticStatus;
import com.brandon.globaleconomy.nations.DiplomacyManager;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Caravaner extends Worker {
    private long lastWorkTime = 0;
    private static final long COOLDOWN_MS = 30000; // 30 seconds

    public Caravaner(City city, String name, UUID npcId) {
        super(city, name, WorkerRole.CARAVANER, npcId);
    }

    @Override
    public void performWork(City city) {
        if (System.currentTimeMillis() - lastWorkTime < COOLDOWN_MS) return;
        lastWorkTime = System.currentTimeMillis();

        City target = findNearestFriendlyCity(city);
        if (target == null) return;

        Bukkit.getLogger().info("[Caravaner] " + name + " is traveling from " + city.getName() + " to " + target.getName());

        new BukkitRunnable() {
            @Override
            public void run() {
                Map<Material, Integer> exports = getSurplus(city, 16);
                Map<Material, Integer> imports = getNeeded(target, exports.keySet(), 12);

                if (exports.isEmpty() || imports.isEmpty()) {
                    Bukkit.getLogger().info("[Caravaner] " + name + " found no viable trade between " + city.getName() + " and " + target.getName());
                    return;
                }

                for (Map.Entry<Material, Integer> export : exports.entrySet()) {
                    Material item = export.getKey();
                    int amount = export.getValue();
                    city.removeItem(item, amount);
                    target.addItem(item, amount);
                }

                for (Map.Entry<Material, Integer> imp : imports.entrySet()) {
                    Material item = imp.getKey();
                    int amount = imp.getValue();
                    target.removeItem(item, amount);
                    city.addItem(item, amount);
                }

                NPC npc = CitizensAPI.getNPCRegistry().getByUniqueId(npcId);
                if (npc != null && npc.isSpawned()) {
                    npc.getNavigator().setTarget(target.getLocation());
                }

                Bukkit.getLogger().info("[Caravaner] " + name + " completed trade: " + exports.size() + " exports and " + imports.size() + " imports.");

                markCooldown();
            }
        }.runTaskLater(PluginCore.getInstance(), 80L); // 4-second delay
    }

    private City findNearestFriendlyCity(City origin) {
        double bestDistance = Double.MAX_VALUE;
        City closest = null;

        for (City other : PluginCore.getInstance().getCityManager().getCities().values()) {
            if (other.equals(origin)) continue;

            DiplomaticStatus status = DiplomacyManager.getInstance().getRelation(origin.getNation(), other.getNation());
            if (status != DiplomaticStatus.FRIENDLY) continue;

            double dist = origin.getLocation().distance(other.getLocation());
            if (dist < bestDistance) {
                bestDistance = dist;
                closest = other;
            }
        }

        return closest;
    }

    private Map<Material, Integer> getSurplus(City city, int threshold) {
        Map<Material, Integer> surplus = new HashMap<>();
        for (Map.Entry<String, Integer> entry : city.getResources().entrySet()) {
            Material mat = Material.getMaterial(entry.getKey());
            if (mat != null && entry.getValue() > threshold) {
                surplus.put(mat, entry.getValue() - threshold);
            }
        }
        return surplus;
    }

    private Map<Material, Integer> getNeeded(City target, Set<Material> available, int minAmount) {
        Map<Material, Integer> needs = new HashMap<>();
        for (Material item : available) {
            int amount = target.getResources().getOrDefault(item.name(), 0);
            if (amount < minAmount) {
                needs.put(item, minAmount - amount);
            }
        }
        return needs;
    }
}
