package com.brandon.globaleconomy.economy.impl.workers;

import com.brandon.globaleconomy.city.City;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class Merchant extends Worker {
    public Merchant(City city, String name, UUID npcId) {
        super(city, name, WorkerRole.MERCHANT, npcId);
    }

    @Override
    public void performWork(City city) {
        this.cooldownMillis = 15000; // 15 seconds between sales
        if (!isReadyToWork()) return;

        // Simulate earning
        double profit = 1.5 + new Random().nextDouble() * 2.0;
        String currency = city.getPrimaryCurrency();
        city.depositToTreasury(currency, profit);


        // Log with rate limiter
        String logKey = getName() + "_" + city.getName(); // unique per worker+city
        String message = getName() + " earned " + profit + " " + currency + " for " + city.getName();
        rateLimitedLog(logKey, message, 5000); // only log once every 5 seconds
    }
}