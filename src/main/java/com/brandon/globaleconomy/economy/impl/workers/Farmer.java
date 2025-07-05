package com.brandon.globaleconomy.economy.impl.workers;

import com.brandon.globaleconomy.city.City;
import java.util.UUID;

public class Farmer extends Worker {
    public Farmer(City city, String name, UUID npcId) {
        super(city, name, WorkerRole.FARMER, npcId);
    }

    @Override
    public void performWork(City city) {
        // Farming logic here
        System.out.println(name + " is farming for " + city.getName());
        markCooldown();
    }
}