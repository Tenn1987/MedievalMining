package com.brandon.globaleconomy.economy.impl.workers;

import com.brandon.globaleconomy.city.City;
import org.bukkit.Bukkit;

import java.util.UUID;

public class Resident extends Worker {
    public Resident(City city, String name, UUID npcId) {
        super(city, name, WorkerRole.RESIDENT, npcId);
    }

    @Override
    public void performWork(City city) {
        // Idle or eventually visit public spaces or houses
        Bukkit.getLogger().info(getName() + " is living peacefully in " + city.getName());
    }
}
