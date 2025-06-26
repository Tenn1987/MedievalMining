
package com.brandon.globaleconomy.commands;

import com.brandon.globaleconomy.economy.WalletManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class WalletCommand implements CommandExecutor {

    private final WalletManager walletManager;

    public WalletCommand(WalletManager walletManager) {
        this.walletManager = walletManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players have wallets.");
            return true;
        }

        Player player = (Player) sender;
        Map<String, Double> balances = walletManager.getAllBalances(player.getUniqueId());

        sender.sendMessage("Your wallet:");
        for (Map.Entry<String, Double> entry : balances.entrySet()) {
            sender.sendMessage("- " + entry.getKey() + ": " + entry.getValue());
        }

        return true;
    }
}
