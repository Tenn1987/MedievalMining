package com.brandon.globaleconomy.commands;

import com.brandon.globaleconomy.economy.WalletManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class WalletCommand implements CommandExecutor {

    private final WalletManager walletManager;

    public WalletCommand(WalletManager walletManager) {
        this.walletManager = walletManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // === /wallet give <player> <currency> <amount> ===
        if (args.length == 4 && args[0].equalsIgnoreCase("give")) {
            String targetName = args[1];
            String currency = args[2].toUpperCase();
            double amount;

            try {
                amount = Double.parseDouble(args[3]);
            } catch (NumberFormatException e) {
                sender.sendMessage("§cInvalid amount.");
                return true;
            }

            Player target = Bukkit.getPlayer(targetName);
            if (target == null || !target.isOnline()) {
                sender.sendMessage("§cPlayer not found or offline.");
                return true;
            }

            UUID uuid = target.getUniqueId();
            if (!walletManager.hasWallet(uuid)) {
                sender.sendMessage("§cTarget does not have a wallet.");
                return true;
            }

            Map<String, Double> wallet = walletManager.getWallet(uuid);
            wallet.put(currency, wallet.getOrDefault(currency, 0.0) + amount);
            sender.sendMessage("§aGave " + amount + " " + currency + " to " + target.getName());
            target.sendMessage("§aYou received " + amount + " " + currency);
            return true;
        }

        // === /wallet === (show own balance)
        if (sender instanceof Player player) {
            UUID uuid = player.getUniqueId();
            Map<String, Double> balances = walletManager.getAllBalances(uuid);

            player.sendMessage("§6Your wallet:");
            for (Map.Entry<String, Double> entry : balances.entrySet()) {
                player.sendMessage("§7- " + entry.getKey() + ": §a" + String.format("%.2f", entry.getValue()));
            }
            return true;
        }

        sender.sendMessage("§cOnly players have wallets.");
        return true;
    }
}
