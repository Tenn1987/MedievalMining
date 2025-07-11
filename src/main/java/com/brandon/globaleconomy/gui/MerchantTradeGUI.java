package com.brandon.globaleconomy.gui;

import com.brandon.globaleconomy.city.City;
import com.brandon.globaleconomy.economy.currencies.CurrencyManager;
import com.brandon.globaleconomy.economy.market.MarketAPI;
import com.brandon.globaleconomy.economy.market.MarketItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

public class MerchantTradeGUI {

    private static final int SELL_SLOT = 26;
    private static CurrencyManager currencyManager;

    public static void setCurrencyManager(CurrencyManager manager) {
        currencyManager = manager;
    }

    public static void open(Player player, City city) {
        Inventory gui = Bukkit.createInventory(null, 27, "Merchant: " + city.getName());

        // Top row (buyable city items)
        int slot = 0;
        for (Map.Entry<String, Integer> entry : city.getResources().entrySet()) {
            if (slot >= 9) break;
            Material mat = Material.getMaterial(entry.getKey());
            if (mat == null) continue;

            MarketItem item = MarketAPI.getInstance().getItem(mat);
            if (item == null) continue;

            ItemStack stack = new ItemStack(mat);
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName("§eBuy: " + item.getCurrentPrice() + " " + city.getEffectiveCurrency(null));
            stack.setItemMeta(meta);
            gui.setItem(slot++, stack);
        }

        // Bottom row: Sell area (player drops items to sell)
        for (int i = 18; i < 26; i++) {
            gui.setItem(i, new ItemStack(Material.AIR));
        }

        // Sell button
        ItemStack sellBtn = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta btnMeta = sellBtn.getItemMeta();
        btnMeta.setDisplayName("§aClick to Sell!");
        sellBtn.setItemMeta(btnMeta);
        gui.setItem(SELL_SLOT, sellBtn);

        player.openInventory(gui);
    }

    public static void handleClick(InventoryClickEvent e, City city) {
        if (!(e.getWhoClicked() instanceof Player player)) return;
        if (!e.getView().getTitle().startsWith("Merchant: ")) return;

        e.setCancelled(true);
        int slot = e.getRawSlot();
        Material currencyMaterial = currencyManager.getCurrencyMaterial(city.getEffectiveCurrency(null));

        // BUY from city (slots 0–8)
        if (slot >= 0 && slot < 9) {
            ItemStack clicked = e.getCurrentItem();
            if (clicked == null || clicked.getType() == Material.AIR) return;

            MarketItem item = MarketAPI.getInstance().getItem(clicked.getType());
            if (item == null) return;

            int price = (int) item.getCurrentPrice();

            if (player.getInventory().contains(currencyMaterial, price)) {
                player.getInventory().removeItem(new ItemStack(currencyMaterial, price));
                player.getInventory().addItem(new ItemStack(clicked.getType(), 1));
                city.removeItem(clicked.getType(), 1);
                city.depositToTreasury(city.getEffectiveCurrency(null), price);
                player.sendMessage("§aBought 1x " + clicked.getType() + " for " + price + " " + currencyMaterial.name());
            } else {
                player.sendMessage("§cYou don’t have enough " + currencyMaterial.name());
            }
        }

        // SELL to city (slot 26)
        if (slot == SELL_SLOT) {
            int totalEarned = 0;
            for (int i = 18; i < 26; i++) {
                ItemStack sellItem = e.getInventory().getItem(i);
                if (sellItem == null || sellItem.getType() == Material.AIR) continue;

                Material mat = sellItem.getType();
                int amount = sellItem.getAmount();
                MarketItem marketItem = MarketAPI.getInstance().getItem(mat);
                if (marketItem == null) continue;

                int payout = (int) (marketItem.getSellPrice() * amount);
                totalEarned += payout;

                city.addItem(mat, amount);
                e.getInventory().setItem(i, null);
            }

            if (totalEarned > 0) {
                player.getInventory().addItem(new ItemStack(currencyMaterial, totalEarned));
                city.withdrawFromTreasury(city.getEffectiveCurrency(null), totalEarned);
                player.sendMessage("§aSold items for " + totalEarned + " " + currencyMaterial.name());
            } else {
                player.sendMessage("§cNo valid items to sell.");
            }
        }
    }
}
