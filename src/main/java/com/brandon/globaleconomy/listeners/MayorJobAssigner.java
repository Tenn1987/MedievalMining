package com.brandon.globaleconomy.listeners;

import com.brandon.globaleconomy.city.City;
import com.brandon.globaleconomy.city.CityManager;
import com.brandon.globaleconomy.core.PluginCore;
import com.brandon.globaleconomy.economy.impl.workers.*;
import com.brandon.globaleconomy.npc.traits.WorkerTrait;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MayorJobAssigner implements Listener {

    private String capitalize(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    @EventHandler
    public void onAssignJob(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Player)) return;

        Player player = event.getPlayer();
        ItemStack tool = player.getInventory().getItemInMainHand();
        Material toolType = tool.getType();

        List<Material> jobItems = List.of(
                Material.IRON_HOE, Material.IRON_AXE, Material.IRON_PICKAXE,
                Material.FISHING_ROD, Material.IRON_SHOVEL, Material.SWEET_BERRIES,
                Material.EMERALD, Material.IRON_SWORD, Material.STICK,
                Material.WHEAT_SEEDS, Material.WRITABLE_BOOK, Material.SADDLE
        );

        if (!jobItems.contains(toolType)) return;

        NPC npc = CitizensAPI.getNPCRegistry().getNPC(event.getRightClicked());
        if (npc == null || !npc.hasTrait(WorkerTrait.class)) return;

        WorkerTrait trait = npc.getTrait(WorkerTrait.class);
        Worker current = trait.getWorker();

        if (current != null && current.getRole() != WorkerRole.RESIDENT && current.getRole() != WorkerRole.MAYOR) {
            player.sendMessage("§cThis NPC is already employed as a " + capitalize(current.getRole().name()));
            return;
        }

        CityManager cityManager = PluginCore.getInstance().getCityManager();
        City city = cityManager.getCityAt(npc.getStoredLocation());
        if (city == null) {
            player.sendMessage("\u00a7cThis NPC is not inside a known city.");
            return;
        }

        Worker newWorker;
        switch (toolType) {
            case IRON_HOE -> newWorker = new Farmer(city, npc.getName(), npc.getUniqueId());
            case IRON_AXE -> newWorker = new Woodsman(city, npc.getName(), npc.getUniqueId());
            case IRON_PICKAXE -> newWorker = new Miner(city, npc.getName(), npc.getUniqueId());
            case FISHING_ROD -> newWorker = new Fisherman(city, npc.getName(), npc.getUniqueId());
            case IRON_SHOVEL -> newWorker = new Digger(city, npc.getName(), npc.getUniqueId());
            case SWEET_BERRIES -> newWorker = new Forager(city, npc.getName(), npc.getUniqueId());
            case EMERALD -> newWorker = new Merchant(city, npc.getName(), npc.getUniqueId());
            case IRON_SWORD -> newWorker = new Guard(city, npc.getName(), npc.getUniqueId());
            case STICK -> newWorker = new Builder(city, npc.getName(), npc.getUniqueId());
            case WHEAT_SEEDS -> newWorker = new Resident(city, npc.getName(), npc.getUniqueId());
            case WRITABLE_BOOK -> newWorker = new Mayor(city, npc.getName(), npc.getUniqueId());
            case SADDLE -> newWorker = new Caravaner(city, npc.getName(), npc.getUniqueId());
            default -> {
                player.sendMessage("\u00a77No known job for that item.");
                return;
            }
        }

        trait.setWorker(newWorker);
        WorkerManager.getInstance().registerWorker(newWorker);

        String displayName = newWorker.getName() + " (" + capitalize(newWorker.getRole().name()) + ")";
        npc.setName(displayName);

        player.sendMessage("\u00a7aAssigned " + capitalize(newWorker.getRole().name()) + " to NPC " + displayName);
    }
}
