package com.brandon.globaleconomy.commands;

import com.brandon.globaleconomy.city.*;
import com.brandon.globaleconomy.config.NameLoader;
import com.brandon.globaleconomy.economy.impl.workers.Worker;
import com.brandon.globaleconomy.economy.impl.workers.WorkerFactory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.*;

public class CityReloadCommand implements CommandExecutor {

    private final CityManager cityManager;
    private final CityYamlLoader cityYamlLoader;
    private final NameLoader nameLoader;
    private final WorkerFactory workerFactory;

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
            Set<String> usedNames = new HashSet<>();
            List<Worker> rebuiltWorkers = new ArrayList<>();

            if (city.getWorkers() != null) {
                for (Worker oldWorker : city.getWorkers()) {
                    String workerName = nameLoader.getRandomName(city.getNation(), usedNames);
                    usedNames.add(workerName);

                    String roleName = oldWorker.getRole().name();
                    Worker rebuilt = WorkerFactory.createWorker(roleName, city, workerName, oldWorker.getNpcId());

                    if (rebuilt != null) rebuiltWorkers.add(rebuilt);
                }
                city.setWorkers(rebuiltWorkers);
            }

            cityManager.addCity(city);
        }

        sender.sendMessage("§aAll cities reloaded from disk!");
        return true;
    }
}