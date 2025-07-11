package com.brandon.globaleconomy.gui;

import com.brandon.globaleconomy.economy.WalletManager;
import com.brandon.globaleconomy.economy.currencies.CurrencyManager;
import org.bukkit.Bukkit;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.*;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class MoneyChangerGUI {

    private static CurrencyManager currencyManager;
    private static WalletManager walletManager;

    public static void setManagers(CurrencyManager cm, WalletManager wm) {
        currencyManager = cm;
        walletManager = wm;
    }

    public static void open(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, "Moneychanger");

        List<String> currencies = new ArrayList<>(currencyManager.getCurrencyCodes());

        for (int i = 0; i < currencies.size() && i < 9; i++) {
            ItemStack source = new ItemStack(Material.PAPER);
            ItemMeta meta = source.getItemMeta();
            meta.setDisplayName("§aConvert from: " + currencies.get(i));
            source.setItemMeta(meta);
            gui.setItem(i, source);

            ItemStack dest = new ItemStack(Material.MAP);
            ItemMeta meta2 = dest.getItemMeta();
            meta2.setDisplayName("§bTo: " + currencies.get(i));
            dest.setItemMeta(meta2);
            gui.setItem(i + 9, dest);
        }

        // Confirm conversion
        ItemStack confirm = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta confirmMeta = confirm.getItemMeta();
        confirmMeta.setDisplayName("§aConfirm Exchange");
        confirm.setItemMeta(confirmMeta);
        gui.setItem(26, confirm);

        player.openInventory(gui);
    }

    public static void handleClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;
        if (!e.getView().getTitle().equalsIgnoreCase("Moneychanger")) return;

        e.setCancelled(true);
        int slot = e.getRawSlot();

        if (slot == 26) {
            // For now: convert a flat 10 units of first found pair
            List<String> currencies = new ArrayList<>(currencyManager.getCurrencyCodes());
            if (currencies.size() < 2) {
                player.sendMessage("§cNot enough currencies registered.");
                return;
            }

            String from = currencies.get(0);
            String to = currencies.get(1);
            double amount = 10.0;

            double balance = walletManager.getBalance(player.getUniqueId(), from);
            if (balance < amount) {
                player.sendMessage("§cYou don’t have enough " + from);
                return;
            }

            double rate = currencyManager.getExchangeRate(from, to);
            double result = amount * rate;

            walletManager.withdraw(player.getUniqueId(), from, amount);
            walletManager.deposit(player.getUniqueId(), to, result);

            player.sendMessage("§aConverted " + amount + " " + from + " to " + String.format("%.2f", result) + " " + to);
        }
    }
}
