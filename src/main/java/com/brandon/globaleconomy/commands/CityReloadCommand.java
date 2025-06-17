package com.brandon.globaleconomy.commands;

import com.brandon.globaleconomy.city.*;
import com.brandon.globaleconomy.config.NameLoader;
import com.brandon.globaleconomy.economy.impl.workers.Worker;
import com.brandon.globaleconomy.economy.impl.workers.WorkerFactory;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.*;

public class CityReloadCommand implements CommandExecutor {

    private final CityManager cityManager;
    private final CityYamlLoader cityYamlLoader;
    private final NameLoader nameLoader; // Adjust as needed for your worker naming
    private final WorkerFactory workerFactory; // Adjust as needed

    public CityReloadCommand(CityManager cityManager, CityYamlLoader cityYamlLoader, NameLoader nameLoader, WorkerFactory workerFactory) {
        this.cityManager = cityManager;
        this.cityYamlLoader = cityYamlLoader;
        this.nameLoader = nameLoader;
        this.workerFactory = workerFactory;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        cityManager.clear();

        Map<String, City> loadedCities = cityYamlLoader.loadCities();

        for (City city : loadedCities.values()) {
            cityManager.addCity(city);

            // Optionally, reassign unique worker names
            Set<String> usedNames = new HashSet<>();
            List<Worker> rebuiltWorkers = new ArrayList<>();
            for (Worker worker : city.getWorkers()) {
                String workerName = nameLoader.getRandomName(city.getNation(), usedNames);
                usedNames.add(workerName);
                // Rebuild worker with new name and add to new list
                Worker rebuilt = workerFactory.createWorker(worker.getRole(), city, workerName);
                if (rebuilt != null) rebuiltWorkers.add(rebuilt);
            }
            city.setWorkers(rebuiltWorkers); // You might need to add a setter, or clear/addAll
        }

        sender.sendMessage("All cities reloaded from disk!");
        return true;
    }
}
