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
        // /worker list <city>
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
        // /worker add <city> <role> <name>
        if (args.length >= 4 && args[0].equalsIgnoreCase("add")) {
            City city = cityManager.getCity(args[1]);
            if (city == null) {
                sender.sendMessage(ChatColor.RED + "City not found.");
                return true;
            }
            Worker worker = workerManager.createWorker(args[2], city, args[3]);
            if (worker == null) {
                sender.sendMessage(ChatColor.RED + "Unknown worker role: " + args[2]);
                return true;
            }
            city.addWorker(worker);
            sender.sendMessage(ChatColor.GREEN + "Worker " + worker.getName() + " added as " + worker.getRole() + " in " + city.getName());
            return true;
        }
        sender.sendMessage(ChatColor.YELLOW + "Usage: /worker list <city> OR /worker add <city> <role> <name>");
        return true;
    }
}
