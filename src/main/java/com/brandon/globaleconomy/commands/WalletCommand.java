package com.brandon.globaleconomy.commands;

import com.brandon.globaleconomy.economy.WalletManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WalletCommand implements CommandExecutor, TabCompleter {

    private final WalletManager walletManager;

    public WalletCommand(WalletManager walletManager) {
        this.walletManager = walletManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 4 || !args[0].equalsIgnoreCase("give")) {
            sender.sendMessage("§cUsage: /wallet give <player> <currency> <amount>");
            return true;
        }

        String playerName = args[1];
        String currency = args[2];
        double amount;

        try {
            amount = Double.parseDouble(args[3]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cAmount must be a number.");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);
        if (target == null) {
            sender.sendMessage("§cPlayer not found.");
            return true;
        }

        walletManager.deposit(target.getUniqueId(), currency, amount);
        sender.sendMessage("§aGave §e" + amount + " " + currency + "§a to §e" + playerName);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], List.of("give"), new ArrayList<>());
        }

        if (args.length == 2) {
            List<String> players = new ArrayList<>();
            for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
                players.add(p.getName());
            }
            return StringUtil.copyPartialMatches(args[1], players, new ArrayList<>());
        }

        if (args.length == 3) {
            // Suggest known currencies — if you want this dynamic, pull from CurrencyManager
            return StringUtil.copyPartialMatches(args[2], Arrays.asList("USD", "GBP", "FRF", "PTE", "REA"), new ArrayList<>());
        }

        return new ArrayList<>();
    }
}
