package com.brandon.globaleconomy.core;

import com.brandon.globaleconomy.city.City;
import com.brandon.globaleconomy.city.CityManager;
import com.brandon.globaleconomy.city.CityYamlLoader;
import com.brandon.globaleconomy.city.claims.ClaimManager;
import com.brandon.globaleconomy.city.impl.commands.CityCommand;
import com.brandon.globaleconomy.city.impl.commands.CityTreasuryCommand;
import com.brandon.globaleconomy.commands.*;
import com.brandon.globaleconomy.config.NameLoader;
import com.brandon.globaleconomy.dynmap.DynmapManager;
import com.brandon.globaleconomy.economy.api.EconomyAPI;
import com.brandon.globaleconomy.economy.currencies.CurrencyManager;
import com.brandon.globaleconomy.economy.currencies.ExchangeEngine;
import com.brandon.globaleconomy.economy.impl.workers.WorkerManager;
import com.brandon.globaleconomy.economy.impl.workers.WorkerFactory;
import com.brandon.globaleconomy.economy.WalletManager;
import com.brandon.globaleconomy.gui.MerchantTradeGUI;
import com.brandon.globaleconomy.gui.MoneyChangerGUI;
import com.brandon.globaleconomy.listeners.*;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;
import org.bukkit.plugin.java.JavaPlugin;
import org.dynmap.DynmapAPI;

import java.io.File;
import java.util.*;

public class PluginCore extends JavaPlugin {

    private static PluginCore instance;

    private CityManager cityManager;
    private ClaimManager claimManager;
    private DynmapManager dynmapManager;
    private WorkerManager workerManager;
    private CityYamlLoader cityYamlLoader;
    private WorkerFactory workerFactory;

    // === NEW Economy Components ===
    private WalletManager walletManager;
    private CurrencyManager currencyManager;
    private ExchangeEngine exchangeEngine;
    private EconomyAPI economyAPI;

    @Override
    public void onEnable() {

        instance = this;
        // === (0) Register custom Citizens trait before any NPCs are created ===
        try {
            net.citizensnpcs.api.trait.TraitInfo workerTraitInfo =
                    net.citizensnpcs.api.trait.TraitInfo.create(com.brandon.globaleconomy.npc.traits.WorkerTrait.class).withName("workertrait");
            net.citizensnpcs.api.CitizensAPI.getTraitFactory().registerTrait(workerTraitInfo);
            getLogger().info("WorkerTrait registered successfully.");
        } catch (Exception e) {
            getLogger().warning("Failed to register WorkerTrait with Citizens.");
            e.printStackTrace();
        }
        try {
            TraitInfo builderTraitInfo =
                    TraitInfo.create(com.brandon.globaleconomy.npc.traits.BuilderTrait.class).withName("buildertrait");
            CitizensAPI.getTraitFactory().registerTrait(builderTraitInfo);
            getLogger().info("BuilderTrait registered successfully.");
        } catch (Exception e) {
            getLogger().warning("Failed to register BuilderTrait with Citizens.");
            e.printStackTrace();
        }


        File dataFolder = getDataFolder();
        cityYamlLoader = new CityYamlLoader(dataFolder);
        claimManager = new ClaimManager(1);
        cityManager = new CityManager();
        workerManager = WorkerManager.getInstance();
        workerFactory = new WorkerFactory();
        NameLoader nameLoader = new NameLoader(dataFolder);

        // === (1) Initialize Economy Components ===
        walletManager = new WalletManager();


        currencyManager = new CurrencyManager();
        MerchantTradeGUI.setCurrencyManager(currencyManager);
        MoneyChangerGUI.setManagers(currencyManager, walletManager);

        exchangeEngine = new ExchangeEngine();

        economyAPI = new EconomyAPI(walletManager, currencyManager, exchangeEngine, cityManager);


        // === (2) Initialize DynmapManager FIRST ===
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

        // === (3) Load cities and claim chunks ===
        Map<String, City> loadedCities = cityYamlLoader.loadCities();
        for (City city : loadedCities.values()) {
            claimManager.claimChunksForCity(city);
            System.out.println("[DEBUG] " + city.getName() + " now owns " + claimManager.getChunksForCity(city).size() + " chunks.");
            cityManager.addCity(city);
        }

        // === (4) Update Dynmap polygons ===
        if (dynmapManager != null) {
            for (City city : cityManager.getCities().values()) {
                System.out.println("DEBUG: addOrUpdateCityAreaPolygon for " + city.getName());
                dynmapManager.addOrUpdateCityAreaPolygon(city, claimManager.getChunksForCity(city));
            }
        }

        // === (5) Register commands ===
        getCommand("city").setExecutor(new CityCommand(cityManager, claimManager, dynmapManager, cityYamlLoader, currencyManager));
        getCommand("citysave").setExecutor(new CitySaveCommand(cityManager, cityYamlLoader));
        getCommand("cityreload").setExecutor(new CityReloadCommand(cityManager, cityYamlLoader, nameLoader, workerFactory));
        getCommand("currency").setExecutor(new CurrencyCommand(currencyManager, cityManager));

// Wallet-related commands
        WalletCommand walletCommand = new WalletCommand(walletManager);
        getCommand("wallet").setExecutor(walletCommand);
        getCommand("wallet").setTabCompleter(walletCommand);

// Other commands
        getCommand("citytreasury").setExecutor(new CityTreasuryCommand(cityManager));
        getCommand("money").setExecutor(walletCommand); // Same WalletCommand as 'wallet'
        getCommand("balance").setExecutor(walletCommand); // Same WalletCommand as 'wallet'
        getCommand("pay").setExecutor(new PayCommand(walletManager)); // Make sure PayCommand is implemented correctly
        getCommand("debugworkers").setExecutor(new WorkerDebugCommand());
        getCommand("merchantdebug").setExecutor(new MerchantDebugCommand());






        // === (6) Register listeners ===
        getServer().getPluginManager().registerEvents(new RegionBannerListener(cityManager), this);
        getServer().getPluginManager().registerEvents(new WorkerTradeListener(cityManager, currencyManager), this);
        getServer().getPluginManager().registerEvents(new MayorJobAssigner(), this);
        getServer().getPluginManager().registerEvents(new NPCClickListener(getCityManager()), this);
        getServer().getPluginManager().registerEvents(new MerchantGUIListener(getCityManager()), this);
        getServer().getPluginManager().registerEvents(new MoneyChangerListener(), this);


        // === (7) Schedule dynamic currency exchange updates ===
        new org.bukkit.scheduler.BukkitRunnable() {
            @Override
            public void run() {
                for (var currency : currencyManager.getCurrencies().values()) {
                    double newValue = currency.getValue(currencyManager.getMetalPool());
                    exchangeEngine.getExchangeRateManager().updateValue(currency.getName(), newValue);
                }
            }
        }.runTaskTimer(this, 0L, 1200L); // Runs every 60 seconds (1200 ticks)

    }

    @Override
    public void onDisable() {
        if (cityYamlLoader != null && cityManager != null) {
            cityYamlLoader.saveCities(cityManager.getCities());
        }
    }

    public EconomyAPI getEconomyAPI() {
        return economyAPI;
    }

    // === [ADDED BELOW] ===
    public static PluginCore getInstance() {
        return instance;
    }

    public CityManager getCityManager() {
        return cityManager;
    }

    public WorkerManager getWorkerManager() {
        return workerManager;
    }

    public WorkerFactory getWorkerFactory() {
        return workerFactory;
    }

    public CurrencyManager getCurrencyManager() {
        return currencyManager;
    }

    public WalletManager getWalletManager() {
        return walletManager;
    }

    public ExchangeEngine getExchangeEngine() {
        return exchangeEngine;
    }
}
