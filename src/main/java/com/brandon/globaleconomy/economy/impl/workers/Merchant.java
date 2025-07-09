package com.brandon.globaleconomy.economy.impl.workers;

import com.brandon.globaleconomy.city.City;
import org.bukkit.Bukkit;

import java.util.Random;
import java.util.UUID;

public class Merchant extends Worker {
    public Merchant(City city, String name, UUID npcId) {
        super(city, name, WorkerRole.MERCHANT, npcId);
    }

    @Override
    public void performWork(City city) {
        if (!isReadyToWork()) return;

        double profit = 1.5 + new Random().nextDouble() * 2.0;
        String currency = city.getPrimaryCurrency();
        city.depositToTreasury(currency, profit);

        Bukkit.getLogger().info(getName() + " earned " + profit + " " + currency + " for " + city.getName());
    }
}