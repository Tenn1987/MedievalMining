package com.brandon.globaleconomy.npc.impl;

import com.brandon.globaleconomy.city.City;
import com.brandon.globaleconomy.economy.impl.workers.Worker;
import com.brandon.globaleconomy.economy.impl.workers.WorkerRole;
import com.brandon.globaleconomy.gui.WorkerTradeGUI;
import com.brandon.globaleconomy.core.PluginCore;
import com.brandon.globaleconomy.economy.impl.workers.WorkerFactory;
import net.citizensnpcs.api.ai.Navigator;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.util.DataKey;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import java.util.UUID;

public class WorkerTrait extends Trait {
    private Worker worker;

    public WorkerTrait() {
        super("workertrait");
    }

    public void setWorker(Worker worker) {
        this.worker = worker;
    }

    public Worker getWorker() {
        return worker;
    }

    @Override
    public void run() {
        if (npc == null || !npc.isSpawned()) return;

        // Log for debugging
        Bukkit.getLogger().info("[DEBUG] WorkerTrait.run() fired.");
        Bukkit.getLogger().info("[DEBUG] NPC is spawned: " + getNPC().getName());

        // Move NPC to target location
        Location targetLocation = getTargetLocation();
        moveNPCToLocation(targetLocation);

        // Check and perform work if the worker is ready
        if (worker != null && worker.isReadyToWork()) {
            try {
                worker.performWork(worker.getCity());
            } catch (Exception e) {
                Bukkit.getLogger().warning("[WorkerTrait] Error during performWork: " + e.getMessage());
            }
        }
    }


    private void moveNPCToLocation(Location targetLocation) {
        if (npc.isSpawned()) {
            // Move the NPC using Citizens' Navigator
            Navigator navigator = npc.getNavigator();  // Use the correct class
            navigator.setTarget(targetLocation);  // Set the target location for movement
        }
    }

    private Location getTargetLocation() {
        if (worker == null) return npc.getEntity().getLocation(); // Safe fallback
        City city = worker.getCity();
        return city != null ? city.getLocation() : npc.getEntity().getLocation();
    }


    @Override
    public boolean isRunImplemented() {
        return true;
    }

    @Override
    public void save(DataKey key) {
        if (worker != null) {
            key.setString("role", worker.getRole().name());
            key.setString("name", worker.getName());
            key.setString("city", worker.getCity().getName());
            key.setString("uuid", worker.getNpcId().toString());
        }
    }

    @Override
    public void load(DataKey key) {
        try {
            String roleStr = key.getString("role");
            String name = key.getString("name");
            String cityName = key.getString("city");
            String uuidStr = key.getString("uuid");

            if (roleStr == null || name == null || cityName == null || uuidStr == null) return;

            WorkerRole role = WorkerRole.valueOf(roleStr);
            UUID npcId = UUID.fromString(uuidStr);

            Bukkit.getScheduler().runTaskLater(JavaPlugin.getProvidingPlugin(getClass()), () -> {
                City city = PluginCore.getInstance().getCityManager().getCity(cityName);
                if (city != null) {
                    this.worker = WorkerFactory.createWorker(role, city, name, npcId);
                } else {
                    System.err.println("[WorkerTrait] Could not find city '" + cityName + "' even after delayed lookup.");
                }
            }, 20L); // Delay 1 second

        } catch (Exception e) {
            System.err.println("[WorkerTrait] Error loading trait data: " + e.getMessage());
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
        if (npc != null && event.getRightClicked().getUniqueId().equals(npc.getUniqueId())) {
            event.setCancelled(true);

            if (worker != null) {
                WorkerTradeGUI.open(event.getPlayer(), worker);
            }
        }
    }
}
