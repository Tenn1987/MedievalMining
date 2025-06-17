package com.brandon.globaleconomy.commands;

import com.brandon.globaleconomy.economy.currencies.NationCurrencyManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BalanceCommand implements CommandExecutor {
    private final NationCurrencyManager nationCurrencyManager;

    public BalanceCommand(NationCurrencyManager nationCurrencyManager) {
        this.nationCurrencyManager = nationCurrencyManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can check their balance.");
            return true;
        }

        Player player = (Player) sender;
        StringBuilder balances = new StringBuilder(ChatColor.YELLOW + "Your Balances:\n");
        for (String code : nationCurrencyManager.getCurrencyCodes()) {
            double balance = nationCurrencyManager.getBalance(player.getUniqueId(), code);
            balances.append(ChatColor.GRAY).append(code).append(": ").append(balance).append("\n");
        }
        player.sendMessage(balances.toString());
        return true;
    }
}
