package com.brandon.globaleconomy.commands;

import com.brandon.globaleconomy.economy.WalletManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class MoneyCommand implements CommandExecutor {
    private final WalletManager walletManager;

    public MoneyCommand(WalletManager walletManager) {
        this.walletManager = walletManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            // Show wallet
            if (sender instanceof Player player) {
                sender.sendMessage(ChatColor.YELLOW + "Your balances:");
                Map<String, Double> balances = walletManager.getAllBalances(player.getUniqueId());
                for (Map.Entry<String, Double> entry : balances.entrySet()) {
                    sender.sendMessage(ChatColor.AQUA + entry.getKey() + ": " + ChatColor.WHITE + entry.getValue());
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Players only.");
            }
            return true;
        }

        String sub = args[0].toLowerCase();

        // /money set <player> <amount> <currency>
        if (sub.equals("set") && args.length == 4) {
            if (!sender.hasPermission("globaleconomy.money.set")) {
                sender.sendMessage(ChatColor.RED + "No permission.");
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Player not found.");
                return true;
            }

            double amount;
            try {
                amount = Double.parseDouble(args[2]);
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "Invalid amount.");
                return true;
            }

            String currency = args[3];
            UUID targetId = target.getUniqueId();
            walletManager.deposit(targetId, currency, amount); // Clear + deposit
            walletManager.withdraw(targetId, currency, walletManager.getBalance(targetId, currency)); // Wipe old
            walletManager.deposit(targetId, currency, amount); // Set new

            sender.sendMessage(ChatColor.GREEN + "Set " + target.getName() + "'s " + currency + " to " + amount);
            return true;
        }

        // TODO: /money add, take, pay, etc.

        sender.sendMessage(ChatColor.YELLOW + "/money set <player> <amount> <currency>");
        return true;
    }
}
