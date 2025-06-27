
package com.brandon.globaleconomy.commands;

import com.brandon.globaleconomy.city.City;
import com.brandon.globaleconomy.city.CityManager;
import com.brandon.globaleconomy.economy.impl.workers.Worker;
import com.brandon.globaleconomy.economy.impl.workers.WorkerManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class WorkerCommand implements CommandExecutor {
    private final CityManager cityManager;
    private final WorkerManager workerManager;

    public WorkerCommand(CityManager cityManager, WorkerManager workerManager) {
        this.cityManager = cityManager;
        this.workerManager = workerManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length >= 2 && args[0].equalsIgnoreCase("list")) {
            City city = cityManager.getCity(args[1]);
            if (city == null) {
                sender.sendMessage(ChatColor.RED + "City not found.");
                return true;
            }
            sender.sendMessage(ChatColor.GOLD + "Workers in " + city.getName() + ":");
            for (Worker worker : city.getWorkers()) {
                sender.sendMessage(ChatColor.GRAY + "- " + worker.getName() + " (" + worker.getRole() + ")");
            }
            return true;
        }

        if (args.length >= 4 && args[0].equalsIgnoreCase("add")) {
            City city = cityManager.getCity(args[1]);
            if (city == null) {
                sender.sendMessage(ChatColor.RED + "City not found.");
                return true;
            }
            String role = args[2].toLowerCase();
            String name = args[3];
            Worker worker = workerManager.createWorker(role, city, name);
            if (worker == null) {
                sender.sendMessage(ChatColor.RED + "Invalid role: " + role);
                return true;
            }
            sender.sendMessage(ChatColor.GREEN + "Added " + role + " named " + name + " to city " + city.getName());
            return true;
        }

        if (args.length >= 3 && args[0].equalsIgnoreCase("remove")) {
            City city = cityManager.getCity(args[1]);
            if (city == null) {
                sender.sendMessage(ChatColor.RED + "City not found.");
                return true;
            }
            String name = args[2];
            if (city.removeWorkerByName(name)) {
                sender.sendMessage(ChatColor.YELLOW + "Removed worker named " + name + " from " + city.getName());
            } else {
                sender.sendMessage(ChatColor.RED + "Worker not found: " + name);
            }
            return true;
        }

        sender.sendMessage(ChatColor.RED + "Usage:");
        sender.sendMessage(ChatColor.GRAY + "/worker list <city>");
        sender.sendMessage(ChatColor.GRAY + "/worker add <city> <role> <name>");
        sender.sendMessage(ChatColor.GRAY + "/worker remove <city> <name>");
        return true;
    }
}
