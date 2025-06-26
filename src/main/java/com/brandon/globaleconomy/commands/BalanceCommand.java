package com.brandon.globaleconomy.commands;

import com.brandon.globaleconomy.economy.WalletManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class BalanceCommand implements CommandExecutor {
    private final WalletManager walletManager;

    public BalanceCommand(WalletManager walletManager) {
        this.walletManager = walletManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can check their balance.");
            return true;
        }

        Player player = (Player) sender;
        Map<String, Double> balances = walletManager.getAllBalances(player.getUniqueId());

        if (balances.isEmpty()) {
            player.sendMessage(ChatColor.RED + "Your wallet is empty.");
            return true;
        }

        StringBuilder message = new StringBuilder(ChatColor.YELLOW + "Your Balances:\n");
        for (Map.Entry<String, Double> entry : balances.entrySet()) {
            message.append(ChatColor.GRAY).append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }

        player.sendMessage(message.toString());
        return true;
    }
}
