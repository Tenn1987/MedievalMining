package com.brandon.globaleconomy.listeners;

import com.brandon.globaleconomy.city.City;
import com.brandon.globaleconomy.city.CityManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class CityRegionListener implements Listener {

    private final CityManager cityManager;

    public CityRegionListener(CityManager cityManager) {
        this.cityManager = cityManager;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().getChunk().equals(event.getTo().getChunk())) return;

        Player player = event.getPlayer();
        Chunk chunk = player.getLocation().getChunk();
        Location location = player.getLocation();

        City city = cityManager.getCityAt(location);

        String message;
        if (city != null) {
            message = "§eEntering " + city.getName();
        } else {
            message = "§7§oWilderness";
        }

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
    }
}
