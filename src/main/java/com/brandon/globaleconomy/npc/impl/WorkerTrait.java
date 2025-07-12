package com.brandon.globaleconomy.npc.impl;

import com.brandon.globaleconomy.city.City;
import com.brandon.globaleconomy.core.PluginCore;
import com.brandon.globaleconomy.economy.impl.workers.Worker;
import com.brandon.globaleconomy.economy.impl.workers.WorkerFactory;
import com.brandon.globaleconomy.economy.impl.workers.WorkerRole;
import com.brandon.globaleconomy.gui.WorkerTradeGUI;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import net.citizensnpcs.api.util.DataKey;
import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

@TraitName("workertrait")
public class WorkerTrait extends Trait implements Listener {

    private Worker worker;

    public WorkerTrait() {
        super("workertrait");
        Bukkit.getPluginManager().registerEvents(this, JavaPlugin.getProvidingPlugin(getClass()));
    }

    public void setWorker(Worker worker) {
        this.worker = worker;
    }

    public Worker getWorker() {
        return worker;
    }

    @Override
    public void run() {
        if (worker != null && worker.isReadyToWork()) {
            try {
                worker.performWork(worker.getCity());
            } catch (Exception e) {
                System.err.println("[WorkerTrait] Error during performWork for " + worker.getName() + ": " + e.getMessage());
            }
        }
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
