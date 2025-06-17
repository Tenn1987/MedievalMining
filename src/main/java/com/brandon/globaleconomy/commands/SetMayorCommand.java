package com.brandon.globaleconomy.commands;

import com.brandon.globaleconomy.city.City;
import com.brandon.globaleconomy.city.CityManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

import java.util.UUID;

public class SetMayorCommand implements CommandExecutor {

    private final CityManager cityManager;

    public SetMayorCommand(CityManager cityManager) {
        this.cityManager = cityManager;
    }

    private boolean isNpcArg(String name) {
        return name.toUpperCase().startsWith("NPC:") && name.length() > 4;
    }

    private int parseNpcId(String name) {
        return Integer.parseInt(name.substring(4));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // Usage: /city setmayor <city> <player|NPC:ID>
        if (args.length < 3 || !args[0].equalsIgnoreCase("setmayor")) {
            sender.sendMessage("§cUsage: /city setmayor <city> <player|NPC:ID>");
            return true;
        }

        String cityName = args[1];
        String target = args[2];
        City city = cityManager.getCity(cityName);

        if (city == null) {
            sender.sendMessage("§cNo such city: " + cityName);
            return true;
        }

        // Permission: current mayor or admin
        boolean allowed = false;
        if (!(sender instanceof Player)) {
            allowed = true;
        } else {
            Player player = (Player) sender;
            if (player.isOp() || player.hasPermission("city.setmayor") ||
                    (city.hasPlayerMayor() && city.getMayorId().equals(player.getUniqueId()))) {
                allowed = true;
            }
        }
        if (!allowed) {
            sender.sendMessage("§cOnly the current mayor or an admin can change the mayor.");
            return true;
        }

        if (isNpcArg(target)) {
            int npcId = parseNpcId(target);
            NPC npc = CitizensAPI.getNPCRegistry().getById(npcId);
            if (npc == null) {
                sender.sendMessage("§cNo such NPC: " + npcId);
                return true;
            }
            city.setMayorNpcId(npcId);
            sender.sendMessage("§a" + npc.getFullName() + " is now the mayor of " + city.getName() + "!");
            return true;
        }

        // Player lookup
        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(target);
        if (targetPlayer == null || (!targetPlayer.hasPlayedBefore() && !targetPlayer.isOnline())) {
            sender.sendMessage("§cNo such player: " + target);
            return true;
        }
        UUID newMayorId = targetPlayer.getUniqueId();
        city.setMayorId(newMayorId);

        sender.sendMessage("§a" + target + " is now the mayor of " + city.getName() + "!");
        if (targetPlayer.isOnline()) {
            ((Player) targetPlayer).sendMessage("§eYou are now the mayor of " + city.getName() + "!");
        }
        return true;
    }
}
