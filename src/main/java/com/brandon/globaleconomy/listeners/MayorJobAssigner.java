package com.brandon.globaleconomy.listeners;

import com.brandon.globaleconomy.city.City;
import com.brandon.globaleconomy.city.CityManager;
import com.brandon.globaleconomy.economy.impl.workers.*;
import com.brandon.globaleconomy.npc.impl.WorkerTrait;
import com.brandon.globaleconomy.core.PluginCore;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class MayorJobAssigner implements Listener {

    @EventHandler
    public void onAssignJob(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof org.bukkit.entity.Player)) return;

        NPC npc = CitizensAPI.getNPCRegistry().getNPC(event.getRightClicked());
        if (npc == null || !npc.hasTrait(WorkerTrait.class)) return;

        @SuppressWarnings("deprecation")
        WorkerTrait trait = npc.getTrait(WorkerTrait.class);
        Worker current = trait.getWorker();

        if (current != null && current.getRole() != WorkerRole.MAYOR) {
            event.getPlayer().sendMessage("§cThis NPC is already employed as a " + current.getRole());
            return;
        }

        Player player = event.getPlayer();
        ItemStack tool = player.getInventory().getItemInMainHand();

        CityManager cityManager = PluginCore.getInstance().getCityManager();
        City city = cityManager.getCityAt(npc.getStoredLocation());
        if (city == null) {
            player.sendMessage("§cThis NPC is not inside a known city.");
            return;
        }

        Worker newWorker = null;
        switch (tool.getType()) {
            case IRON_HOE -> newWorker = new Farmer(city, npc.getName(), npc.getUniqueId());
            case IRON_AXE -> newWorker = new Woodsman(city, npc.getName(), npc.getUniqueId());
            case IRON_PICKAXE -> newWorker = new Miner(city, npc.getName(), npc.getUniqueId());
            case FISHING_ROD -> newWorker = new Fisherman(city, npc.getName(), npc.getUniqueId());
            case IRON_SWORD -> newWorker = new Guard(city, npc.getName(), npc.getUniqueId());
            case EMERALD -> newWorker = new Merchant(city, npc.getName(), npc.getUniqueId());
            case STICK -> newWorker = new Builder(city, npc.getName(), npc.getUniqueId());
            default -> {
                player.sendMessage("§7No known job for that item.");
                return;
            }
        }

        trait.setWorker(newWorker);
        WorkerManager.getInstance().registerWorker(newWorker);
        player.sendMessage("§aAssigned " + newWorker.getRole() + " to NPC " + npc.getName());
    }
}
