package com.brandon.globaleconomy.commands;

import com.brandon.globaleconomy.economy.WalletManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PayCommand implements CommandExecutor {

    private final WalletManager walletManager;

    public PayCommand(WalletManager walletManager) {
        this.walletManager = walletManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player senderPlayer)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /pay <player> <amount> [currency]");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            sender.sendMessage(ChatColor.RED + "Player not found.");
            return true;
        }

        if (target.equals(senderPlayer)) {
            sender.sendMessage(ChatColor.RED + "You can't pay yourself.");
            return true;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[1]);
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Invalid amount.");
            return true;
        }

        String currency = args.length >= 3 ? args[2].toUpperCase() : "GBP"; // Default currency
        if (walletManager.getBalance(senderPlayer.getUniqueId(), currency) < amount) {
            sender.sendMessage(ChatColor.RED + "Insufficient funds.");
            return true;
        }

        // Perform the transfer
        boolean withdrawn = walletManager.withdraw(senderPlayer.getUniqueId(), currency, amount);
        if (withdrawn) {
            walletManager.deposit(target.getUniqueId(), currency, amount);
            senderPlayer.sendMessage(ChatColor.GREEN + "You paid " + amount + " " + currency + " to " + target.getName() + ".");
            target.sendMessage(ChatColor.YELLOW + "You received " + amount + " " + currency + " from " + senderPlayer.getName() + "!");
        } else {
            sender.sendMessage(ChatColor.RED + "Transaction failed.");
        }

        return true;
    }
}