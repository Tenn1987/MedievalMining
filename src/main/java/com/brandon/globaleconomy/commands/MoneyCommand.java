package com.brandon.globaleconomy.commands;

import com.brandon.globaleconomy.economy.currencies.NationCurrencyManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MoneyCommand implements CommandExecutor {
    private final NationCurrencyManager nationCurrencyManager;

    public MoneyCommand(NationCurrencyManager nationCurrencyManager) {
        this.nationCurrencyManager = nationCurrencyManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            // Show wallet
            if (sender instanceof Player player) {
                sender.sendMessage(ChatColor.YELLOW + "Your balances:");
                for (String code : nationCurrencyManager.getCurrencyCodes()) {
                    double bal = nationCurrencyManager.getBalance(player.getUniqueId(), code);
                    sender.sendMessage(ChatColor.AQUA + code + ": " + ChatColor.WHITE + bal);
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Players only.");
            }
            return true;
        }

        String sub = args[0].toLowerCase();
        // Example: /money set SgtHilliard 100 Denari
        if (sub.equals("set") && args.length == 4) {
            if (!sender.hasPermission("globaleconomy.money.set")) {
                sender.sendMessage(ChatColor.RED + "No permission.");
                return true;
            }
            Player target = Bukkit.getPlayer(args[1]);
            double amount;
            try { amount = Double.parseDouble(args[2]); } catch (Exception e) { sender.sendMessage("Invalid amount."); return true; }
            String currency = args[3];
            nationCurrencyManager.setBalance(target.getUniqueId(), currency, amount);
            sender.sendMessage("Set " + target.getName() + "'s " + currency + " to " + amount);
            return true;
        }
        // ... (add more subcommands: add, take, pay, etc.)

        sender.sendMessage(ChatColor.YELLOW + "/money [set|add|take|pay] <player> <amount> <currency>");
        return true;
    }
}
