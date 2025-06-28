package com.brandon.globaleconomy.city.impl.commands;

import com.brandon.globaleconomy.city.City;
import com.brandon.globaleconomy.city.CityManager;
import com.brandon.globaleconomy.city.CityYamlLoader;
import com.brandon.globaleconomy.city.claims.ClaimManager;
import com.brandon.globaleconomy.dynmap.DynmapManager;
import com.brandon.globaleconomy.economy.currencies.CurrencyManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class CityCommand implements CommandExecutor {
    private final CityManager cityManager;
    private final ClaimManager claimManager;
    private final DynmapManager dynmapManager;
    private final CityYamlLoader cityYamlLoader;
    private final CurrencyManager currencyManager;

    public CityCommand(CityManager cityManager, ClaimManager claimManager, DynmapManager dynmapManager, CityYamlLoader cityYamlLoader, CurrencyManager currencyManager) {
        this.cityManager = cityManager;
        this.claimManager = claimManager;
        this.dynmapManager = dynmapManager;
        this.cityYamlLoader = cityYamlLoader;
        this.currencyManager = currencyManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }
        Player player = (Player) sender;

        if (args.length == 0) {
            sendUsage(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("info") && args.length >= 2) {
            City city = cityManager.getCity(args[1]);
            if (city == null) {
                player.sendMessage(ChatColor.RED + "City not found.");
                return true;
            }

            player.sendMessage(ChatColor.GOLD + "City: " + city.getName());
            player.sendMessage(ChatColor.GRAY + "Nation: " + city.getNation());
            player.sendMessage(ChatColor.GRAY + "Population: " + city.getPopulation());
            player.sendMessage(ChatColor.GRAY + "Mayor: " + city.getMayorDisplayName());
            player.sendMessage(ChatColor.GRAY + "Currency: " + city.getEffectiveCurrency(cityManager));
            if (city.getParentCityName() != null) {
                player.sendMessage(ChatColor.GRAY + "Parent City: " + city.getParentCityName());
            }
            player.sendMessage(ChatColor.GRAY + "Balance: " + city.getCityBalance(city.getEffectiveCurrency(cityManager)));
            player.sendMessage(ChatColor.DARK_GREEN + "Resources:");
            for (Map.Entry<String, Integer> entry : city.getResources().entrySet()) {
                if (entry.getValue() > 0) {
                    player.sendMessage(ChatColor.GRAY + "- " + entry.getKey() + ": " + entry.getValue());
                }
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("delete") && args.length >= 2) {
            String name = args[1];
            if (cityManager.removeCity(name)) {
                player.sendMessage(ChatColor.RED + "City '" + name + "' deleted.");
            } else {
                player.sendMessage(ChatColor.RED + "City not found.");
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("create") && args.length >= 4) {
            String name = args[1];
            String nation = args[2];
            String currency = args[3];
            String parentCity = args.length >= 5 ? args[4] : null;

            Location loc = player.getLocation();
            cityManager.addCityWithMayor(name, nation, loc, 1, cityManager.getRandomColor(), currency, player.getUniqueId(), parentCity);
            cityYamlLoader.saveCities(cityManager.getCities());
            claimManager.claimChunksForCity(cityManager.getCity(name));
            dynmapManager.addOrUpdateCityAreaPolygon(cityManager.getCity(name), claimManager.getChunksForCity(cityManager.getCity(name)));
            player.sendMessage(ChatColor.GREEN + "City '" + name + "' created.");
            return true;
        }

        sendUsage(player);
        return true;
    }

    private void sendUsage(Player player) {
        player.sendMessage(ChatColor.RED + "Invalid usage. Try:");
        player.sendMessage(ChatColor.YELLOW + "/city info <name>");
        player.sendMessage(ChatColor.YELLOW + "/city create <name> <nation> <currency> [parentCity]");
        player.sendMessage(ChatColor.YELLOW + "/city delete <name>");
        player.sendMessage(ChatColor.YELLOW + "/city setcurrency <name> <currency>");
    }
}
