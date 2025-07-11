package com.brandon.globaleconomy.listeners;

import com.brandon.globaleconomy.city.City;
import com.brandon.globaleconomy.city.CityManager;
import com.brandon.globaleconomy.economy.currencies.CurrencyManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class WorkerTradeListener implements Listener {

    private final CityManager cityManager;
    private final CurrencyManager currencyManager;

    public WorkerTradeListener(CityManager cityManager, CurrencyManager currencyManager) {
        this.cityManager = cityManager;
        this.currencyManager = currencyManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        InventoryView view = event.getView();
        if (!view.getTitle().endsWith("Trade")) return;

        event.setCancelled(true);
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;

        ItemMeta meta = clicked.getItemMeta();
        if (meta == null || !meta.hasLore()) return;

        List<String> lore = meta.getLore();
        if (lore == null || lore.isEmpty()) return;

        int cost = extractCostFromLore(lore.get(0));

        City city = cityManager.getCityAt(player.getLocation());
        if (city == null) {
            player.sendMessage(ChatColor.RED + "You're not inside a city.");
            return;
        }

        String currencyName = city.getEffectiveCurrency(null);
        Material currencyMaterial = currencyManager.getCurrencyMaterial(currencyName);

        if (player.getInventory().contains(currencyMaterial, cost)) {
            player.getInventory().removeItem(new ItemStack(currencyMaterial, cost));
            player.getInventory().addItem(new ItemStack(clicked.getType(), 1));
            city.depositToTreasury(currencyName, cost);
            player.sendMessage(ChatColor.GREEN + "Trade successful!");
        } else {
            player.sendMessage(ChatColor.RED + "Not enough " + currencyMaterial.name() + "!");
        }

        player.closeInventory();
    }

    private int extractCostFromLore(String loreLine) {
        try {
            return Integer.parseInt(loreLine.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            return Integer.MAX_VALUE;
        }
    }
}
