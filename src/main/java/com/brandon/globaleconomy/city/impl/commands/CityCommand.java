package com.brandon.globaleconomy.city.impl.commands;

import com.brandon.globaleconomy.city.City;
import com.brandon.globaleconomy.city.CityManager;
import com.brandon.globaleconomy.city.CityYamlLoader;
import com.brandon.globaleconomy.city.claims.ClaimManager;
import com.brandon.globaleconomy.economy.currencies.CurrencyManager;
import com.brandon.globaleconomy.dynmap.DynmapManager;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Location;

import java.util.Set;
import java.util.UUID;

public class CityCommand implements CommandExecutor {
    private final CityManager cityManager;
    private final ClaimManager claimManager;
    private final DynmapManager dynmapManager;
    private final CityYamlLoader cityYamlLoader;
    private final CurrencyManager currencyManager;


    public CityCommand(
            CityManager cityManager,
            ClaimManager claimManager,
            DynmapManager dynmapManager,
            CityYamlLoader cityYamlLoader,
            CurrencyManager currencyManager
    ) {
        this.cityManager = cityManager;
        this.claimManager = claimManager;
        this.dynmapManager = dynmapManager;
        this.cityYamlLoader = cityYamlLoader;
        this.currencyManager = currencyManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.YELLOW + "Usage: /city <create|delete|list|info|color|setcolor>");
            return true;
        }
        String sub = args[0].toLowerCase();

        // /city list
        if (sub.equals("list")) {
            sender.sendMessage(ChatColor.YELLOW + "Cities:");
            for (City city : cityManager.getCities().values()) {
                sender.sendMessage(ChatColor.GRAY + " - " + city.getName() + ChatColor.RESET + " (Color: " + city.getColor() + ")");
            }
            return true;
        }

        // /city info <name>
        if (sub.equals("info") && args.length >= 2) {
            City city = cityManager.getCity(args[1]);
            if (city == null) {
                sender.sendMessage(ChatColor.RED + "City not found.");
                return true;
            }
            sender.sendMessage(ChatColor.YELLOW + "City: " + city.getName());
            sender.sendMessage(ChatColor.YELLOW + "Nation: " + city.getNation());
            sender.sendMessage(ChatColor.YELLOW + "Population: " + city.getPopulation());
            sender.sendMessage(ChatColor.YELLOW + "Color: " + city.getColor());
            return true;
        }


        // /city create <name> <nation>
        if (sub.equals("create") && sender instanceof Player && args.length >= 3) {
            Player player = (Player) sender;
            String cityName = args[1];
            String nation = args[2];

            String currencyName = nation; //Base it off nation name

            // Ensure currency exists or create a default gold-backed one
            if (!currencyManager.hasCurrency(currencyName)) {
                currencyManager.createCurrency(currencyName, true, "GOLD_INGOT", 1.0);
                sender.sendMessage(ChatColor.YELLOW + "Currency '" + currencyName + "' created (gold-backed).");
            }
            
            if (cityManager.getCity(cityName) != null) {
                player.sendMessage(ChatColor.RED + "City already exists.");
                return true;
            }
            Location location = player.getLocation();
            int population = 1; // Default population

            // Pick a color at creation
            String color = cityManager.getRandomColor();

            UUID mayorId = player.getUniqueId(); // Creator is mayor by default

            City city = new City(cityName, nation, location, population, color, currencyName, mayorId);

            cityManager.addCity(city);

          
            // Claim chunks for this city (should return false if claim failed)
            boolean claimed = claimManager.claimChunksForCity(city);
            Set<Chunk> claimedChunks = claimManager.getChunksForCity(city);
            System.out.println("DEBUG: City '" + cityName + "' claim result: " + claimed + ", chunks claimed: " + (claimedChunks != null ? claimedChunks.size() : "null"));
            if (claimedChunks != null) {
                for (Chunk chunk : claimedChunks) {
                    System.out.println("DEBUG: Claimed chunk: " + chunk.getWorld().getName() + " [" + chunk.getX() + "," + chunk.getZ() + "]");
                }
            }
            if (!claimed) {
                player.sendMessage(ChatColor.RED + "Cannot found a city here, land is already claimed!");
                cityManager.removeCity(cityName); // Clean up failed city creation
                return true;
            }
            if (dynmapManager != null) {
                System.out.println("DEBUG: Calling DynmapManager.addOrUpdateCityAreaPolygon for: " + cityName);
            }


            // Update Dynmap
            if (dynmapManager != null)
                dynmapManager.addOrUpdateCityAreaPolygon(city, claimManager.getChunksForCity(city));

            // Save to YAML
            cityYamlLoader.saveCities(cityManager.getCities());

            player.sendMessage(ChatColor.GREEN + "City " + cityName + " created with color " + color + "!");
            return true;
        }

        // /city delete <name>
        if (sub.equals("delete") && args.length >= 2) {
            String cityName = args[1];
            City city = cityManager.getCity(cityName);
            if (city == null) {
                sender.sendMessage(ChatColor.RED + "City not found: " + cityName);
                return true;
            }

            // Unclaim city land
            claimManager.unclaimChunksForCity(city);

            // Remove from Dynmap, if enabled
            if (dynmapManager != null)
                dynmapManager.removeCityAreaPolygon(city);

            // Remove from city manager
            cityManager.removeCity(cityName);

            // Save to YAML
            cityYamlLoader.saveCities(cityManager.getCities());

            sender.sendMessage(ChatColor.GREEN + "City deleted: " + cityName);
            return true;
        }

        // /city setcolor <hex>
        if (sub.equals("setcolor") && args.length == 2 && sender instanceof Player) {
            Player player = (Player) sender;
            City city = getCityAtPlayerLocation(player);
            if (city == null) {
                player.sendMessage(ChatColor.RED + "You are not standing in a city you own.");
                return true;
            }
            String color = args[1];
            if (!color.matches("^#([A-Fa-f0-9]{6})$")) {
                player.sendMessage(ChatColor.RED + "Invalid color! Use hex like #ff00ff.");
                return true;
            }
            city.setColor(color);
            player.sendMessage(ChatColor.GREEN + "City color set to " + color);
            if (dynmapManager != null)
                dynmapManager.addOrUpdateCityAreaPolygon(city, claimManager.getChunksForCity(city));

            // Save to YAML
            cityYamlLoader.saveCities(cityManager.getCities());

            return true;
        }

        // /city color
        if (sub.equals("color") && sender instanceof Player) {
            Player player = (Player) sender;
            City city = getCityAtPlayerLocation(player);
            if (city == null) {
                player.sendMessage(ChatColor.RED + "You are not standing in a city you own.");
                return true;
            }
            player.sendMessage(ChatColor.YELLOW + "City " + city.getName() + " color: " + city.getColor());
            return true;
        }

        sender.sendMessage(ChatColor.YELLOW + "Usage: /city <create|delete|list|info|color|setcolor>");
        return true;
    }

    // Helper: Get the city the player is standing in
    private City getCityAtPlayerLocation(Player player) {
        Location location = player.getLocation();
        for (City city : cityManager.getCities().values()) {
            // TODO: Replace with claim-based logic for more accuracy
            if (city.getLocation().getWorld().equals(location.getWorld())
                    && city.getLocation().distance(location) < 100) { // Placeholder radius
                return city;
            }
        }
        return null;
    }
}
