package com.brandon.globaleconomy.listeners;

import com.brandon.globaleconomy.city.City;
import com.brandon.globaleconomy.city.CityManager;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.HashMap;
import java.util.Map;

public class RegionBannerListener implements Listener {
    private final CityManager cityManager;
    private final Map<Player, Chunk> lastChunk = new HashMap<>();

    public RegionBannerListener(CityManager cityManager) {
        this.cityManager = cityManager;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Chunk newChunk = player.getLocation().getChunk();
        Chunk oldChunk = lastChunk.get(player);

        if (oldChunk != null && oldChunk.equals(newChunk)) return;
        lastChunk.put(player, newChunk);

        City city = cityManager.getCityAt(player.getLocation());
        if (city != null) {
            String name = city.getName();
            String color = city.getColor();
            String message = color + "Entering §e" + name;
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
        } else {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§7§oWilderness"));
        }
    }
}
