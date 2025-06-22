package com.brandon.globaleconomy.listeners;

import com.brandon.globaleconomy.city.City;
import com.brandon.globaleconomy.city.CityManager;
import com.brandon.globaleconomy.city.claims.ClaimManager;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RegionBannerListener implements Listener {

    private final ClaimManager claimManager;
    private final CityManager cityManager;
    private final Plugin plugin;
    private final Map<UUID, String> lastCityByPlayer = new HashMap<>();

    public RegionBannerListener(Plugin plugin, ClaimManager claimManager, CityManager cityManager) {
        this.plugin = plugin;
        this.claimManager = claimManager;
        this.cityManager = cityManager;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().getChunk().equals(event.getTo().getChunk())) return;
        Player player = event.getPlayer();
        System.out.println("[DEBUG] PlayerMoveEvent triggered for: " + player.getName());
        System.out.println("[DEBUG] From chunk: " + event.getFrom().getChunk() + " To chunk: " + event.getTo().getChunk());
// if (event.getFrom().getChunk().equals(event.getTo().getChunk())) return;

        Chunk chunk = event.getTo().getChunk();
        String cityName = claimManager.getCityNameAtChunk(chunk);

        // Make wilderness always null for the tracker
        if (cityName != null && cityName.trim().isEmpty()) cityName = null;

        String lastCity = lastCityByPlayer.get(player.getUniqueId());

        // Only trigger if entering a new city or newly entering wilderness
        if ((lastCity == null && cityName == null) || (lastCity != null && lastCity.equals(cityName))) {
            return;
        }
        lastCityByPlayer.put(player.getUniqueId(), cityName);

        String subtitle;
        if (cityName != null) {
            City city = cityManager.getCityByName(cityName);
            String name = city.getName().toLowerCase();
            String color;
            switch (name) {
                case "moscow": color = "§4"; break;     // dark red
                case "kiev": color = "§6"; break;       // gold
                default: color = "§f"; break;           // white
            }
            subtitle = color + "Entering §e" + cityName;
        } else {
            subtitle = "§7§oWilderness"; // gray, italic
        }

        // Use only the subtitle, with blank title, for a small format
        System.out.println("[DEBUG] Showing action bar: " + subtitle);
        player.spigot().sendMessage(
                net.md_5.bungee.api.ChatMessageType.ACTION_BAR,
                new net.md_5.bungee.api.chat.TextComponent(subtitle)
        );
    }

    public void onPlayerQuit(UUID uuid) {
        lastCityByPlayer.remove(uuid);
    }
}
