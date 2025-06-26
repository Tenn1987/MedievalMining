package com.brandon.globaleconomy.city.impl.commands;

import com.brandon.globaleconomy.city.City;
import com.brandon.globaleconomy.city.CityManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class CityTreasuryCommand implements CommandExecutor {
    private final CityManager cityManager;

    public CityTreasuryCommand(CityManager cityManager) {
        this.cityManager = cityManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        City city = cityManager.getCityByMayor(player.getUniqueId());
        if (city == null) {
            player.sendMessage(ChatColor.RED + "You are not the mayor of any city.");
            return true;
        }

        Map<String, Double> balances = city.getAllCityBalances();
        if (balances.isEmpty()) {
            player.sendMessage(ChatColor.YELLOW + "City treasury is currently empty.");
            return true;
        }

        player.sendMessage(ChatColor.GOLD + "City Treasury for " + city.getName() + ":");
        for (Map.Entry<String, Double> entry : balances.entrySet()) {
            player.sendMessage(ChatColor.GRAY + "- " + entry.getKey() + ": " + entry.getValue());
        }

        return true;
    }
}
