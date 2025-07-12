package com.brandon.globaleconomy.economy.impl.workers;

import com.brandon.globaleconomy.city.City;
import org.bukkit.Bukkit;

import java.util.UUID;

public class Resident extends Worker {

    private int workLogCooldown = 0; // Correctly placed field

    public Resident(City city, String name, UUID npcId) {
        super(city, name, WorkerRole.RESIDENT, npcId);
    }

    @Override
    public void performWork(City city) {
        if (workLogCooldown-- <= 0) {
            Bukkit.getLogger().info(getName() + " is living peacefully in " + city.getName());
            workLogCooldown = 100; // Log once every 5 seconds (assuming 20 ticks/sec)
        }
    }
}
