package com.brandon.globaleconomy.city.impl.commands;

import com.brandon.globaleconomy.city.City;
import com.brandon.globaleconomy.city.CityManager;
import com.brandon.globaleconomy.city.CityYamlLoader;
import com.brandon.globaleconomy.city.claims.ClaimManager;
import com.brandon.globaleconomy.dynmap.DynmapManager;
import com.brandon.globaleconomy.economy.currencies.CurrencyManager;
import com.brandon.globaleconomy.economy.currencies.ExchangeRateManager;
import com.brandon.globaleconomy.economy.impl.workers.WorkerRole;
import com.brandon.globaleconomy.npc.impl.BuilderSpawner;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;

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
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        if (args.length == 0) {
            sendUsage(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "info":
                return handleInfo(player, args);
            case "inventory":
                return handleInventory(player, args);
            case "delete":
                return handleDelete(player, args);
            case "create":
                return handleCreate(player, args);
            default:
                sendUsage(player);
                return true;
        }
    }

    private boolean handleInfo(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /city info <name>");
            return true;
        }

        City city = cityManager.getCity(args[1]);
        if (city == null) {
            player.sendMessage(ChatColor.RED + "City not found.");
            return true;
        }

        long total = city.getWorkers().size();
        long unemployed = city.getWorkers().stream().filter(w -> w.getRole() == WorkerRole.RESIDENT).count();
        double unemploymentRate = total > 0 ? (double) unemployed / total * 100 : 0.0;

        player.sendMessage(ChatColor.GOLD + "City: " + city.getName());
        player.sendMessage(ChatColor.GRAY + "Nation: " + city.getNation());
        player.sendMessage(ChatColor.GRAY + "Population: " + city.getPopulation());
        player.sendMessage(ChatColor.GRAY + "Mayor: " + city.getMayorDisplayName());
        player.sendMessage(ChatColor.GRAY + "Currency: " + city.getEffectiveCurrency(cityManager));
        if (city.getParentCityName() != null) {
            player.sendMessage(ChatColor.GRAY + "Parent City: " + city.getParentCityName());
        }
        player.sendMessage(ChatColor.GRAY + "Balance: " + city.getCityBalance(city.getEffectiveCurrency(cityManager)));
        player.sendMessage(ChatColor.GRAY + "Unemployment Rate: " + String.format("%.1f", unemploymentRate) + "%");

        player.sendMessage(ChatColor.DARK_GREEN + "Top 3 Resources:");
        city.getResources().entrySet().stream()
                .filter(e -> e.getValue() > 0)
                .sorted((a, b) -> b.getValue() - a.getValue())
                .limit(3)
                .forEach(entry -> player.sendMessage(ChatColor.GRAY + "- " + entry.getKey() + ": " + entry.getValue()));

        return true;
    }

    private boolean handleInventory(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /city inventory <name>");
            return true;
        }

        City city = cityManager.getCity(args[1]);
        if (city == null) {
            player.sendMessage(ChatColor.RED + "City not found.");
            return true;
        }

        player.sendMessage(ChatColor.GOLD + "Inventory for " + city.getName() + ":");
        for (Map.Entry<Material, Integer> entry : city.getCityInventory().entrySet()) {
            if (entry.getValue() > 0) {
                player.sendMessage(ChatColor.GRAY + "- " + entry.getKey().name() + ": " + entry.getValue());
            }
        }
        return true;
    }

    private boolean handleDelete(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /city delete <name>");
            return true;
        }

        String name = args[1];
        if (cityManager.removeCity(name)) {
            player.sendMessage(ChatColor.RED + "City '" + name + "' deleted.");
        } else {
            player.sendMessage(ChatColor.RED + "City not found.");
        }
        return true;
    }

    private boolean handleCreate(Player player, String[] args) {
        if (args.length < 4) {
            player.sendMessage(ChatColor.RED + "Usage: /city create <name> <nation> <currency> [parentCity] [backingMaterial]");
            return true;
        }

        String name = args[1];
        String nation = args[2];
        String currency = args[3];
        String parentCity = args.length >= 5 ? args[4] : null;

        // Determine backing material
        String backingInput = args.length >= 6 ? args[5].toUpperCase() : "GOLD_INGOT";
        Material backingMaterial = Material.matchMaterial(backingInput);
        Set<Material> allowed = Set.of(
                Material.GOLD_INGOT,
                Material.GOLD_NUGGET,
                Material.IRON_INGOT,
                Material.IRON_NUGGET,
                Material.COPPER_INGOT,
                Material.EMERALD,
                Material.DIAMOND,
                Material.NETHERITE_INGOT
        );

        if (backingMaterial == null || !allowed.contains(backingMaterial)) {
            player.sendMessage(ChatColor.RED + "Invalid or unsupported backing material.");
            player.sendMessage(ChatColor.YELLOW + "Allowed: GOLD_INGOT, IRON_INGOT, COPPER_INGOT, EMERALD, DIAMOND, NETHERITE_INGOT");
            return true;
        }

        if (!ExchangeRateManager.getInstance().hasCurrency(currency)) {
            ExchangeRateManager.getInstance().registerCurrency(currency, 1.0, backingMaterial.name());
        }

        Location loc = player.getLocation();
        cityManager.addCityWithMayor(name, nation, loc, 1, cityManager.getRandomColor(), currency, player.getUniqueId(), parentCity);
        City city = cityManager.getCity(name);

        cityYamlLoader.saveCities(cityManager.getCities());
        claimManager.claimChunksForCity(city);
        dynmapManager.addOrUpdateCityAreaPolygon(city, claimManager.getChunksForCity(city));

        // Optional chest setting
        Block target = player.getTargetBlockExact(5);
        if (target != null && target.getType() == Material.CHEST) {
            city.setChestLocation(target.getLocation());
            cityYamlLoader.saveCities(cityManager.getCities());
            player.sendMessage(ChatColor.YELLOW + "Chest automatically set at: " +
                    target.getLocation().getBlockX() + ", " +
                    target.getLocation().getBlockY() + ", " +
                    target.getLocation().getBlockZ());
        } else {
            player.sendMessage(ChatColor.RED + "No nearby chest found. Use /city setchest " + name + " manually.");
        }

        // Spawn the Builder NPC
        BuilderSpawner.spawn(city);

        player.sendMessage(ChatColor.GREEN + "City '" + name + "' created.");
        return true;
    }

    private void sendUsage(Player player) {
        player.sendMessage(ChatColor.RED + "Invalid usage. Try:");
        player.sendMessage(ChatColor.YELLOW + "/city info <name>");
        player.sendMessage(ChatColor.YELLOW + "/city inventory <name>");
        player.sendMessage(ChatColor.YELLOW + "/city create <name> <nation> <currency> [parentCity] [backingMaterial]");
        player.sendMessage(ChatColor.YELLOW + "/city delete <name>");
        player.sendMessage(ChatColor.YELLOW + "/city setcurrency <name> <currency>");
        player.sendMessage(ChatColor.YELLOW + "/city setchest <name>");
    }
}
