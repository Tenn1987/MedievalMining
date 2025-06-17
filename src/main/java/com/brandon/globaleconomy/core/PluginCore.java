package com.brandon.globaleconomy.core;

import com.brandon.globaleconomy.city.City;
import com.brandon.globaleconomy.city.CityManager;
import com.brandon.globaleconomy.city.claims.ClaimManager;
import com.brandon.globaleconomy.city.impl.commands.CityCommand;
import com.brandon.globaleconomy.city.CityYamlLoader;
import com.brandon.globaleconomy.commands.CityReloadCommand;
import com.brandon.globaleconomy.commands.CitySaveCommand;
import com.brandon.globaleconomy.config.NameLoader;
import com.brandon.globaleconomy.dynmap.DynmapManager;
import com.brandon.globaleconomy.economy.impl.workers.Worker;
import com.brandon.globaleconomy.economy.impl.workers.WorkerManager;
import com.brandon.globaleconomy.economy.impl.workers.WorkerFactory;
import com.brandon.globaleconomy.listeners.CityRegionListener;
import com.brandon.globaleconomy.listeners.RegionBannerListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.dynmap.DynmapAPI;

import java.io.File;
import java.util.*;

import org.bukkit.Chunk;

import static org.dynmap.bukkit.DynmapPlugin.plugin;

public class PluginCore extends JavaPlugin {
    private CityManager cityManager;
    private ClaimManager claimManager;
    private DynmapManager dynmapManager;
    private WorkerManager workerManager;
    private CityYamlLoader cityYamlLoader;
    private WorkerFactory workerFactory;


    @Override
    public void onEnable() {
        File dataFolder = getDataFolder();
        cityYamlLoader = new CityYamlLoader(dataFolder);
        claimManager = new ClaimManager(1);
        cityManager = new CityManager();
        workerManager = new WorkerManager();
        workerFactory = new WorkerFactory();
        NameLoader nameLoader = new NameLoader(dataFolder);

        // ==== (1) Initialize DynmapManager FIRST ====
        DynmapAPI dynmapAPI = null;
        if (getServer().getPluginManager().getPlugin("dynmap") != null &&
                getServer().getPluginManager().getPlugin("dynmap").isEnabled()) {
            dynmapAPI = (DynmapAPI) getServer().getPluginManager().getPlugin("dynmap");
            dynmapManager = new DynmapManager(this, dynmapAPI);
            System.out.println("DEBUG: DynmapManager initialized successfully.");
        } else {
            dynmapManager = null;
            System.out.println("DEBUG: DynmapManager is null after initialization!");
        }

        // ==== (2) Load cities from YAML and add to cityManager ====
        Map<String, City> loadedCities = cityYamlLoader.loadCities();
        for (City city : loadedCities.values()) {
            cityManager.addCity(city);
        }

        // ==== (3) Update Dynmap polygons for all loaded cities ====
        if (dynmapManager != null) {
            for (City city : cityManager.getCities().values()) {
                System.out.println("DEBUG: addOrUpdateCityAreaPolygon for " + city.getName());
                dynmapManager.addOrUpdateCityAreaPolygon(city, claimManager.getChunksForCity(city));
            }
        }

        // ==== (4) Register commands and listeners ====
        getCommand("city").setExecutor(new CityCommand(cityManager, claimManager, dynmapManager, cityYamlLoader));
        getCommand("citysave").setExecutor(new CitySaveCommand(cityManager, cityYamlLoader));
        getCommand("cityreload").setExecutor(new CityReloadCommand(cityManager, cityYamlLoader, nameLoader, workerFactory));
        getServer().getPluginManager().registerEvents(new RegionBannerListener(this, claimManager, cityManager), this);

        // ... any other setup ...
    }

    @Override
    public void onDisable() {
        if (cityYamlLoader != null && cityManager != null) {
            cityYamlLoader.saveCities(cityManager.getCities());
        }
    }
}