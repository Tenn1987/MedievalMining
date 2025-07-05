package com.brandon.globaleconomy.listeners;

import com.brandon.globaleconomy.npc.impl.WorkerTradeGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class WorkerTradeListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        InventoryView view = event.getView();
        if (!view.getTitle().endsWith("Trade")) return;


        event.setCancelled(true);
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;

        ItemMeta meta = clicked.getItemMeta();
        if (meta == null || !meta.hasLore()) return;

        List<String> lore = meta.getLore();
        if (lore == null || lore.isEmpty()) return;

        String costLine = lore.get(0);
        int cost = extractCostFromLore(costLine);

        // Placeholder: Deduct 1 emerald per coin
        if (player.getInventory().contains(Material.EMERALD, cost)) {
            player.getInventory().removeItem(new ItemStack(Material.EMERALD, cost));
            player.getInventory().addItem(new ItemStack(clicked.getType(), 1));
            player.sendMessage(ChatColor.GREEN + "Trade successful!");
        } else {
            player.sendMessage(ChatColor.RED + "Not enough emeralds!");
        }

        player.closeInventory();
    }

    private int extractCostFromLore(String lore) {
        try {
            return Integer.parseInt(lore.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            return Integer.MAX_VALUE;
        }
    }
}
