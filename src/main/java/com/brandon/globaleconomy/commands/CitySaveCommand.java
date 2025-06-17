package com.brandon.globaleconomy.commands;

import com.brandon.globaleconomy.city.CityManager;
import com.brandon.globaleconomy.city.CityYamlLoader;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CitySaveCommand implements CommandExecutor {

    private final CityManager cityManager;
    private final CityYamlLoader cityYamlLoader;

    public CitySaveCommand(CityManager cityManager, CityYamlLoader cityYamlLoader) {
        this.cityManager = cityManager;
        this.cityYamlLoader = cityYamlLoader;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        cityYamlLoader.saveCities(cityManager.getCities());
        sender.sendMessage("All cities saved!");
        return true;
    }
}