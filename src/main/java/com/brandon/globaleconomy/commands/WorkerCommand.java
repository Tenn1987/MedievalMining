package com.brandon.globaleconomy.commands;

import com.brandon.globaleconomy.city.City;
import com.brandon.globaleconomy.city.CityManager;
import com.brandon.globaleconomy.economy.impl.workers.Worker;
import com.brandon.globaleconomy.economy.impl.workers.WorkerFactory;
import com.brandon.globaleconomy.economy.impl.workers.WorkerManager;
import com.brandon.globaleconomy.economy.impl.workers.WorkerRole;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class WorkerCommand implements CommandExecutor {

    private final CityManager cityManager;

    public WorkerCommand(CityManager cityManager) {
        this.cityManager = cityManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("globaleconomy.admin")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        List<Worker> workers = WorkerManager.getInstance().getAllWorkers();

        if (args.length == 0 || args[0].equalsIgnoreCase("list")) {
            sender.sendMessage(ChatColor.YELLOW + "=== Active Workers (" + workers.size() + ") ===");
            for (Worker worker : workers) {
                sender.sendMessage(ChatColor.GRAY + "- " + worker.getName() + " (" + worker.getRole() + ")");
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("role") && args.length >= 2) {
            String roleFilter = args[1].toUpperCase(Locale.ROOT);
            try {
                WorkerRole role = WorkerRole.valueOf(roleFilter);
                sender.sendMessage(ChatColor.YELLOW + "=== Workers with Role: " + role + " ===");
                for (Worker worker : workers) {
                    if (worker.getRole() == role) {
                        sender.sendMessage(ChatColor.GRAY + "- " + worker.getName());
                    }
                }
            } catch (IllegalArgumentException e) {
                sender.sendMessage(ChatColor.RED + "Invalid role: " + args[1]);
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("testbuilder")) {
            City city = cityManager.getCityByName("London"); // Replace "London" with a valid test city name
            if (city != null) {
                Worker builder = WorkerFactory.createWorker("builder", city, "Conan", UUID.randomUUID());
                city.addWorker(builder);
                sender.sendMessage("Builder worker added to " + city.getName());
            } else {
                sender.sendMessage("City not found.");
            }
            return true;
        }

        sender.sendMessage(ChatColor.RED + "Unknown subcommand. Try /workers list, /workers role <role>, or /workers testbuilder");
        return true;
    }
}
