package com.brandon.globaleconomy.npc.impl;

import com.brandon.globaleconomy.economy.impl.workers.Worker;
import com.brandon.globaleconomy.economy.impl.workers.WorkerRole;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class WorkerTradeGUI {

    public static void open(Player player, Worker worker) {
        WorkerRole role = worker.getRole();
        Inventory gui = Bukkit.createInventory(null, InventoryType.CHEST, role.name() + " Trade");

        // Example placeholder trades per role
        switch (role) {
            case FARMER:
                gui.addItem(createTradeItem(Material.WHEAT, "Buy Wheat", 1));
                gui.addItem(createTradeItem(Material.WHEAT_SEEDS, "Buy Seeds", 1));
                break;
            case WOODSMAN:
                gui.addItem(createTradeItem(Material.OAK_LOG, "Buy Logs", 2));
                break;
            case MERCHANT:
                gui.addItem(createTradeItem(Material.EMERALD, "Exchange Currency", 0));
                break;
            case MAYOR:
                gui.addItem(createTradeItem(Material.BOOK, "City Overview", 0));
                break;
            default:
                gui.addItem(createTradeItem(Material.BARRIER, "No Trades Available", 0));
        }

        player.openInventory(gui);
    }

    private static ItemStack createTradeItem(Material mat, String name, int cost) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(Arrays.asList("Cost: " + cost + " coins"));
            item.setItemMeta(meta);
        }
        return item;
    }

    // In your plugin's main listener class, add InventoryClickEvent handling to process trades.
    public static void handleClick(InventoryClickEvent event) {
        // Placeholder: validate and process trades based on clicked item
        // Optionally, read item name/lore to identify trade and deduct/add inventory
    }
}
