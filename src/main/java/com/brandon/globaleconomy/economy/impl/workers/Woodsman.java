package com.brandon.globaleconomy.economy.impl.workers;

import com.brandon.globaleconomy.city.City;

import java.util.UUID;

public class Woodsman extends Worker {
    public Woodsman(City city, String name, UUID npcId) {
        super(city, name, WorkerRole.WOODSMAN, npcId);
    }

    @Override
    public void performWork(City city) {
        // Logging logic here
        markCooldown();
    }
}