package com.brandon.globaleconomy.listeners;

import com.brandon.globaleconomy.city.City;
import com.brandon.globaleconomy.city.CityManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// Bungee/Spigot API for action bar:
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class CityRegionListener implements Listener {
    private final CityManager cityManager;
    private final Map<UUID, String> lastCity = new HashMap<>();
    private final int cityRadius = 48; // You can adjust this per city if you want!

    public CityRegionListener(CityManager cityManager) {
        this.cityManager = cityManager;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location loc = player.getLocation();

        City city = getCityAt(loc);
        String currentCity = city != null ? city.getName() : "Wilderness";
        String last = lastCity.getOrDefault(player.getUniqueId(), "Wilderness");

        if (!currentCity.equals(last)) {
            if (city != null) {
                player.spigot().sendMessage(
                        ChatMessageType.ACTION_BAR,
                        new TextComponent("§bEntering §f" + city.getName() + " §7(" + city.getNation() + ")")
                );
            } else {
                player.spigot().sendMessage(
                        ChatMessageType.ACTION_BAR,
                        new TextComponent("§7Entering Wilderness")
                );
            }
            lastCity.put(player.getUniqueId(), currentCity);
        }
    }

    // Fixed-radius city check (change if you use chunk/polygon claims)
    private City getCityAt(Location loc) {
        for (City city : cityManager.getCities().values()) {
            if (isLocationInCity(city, loc)) return city;
        }
        return null;
    }

    private boolean isLocationInCity(City city, Location loc) {
        if (loc == null || city.getLocation() == null) return false;
        if (!loc.getWorld().equals(city.getLocation().getWorld())) return false;
        double dx = loc.getBlockX() - city.getLocation().getBlockX();
        double dz = loc.getBlockZ() - city.getLocation().getBlockZ();
        double distance = Math.sqrt(dx * dx + dz * dz);
        return distance <= cityRadius;
    }
}
